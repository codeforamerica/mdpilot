package org.mdbenefits.app.submission.actions;

import com.mailgun.model.message.MessageResponse;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.email.MailgunEmailClient;
import formflow.library.pdf.PdfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.mdbenefits.app.data.Transmission;
import org.mdbenefits.app.data.TransmissionRepository;
import org.mdbenefits.app.data.enums.County;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@SpringBootTest
class HandleApplicationSignedTest {

    @MockBean
    MailgunEmailClient mailgunEmailClient;
    @Autowired
    PdfService pdfService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    SubmissionRepositoryService submissionRepositoryService;
    @Autowired
    TransmissionRepository transmissionRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Captor
    ArgumentCaptor<String> emailBodyCaptor;
    @Captor
    ArgumentCaptor<List<File>> attachmentsCaptor;

    private HandleApplicationSigned handleApplicationSigned;

    @BeforeEach
    void setup() {
        handleApplicationSigned = new HandleApplicationSigned(messageSource, mailgunEmailClient, submissionRepositoryService,
                transmissionRepository, jdbcTemplate, pdfService);
    }

    @Test
    public void shouldSkipSendingIfNoEmailAddress() {
        Submission submission = new SubmissionTestBuilder()
                .build();
        submission.setFlow("mdBenefitsFlow");

        handleApplicationSigned.run(submission);

        Mockito.verify(mailgunEmailClient, Mockito.never()).sendEmail(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void shouldRecordSuccessfulSend() {
        Submission submission = buildValidSubmission();
        MessageResponse mockResponse = mock(MessageResponse.class);
        Mockito.when(mailgunEmailClient.sendEmail(any(), any(), any(), any()))
                .thenReturn(mockResponse);

        handleApplicationSigned.run(submission);

        assertThat(submission.getInputData().get("sentEmailToApplicant")).isEqualTo(true);
        assertThat((String) submission.getInputData().get("confirmationNumber")).matches("M\\d{5,}");
        Transmission transmission = transmissionRepository.findTransmissionBySubmission(submission);
        assertThat(transmission.getStatus()).isEqualTo("QUEUED");
    }

    @Test
    public void includesConfirmationNumberInEmailBody() {
        Submission submission = buildValidSubmission();
        MessageResponse mockResponse = mock(MessageResponse.class);
        Mockito.when(mailgunEmailClient.sendEmail(any(), any(), emailBodyCaptor.capture(), any()))
                .thenReturn(mockResponse);

        handleApplicationSigned.run(submission);

        assertThat(emailBodyCaptor.getValue()).contains((String) submission.getInputData().get("confirmationNumber"));
    }


    @Test
    public void includesAttachedPdf() {
        Submission submission = buildValidSubmission();
        MessageResponse mockResponse = mock(MessageResponse.class);
        Mockito.when(mailgunEmailClient.sendEmail(any(), any(), any(), attachmentsCaptor.capture()))
                .thenReturn(mockResponse);

        handleApplicationSigned.run(submission);

        assertThat(attachmentsCaptor.getValue()).hasSize(1);
    }

    @Test
    public void shouldRecordFailedSend() {
        Submission submission = buildValidSubmission();
        Mockito.when(mailgunEmailClient.sendEmail(any(), any(), any()))
                .thenReturn(null);

        handleApplicationSigned.run(submission);

        assertThat(submission.getInputData().get("sentEmailToApplicant")).isEqualTo(false);
    }

    private Submission buildValidSubmission() {
        Submission submission = new SubmissionTestBuilder()
                .with("emailAddress", "foo@example.com")
                .with("county", County.QUEEN_ANNES.name())
                .build();
        submission.setFlow("mdBenefitsFlow");
        return submission;
    }
}
