package org.mdbenefits.app.preparers;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;

class HouseholdPregnancyAndDisabilityPreparerTest {

    private final HouseholdPregnancyAndDisabilityPreparer preparer = new HouseholdPregnancyAndDisabilityPreparer();

    @Test
    public void testWithHouseholdMember() {
        Submission submission = new SubmissionTestBuilder()
            .withHouseholdMemberApplying("Betty", "White", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                "123456789", null, null)
            .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("householdMemberIsPregnant")).isEqualTo(
            new SingleField("householdMemberIsPregnant", "Yes", null));
        assertThat(result.get("householdMemberIsPregnantSeeCover")).isEqualTo(
            new SingleField("householdMemberIsPregnantSeeCover", "See cover page", null));
        assertThat(result.get("householdMemberHasDisability")).isEqualTo(
            new SingleField("householdMemberHasDisability", "Yes", null));
        assertThat(result.get("householdMemberHasDisabilitySeeCover")).isEqualTo(
            new SingleField("householdMemberHasDisabilitySeeCover", "See cover page", null));
    }
}
