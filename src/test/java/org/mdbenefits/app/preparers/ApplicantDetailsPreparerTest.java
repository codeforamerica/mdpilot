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
    public void testDefaultNoIndicators() {
        Submission submission = new SubmissionTestBuilder()
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("applicantStudentInd"))
                .isEqualTo(new SingleField("applicantStudentInd", "No", null));
        assertThat(result.get("applicantNonCitizenInd"))
                .isEqualTo(new SingleField("applicantNonCitizenInd", "No", null));
        assertThat(result.get("applicantHomelessInd"))
                .isEqualTo(new SingleField("applicantHomelessInd", "No", null));
    }

    @Test
    public void testYesIndicators() {
        Submission submission = new SubmissionTestBuilder()
                .withStudents(List.of("you"))
                .withNonCitizens(List.of("you"))
                .withHomelessness(List.of("you"))
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("applicantStudentInd"))
                .isEqualTo(new SingleField("applicantStudentInd", "Yes", null));
        assertThat(result.get("applicantNonCitizenInd"))
                .isEqualTo(new SingleField("applicantNonCitizenInd", "Yes", null));
        assertThat(result.get("applicantHomelessInd"))
                .isEqualTo(new SingleField("applicantHomelessInd", "Yes", null));
    }

    @Test
    public void testBirthdayFormat() {
        Submission submission = new SubmissionTestBuilder()
                .withPersonalInfo("", "", "10", "12", "1999",
                        "", "", "", "", "")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("applicantBirthday"))
                .isEqualTo(new SingleField("applicantBirthdayFormatted", "12/10/1999", null));
    }

    @Test
    public void testEducation() {
        Submission submission = new SubmissionTestBuilder()
                .withPersonalInfo("", "", "", "", "",
                        "", "", "", "certificateOrDiploma", "")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.get("highestEducation"))
                .isEqualTo(new SingleField("highestEducationFormatted", "Other certificate or diploma", null));
    }

}
