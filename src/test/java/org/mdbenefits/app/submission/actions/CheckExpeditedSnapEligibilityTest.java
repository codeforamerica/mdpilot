package org.mdbenefits.app.submission.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


class CheckExpeditedSnapEligibilityTest {

    private final CheckExpeditedSnapEligibility checkExpeditedSnapEligibility = new CheckExpeditedSnapEligibility();

    @Test
    void shouldQualifyForExpeditedSnapIfIncomeAndCasOnHandIsLessThanExpenses() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("isApplyingForExpeditedSnap", "true");
        inputData.put("householdIncomeLast30Days", "806.12");
        inputData.put("expeditedMoneyOnHandAmount", "304.45");
        inputData.put("householdRentAmount", "1500");
        inputData.put("migrantOrSeasonalFarmWorkerInd", "false");

        Submission submission = Submission.builder()
                .inputData(inputData)
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void shouldNotQualifyIfIncomeAndCashOnHandIsGreaterThanExpenses() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("isApplyingForExpeditedSnap", "true");
        inputData.put("householdIncomeLast30Days", "1600.51");
        inputData.put("expeditedMoneyOnHandAmount", "400.45");
        inputData.put("householdRentAmount", "1399.99");
        inputData.put("migrantOrSeasonalFarmWorkerInd", "false");

        Submission submission = Submission.builder()
                .inputData(inputData)
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("false", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Disabled
    @Test
    void shouldQualifyIfIncomeAndCashOnHandIsEqualToExpenses() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("isApplyingForExpeditedSnap", "true");
        inputData.put("householdIncomeLast30Days", "1500.00");
        inputData.put("expeditedMoneyOnHandAmount", "300.00");
        inputData.put("householdRentAmount", "1500");
        inputData.put("migrantOrSeasonalFarmWorkerInd", "false");

        Submission submission = Submission.builder()
                .inputData(inputData)
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void shouldQualifyIfMonthlyIncomeIsLessThan150AndCashOnHandIsLessThan100() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("isApplyingForExpeditedSnap", "true");
        inputData.put("householdIncomeLast30Days", "149.99");
        inputData.put("expeditedMoneyOnHandAmount", "99.99");
        inputData.put("householdRentAmount", "0");
        inputData.put("migrantOrSeasonalFarmWorkerInd", "false");

        Submission submission = Submission.builder()
                .inputData(inputData)
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void shouldNotQualifyIfMonthlyIncomeIsLessThan150AndCashOnHandIsGreaterThan100() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("isApplyingForExpeditedSnap", "true");
        inputData.put("householdIncomeLast30Days", "149.99");
        inputData.put("expeditedMoneyOnHandAmount", "100.01");
        inputData.put("householdRentAmount", "0");
        inputData.put("migrantOrSeasonalFarmWorkerInd", "false");

        Submission submission = Submission.builder()
                .inputData(inputData)
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("false", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void shouldNotQualifyIfMonthlyIncomeIsGreaterThan150AndCashOnHandIsLessThan100() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("isApplyingForExpeditedSnap", "true");
        inputData.put("householdIncomeLast30Days", "150.01");
        inputData.put("expeditedMoneyOnHandAmount", "99.99");
        inputData.put("householdRentAmount", "0");
        inputData.put("migrantOrSeasonalFarmWorkerInd", "false");

        Submission submission = Submission.builder()
                .inputData(inputData)
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("false", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void shouldQualifyIfMigrantOrSeasonalFarmWorkerAndCashOnHandIsLessThan100() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("isApplyingForExpeditedSnap", "true");
        inputData.put("householdIncomeLast30Days", "0");
        inputData.put("expeditedMoneyOnHandAmount", "99.99");
        inputData.put("householdRentAmount", "0");
        inputData.put("migrantOrSeasonalFarmWorkerInd", "true");

        Submission submission = Submission.builder()
                .inputData(inputData)
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }
}
