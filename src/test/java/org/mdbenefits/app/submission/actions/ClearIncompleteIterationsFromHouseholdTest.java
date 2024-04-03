package org.mdbenefits.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ClearIncompleteIterationsFromHouseholdTest {
    
    @Autowired
    SubmissionRepositoryService submissionRepositoryService;
    
    @Test
    void shouldClearIncompleteIterationsFromHouseholdSubflow() {
        ClearIncompleteIterationsFromHousehold clearIncompleteIterationsFromHousehold = new ClearIncompleteIterationsFromHousehold(submissionRepositoryService);
        Submission submission = new Submission().builder()
                .id(UUID.randomUUID())
                .flow("flow")
                .build();
        var household = new ArrayList<Map<String, Object>>();
        var householdMember1 = new HashMap<String, Object>();   
        householdMember1.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        var householdMember2 = new HashMap<String, Object>();
        householdMember2.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        var householdMember3 = new HashMap<String, Object>();
        householdMember3.put(Submission.ITERATION_IS_COMPLETE_KEY, false);
        household.add(householdMember1);
        household.add(householdMember2);
        household.add(householdMember3);
        HashMap<String, Object> inputData = new HashMap<>();
        inputData.put("household", household);
        submission.setInputData(inputData);
        clearIncompleteIterationsFromHousehold.run(submission);
        ArrayList<HashMap<String, Object>> incomeSubflow = (ArrayList<HashMap<String, Object>>) submission.getInputData().get("household");
        assertThat(incomeSubflow).size().isEqualTo(2);
        assertThat(incomeSubflow).doesNotContain(householdMember3);
        assertThat(incomeSubflow).isEqualTo(List.of(householdMember1, householdMember2));
    }
}