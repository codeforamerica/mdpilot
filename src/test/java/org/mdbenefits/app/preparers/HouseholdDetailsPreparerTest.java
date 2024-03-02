package org.mdbenefits.app.preparers;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.mdbenefits.app.data.enums.EthnicityType;
import org.mdbenefits.app.data.enums.RaceType;

class HouseholdDetailsPreparerTest {

    private final HouseholdDetailsPreparer preparer = new HouseholdDetailsPreparer();

    @Test
    public void testWithHouseholdMember() {
        Submission submission = new SubmissionTestBuilder()
            .withHouseholdMemberApplying("Betty", "White", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                "123456789", "Yes", "Yes", List.of(RaceType.WHITE.name()), "No")
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
    
    @CsvSource(value = {
            "AMERICAN_INDIAN,HISPANIC_OR_LATINO,1,1",
            "ASIAN,NOT_HISPANIC_OR_LATINO,2,2",
            "BLACK_OR_AFRICAN_AMERICAN,PREFER_NO_ANSWER,3,''",
            "NATIVE_HAWAIIAN_OR_PACIFIC_ISLANDER,HISPANIC_OR_LATINO,4,1",
            "WHITE,NOT_HISPANIC_OR_LATINO,5,2"
    })
    @ParameterizedTest
    public void shouldMapRaceAndEthnicityCodes(String race, String ethnicity, String racePdfValue, String ethnicityPdfValue) {
        Submission submission = new SubmissionTestBuilder()
            .withHouseholdMemberApplying("Betty", "White", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                "123456789", List.of(race), ethnicity)
            .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("householdMemberRace_1"))
            .isEqualTo(new SingleField("householdMemberRace", racePdfValue, 1));
        assertThat(result.get("householdMemberEthnicity_1"))
            .isEqualTo(new SingleField("householdMemberEthnicity", ethnicityPdfValue, 1));
    }
    
    @Test
    public void shouldMapMultipleRaceValues() {
        Submission submission = new SubmissionTestBuilder()
                .withHouseholdMemberApplying("Betty", "White", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                        "123456789", List.of(RaceType.ASIAN.name(), RaceType.BLACK_OR_AFRICAN_AMERICAN.name()), EthnicityType.NOT_HISPANIC_OR_LATINO.name())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("householdMemberRace_1"))
                .isEqualTo(new SingleField("householdMemberRace", "2,3", 1));
    }
    
    @Test
    public void shouldNotMapPreferNotToAnswer() {
        Submission submission = new SubmissionTestBuilder()
                .withHouseholdMemberApplying("Betty", "White", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                        "123456789", List.of(RaceType.PREFER_NO_ANSWER.name()), EthnicityType.PREFER_NO_ANSWER.name())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("householdMemberRace_1"))
                .isEqualTo(new SingleField("householdMemberRace", "", 1));
        assertThat(result.get("householdMemberEthnicity_1"))
                .isEqualTo(new SingleField("householdMemberEthnicity", "", 1));
    }
}
