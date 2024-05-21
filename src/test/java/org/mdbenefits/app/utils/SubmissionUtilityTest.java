package org.mdbenefits.app.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class SubmissionUtilityTest {

    @CsvSource(value = {
            "12,10,2010,'12/10/2010'",
            "1,2,2010,'01/02/2010'",
            "2,04,2011,'02/04/2011'",
    })
    @ParameterizedTest
    public void shouldBeCorrectlyFormatted(String month, String day, String year, String fullDate) {
        Map<String, Object> inputData = Map.of(
                "birthMonth", month,
                "birthDay", day,
                "birthYear", year);
        assertThat(SubmissionUtilities.getFormattedBirthdate(inputData)).isEqualTo(fullDate);
    }

    @Test
    public void getHouseholdIncomeReviewItemsShouldIgnoreIncompleteSubflowIterations() {
        Submission submission = new Submission();
        HashMap<String, Object> job1 = new HashMap<>();
        job1.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        job1.put("householdMemberJobAdd", "you");
        job1.put("employerName", "ACME Inc");
        job1.put("payPeriod", "It varies");
        job1.put("payAmountFor30Days", 400.0);

        HashMap<String, Object> job2 = new HashMap<>();
        job2.put(Submission.ITERATION_IS_COMPLETE_KEY, false);
        job2.put("employerName", "Monsters Inc");
        job2.put("payAmountFor30Days", 200.0);

        ArrayList<Map<String, Object>> income = new ArrayList<>();
        income.add(job1);
        income.add(job2);

        HashMap<String, Object> inputData = new HashMap<>();
        inputData.put("income", income);
        submission.setInputData(inputData);

        ArrayList<HashMap<String, Object>> householdIncomeReviewItems = SubmissionUtilities.getHouseholdIncomeReviewItems(
                submission);
        assertThat(householdIncomeReviewItems.stream()
                .noneMatch(item -> item.getOrDefault("jobName", "").equals("Monsters Inc"))).isTrue();
        assertThat(
                householdIncomeReviewItems.stream().noneMatch(item -> item.getOrDefault("income", "").equals("$200"))).isTrue();
    }
}
