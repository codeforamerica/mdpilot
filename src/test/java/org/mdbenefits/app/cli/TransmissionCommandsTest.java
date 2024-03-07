package org.mdbenefits.app.cli;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.email.MailgunEmailClient;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.enums.EthnicityType;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.mdbenefits.app.data.Transmission;
import org.mdbenefits.app.data.TransmissionRepository;
import org.mdbenefits.app.data.enums.Counties;
import org.mdbenefits.app.data.enums.ProgramType;
import org.mdbenefits.app.data.enums.RaceType;
import org.mdbenefits.app.data.enums.TransmissionStatus;
import org.mdbenefits.app.submission.actions.HandleApplicationSigned;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TransmissionCommandsTest {

    @MockBean
    MailgunEmailClient mailgunEmailClient;
    @Autowired
    MessageSource messageSource;
    @Autowired
    SubmissionRepositoryService submissionRepositoryService;
    @Autowired
    TransmissionRepository transmissionRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    TransmissionCommands transmissionCommands;

    private HandleApplicationSigned handleApplicationSigned;

    private List<Submission> submissionList = new ArrayList<>();

    @BeforeEach
    void setup() {
        handleApplicationSigned = new HandleApplicationSigned(messageSource, mailgunEmailClient, submissionRepositoryService,
                transmissionRepository, jdbcTemplate, transmissionCommands);

        submissionList.add(new SubmissionTestBuilder()
                .with("programs[]", List.of(ProgramType.SNAP))
                .withPersonalInfo("John", "Doe", "10", "12", "1999",
                        "", "", "", "", "")
                .withHouseholdMemberApplying("Betty", "White", "10", "2", "1999",
                        "Child", "F", "NeverMarried", "firstGrade", "123456789", 
                        "Yes", "Yes", List.of(RaceType.ASIAN.name()), EthnicityType.NOT_HISPANIC_OR_LATINO.name())
                .with("county", Counties.BALTIMORE.name())
                .build()
        );

        submissionList.add(new SubmissionTestBuilder()
                .with("programs[]", List.of(ProgramType.SNAP))
                .withPersonalInfo("John", "Doe II", "10", "12", "1999",
                        "", "", "", "", "")
                .withHouseholdMemberApplying("Betty", "Red", "10", "2", "1999",
                        "Child", "F", "NeverMarried", "firstGrade", "123456789",
                        "Yes", "Yes", List.of(RaceType.ASIAN.name()), EthnicityType.NOT_HISPANIC_OR_LATINO.name())
                .with("county", Counties.BALTIMORE.name())
                .build()
        );

        submissionList.add(new SubmissionTestBuilder()
                .with("programs[]", List.of(ProgramType.SNAP))
                .withPersonalInfo("John", "Doe Jr", "10", "12", "1999",
                        "", "", "", "", "")
                .withHouseholdMemberApplying("Malcolm", "Tester", "10", "2", "1999",
                        "Child", "F", "NeverMarried", "firstGrade", "123456789",
                        "Yes", "Yes", List.of(RaceType.ASIAN.name()), EthnicityType.NOT_HISPANIC_OR_LATINO.name())
                .with("county", Counties.BALTIMORE.name())
                .build()
        );

        submissionList.forEach(s -> {
                    s.setFlow("mdBenefitsFlow");
                    s.setSubmittedAt(OffsetDateTime.now());
                    handleApplicationSigned.run(s);
                    submissionRepositoryService.save(s);
                }
        );
    }

    @Test
    public void ensureSubmittedSubmissionsAreEnqueued() {
        List<Transmission> transmissions = transmissionRepository.findTransmissionsByStatus(TransmissionStatus.QUEUED.name());

        assertThat(transmissions.size()).isEqualTo(submissionList.size());

        transmissionCommands.transmit();

        assertThat(transmissionRepository.findTransmissionsByStatus(TransmissionStatus.QUEUED.name()).isEmpty()).isTrue();

        submissionList.forEach(s -> {
            Transmission transmission = transmissionRepository.findTransmissionBySubmission(s);
            assertThat(transmission.getStatus().equals(TransmissionStatus.COMPLETED.name())).isTrue();
        });
    }
}
