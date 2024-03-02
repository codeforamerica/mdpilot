package org.mdbenefits.app.preparers;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;

class HouseholdDetailsPreparerTest {

    private final HouseholdDetailsPreparer preparer = new HouseholdDetailsPreparer();

    @Test
    public void testWithHouseholdMember() {
        Submission submission = new SubmissionTestBuilder()
            .withHouseholdMemberApplying("Betty", "White", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                "123456789", "Yes", "Yes", null, null)
            .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("householdMemberFullName_1")).isEqualTo(
            new SingleField("householdMemberFullName", "White, Betty", 1));
        assertThat(result.get("householdMemberRelationship_1"))
            .isEqualTo(new SingleField("householdMemberRelationship", "Child", 1));
        assertThat(result.get("householdMemberCitizen_1"))
            .isEqualTo(new SingleField("householdMemberCitizen", "Yes", 1));
        assertThat(result.get("householdMemberDOB_1"))
            .isEqualTo(new SingleField("householdMemberDOB", "2/10/1999", 1));
        assertThat(result.get("householdMemberSex_1"))
            .isEqualTo(new SingleField("householdMemberSex", "F", 1));
    }

    @Test
    public void shouldRemoveTrailingCommaAndSpaceFromFullName() {
        assertThat(preparer.removeTrailingCommaAndSpace("White, Betty, ")).isEqualTo("White, Betty");

        assertThat(preparer.removeTrailingCommaAndSpace("White, Betty, Middle")).isEqualTo("White, Betty, Middle");
    }
}
