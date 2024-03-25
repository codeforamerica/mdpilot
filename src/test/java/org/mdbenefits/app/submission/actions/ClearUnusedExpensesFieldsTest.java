package org.mdbenefits.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.mdbenefits.app.data.enums.HomeExpensesType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class ClearUnusedExpensesFieldsTest {

    private ClearUnusedExpensesFields clearUnusedExpensesFields;

    @BeforeEach
    void setup() {
        clearUnusedExpensesFields = new ClearUnusedExpensesFields();
    }

    @Test
    public void shouldClearAllFieldsIfNoneSelected() {
        Submission submission = new SubmissionTestBuilder()
                .with("householdHomeExpenses[]", List.of("NONE"))
                .with(HomeExpensesType.RENT.getInputFieldName(), "2000")
                .with(HomeExpensesType.WATER.getInputFieldName(), "222")
                .with(HomeExpensesType.OIL.getInputFieldName(), "1000")
                .build();
        submission.setFlow("mdBenefitsFlow");

        clearUnusedExpensesFields.run(submission);

        assertThat(submission.getInputData().get(HomeExpensesType.RENT.getInputFieldName())).isNull();
        assertThat(submission.getInputData().get(HomeExpensesType.WATER.getInputFieldName())).isNull();
        assertThat(submission.getInputData().get(HomeExpensesType.OIL.getInputFieldName())).isNull();
    }

    @Test
    public void shouldClearAllFieldsIfDifferentOnesSelected() {
        Submission submission = new SubmissionTestBuilder()
                .with("householdHomeExpenses[]", List.of(
                        HomeExpensesType.RENT.name(),
                        HomeExpensesType.PHONE.name(),
                        HomeExpensesType.OTHER.name()))
                .with(HomeExpensesType.RENT.getInputFieldName(), "2000")
                .with(HomeExpensesType.PHONE.getInputFieldName(), "1000")
                .with(HomeExpensesType.OTHER.getInputFieldName(), "500")
                // pretend these below were there before, they should get cleared out, since they
                // are not in the list
                .with(HomeExpensesType.WATER.getInputFieldName(), "222")
                .with(HomeExpensesType.PROPERTY_TAX.getInputFieldName(), "111")
                .with(HomeExpensesType.OIL.getInputFieldName(), "1000")
                .build();
        submission.setFlow("mdBenefitsFlow");

        clearUnusedExpensesFields.run(submission);

        assertThat(submission.getInputData().get(HomeExpensesType.RENT.getInputFieldName())).isEqualTo("2000");
        assertThat(submission.getInputData().get(HomeExpensesType.PHONE.getInputFieldName())).isEqualTo(
                "1000");
        assertThat(submission.getInputData().get(HomeExpensesType.OTHER.getInputFieldName())).isEqualTo("500");

        // these should have been cleared out.
        assertThat(submission.getInputData().get(HomeExpensesType.WATER.getInputFieldName())).isNull();
        assertThat(submission.getInputData().get(HomeExpensesType.PROPERTY_TAX.getInputFieldName())).isNull();
        assertThat(submission.getInputData().get(HomeExpensesType.OIL.getInputFieldName())).isNull();
    }
}
