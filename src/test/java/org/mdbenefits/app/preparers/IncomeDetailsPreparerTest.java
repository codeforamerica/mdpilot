package org.mdbenefits.app.preparers;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class IncomeDetailsPreparerTest {


    @Autowired
    private MessageSource messageSource;

    private IncomeDetailsPreparer incomePreparer;

    @BeforeEach
    void setup() {
        incomePreparer = new IncomeDetailsPreparer(messageSource);
    }

    @Test
    public void testNoJobsNoAdditionalIncome() {
        var results = incomePreparer.prepareSubmissionFields(new Submission(), null);
        assertThat(results).isEmpty();
    }

    @Test
    public void testMultipleJobs() {
        Submission submission = new SubmissionTestBuilder()
                .withJob("Joka Aksj", "CfA", "10", "11", "true")
                .withJob("Bsod Aksj", "CgA", "90", "10", "true")
                .withJob("Olas Aksj", "ChA", "Every 2 weeks", "50", "false")
                .withJob("Ydopa Aksj", "CiA", "It varies", "1000", "false")
                .build();

        var results = incomePreparer.prepareSubmissionFields(submission, null);
        assertThat(results.size()).isEqualTo(20);
    }

    @Test
    public void testAdditionalIncomeOfNone() {
        Submission submission = new SubmissionTestBuilder()
                .with("additionalIncome[]", List.of("NONE"))
                .build();

        var results = incomePreparer.prepareSubmissionFields(submission, null);
        assertThat(results).isEmpty();
    }

    @Test
    public void testAdditionalIncome() {
        Submission submission = new SubmissionTestBuilder()
                .with("additionalIncome[]",
                        List.of(
                                "ALIMONY",
                                "CHILD_SUPPORT",
                                "UNEMPLOYMENT",
                                "PENSION_RETIREMENT",
                                "SOCIAL_SECURITY",
                                "WORKERS_COMPENSATION",
                                "VETERANS_BENEFITS"))
                .with("additionalIncomeAlimony", "500")
                .with("additionalIncomeChildSupport", "200")
                .with("additionalIncomeUnemployment", "200")
                .with("additionalIncomePensionRetirement", "200")
                .with("additionalIncomeSS", "200")
                .with("additionalIncomeWorkersComp", "200")
                .with("additionalIncomeVeteransBenefits", "200")
                .build();
        Map<String, SubmissionField> fields = incomePreparer.prepareSubmissionFields(submission, null);
        assertThat(fields.size()).isEqualTo(21);

        // spot check
        SingleField alimonySingleField = (SingleField) fields.get("additionalIncomeTypeOfBenefitRow1");
        assertThat(alimonySingleField.getValue()).isEqualTo("Alimony");

        SingleField alimonySingleFieldAmount = (SingleField) fields.get("additionalIncomeAmountRow1");
        assertThat(alimonySingleFieldAmount.getValue()).isEqualTo("$500");

        SingleField vbSingleField = (SingleField) fields.get("additionalIncomeTypeOfBenefitRow7");
        assertThat(vbSingleField.getValue()).isEqualTo("Veteran's Benefits");

        SingleField vbSingleFieldAmount = (SingleField) fields.get("additionalIncomeAmountRow7");
        assertThat(vbSingleFieldAmount.getValue()).isEqualTo("$200");

        SingleField yesRow = (SingleField) fields.get("additionalIncomeReceivedRow5");
        assertThat(yesRow.getValue()).isEqualToIgnoringCase("Yes");
    }


    @Test
    public void testPrepareMoneyOnHandResourcesDoesNotAddWhenNONEisPresent() {
        Submission submission = new SubmissionTestBuilder()
            .withPersonalInfo("Person", "One", "", "", "", "", "", "", "", "")
            .with("moneyOnHandTypes[]", List.of("NONE"))
            .build();

        Map<String, SubmissionField> result = incomePreparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("householdHasResourcesOrAssets"))
            .isEqualTo(new SingleField("householdHasResourcesOrAssets", "false", null));
    }

    @Test
    public void testPrepareMoneyOnHandResourcesAddsRelevantItems() {
        Submission submission = new SubmissionTestBuilder()
            .withPersonalInfo("Person", "One", "", "", "", "", "", "", "", "")
            .with("moneyOnHandTypes[]", List.of("CHECKING", "SAVINGS", "BONDS"))
            .build();

        Map<String, SubmissionField> result = incomePreparer.prepareSubmissionFields(submission, null);

        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get("householdHasResourcesOrAssets"))
            .isEqualTo(new SingleField("householdHasResourcesOrAssets", "true", null));
        assertThat(result.get("resourcesOrAssetsType1"))
            .isEqualTo(new SingleField("resourcesOrAssetsType1", "Checking account", null));

        assertThat(result.get("resourcesOrAssetsType2"))
            .isEqualTo(new SingleField("resourcesOrAssetsType2", "Savings account", null));

        assertThat(result.get("resourcesOrAssetsType3"))
            .isEqualTo(new SingleField("resourcesOrAssetsType3", "Bonds", null));
    }
}
