package org.mdbenefits.app.submission.actions;

import formflow.library.data.Submission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.mdbenefits.app.data.enums.AdditionalIncomeType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mdbenefits.app.utils.SubmissionUtilities.NONE_OF_ABOVE_SELECTION_VALUE;

@ActiveProfiles("test")
@SpringBootTest
class ClearUnusedAdditionalIncomeFieldsTest {

    private ClearUnusedAdditionalIncomeFields clearUnusedAdditionalIncomeFields;

    @BeforeEach
    void setup() {
        clearUnusedAdditionalIncomeFields = new ClearUnusedAdditionalIncomeFields();
    }

    @Test
    public void shouldClearAllFieldsIfNoneSelected() {
        Submission submission = new SubmissionTestBuilder()
                .with("additionalIncome[]", List.of(NONE_OF_ABOVE_SELECTION_VALUE))
                .with(AdditionalIncomeType.ALIMONY.getInputFieldName(), "2000")
                .with(AdditionalIncomeType.PENSION_RETIREMENT.getInputFieldName(), "222")
                .with(AdditionalIncomeType.VETERANS_BENEFITS.getInputFieldName(), "1000")
                .build();
        submission.setFlow("mdBenefitsFlow");

        clearUnusedAdditionalIncomeFields.run(submission);

        assertThat(submission.getInputData().get(AdditionalIncomeType.ALIMONY.getInputFieldName())).isNull();
        assertThat(submission.getInputData().get(AdditionalIncomeType.PENSION_RETIREMENT.getInputFieldName())).isNull();
        assertThat(submission.getInputData().get(AdditionalIncomeType.VETERANS_BENEFITS.getInputFieldName())).isNull();
    }

    @Test
    public void shouldClearAllFieldsIfDifferentOnesSelected() {
        Submission submission = new SubmissionTestBuilder()
                .with("additionalIncome[]", List.of(
                        AdditionalIncomeType.ALIMONY.name(),
                        AdditionalIncomeType.FRIENDS_FAMILY_CONTRIBUTION.name(),
                        AdditionalIncomeType.OTHER.name()))
                .with(AdditionalIncomeType.ALIMONY.getInputFieldName(), "2000")
                .with(AdditionalIncomeType.FRIENDS_FAMILY_CONTRIBUTION.getInputFieldName(), "1000")
                .with(AdditionalIncomeType.OTHER.getInputFieldName(), "500")
                // pretend these below were there before, they should get cleared out, since they
                // are not in the list
                .with(AdditionalIncomeType.PENSION_RETIREMENT.getInputFieldName(), "222")
                .with(AdditionalIncomeType.WORKERS_COMPENSATION.getInputFieldName(), "111")
                .with(AdditionalIncomeType.VETERANS_BENEFITS.getInputFieldName(), "1000")
                .build();
        submission.setFlow("mdBenefitsFlow");

        clearUnusedAdditionalIncomeFields.run(submission);

        assertThat(submission.getInputData().get(AdditionalIncomeType.ALIMONY.getInputFieldName())).isEqualTo("2000");
        assertThat(submission.getInputData().get(AdditionalIncomeType.FRIENDS_FAMILY_CONTRIBUTION.getInputFieldName())).isEqualTo(
                "1000");
        assertThat(submission.getInputData().get(AdditionalIncomeType.OTHER.getInputFieldName())).isEqualTo("500");

        // these should have been cleared out.
        assertThat(submission.getInputData().get(AdditionalIncomeType.PENSION_RETIREMENT.getInputFieldName())).isNull();
        assertThat(submission.getInputData().get(AdditionalIncomeType.WORKERS_COMPENSATION.getInputFieldName())).isNull();
        assertThat(submission.getInputData().get(AdditionalIncomeType.VETERANS_BENEFITS.getInputFieldName())).isNull();
    }
}
