package org.mdbenefits.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

import com.mailgun.model.message.MessageResponse;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.email.MailgunEmailClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class HandleApplicationSignedTest {
    @MockBean
    MailgunEmailClient mailgunEmailClient;
    @Autowired
    MessageSource messageSource;
    @MockBean
    SubmissionRepositoryService submissionRepositoryService;

    private HandleApplicationSigned handleApplicationSigned;

    @BeforeEach
    void setup() {
        handleApplicationSigned = new HandleApplicationSigned(messageSource, mailgunEmailClient, submissionRepositoryService);
    }

    @Test
    public void shouldSkipSendingIfNoEmailAddress() {
        Submission submission = new SubmissionTestBuilder()
                .build();

        handleApplicationSigned.run(submission);

        Mockito.verify(mailgunEmailClient, Mockito.never()).sendEmail(Mockito.any(), Mockito.any(), Mockito.any());    }

    @Test
    public void shouldRecordSuccessfulSend() {
        Submission submission = new SubmissionTestBuilder()
                .with("emailAddress", "foo@example.com")
                .build();
        MessageResponse mockResponse = mock(MessageResponse.class);
        Mockito.when(mailgunEmailClient.sendEmail(any(), any(), any()))
                .thenReturn(mockResponse);

        handleApplicationSigned.run(submission);

        assertThat(submission.getInputData().get("sentEmailToApplicant")).isEqualTo(true);
    }

    @Test
    public void shouldRecordFailedSend() {
        Submission submission = new SubmissionTestBuilder()
                .with("emailAddress", "foo@example.com")
                .build();
        Mockito.when(mailgunEmailClient.sendEmail(any(), any(), any()))
                .thenReturn(null);

        handleApplicationSigned.run(submission);

        assertThat(submission.getInputData().get("sentEmailToApplicant")).isEqualTo(false);
    }
}
