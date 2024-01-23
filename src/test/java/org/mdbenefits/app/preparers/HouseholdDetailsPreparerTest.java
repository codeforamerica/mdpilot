package org.mdbenefits.app.preparers;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;

class HouseholdDetailsPreparerTest {

    private final HouseholdDetailsPreparer preparer = new HouseholdDetailsPreparer();

    @Test
    public void testWithHouseholdMember() {
        Submission submission = new SubmissionTestBuilder()
                .withHouseholdMember("Betty", "White", "10", "2", "1999", "halfSibling", "F", "NeverMarried", "firstGrade",
                        "123456789", null, null)
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("householdMemberFullName_0"))
                .isEqualTo(new SingleField("householdMemberFullName", "White, Betty", 1));
        assertThat(result.get("householdMemberDOB_0"))
                .isEqualTo(new SingleField("householdMemberDOB", "2/10/1999", 1));
    }

    @Test
    @Disabled
    public void testWithNonCitizenHouseholdMember() {
        Submission submission = new SubmissionTestBuilder()
                .withHouseholdMember("Betty", "White", "10", "2", "1999", "child", "F", "NeverMarried", "firstGrade", "123456789",
                        null, null)
                .withNonCitizens(List.of("betty-white"))
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("householdRelationship0"))
                .isEqualTo(new SingleField("householdRelationshipFormatted", "child", 1));
        assertThat(result.get("householdMaritalStatus0"))
                .isEqualTo(new SingleField("householdMaritalStatusFormatted", "Never Married", 1));
        assertThat(result.get("householdHighestEducation0"))
                .isEqualTo(new SingleField("householdHighestEducationFormatted", "1st grade", 1));
        assertThat(result.get("householdBirthday0"))
                .isEqualTo(new SingleField("householdBirthdayFormatted", "2/10/1999", 1));
        assertThat(result.get("householdUSCitizen0"))
                .isEqualTo(new SingleField("householdUSCitizenDerived", "No", 1));
    }
}
