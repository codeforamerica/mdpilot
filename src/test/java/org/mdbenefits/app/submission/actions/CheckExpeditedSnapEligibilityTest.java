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
                .with("householdMoneyOnHandLessThan100", "true")
                .with("incomeLessThan150", "true")
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void eligibleSnapEvenIfNotDirectlyApplying() {
        Submission submission = new SubmissionTestBuilder()
                .with("isApplyingForExpeditedSnap", "false")
                .with("householdMoneyOnHandLessThan100", "true")
                .with("incomeLessThan150", "true")
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void ineligibleIfMonthlyIncomeIsGreaterThan150AndCashOnHandIsLessThan100() {
        Submission submission = new SubmissionTestBuilder()
                .with("isApplyingForExpeditedSnap", "true")
                .with("householdMoneyOnHandLessThan100", "false")
                .with("incomeLessThan150", "true")
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("false", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void eligibleIfMigrantOrSeasonalFarmWorkerAndCashOnHandIsLessThan100() {
        Submission submission = new SubmissionTestBuilder()
                .with("isApplyingForExpeditedSnap", "true")
                .with("householdMoneyOnHandLessThan100", "true")
                .with("migrantOrSeasonalFarmWorkerInd", "true")
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void ineligibleIfMigrantOrSeasonalFarmWorkerAndCashOnHandIsGreaterThan100() {
        Submission submission = new SubmissionTestBuilder()
                .with("isApplyingForExpeditedSnap", "true")
                .with("householdMoneyOnHandLessThan100", "false")
                .with("migrantOrSeasonalFarmWorkerInd", "true")
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("false", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void eligibleIfIncomeAndCashOnHandIsEqualToExpenses() {
        Submission submission = new SubmissionTestBuilder()
                .with("isApplyingForExpeditedSnap", "true")
                .with("householdIncomeLast30Days", "1500")
                .with("expeditedMoneyOnHandAmount", "200")
                .with("householdHomeExpenses[]", List.of("GARBAGE", "PHONE", "RENT"))
                .with("homeExpenseGarbage", "500")
                .with("homeExpensePhone", "1000")
                .with("homeExpenseRent", "200")
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void eligibleIfIncomeAndCashOnHandLessThanExpenses() {
        Submission submission = new SubmissionTestBuilder()
                .with("isApplyingForExpeditedSnap", "true")
                .with("householdIncomeLast30Days", "1000")
                .with("expeditedMoneyOnHandAmount", "200")
                .with("householdHomeExpenses[]", List.of("GARBAGE", "PHONE", "RENT"))
                .with("homeExpenseGarbage", "500")
                .with("homeExpensePhone", "1000")
                .with("homeExpenseRent", "200")
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void ineligibleIfIncomeAndCashOnHandIsGreaterThanExpenses() {
        Submission submission = new SubmissionTestBuilder()
                .with("isApplyingForExpeditedSnap", "true")
                .with("householdIncomeLast30Days", "1000")
                .with("expeditedMoneyOnHandAmount", "1000")
                .with("householdHomeExpenses[]", List.of("GARBAGE", "PHONE", "RENT"))
                .with("homeExpenseGarbage", "500")
                .with("homeExpensePhone", "1000")
                .with("homeExpenseRent", "200")
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("false", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }

    @Test
    void eligibleEvenIfContradictoryDataIsProvided() {
        Submission submission = new SubmissionTestBuilder()
                .with("isApplyingForExpeditedSnap", "true")
                .with("householdMoneyOnHandLessThan100", "true")
                .with("householdIncomeLast30Days", "1000")
                .with("incomeLessThan150", "true")
                .with("expeditedMoneyOnHandAmount", "1000")
                .build();

        checkExpeditedSnapEligibility.run(submission);
        assertEquals("true", submission.getInputData().get("isEligibleForExpeditedSnap"));
    }
}
