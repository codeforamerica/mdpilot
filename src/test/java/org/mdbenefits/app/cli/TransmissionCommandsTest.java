package org.mdbenefits.app.cli;

import com.mailgun.model.message.MessageResponse;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.email.MailgunEmailClient;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import formflow.library.pdf.PdfService;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.mdbenefits.app.data.enums.EthnicityType;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.mdbenefits.app.data.Transmission;
import org.mdbenefits.app.data.TransmissionRepository;
import org.mdbenefits.app.data.enums.County;
import org.mdbenefits.app.data.enums.ProgramType;
import org.mdbenefits.app.data.enums.RaceType;
import org.mdbenefits.app.data.enums.TransmissionStatus;
import org.mdbenefits.app.submission.actions.HandleApplicationSigned;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(
        properties = {
                "transmission.transmission-rate-seconds=5",
                "transmission.transmission-initial-delay-seconds=2"
        },
        webEnvironment = WebEnvironment.RANDOM_PORT)
public class TransmissionCommandsTest {

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
    @SpyBean
    TransmissionCommands transmissionCommands;

    private HandleApplicationSigned handleApplicationSigned;

    private final List<Submission> submissionList = new ArrayList<>();

    @BeforeAll
    void setup() {
        handleApplicationSigned = new HandleApplicationSigned(messageSource, mailgunEmailClient, submissionRepositoryService,
                transmissionRepository, jdbcTemplate, pdfService);

        submissionList.add(new SubmissionTestBuilder()
                .with("programs[]", List.of(ProgramType.SNAP))
                .withPersonalInfo("John", "Doe", "10", "12", "1999",
                        "", "", "", "", "")
                .withHouseholdMemberApplying("Betty", "White", "10", "2", "1999",
                        "Child", "F", "NeverMarried", "firstGrade", "123456789",
                        "Yes", "Yes", List.of(RaceType.ASIAN.name()), EthnicityType.NOT_HISPANIC_OR_LATINO.name())
                .with("county", County.BALTIMORE.name())
                .build()
        );

        submissionList.add(new SubmissionTestBuilder()
                .with("programs[]", List.of(ProgramType.SNAP))
                .withPersonalInfo("John", "Doe II", "10", "12", "1999",
                        "", "", "", "", "")
                .withHouseholdMemberApplying("Betty", "Red", "10", "2", "1999",
                        "Child", "F", "NeverMarried", "firstGrade", "123456789",
                        "Yes", "Yes", List.of(RaceType.ASIAN.name()), EthnicityType.NOT_HISPANIC_OR_LATINO.name())
                .with("county", County.BALTIMORE.name())
                .build()
        );

        submissionList.add(new SubmissionTestBuilder()
                .with("programs[]", List.of(ProgramType.SNAP))
                .withPersonalInfo("John", "Doe Jr", "10", "12", "1999",
                        "", "", "", "", "")
                .withHouseholdMemberApplying("Malcolm", "Tester", "10", "2", "1999",
                        "Child", "F", "NeverMarried", "firstGrade", "123456789",
                        "Yes", "Yes", List.of(RaceType.ASIAN.name()), EthnicityType.NOT_HISPANIC_OR_LATINO.name())
                .with("county", County.BALTIMORE.name())
                .build()
        );

        submissionList.forEach(s -> {
                    s.setFlow("mdBenefitsFlow");
                    s.setSubmittedAt(OffsetDateTime.now());
                    handleApplicationSigned.run(s);
                    submissionRepositoryService.save(s);
                }
        );

        MessageResponse messageResponse = MessageResponse.builder()
                .id("12345")
                .message("Queued. Thank you")
                .build();

        when(mailgunEmailClient.sendEmail(any(), any(), any())).thenReturn(messageResponse);
    }

    @Test
    @Order(1)
    public void ensureSubmittedSubmissionsAreEnqueued() {
        List<Transmission> transmissions = transmissionRepository.findTransmissionsByStatus(TransmissionStatus.QUEUED.name());

        assertThat(transmissions.size()).isEqualTo(submissionList.size());
    }

    @Test
    @Order(2)
    public void transmitterRunsAndProcessesWork() {
        // Note: if more submitted submissions are added it may take longer than 13 seconds to process everything.
        await().atMost(13, TimeUnit.SECONDS).untilAsserted(
                () -> verify(transmissionCommands, times(2)).transmit());

        // ensure that all transmissions were processed
        assertThat(transmissionRepository.findTransmissionsByStatus(TransmissionStatus.QUEUED.name()).isEmpty()).isTrue();

        submissionList.forEach(s -> {
            Transmission transmission = transmissionRepository.findTransmissionBySubmission(s);
            assertThat(transmission.getStatus().equals(TransmissionStatus.COMPLETED.name())).isTrue();
        });
    }

    @Test
    @Order(3)
    public void transmitterRunsWhenNoWorkIsQueued() {

        assertThat(transmissionRepository.findTransmissionsByStatus(TransmissionStatus.QUEUED.name())).isEmpty();

        await().atMost(12, TimeUnit.SECONDS).untilAsserted(
                () -> verify(transmissionCommands, times(2)).transmit());
    }
}