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
        assertThat(withNoMedicalExpenses(result)).isEmpty();
    }

    @Test
    public void shouldAddFieldsForHouseholdExpensesFrequency() {
        Submission submission = new SubmissionTestBuilder()
            .with("householdHomeExpenses[]", List.of("RENT", "GARBAGE", "PHONE"))
            .with("homeExpenseRent", "20")
            .with("homeExpenseGarbage", "30")
            .with("homeExpensePhone", "23")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(withNoMedicalExpenses(result).size()).isEqualTo(3);
        assertThat(result.get("homeExpenseRentFrequency"))
            .isEqualTo(new SingleField("homeExpenseRentFrequency", "Monthly", null));

        assertThat(result.get("homeExpenseGarbageFrequency"))
            .isEqualTo(new SingleField("homeExpenseGarbageFrequency", "Monthly", null));

        assertThat(result.get("homeExpensePhoneFrequency"))
            .isEqualTo(new SingleField("homeExpensePhoneFrequency", "Monthly", null));
    }

    private Map<String, SubmissionField> withNoMedicalExpenses(Map<String, SubmissionField> results) {
        assertThat(results.keySet()).contains("hasMedicalExpenses");
        var hasMedicalExpenses = (SingleField) results.remove("hasMedicalExpenses");
        assertThat(hasMedicalExpenses.getValue()).isEqualTo("false");
        return results;
    }
}
