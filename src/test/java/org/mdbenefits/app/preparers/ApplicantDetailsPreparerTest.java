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
                .with("isApplicantPregnant", "Yes")
                .with("applicantHasDisability", "Yes")
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

    @Test
    public void whenMailingAddressSameAsHomeAddressThenEmptyMailingAddress() {
        Map<String, Object> inputData = Map.of("sameAsHomeAddress", "true");
        assertThat(preparer.formatMailingAddress(inputData)).isEqualTo("");
    }

    @Test
    public void whenMailingAddressStreetAddress2PresentThenIncludesIt() {
        Map<String, Object> inputData = Map.of(
                "mailingAddressStreetAddress1", "10 Main St",
                "mailingAddressStreetAddress2", "Unit #A",
                "mailingAddressCity", "Baltimore",
                "mailingAddressState", "MD",
                "mailingAddressZipCode", "21201"
        );
        assertThat(preparer.formatMailingAddress(inputData)).isEqualTo("10 Main St, Unit #A, Baltimore, MD 21201");
    }

    @Test
    public void whenMailingAddressStreetAddress2AbsentThenOmitsIt() {
        Map<String, Object> inputData = Map.of(
                "mailingAddressStreetAddress1", "10 Main St",
                "mailingAddressCity", "Baltimore",
                "mailingAddressState", "MD",
                "mailingAddressZipCode", "21201"
        );
        assertThat(preparer.formatMailingAddress(inputData)).isEqualTo("10 Main St, Baltimore, MD 21201");
    }

    @Test
    public void testWithMiddleName() {
        submission.getInputData().put("middleName", "MiddleName");
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("applicantFullName"))
                .isEqualTo(new SingleField("applicantFullName", "Doe, John, MiddleName", null));
    }

    @Test
    public void testWithMinimumApp() {
        Submission minAppSubmission = new SubmissionTestBuilder()
                .with("firstName", "John")
                .with("lastName", "Doe")
                .with("birthDay", "10")
                .with("birthMonth", "12")
                .with("birthYear", "1999")
                .with("useSuggestedAddress", "true")
                .with("mailingAddressStreetAddress1_validated", "123 Main Street")
                .with("mailingAddressCity_validated", "Some City")
                .with("mailingAddressState_validated", "NY")
                .with("mailingAddressZipCode_validated", "14626")
                .with("isMinimumApplication", "True")
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(minAppSubmission, null);
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get("isApplicantApplying"))
                .isEqualTo(new SingleField("isApplicantApplying", "Yes", null));
        assertThat(result.get("applicantDOB")).isNull();
        assertThat(result.get("applicantCitizenshipStatus")).isNull();
    }
}
