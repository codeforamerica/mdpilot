package org.mdbenefits.app.cli;

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
            GoogleDriveClient googleDriveClient) {

        this.transmissionRepository = transmissionRepository;
        this.cloudFileRepository = cloudFileRepository;
        this.userFileRepositoryService = userFileRepositoryService;
        this.pdfService = pdfService;
        this.googleDriveClient = googleDriveClient;
    }

    // TODO:  ensure only one running at a time via @Scheduled config (another ticket, I think)
    // I think there is a configuration where we can specify that that the next one cannot start
    // until the first one is done.
    //
    //@Scheduled(fixedRateString = "${transmissions.wic-ece-transmission-rate}")
    @ShellMethod(key = "send-files")
    public void transmit() {
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

    private void transmitBatch(List<Transmission> transmissions) {

        Map<String, String> errors = new HashMap<>();
        log.info("Preparing to send {} transmissions.", transmissions.size());

        transmissions.forEach(transmission -> {
            Submission submission = transmission.getSubmission();
            log.info("Sending transmission with ID {} for submission with ID: {}.", transmission.getId(), submission.getId());
            updateTransmissionStatus(transmission, TransmissionStatus.TRANSMITTING, errors, false);
            try {
                byte[] pdfFileBytes = pdfService.getFilledOutPDF(submission);

                String county = (String) submission.getInputData().get("county");
                String folderId = getCountyFolderId(county);
                String confirmationNumber = (String) submission.getInputData().get("confirmationNumber");

                String pdfFileName = getPdfFilename(confirmationNumber);

                List<File> existingDirectories = googleDriveClient.findDirectory(confirmationNumber, folderId);
                if (!existingDirectories.isEmpty()) {
                    log.info("Found {} existing instances of folder for submission with ID: {}. Deleting existing instances.", existingDirectories.size(),
                            submission.getId());
                    // remove folder
                    for (File dir : existingDirectories) {
                        googleDriveClient.deleteDirectory(dir.getName(), dir.getId(), errors);
                    }
                }

                String entryFolderId = googleDriveClient.createFolder(folderId, confirmationNumber, errors);
                if (entryFolderId == null) {
                    // something is really wrong here; note the error and skip the entry
                    log.error("Failed to create folder for submission with ID: {}.", submission.getId());
                    updateTransmissionStatus(transmission, TransmissionStatus.FAILED, errors, false);
                    return;
                }

                List<UserFile> userFilesForSubmission = userFileRepositoryService.findAllBySubmission(submission);

                for (int count = 0; count < userFilesForSubmission.size(); count++) {
                    UserFile file = userFilesForSubmission.get(count);

                    // get the file from S3
                    CloudFile cloudFile = cloudFileRepository.get(file.getRepositoryPath());

                    String fileName = getUserFileName(confirmationNumber, file, count + 1, userFilesForSubmission.size());
                    log.info("Uploading file {} of {} for submission with ID: {}.", count + 1, userFilesForSubmission.size(),
                            submission.getId());
                    // send to google
                    googleDriveClient.uploadFile(entryFolderId, fileName, file.getMimeType(), cloudFile.getFileBytes(),
                            file.getFileId().toString(), errors);
                }

                // upload pdf file
                googleDriveClient.uploadFile(entryFolderId, pdfFileName, "application/pdf", pdfFileBytes,
                        confirmationNumber + "_PDF", errors);
            } catch (IOException e) {
                log.error("Failed to generate PDF for submission with ID {}: {}.", submission.getId(), e.getMessage());
            }

            // TODO - send emails to state (another ticket)

            updateTransmissionStatus(transmission, TransmissionStatus.COMPLETED, errors, true);
        });
    }

    private void updateTransmissionStatus(Transmission transmission, TransmissionStatus status, Map<String, String> errors,
            boolean markSent) {
        transmission.setStatus(status.name());
        transmission.setErrors(errors);
        if (markSent) {
            transmission.setSentAt(OffsetDateTime.now());
        }
        transmissionRepository.save(transmission);
    }

    /**
     * Return a specially formatted file name for PDF files.  Example: M000000-9701-Maryland-Benefits-Pilot.pdf
     *
     * @param confirmationNumber application confirmation number (ex. M00001)
     * @return String filename
     */
    private String getPdfFilename(String confirmationNumber) {
        return String.format("%s-9701-Maryland-Benefits-Pilot.pdf", confirmationNumber);
    }

    /**
     * Return a specially formatted file name for user files.  Example: M000000-doc1of2-Maryland-Benefits-Pilot.jpg
     *
     * @param confirmationNumber application confirmation number (ex. M00001)
     * @param file               UserFile to pull information from
     * @param number             the number that this file is (X in Xof10)
     * @param totalNumFiles      the total number of user files (X in 1ofX)
     * @return String filename
     */
    private String getUserFileName(String confirmationNumber, UserFile file, int number, int totalNumFiles) {
        int extIndex = file.getOriginalName().lastIndexOf(".");
        String ext = file.getOriginalName().substring(extIndex + 1);
        return String.format("%s-doc%dof%d-Maryland-Benefits-Pilot.%s", confirmationNumber, number, totalNumFiles, ext);
    }

    /**
     * Return the proper Google Drive ID based on the county name.
     *
     * @param county String containing the county name
     * @return String containing the Google Drive ID.
     */
    private String getCountyFolderId(String county) {
        return county.equals(Counties.BALTIMORE.name()) ? BALTIMORE_COUNTY_GOOGLE_DIR_ID : QUEENANNES_COUNTY_GOOGLE_DIR_ID;
    }
}
