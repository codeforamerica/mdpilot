package org.mdbenefits.app.submission.actions;

import com.mailgun.model.message.MessageResponse;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.email.MailgunEmailClient;
import formflow.library.pdf.PdfService;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.mdbenefits.app.data.Transmission;
import org.mdbenefits.app.data.TransmissionRepository;
import org.mdbenefits.app.data.enums.Counties;
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
@TestInstance(Lifecycle.PER_CLASS)
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

    @Autowired
    private SubmissionRepository submissionRepository;

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

        cleanupSubmissionTransmission(submission);
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

        cleanupSubmissionTransmission(submission);
    }

    @Test
    public void includesConfirmationNumberInEmailBody() {
        Submission submission = buildValidSubmission();
        MessageResponse mockResponse = mock(MessageResponse.class);
        Mockito.when(mailgunEmailClient.sendEmail(any(), any(), emailBodyCaptor.capture(), any()))
                .thenReturn(mockResponse);

        handleApplicationSigned.run(submission);

        assertThat(emailBodyCaptor.getValue()).contains((String) submission.getInputData().get("confirmationNumber"));

        cleanupSubmissionTransmission(submission);
    }


    @Test
    public void includesAttachedPdf() {
        Submission submission = buildValidSubmission();
        MessageResponse mockResponse = mock(MessageResponse.class);
        Mockito.when(mailgunEmailClient.sendEmail(any(), any(), any(), attachmentsCaptor.capture()))
                .thenReturn(mockResponse);

        handleApplicationSigned.run(submission);

        assertThat(attachmentsCaptor.getValue()).hasSize(1);

        cleanupSubmissionTransmission(submission);
    }

    @Test
    public void shouldRecordFailedSend() {
        Submission submission = buildValidSubmission();
        Mockito.when(mailgunEmailClient.sendEmail(any(), any(), any()))
                .thenReturn(null);

        handleApplicationSigned.run(submission);

        assertThat(submission.getInputData().get("sentEmailToApplicant")).isEqualTo(false);
        cleanupSubmissionTransmission(submission);
    }


    private Submission buildValidSubmission() {
        Submission submission = new SubmissionTestBuilder()
                .with("emailAddress", "foo@example.com")
                .with("county", Counties.QUEEN_ANNES.name())
                .build();
        submission.setFlow("mdBenefitsFlow");
        return submission;
    }

    private void cleanupSubmissionTransmission(Submission submission) {
        Transmission transmission = transmissionRepository.findTransmissionBySubmission(submission);
        if (transmission != null) {
            transmissionRepository.delete(transmission);
        }
        submissionRepository.delete(submission);
    }
}
