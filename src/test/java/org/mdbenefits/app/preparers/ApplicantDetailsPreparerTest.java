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
import org.mdbenefits.app.data.enums.EthnicityType;
import org.mdbenefits.app.data.enums.RaceType;

public class ApplicantDetailsPreparerTest {

    private final ApplicantDetailsPreparer preparer = new ApplicantDetailsPreparer();

    private Submission submission;

    @BeforeEach
    void setup() {
        submission = new SubmissionTestBuilder()
                .withPersonalInfo("John", "Doe", "10", "12", "1999",
                        "", "", "", "", "")
                .with("isApplicantApplying", "Yes")
                .with("isApplicantPregnant", "No")
                .with("applicantRace[]", List.of(RaceType.ASIAN.name(), RaceType.AMERICAN_INDIAN.name()))
                .with("applicantEthnicity", EthnicityType.HISPANIC_OR_LATINO.name())
                .build();
    }

    @Test
    public void testFields() {
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("applicantFullName"))
                .isEqualTo(new SingleField("applicantFullName", "Doe, John", null));
        assertThat(result.get("applicantDOB"))
                .isEqualTo(new SingleField("applicantDOB", "12/10/1999", null));
        assertThat(result.get("applicantRace"))
                .isEqualTo(new SingleField("applicantRace", "1,2", null));
        assertThat(result.get("applicantEthnicity"))
                .isEqualTo(new SingleField("applicantEthnicity", "1", null));
    }

}
