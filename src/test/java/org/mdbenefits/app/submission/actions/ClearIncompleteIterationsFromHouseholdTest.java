package org.mdbenefits.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ClearIncompleteIterationsFromHouseholdTest {
    @Test
    void shouldClearIncompleteIterationsFromHouseholdSubflow() {
        ClearIncompleteIterationsFromHousehold clearIncompleteIterationsFromHousehold = new ClearIncompleteIterationsFromHousehold();
        Submission submission = new Submission();
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