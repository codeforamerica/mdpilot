package org.mdbenefits.app.submission.actions;

import com.mailgun.model.message.MessageResponse;
import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.email.MailgunEmailClient;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.data.Transmission;
import org.mdbenefits.app.data.TransmissionRepository;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HandleApplicationSigned implements Action {

    private final MessageSource messageSource;

    private final MailgunEmailClient mailgunEmailClient;

    private final SubmissionRepositoryService submissionRepositoryService;

    private final TransmissionRepository transmissionRepository;

    private final JdbcTemplate jdbcTemplate;

    public HandleApplicationSigned(MessageSource messageSource, MailgunEmailClient mailgunEmailClient,
            SubmissionRepositoryService submissionRepositoryService, TransmissionRepository transmissionRepository, JdbcTemplate jdbcTemplate) {
        this.messageSource = messageSource;
        this.mailgunEmailClient = mailgunEmailClient;
        this.submissionRepositoryService = submissionRepositoryService;
        this.transmissionRepository = transmissionRepository;
        this.jdbcTemplate = jdbcTemplate;
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
        String subject = messageSource.getMessage("email-to-recipient.subject", null, null);
        String messageBody = messageSource.getMessage("email-to-recipient.body", null, null);
        MessageResponse mailgunResponse = mailgunEmailClient.sendEmail(
                subject,
                recipientEmail,
                messageBody
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
