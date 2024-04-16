package org.mdbenefits.app.submission.actions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import formflow.library.data.Submission;
import java.util.List;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;


class CheckExpeditedSnapEligibilityTest {

    private final CheckExpeditedSnapEligibility checkExpeditedSnapEligibility = new CheckExpeditedSnapEligibility();

    @Test
    void eligibleForExpeditedSnapIfIncomeAndCashOnHandIsLessThanExpenses() {
        Submission submission = new SubmissionTestBuilder()
            .with("isApplyingForExpeditedSnap", "true")
            .with("householdIncomeLast30Days", "806.12")
            .with("expeditedMoneyOnHandAmount", "304.45")
            .with("householdRentAmount", "1500")
            .with("migrantOrSeasonalFarmWorkerInd", "false")
            .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void ineligibleIfIncomeAndCashOnHandIsGreaterThanExpenses() {
        Submission submission = new SubmissionTestBuilder()
            .with("isApplyingForExpeditedSnap", "true")
            .with("householdIncomeLast30Days", "1600.51")
            .with("expeditedMoneyOnHandAmount", "400.45")
            .with("householdRentAmount", "1399.99")
            .with("migrantOrSeasonalFarmWorkerInd", "false")
            .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("false", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void eligibleIfIncomeAndCashOnHandIsEqualToExpenses() {
        Submission submission = new SubmissionTestBuilder()
            .with("isApplyingForExpeditedSnap", "true")
            .with("householdIncomeLast30Days", "1500.00")
            .with("householdRentAmount", "1500")
            .with("migrantOrSeasonalFarmWorkerInd", "false")
            .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void eligibleIfIncomeAndCashOnHandIsEqualToUtilitiesExpenses() {
        Submission submission = new SubmissionTestBuilder()
            .with("householdHomeExpenses[]", List.of("GARBAGE", "PHONE"))
            .with("homeExpenseGarbage", "500")
            .with("homeExpensePhone", "1000")
            .with("isApplyingForExpeditedSnap", "true")
            .with("householdIncomeLast30Days", "1500.00")
            .with("migrantOrSeasonalFarmWorkerInd", "false")
            .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void inEligibleIfIncomeAndCashOnHandIsGreaterThanUtilitiesExpenses() {
        Submission submission = new SubmissionTestBuilder()
            .with("householdHomeExpenses[]", List.of("RENT", "GARBAGE", "PHONE"))
            .with("homeExpenseRent", "100")
            .with("homeExpenseGarbage", "400")
            .with("homeExpensePhone", "1000")
            .with("isApplyingForExpeditedSnap", "true")
            .with("householdIncomeLast30Days", "1500.00")
            .with("migrantOrSeasonalFarmWorkerInd", "false")
            .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("false", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void eligibleIfMonthlyIncomeIsLessThan150AndCashOnHandIsLessThan100() {
        Submission submission = new SubmissionTestBuilder()
            .with("isApplyingForExpeditedSnap", "true")
            .with("householdIncomeLast30Days", "149.99")
            .with("expeditedMoneyOnHandAmount", "99.99")
            .with("householdRentAmount", "0")
            .with("migrantOrSeasonalFarmWorkerInd", "false")
            .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void ineligibleIfMonthlyIncomeIsLessThan150AndCashOnHandIsGreaterThan100() {
        Submission submission = new SubmissionTestBuilder()
            .with("isApplyingForExpeditedSnap", "true")
            .with("householdIncomeLast30Days", "149.99")
            .with("expeditedMoneyOnHandAmount", "100.01")
            .with("householdRentAmount", "0")
            .with("migrantOrSeasonalFarmWorkerInd", "false")
            .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("false", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void ineligibleIfMonthlyIncomeIsGreaterThan150AndCashOnHandIsLessThan100() {
        Submission submission = new SubmissionTestBuilder()
            .with("isApplyingForExpeditedSnap", "true")
            .with("householdIncomeLast30Days", "150.01")
            .with("expeditedMoneyOnHandAmount", "99.99")
            .with("householdRentAmount", "0")
            .with("migrantOrSeasonalFarmWorkerInd", "false")
            .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("false", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void eligibleIfMigrantOrSeasonalFarmWorkerAndCashOnHandIsLessThan100() {
        Submission submission = new SubmissionTestBuilder()
            .with("isApplyingForExpeditedSnap", "true")
            .with("householdIncomeLast30Days", "500")
            .with("expeditedMoneyOnHandAmount", "99.99")
            .with("householdRentAmount", "0")
            .with("migrantOrSeasonalFarmWorkerInd", "true")
            .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }
}
