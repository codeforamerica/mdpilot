package org.mdbenefits.app.preparers;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mdbenefits.app.utils.SubmissionUtilities.NONE_OF_ABOVE_SELECTION_VALUE;

@ActiveProfiles("test")
@SpringBootTest
class IncomeDetailsPreparerTest {


    @Autowired
    private MessageSource messageSource;

    private IncomeDetailsPreparer incomePreparer;

    final int FIELDS_PER_JOB = 4;

    @BeforeEach
    void setup() {
        incomePreparer = new IncomeDetailsPreparer(messageSource);
    }

    @Test
    public void testNoJobsNoAdditionalIncome() {
        var results = incomePreparer.prepareSubmissionFields(new Submission(), null);

        assertThat(withNoJobIncome(results)).isEmpty();
    }

    @Test
    public void testMultipleJobs() {
        Submission submission = new SubmissionTestBuilder()
                .withJob("Joka Aksj", "CfA", "Every week", "11")
                .withJob("Bsod Aksj", "CgA", "Twice a month", "10")
                .withJob("Olas Aksj", "ChA", "Every 2 weeks", "50")
                .withJob("Ydopa Aksj", "CiA", "It varies", "1000")
                .build();

        var results = incomePreparer.prepareSubmissionFields(submission, null);

        assertThat(withJobIncome(results).size()).isEqualTo(FIELDS_PER_JOB * 4);
    }

    @Test
    public void testAdditionalIncomeOfNone() {
        Submission submission = new SubmissionTestBuilder()
                .with("additionalIncome[]", List.of(NONE_OF_ABOVE_SELECTION_VALUE))
                .build();

        var results = incomePreparer.prepareSubmissionFields(submission, null);
        assertThat(withNoJobIncome(results)).isEmpty();
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
        assertThat(withNoJobIncome(fields).size()).isEqualTo(21);

        // spot check
        SingleField alimonySingleField = (SingleField) fields.get("additionalIncomeTypeOfBenefitRow1");
        assertThat(alimonySingleField.getValue()).isEqualTo("Alimony");

        SingleField alimonySingleFieldAmount = (SingleField) fields.get("additionalIncomeAmountRow1");
        assertThat(alimonySingleFieldAmount.getValue()).isEqualTo("500");

        SingleField vbSingleField = (SingleField) fields.get("additionalIncomeTypeOfBenefitRow7");
        assertThat(vbSingleField.getValue()).isEqualTo("Veteran's Benefits");

        SingleField vbSingleFieldAmount = (SingleField) fields.get("additionalIncomeAmountRow7");
        assertThat(vbSingleFieldAmount.getValue()).isEqualTo("200");

        SingleField yesRow = (SingleField) fields.get("additionalIncomeReceivedRow5");
        assertThat(yesRow.getValue()).isEqualToIgnoringCase("Yes");
    }

    private void assertJobIncome(Map<String, SubmissionField> results, String expected) {
        assertThat(results).containsKey("householdHasEarnedIncome");
        var hasIncomeField = (SingleField) results.get("householdHasEarnedIncome");
        assertThat(hasIncomeField.getValue()).isEqualTo(expected);
    }

    private Map<String, SubmissionField> withNoJobIncome(Map<String, SubmissionField> results) {
        assertJobIncome(results, "false");
        results.remove("householdHasEarnedIncome");
        return results;
    }

    private Map<String, SubmissionField> withJobIncome(Map<String, SubmissionField> results) {
        assertJobIncome(results, "true");
        results.remove("householdHasEarnedIncome");
        return results;
    }
}
