package org.mdbenefits.app.preparers;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;

class PregnancyDueDatePreparerTest {

    public static final PregnancyDueDatePreparer preparer = new PregnancyDueDatePreparer();

    @Test
    public void testNoPregnancyDetailsMappings() {
        var results = preparer.prepareSubmissionFields(new Submission(), null);
        assertThat(results).isEmpty();
    }

    @Test
    public void testMultipleHouseholdPregnancies() {
        Submission submission = new SubmissionTestBuilder()
                .withHouseholdMemberApplying("Butter", "Ball", "", "", "", "", "", "", "", "", "", "", List.of(), "")
                .withHouseholdMemberApplying("Batter", "Ball", "", "", "", "", "", "", "", "", "", "", List.of(), "")
                .withPregnancies(List.of("butter-ball", "batter-ball"), List.of("12/12/1212", "2/2/2222"))
                .build();

        var results = preparer.prepareSubmissionFields(submission, null);

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get("pregnantMemberNames")).isEqualTo(
                new SingleField("pregnantMemberNames", "Butter Ball,Batter Ball", null));
        assertThat(results.get("pregnantMemberDueDates")).isEqualTo(
                new SingleField("pregnantMemberDueDates", "12/12/1212,2/2/2222", null));
    }
}
