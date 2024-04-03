package org.mdbenefits.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ClearIncompleteIncomeIterationsTest {

    @Test
    void shouldClearIncompleteIterationsFromIncomeSubflow() {
        ClearIncompleteIncomeIterations clearIncompleteIncomeIterations = new ClearIncompleteIncomeIterations();
        Submission submission = new Submission();
        var income = new ArrayList<Map<String, Object>>();
        var job1 = new HashMap<String, Object>();
        job1.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        job1.put("uuid", "complete iteration");
        job1.put("employerName", "ACME Inc");
        job1.put("payPeriod", "It varies");
        job1.put("payPeriodAmount", 400.0);
        var job2 = new HashMap<String, Object>();
        job2.put(Submission.ITERATION_IS_COMPLETE_KEY, false);
        job2.put("uuid", "Current Iteration");
        job2.put("employerName", "Monsters Inc");
        job2.put("payPeriodAmount", 200.0);
        var job3 = new HashMap<String, Object>();
        job3.put(Submission.ITERATION_IS_COMPLETE_KEY, false);
        job3.put("uuid", "Incomplete Iteration");
        job3.put("employerName", "Disney Inc");
        income.add(job1);
        income.add(job2);
        income.add(job3);
        HashMap<String, Object> inputData = new HashMap<>();
        inputData.put("income", income);
        submission.setInputData(inputData);
        clearIncompleteIncomeIterations.run(submission, "Current Iteration");
        ArrayList<HashMap<String, Object>> incomeSubflow = (ArrayList<HashMap<String, Object>>) submission.getInputData().get("income");
        assertThat(incomeSubflow).size().isEqualTo(2);
        assertThat(incomeSubflow).doesNotContain(job3);
        assertThat(incomeSubflow).isEqualTo(List.of(job1, job2));
    }
}