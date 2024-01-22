package org.mdbenefits.app.preparers;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;

class MaritalStatusPreparerTest {

    private final MaritalStatusPreparer preparer = new MaritalStatusPreparer();

    @Test
    public void testMaritalStatusMarriedLivingWithSpouse() {
        Submission submission = new SubmissionTestBuilder()
                .withPersonalInfo("", "", "", "", "",
                        "", "", "MarriedLivingWithSpouse", "", "")
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("maritalStatus")).isEqualTo(new SingleField("maritalStatus", "Married", null));
    }

    @Test
    public void testMaritalStatusMarriedNotLivingWithSpouse() {
        Submission submission = new SubmissionTestBuilder()
                .withPersonalInfo("", "", "", "", "",
                        "", "", "MarriedNotLivingWithSpouse", "", "")
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("maritalStatus")).isEqualTo(new SingleField("maritalStatus", "Married", null));
    }
}
