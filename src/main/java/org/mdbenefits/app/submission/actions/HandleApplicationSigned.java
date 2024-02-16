package org.mdbenefits.app.submission.actions;

import com.mailgun.model.message.MessageResponse;
import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.email.MailgunEmailClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HandleApplicationSigned implements Action {

    private final MessageSource messageSource;

    private final MailgunEmailClient mailgunEmailClient;

    private final SubmissionRepositoryService submissionRepositoryService;

    public HandleApplicationSigned(MessageSource messageSource, MailgunEmailClient mailgunEmailClient,
            SubmissionRepositoryService submissionRepositoryService) {
        this.messageSource = messageSource;
        this.mailgunEmailClient = mailgunEmailClient;
        this.submissionRepositoryService = submissionRepositoryService;
    }

    @Override
    public void run(Submission submission) {
        sendEmailToApplicant(submission);
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
            log.info("Sent email for application with ID: {}. Mailgun response: {}", submission.getId(), mailgunResponse.getMessage());
            submission.getInputData().put("sentEmailToApplicant", true);
        } else {
            log.error("Failed to send email. See logs for error");
            submission.getInputData().put("sentEmailToApplicant", false);
        }
        submissionRepositoryService.save(submission);
    }
}
