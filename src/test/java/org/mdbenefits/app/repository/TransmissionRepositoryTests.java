package org.mdbenefits.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mdbenefits.app.data.Transmission;
import org.mdbenefits.app.data.TransmissionRepository;
import org.mdbenefits.app.data.enums.TransmissionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TransmissionRepositoryTests {

    @Autowired
    private TransmissionRepository transmissionRepository;

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    private List<Submission> submissionList = new ArrayList<>();

    @BeforeAll
    void setup() {

        for (int i = 0; i < 6; i++) {
            Map<String, Object> inputData = Map.of(
                    "confirmationNumber", "M0000" + i,
                    "testKey", "this is a test value",
                    "otherTestKey", List.of("A", "B", "C"),
                    "household", List.of(Map.of("firstName", "John", "lastName", "Perez")));
            Submission submission = Submission.builder()
                    .inputData(inputData)
                    .urlParams(new HashMap<>())
                    .flow("testFlow")
                    .submittedAt(OffsetDateTime.now())
                    .build();

            submission = saveAndReload(submission);

            Transmission transmission = Transmission.fromSubmission(submission);
            transmission.setRetryCount(i);
            transmissionRepository.save(transmission);
            submissionList.add(submission);
        }
    }

    @Test
    void testGettingTransmissionBySubmission() {
        submissionList.forEach(submission -> {
            Transmission t = transmissionRepository.findTransmissionBySubmission(submission);
            assertThat(t).isNotNull();
        });
    }

    @Test
    void testGettingTransmissionBySubmissionByStatusAndRequeueCount() {
        List<Transmission> transmissionsList = transmissionRepository.findByStatusAndRetryCountLessThanOrderByCreatedAtAsc(
                TransmissionStatus.QUEUED.name(), 5);
        assertThat(transmissionsList.size()).isEqualTo(5);

        transmissionsList = transmissionRepository.findByStatusAndRetryCountLessThanOrderByCreatedAtAsc(
                TransmissionStatus.QUEUED.name(), 2);
        assertThat(transmissionsList.size()).isEqualTo(2);
    }


    private Submission saveAndReload(Submission submission) {
        Submission savedSubmission = submissionRepositoryService.save(submission);
        return submissionRepositoryService.findById(savedSubmission.getId()).orElseThrow();
    }

}
