package org.mdbenefits.app.cli;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import formflow.library.data.Submission;
import formflow.library.data.UserFile;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFile;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.data.Transmission;
import org.mdbenefits.app.data.TransmissionRepository;
import org.mdbenefits.app.data.enums.Counties;
import org.mdbenefits.app.data.enums.TransmissionStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.stereotype.Service;
import com.google.api.services.drive.model.File;

@Slf4j
@Service
@ShellComponent
public class TransmissionCommands {

    // TODO: make these config variables in .env and application yaml files.
    // These, below, are for the staging folders
    @Value("${google.drive.baltimore-county-directory-id}")
    private String BALTIMORE_COUNTY_GOOGLE_DIR_ID;
    
    @Value("${google.drive.queen-annes-county-directory-id}")
    private String QUEENANNES_COUNTY_GOOGLE_DIR_ID;

    private final TransmissionRepository transmissionRepository;
    private final CloudFileRepository cloudFileRepository;
    private final PdfService pdfService;
    private final GoogleDriveClient googleDriveClient;
    private final UserFileRepositoryService userFileRepositoryService;

    public TransmissionCommands(
            TransmissionRepository transmissionRepository,
            CloudFileRepository cloudFileRepository,
            UserFileRepositoryService userFileRepositoryService,
            PdfService pdfService,
            GoogleDriveClientImpl googleDriveClient) throws GeneralSecurityException, IOException {

        this.transmissionRepository = transmissionRepository;
        this.cloudFileRepository = cloudFileRepository;
        this.userFileRepositoryService = userFileRepositoryService;
        this.pdfService = pdfService;
        this.googleDriveClient = googleDriveClient;
    }

    // TODO:  ensure only one running at a time via @Scheduled config
    // I think there is a configuration where we can specify that that the next one cannot start
    // until the first one is done.
    //
    //@Scheduled(fixedRateString = "${transmissions.wic-ece-transmission-rate}")
    @ShellMethod(key = "send-files")
    public void transmit() throws IOException, JSchException, SftpException {
        log.info("Finding submissions to transmit...");

        List<Transmission> queuedTransmissions = transmissionRepository.findTransmissionsByStatus("QUEUED");
        if (queuedTransmissions.isEmpty()) {
            log.info("Nothing to transmit. Exiting.");
            return;
        }

        log.info("Found %s queued transmissions".formatted(queuedTransmissions.size()));

        // Consideration - should we batch these?  Probably not, initially.
        // Worth keeping an eye on.
        if (queuedTransmissions.size() > 50) {
            log.warn("More than 50 submissions at once ({})", queuedTransmissions.size());
        }
        transmitBatch(queuedTransmissions);
    }

    private void transmitBatch(List<Transmission> transmissions)
            throws IOException, JSchException, SftpException {

        Map<String, String> errors = new HashMap<>();
        log.info("Preparing to send {} submissions", transmissions.size());

        transmissions.forEach(transmission -> {
            Submission submission = transmission.getSubmission();
            updateTransmissionStatus(transmission, TransmissionStatus.TRANSMITTING, errors, false);
            try {
                byte[] pdfFileBytes = pdfService.getFilledOutPDF(submission);

                String county = (String) submission.getInputData().get("county");
                String folderId = getCountyFolderId(county);
                String confirmationNumber = (String) submission.getInputData().get("confirmationNumber");

                String pdfFileName = getPdfFilename(confirmationNumber);

                // directory duplication can happen when:
                //   1) previous run has errored out
                //   2) state requests we re-run record for whatever reason
                //
                // As long as we have the proper link in the email, they state will get the
                // correct record to look at (via the email).
                //

                // do any directories in this county exist by that name? if so delete them and their files.
                List<File> existingDirectories = googleDriveClient.findDirectory(confirmationNumber, folderId);
                if (!existingDirectories.isEmpty()) {
                    // remove folder
                    for (File dir : existingDirectories) {
                        googleDriveClient.deleteDirectory(dir.getName(), dir.getId(), errors);
                    }
                }

                String entryFolderId = googleDriveClient.createFolder(folderId, confirmationNumber, errors);
                if (entryFolderId == null) {
                    // something is really wrong here; note the error and skip the entry
                    log.error("Failed to create folder for submission {}", submission.getId());
                    updateTransmissionStatus(transmission, TransmissionStatus.FAILED, errors, false);
                    return;
                }

                List<UserFile> userFilesForSubmission = userFileRepositoryService.findAllBySubmission(submission);

                for (int count = 0; count < userFilesForSubmission.size(); count++) {
                    UserFile file = userFilesForSubmission.get(count);
                    // get the file from S3
                    CloudFile cloudFile = cloudFileRepository.get(file.getRepositoryPath());
                    String fileName = getUserFileName(confirmationNumber, file, count + 1, userFilesForSubmission.size());
                    // send to google
                    googleDriveClient.uploadFile(entryFolderId, fileName, file.getMimeType(),
                            cloudFile.getFileBytes(), errors);
                }

                // upload pdf file
                googleDriveClient.uploadFile(entryFolderId, pdfFileName, "application/pdf", pdfFileBytes, errors);
            } catch (IOException e) {
                log.error("Failed to generate PDF for submission {}: {}", submission.getId(), e.getMessage());
            }

            // TODO - send emails to state (another ticket)

            updateTransmissionStatus(transmission, TransmissionStatus.COMPLETED, errors, true);
        });
    }

    private void updateTransmissionStatus(Transmission transmission, TransmissionStatus status, Map<String, String> errors,
            boolean markSent) {
        transmission.setUpdatedAt(OffsetDateTime.now());
        transmission.setStatus(status.name());
        transmission.setErrors(errors);
        if (markSent) {
            transmission.setSentAt(OffsetDateTime.now());
        }
        transmissionRepository.save(transmission);
    }

    private String getPdfFilename(String confirmationNumber) {
        // Final form: M000000-9701-Maryland-Benefits-Pilot.pdf
        return String.format("%s-9701-Maryland-Benefits-Pilot.pdf", confirmationNumber);
    }

    private String getUserFileName(String confirmationNumber, UserFile file, int number, int totalNumFiles) {
        // Final form: M000000-doc1of2-Maryland-Benefits-Pilot.[filetype]
        int extIndex = file.getOriginalName().lastIndexOf(".");
        String ext = file.getOriginalName().substring(extIndex + 1);
        return String.format("%s-doc%dof%d-Maryland-Benefits-Pilot.%s", confirmationNumber, number, totalNumFiles, ext);
    }

    private String getCountyFolderId(String county) {
        return county.equals(Counties.BALTIMORE.name()) ? BALTIMORE_COUNTY_GOOGLE_DIR_ID : QUEENANNES_COUNTY_GOOGLE_DIR_ID;
    }
}
