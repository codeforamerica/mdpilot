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
        Submission submission = new SubmissionTestBuilder()
            .with("householdHomeExpenses[]", List.of("None"))
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldAddFieldsForHouseholdExpenses() {
        Submission submission = new SubmissionTestBuilder()
            .with("householdHomeExpenses[]", List.of("RENT", "GARBAGE", "PHONE"))
            .with("homeExpenseRent", "20")
            .with("homeExpenseGarbage", "30")
            .with("homeExpensePhone", "23")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.size()).isEqualTo(9);
        assertThat(result.get("householdExpensesType1"))
            .isEqualTo(new SingleField("householdExpensesType", "Homeowner's Insurance", 1));
        assertThat(result.get("householdExpensesAmount1"))
            .isEqualTo(new SingleField("homeExpenseGarbage", "30", null));
        assertThat(result.get("householdExpensesFreq1"))
            .isEqualTo(new SingleField("householdExpensesFreq", "Monthly", null));

        assertThat(result.get("householdExpensesType2"))
            .isEqualTo(new SingleField("householdExpensesType", "Mortgage", null));
        assertThat(result.get("householdExpensesAmount2"))
            .isEqualTo(new SingleField("homeExpenseRent", "20", null));
        assertThat(result.get("householdExpensesFreq2"))
            .isEqualTo(new SingleField("householdExpensesFreq", "Monthly", null));

        assertThat(result.get("householdExpensesType2"))
            .isEqualTo(new SingleField("householdExpensesType", "Mortgage", null));
        assertThat(result.get("householdExpensesAmount2"))
            .isEqualTo(new SingleField("homeExpensePhone", "23", null));
        assertThat(result.get("householdExpensesFreq2"))
            .isEqualTo(new SingleField("householdExpensesFreq", "Monthly", null));
    }
}
