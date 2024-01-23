package org.mdbenefits.app.preparers;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;

public class ApplicantDetailsPreparerTest {

    private final ApplicantDetailsPreparer preparer = new ApplicantDetailsPreparer();

    @Test
    public void testApplicantFullName() {
        Submission submission = new SubmissionTestBuilder()
                .withPersonalInfo("John", "Doe", "10", "12", "1999",
                        "", "", "", "", "")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("applicantFullName"))
                .isEqualTo(new SingleField("applicantFullName", "Doe, John", null));
    }


}
