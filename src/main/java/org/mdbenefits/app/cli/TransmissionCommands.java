package org.mdbenefits.app.cli;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import formflow.library.data.Submission;
import formflow.library.data.UserFile;
import formflow.library.data.UserFileRepositoryService;
import formflow.library.email.MailgunEmailClient;
import formflow.library.file.CloudFile;
import formflow.library.file.CloudFileRepository;
import formflow.library.pdf.PdfService;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.data.Transmission;
import org.mdbenefits.app.data.TransmissionRepository;
import org.mdbenefits.app.data.enums.Counties;
import org.mdbenefits.app.data.enums.TransmissionStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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

    private List<String> emailRecipients_Baltimore =
            List.of(
                    "bseeger+balt@codeforamerica.org",
                    "vraj+balt@codeforamerica.org"
            );
    private List<String> emailRecipients_QueenAnnes =
            List.of(
                    "bseeger+qa@codeforamerica.org",
                    "vraj+qa@codeforamerica.org"
            );

    private final TransmissionRepository transmissionRepository;
    private final CloudFileRepository cloudFileRepository;
    private final PdfService pdfService;
    private final GoogleDriveClient googleDriveClient;
    private final UserFileRepositoryService userFileRepositoryService;

    private final MailgunEmailClient mailgunEmailClient;
    private final MessageSource messageSource;

    public TransmissionCommands(
            TransmissionRepository transmissionRepository,
            CloudFileRepository cloudFileRepository,
            UserFileRepositoryService userFileRepositoryService,
            PdfService pdfService,
            GoogleDriveClient googleDriveClient,
            MailgunEmailClient mailgunEmailClient,
            MessageSource messageSource) {

        this.transmissionRepository = transmissionRepository;
        this.cloudFileRepository = cloudFileRepository;
        this.userFileRepositoryService = userFileRepositoryService;
        this.pdfService = pdfService;
        this.googleDriveClient = googleDriveClient;
        this.mailgunEmailClient = mailgunEmailClient;
        this.messageSource = messageSource;
    }

    // TODO:  ensure only one running at a time via @Scheduled config (another ticket, I think)
    // I think there is a configuration where we can specify that that the next one cannot start
    // until the first one is done.
    //
    //@Scheduled(fixedRateString = "${transmissions.wic-ece-transmission-rate}")
    @ShellMethod(key = "send-files")
    public void transmit() {
        log.info("[Transmission] Finding submissions to transmit...");

        List<Transmission> queuedTransmissions = transmissionRepository.findTransmissionsByStatus("QUEUED");
        if (queuedTransmissions.isEmpty()) {
            log.info("[Transmission] Nothing to transmit. Exiting.");
            return;
        }

        log.info("[Transmission] Found %s queued transmissions".formatted(queuedTransmissions.size()));

        // Consideration - should we batch these?  Probably not, initially.
        // Worth keeping an eye on.
        if (queuedTransmissions.size() > 50) {
            log.warn("[Transmission] More than 50 submissions at once ({})", queuedTransmissions.size());
        }
        transmitBatch(queuedTransmissions);
    }

    private void transmitBatch(List<Transmission> transmissions) {

        log.info("[Transmission] Preparing to send {} transmissions.", transmissions.size());

        transmissions.forEach(transmission -> {
            Map<String, String> errorMap = new HashMap<>();
            Submission submission = transmission.getSubmission();
            log.info("[Transmission {}] Sending transmission for submission with ID: {}.", transmission.getId(),
                    submission.getId());
            updateTransmissionStatus(transmission, TransmissionStatus.TRANSMITTING, errorMap, false);
            byte[] pdfFileBytes;
            try {
                pdfFileBytes = pdfService.getFilledOutPDF(submission);
            } catch (IOException e) {
                String error = String.format("Failed to generate PDF for Transmission ID %s (Submission ID %s): %s.",
                        transmission.getId(),
                        submission.getId(),
                        e.getMessage());
                handleError(transmission, "pdfGeneration", error, errorMap);
                return;
            }
            String county = (String) submission.getInputData().get("county");
            String folderId = getCountyFolderId(county);
            String emailRecipients = getCountyEmailRecipients(county);
            String confirmationNumber = (String) submission.getInputData().get("confirmationNumber");

            String pdfFileName = getPdfFilename(confirmationNumber);

            List<File> existingDirectories = googleDriveClient.findDirectory(confirmationNumber, folderId);
            if (!existingDirectories.isEmpty()) {
                log.info(
                        "[Transmission {}]: Found {} existing instances of folder '{}' for submission with ID: {}. Deleting existing instances.",
                        transmission.getId(),
                        existingDirectories.size(),
                        confirmationNumber,
                        submission.getId());
                // remove any already existing folders
                for (File dir : existingDirectories) {
                    if (!googleDriveClient.deleteDirectory(dir.getName(), dir.getId(), errorMap)) {
                        String error = String.format("Failed to delete existing Google Drive directory '%s'", dir.getId());
                        handleError(transmission, null, error, errorMap);
                        // don't return - keep going. A new folder will be created and the link to that new
                        // folder will be sent to caseworker's office
                    }
                }
            }

            GoogleDriveFolder newFolder = googleDriveClient.createFolder(folderId, confirmationNumber, errorMap);
            if (newFolder.getId() == null) {
                // something is really wrong here; note the error and skip the entry
                String error = String.format("Failed to create folder in Google Drive for submission with ID: %s.",
                        submission.getId());
                handleError(transmission, null, error, errorMap);
                return;
            }

            // upload pdf file
            String pdfGDId = googleDriveClient.uploadFile(newFolder.getId(), pdfFileName, "application/pdf", pdfFileBytes,
                    confirmationNumber + "_PDF", errorMap);

            if (pdfGDId.isBlank()) {
                // the PDF did not get sent correctly.  Note it and skip the entry
                googleDriveClient.deleteDirectory(confirmationNumber, newFolder.getId(), errorMap);
                String error = String.format("Unable to upload the PDF file for submission with ID: %s", submission.getId());
                handleError(transmission, "pdfTransfer", error, errorMap);
                return;
            }

            List<UserFile> userFilesForSubmission = userFileRepositoryService.findAllBySubmission(submission);

            for (int count = 0; count < userFilesForSubmission.size(); count++) {
                UserFile file = userFilesForSubmission.get(count);
                try {
                    // get the file from S3
                    CloudFile cloudFile = cloudFileRepository.get(file.getRepositoryPath());

                    String fileName = getUserFileName(confirmationNumber, file, count + 1, userFilesForSubmission.size());
                    log.info("[Transmission {}] Uploading file {} of {} for submission with ID: {}.",
                            transmission.getId(),
                            count + 1,
                            userFilesForSubmission.size(),
                            submission.getId());
                    // send to google
                    googleDriveClient.uploadFile(newFolder.getId(), fileName, file.getMimeType(), cloudFile.getFileBytes(),
                            file.getFileId().toString(), errorMap);
                } catch (AmazonS3Exception e) {
                    String error = String.format(
                            "Unable to upload the UserFile (ID: %s) for submission with ID: %s. Exception: %s",
                            file.getFileId(), submission.getId(), e.getMessage());
                    handleError(transmission, "fetchingS3File", error, errorMap);
                }
            }

            sendEmailToCaseworkers(transmission, confirmationNumber, emailRecipients, newFolder.getUrl(), errorMap);

            updateTransmissionStatus(transmission, TransmissionStatus.COMPLETED, errorMap, true);
        });
    }

    // email addresses is a string of email addresses with a space in between
    private void sendEmailToCaseworkers(Transmission transmission, String confirmationNumber, String emailAddresses,
            String googleDriveLink, Map<String, String> errorMap) {

        if (emailAddresses.isEmpty()) {
            String error = "Unable to send email, as there are no recipients configured.";
            handleError(transmission, "sendingEmail", error, errorMap);
            return;
        }

        Locale locale = new Locale("en");
        String subject = messageSource.getMessage("email-to-caseworkers.subject", new Object[]{confirmationNumber}, null, locale);
        String body = messageSource.getMessage("email-to-caseworkers.body", new Object[]{confirmationNumber, googleDriveLink},
                null, locale);

        if (mailgunEmailClient.sendEmail(subject, emailAddresses, body) == null) {
            String error = String.format("Sending email to '%s' failed.", emailAddresses);
            handleError(transmission, "sendingEmail", error, errorMap);
        }
    }

    // Set errorKey = null when the errorMsg has already been recorded in 'errorMap'
    private void handleError(Transmission transmission, String errorKey, String errorMsg, Map<String, String> errorMap) {
        log.error("[Transmission {}]: {}", transmission.getId(), errorMsg);
        if (errorKey != null) {
            errorMap.put(errorKey, errorMsg);
        }
        updateTransmissionStatus(transmission, TransmissionStatus.FAILED, errorMap, false);
    }

    private void updateTransmissionStatus(Transmission transmission, TransmissionStatus status, Map<String, String> errorMap,
            boolean markSent) {
        transmission.setStatus(status.name());
        transmission.setErrors(errorMap);
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

    private String getCountyEmailRecipients(String county) {
        List<String> recipients =
                county.equals(Counties.BALTIMORE.name()) ? emailRecipients_Baltimore : emailRecipients_QueenAnnes;
        return String.join(", ", recipients);
    }
}
