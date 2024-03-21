package org.mdbenefits.app.preparers;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;

class HouseholdExpensesPreparerTest {

    private final HouseholdExpensesPreparer preparer = new HouseholdExpensesPreparer();


    @Test
    public void shouldntAddAnythingForNoExpenses() {
        Submission submission = new Submission();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldAddFieldsForHouseholdExpenses() {
        Submission submission = new SubmissionTestBuilder()
                .with("householdHomeExpenses[]", List.of("mortgage", "homeOwnerInsurance"))
                .with("householdHomeExpenseAmount_wildcard_mortgage", "20")
                .with("householdHomeExpenseAmount_wildcard_homeownerInsurance", "30")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.size()).isEqualTo(6);
        assertThat(result.get("householdExpensesType1"))
                .isEqualTo(new SingleField("householdExpensesType", "Homeowner's Insurance", 1));
        assertThat(result.get("householdExpensesAmount1"))
                .isEqualTo(new SingleField("householdExpensesAmount", "30", 1));
        assertThat(result.get("householdExpensesFreq1"))
                .isEqualTo(new SingleField("householdExpensesFreq", "Monthly", 1));

        assertThat(result.get("householdExpensesType2"))
                .isEqualTo(new SingleField("householdExpensesType", "Mortgage", 2));
        assertThat(result.get("householdExpensesAmount2"))
                .isEqualTo(new SingleField("householdExpensesAmount", "20", 2));
        assertThat(result.get("householdExpensesFreq2"))
                .isEqualTo(new SingleField("householdExpensesFreq", "Monthly", 2));
    }
}
