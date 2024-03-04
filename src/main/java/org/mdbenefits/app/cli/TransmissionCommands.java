package org.mdbenefits.app.cli;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import formflow.library.data.Submission;
import formflow.library.data.UserFile;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.file.CloudFile;
import formflow.library.file.CloudFileRepository;
import formflow.library.file.S3CloudFileRepository;
import formflow.library.pdf.PdfService;
import io.opencensus.stats.Aggregation.Count;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mdbenefits.app.data.Transmission;
import org.mdbenefits.app.data.TransmissionRepository;
import org.mdbenefits.app.data.enums.Counties;
import org.mdbenefits.app.data.enums.TransmissionStatus;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@ShellComponent
public class TransmissionCommands {

    // TODO: make these config variables.  These are staging folders
    private final String BALTIMORE_COUNTY_GOOGLE_DIR_ID = "1YOYOyWQk4FwWKzcm7LawsgdJcYDar3vF";
    private final String QUEENANNES_COUNTY_GOOGLE_DIR_ID = "17IV3UXlIY6JVtFuDXYbiwMibcUGZx-Ga";

    private final TransmissionRepository transmissionRepository;
    private final CloudFileRepository cloudFileRepository;
    private final PdfService pdfService;
    private final GoogleDriveClient googleDriveClient;
    private final UserFileRepositoryService userFileRepositoryService;

    private final String failedSubmissionKey = "failed";

    private final String failedDocumentationKey = "failed_documentation";

    private final String successfulSubmissionKey = "success";


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

        List<String> errors = new ArrayList<>();
        log.info("Preparing to send {} submissions", transmissions.size());

        transmissions.forEach(transmission -> {
            // TODO change status to "TRANSMITTING"
            transmission.setStatus(TransmissionStatus.TRANSMITTING.name());
            transmissionRepository.save(transmission);
            Submission submission = transmission.getSubmission();
            try {
                byte[] pdfFileBytes = pdfService.getFilledOutPDF(submission);

                String county = (String) submission.getInputData().get("county");
                String folderId = getCountyFolderId(county);
                String confirmationNumber = (String) submission.getInputData().get("confirmationNumber");

                String pdfFileName = getPdfFilename(confirmationNumber);
                
                

                // TODO - check if folder with that name exists already.
                //   Clear out contents from folder.
                //   Re-add items.
                //
                // directory duplication can happen when:
                //   1) previous run has errored out
                //   2) state requests we re-run record for whatever reason
                //
                // As long as we have the proper link in the email, they state will get the
                // correct record to look at (via the email).
                //
                String entryFolderId = null;
                List<String> existingDirectories = googleDriveClient.findDirectory(confirmationNumber, folderId);
                if (existingDirectories.size() > 0) {
                    // remove folder
                    
                    // TODO - clear out contents of folder
                    // TODO - log that we are clearing out contents of folder
                    // TODO - log that we are re-uploading files
                } else {
                    entryFolderId = googleDriveClient.createFolder(folderId, confirmationNumber, errors);
                }
                
                if (entryFolderId == null) {
                    // something is really wrong here
                    log.error("Failed to create folder for submission {}", submission.getId());
                    // TODO update Transmission table with errors
                    // set status to FAILED
                    return;
                }

                List<UserFile> userFilesForSubmission = userFileRepositoryService.findAllBySubmission(submission);

                for (UserFile file : userFilesForSubmission) {
                    // get file from S3
                    CloudFile cloudFile = cloudFileRepository.get(file.getRepositoryPath());
                    // TODO = craft proper filename
                    //String fileName = getUserFileName();

                    // send to google
                    googleDriveClient.uploadFile(entryFolderId, file.getOriginalName(), file.getMimeType(),
                            cloudFile.getFileBytes(), errors);
                }

                // upload pdf file
                googleDriveClient.uploadFile(entryFolderId, pdfFileName, "application/pdf", pdfFileBytes, errors);
            } catch (IOException e) {
                log.error("Failed to generate PDF for submission {}: {}", submission.getId(), e.getMessage());
            }

            // TODO - update accounting in transmission table that the records was sent
            // Change status to "COMPLETE"
            // TODO - send emails to state
        });


        /*
        List<UUID> successfullySubmittedIds = new ArrayList<>();

        successfullySubmittedIds.forEach(id -> {
            Submission submission = Submission.builder().id(id).build();
            Transmission transmission = transmissionRepository.findBySubmissionAndTransmissionType(submission, transmissionType);
            transmission.setTimeSent(new Date());
            transmission.setStatus(TransmissionStatus.Complete);
            transmissionRepository.save(transmission);
        });
        log.info("Finished transmission of a batch");

        failedSubmissions.forEach((id, errorMessages) -> {
                    Submission submission = Submission.builder().id(id).build();
                    Transmission transmission = transmissionRepository.findBySubmissionAndTransmissionType(submission, transmissionType);
                    transmission.setStatus(TransmissionStatus.Failed);
                    transmission.setRunId(runId);
                    transmission.setSubmissionErrors(errorMessages);
                    transmissionRepository.save(transmission);
                }
        );
        failedDocumentation.forEach((id, errorMessages) -> {
                    Submission submission = Submission.builder().id(id).build();
                    Transmission transmission = transmissionRepository.findBySubmissionAndTransmissionType(submission, transmissionType);
                    transmission.setDocumentationErrors(errorMessages);
                    transmissionRepository.save(transmission);
                }
        );
        */
    }

    private String getPdfFilename(String confirmationNumber) {
        // Final form: M000000-9701-Maryland-Benefits-Pilot.pdf
        return String.format("%s-9701-Maryland-Benefits-Pilot.pdf", confirmationNumber);
    }

    private String getCountyFolderId(String county) {
        return county.equals(Counties.BALTIMORE.name()) ? BALTIMORE_COUNTY_GOOGLE_DIR_ID : QUEENANNES_COUNTY_GOOGLE_DIR_ID;
    }
}
