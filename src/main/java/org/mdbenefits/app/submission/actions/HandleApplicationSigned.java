package org.mdbenefits.app.submission.actions;

import com.mailgun.model.message.MessageResponse;
import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.email.MailgunEmailClient;
import formflow.library.pdf.PdfService;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.data.Transmission;
import org.mdbenefits.app.data.TransmissionRepository;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class HandleApplicationSigned implements Action {

    private final MessageSource messageSource;

    private final MailgunEmailClient mailgunEmailClient;

    private final SubmissionRepositoryService submissionRepositoryService;

    private final TransmissionRepository transmissionRepository;

    private final JdbcTemplate jdbcTemplate;

    private final PdfService pdfService;

    public HandleApplicationSigned(
            MessageSource messageSource,
            MailgunEmailClient mailgunEmailClient,
            SubmissionRepositoryService submissionRepositoryService,
            TransmissionRepository transmissionRepository,
            JdbcTemplate jdbcTemplate,
            PdfService pdfService) {
        this.messageSource = messageSource;
        this.mailgunEmailClient = mailgunEmailClient;
        this.submissionRepositoryService = submissionRepositoryService;
        this.transmissionRepository = transmissionRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.pdfService = pdfService;
    }

    @Override
    public void run(Submission submission) {
        assignConfirmationNumber(submission);
        sendEmailToApplicant(submission);
        enqueueTransmission(submission);
    }

    private void enqueueTransmission(Submission submission) {
        Transmission transmission = Transmission.fromSubmission(submission);
        transmissionRepository.save(transmission);
        log.info("Created transmission");
   }

    private void assignConfirmationNumber(Submission submission) {
        String confirmationNumber = jdbcTemplate.queryForObject("select 'M' || nextval('confirmation_sequence')", String.class);
        submission.getInputData().put("confirmationNumber", confirmationNumber);
        log.info("Using confirmationNumber: {}", confirmationNumber);
        submissionRepositoryService.save(submission);
    }

    private void sendEmailToApplicant(Submission submission) {
        String recipientEmail = (String) submission.getInputData().get("emailAddress");
        if (recipientEmail == null || recipientEmail.isBlank()) {
            return;
        }

        String confirmationNumber = (String) submission.getInputData().get("confirmationNumber");
        String county = (String) submission.getInputData().get("county");

        String countyPhone = messageSource.getMessage("email-to-recipient.phone." + county, null, Locale.ENGLISH);
        String countyEmail = messageSource.getMessage("email-to-recipient.email." + county, null, Locale.ENGLISH);

        String subject = messageSource.getMessage("email-to-recipient.subject", null, Locale.ENGLISH);
        String messageBody = messageSource.getMessage(
                "email-to-recipient.body", new Object[]{confirmationNumber, countyPhone, countyEmail}, Locale.ENGLISH);

        File applicationPdfFile = pdfService.generate(submission);

        MessageResponse mailgunResponse = mailgunEmailClient.sendEmail(
                subject,
                recipientEmail,
                messageBody,
                List.of(applicationPdfFile)
        );
        if (mailgunResponse != null) {
            log.info("Sent email. Mailgun response: {}", mailgunResponse.getMessage());
            submission.getInputData().put("sentEmailToApplicant", true);
        } else {
            log.error("Failed to send email. See logs for error");
            submission.getInputData().put("sentEmailToApplicant", false);
        }
        submissionRepositoryService.save(submission);
    }
}
