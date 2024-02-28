package org.mdbenefits.app.preparers;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;

class SpecialSituationsPreparerTest {

    public static final SpecialSituationsPreparer preparer = new SpecialSituationsPreparer();

    @Test
    public void testNoMappings() {
        var results = preparer.prepareSubmissionFields(new Submission(), null);
        assertThat(results).isEmpty();
    }

    @Test
    public void testMultipleMembersNameMappings() {
        Submission submission = new SubmissionTestBuilder()
                .withPersonalInfo("Btter", "Bll", "", "", "", "", "", "", "", "")
                .withHouseholdMember("Butter", "Bull", "", "", "", "", "", "", "", "", null, null)
                .withHouseholdMember("Batter", "Ball", "", "", "", "", "", "", "", "", null, null)
                .withHouseholdMember("Bitter", "Bill", "", "", "", "", "", "", "", "", null, null)
                .withHouseholdMember("Better", "Bell", "", "", "", "", "", "", "", "", null, null)
                .with("preparesFood[]", List.of("butter-bull", "you"))
                .build();

        var results = preparer.prepareSubmissionFields(submission, null);

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get("preparesFoodNames")).isEqualTo(
                new SingleField("preparesFoodNames", "Butter Bull,Btter Bll", null));
    }

}
