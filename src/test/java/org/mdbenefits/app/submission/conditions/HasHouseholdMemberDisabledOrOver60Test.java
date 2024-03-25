package org.mdbenefits.app.submission.conditions;

import formflow.library.data.Submission;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class HasHouseholdMemberDisabledOrOver60Test {

    HasHouseholdMemberDisabledOrOver60 condition = new HasHouseholdMemberDisabledOrOver60();

    @Test
    void applicantIsDisabledButUnder60() {
        Submission submission = new SubmissionTestBuilder()
                .withPersonalInfo("John", "Doe", "10", "12", "1999",
                        "", "", "", "", "")
                .withHouseholdMemberApplying("Betty", "White", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                        "123456789", "No", "No", null, null)
                .withHouseholdMemberApplying("Bea", "Arthur", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                        "123456789", "No", "No", null, null)
                .with("applicantHasDisability", "Yes")
                .build();

        assertThat(condition.run(submission)).isTrue();
    }

    @Test
    void householdMemberHasDisability() {
        Submission submission = new SubmissionTestBuilder()
                .withPersonalInfo("John", "Doe", "10", "12", "1999",
                        "", "", "", "", "")
                .withHouseholdMemberApplying("Betty", "White", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                        "123456789", "No", "No", null, null)
                .withHouseholdMemberApplying("Bea", "Arthur", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                        "123456789", "No", "Yes", null, null)
                .with("applicantIsDisabled", "No")
                .build();

        assertThat(condition.run(submission)).isTrue();
    }

    @Test
    void applicantIsOldEnoughAndNotDisabled() {
        Submission submission = new SubmissionTestBuilder()
                .withPersonalInfo("John", "Doe", "25", "3", "1964",
                        "", "", "", "", "")
                .withHouseholdMemberApplying("Betty", "White", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                        "123456789", "No", "No", null, null)
                .withHouseholdMemberApplying("Bea", "Arthur", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                        "123456789", "No", "No", null, null)
                .with("applicantIsDisabled", "No")
                .build();

        assertThat(condition.run(submission)).isTrue();
    }

    @Test
    void householdMemberIsOldEnoughAndNotDisabled() {
        Submission submission = new SubmissionTestBuilder()
                .withPersonalInfo("John", "Doe", "25", "3", "1978",
                        "", "", "", "", "")
                .withHouseholdMemberApplying("Betty", "White", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                        "123456789", "No", "No", null, null)
                .withHouseholdMemberApplying("Bea", "Arthur", "28", "2", "1935", "Child", "F", "NeverMarried", "firstGrade",
                        "123456789", "No", "No", null, null)
                .with("applicantIsDisabled", "No")
                .build();

        assertThat(condition.run(submission)).isTrue();
    }

    @Test
    void noOneQualifies() {
        Submission submission = new SubmissionTestBuilder()
                .withPersonalInfo("John", "Doe", "10", "12", "1999",
                        "", "", "", "", "")
                .withHouseholdMemberApplying("Betty", "White", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                        "123456789", "No", "No", null, null)
                .withHouseholdMemberApplying("Bea", "Arthur", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                        "123456789", "No", "No", null, null)
                .with("applicantIsDisabled", "No")
                .build();

        assertThat(condition.run(submission)).isFalse();
    }
}
