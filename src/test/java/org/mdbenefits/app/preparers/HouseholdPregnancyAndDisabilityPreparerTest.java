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
    public void pregnancyListCutsOffAtFive(){
        Submission submission = new SubmissionTestBuilder()
            .withApplicantWithPregnancy("Alice", "Test")
            .withHouseholdMemberWithPregnancy("Benny", "Test")
            .withHouseholdMemberWithPregnancy("Carla", "Test")
            .withHouseholdMemberWithPregnancy("Damian", "Test")
            .withHouseholdMemberWithPregnancy("Eli", "Test")
            .withHouseholdMemberWithPregnancy("Frankie", "Test")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("householdHasPregnancy")).isEqualTo(new SingleField("householdHasPregnancy", "Yes", null));
        assertThat(result.get("householdHasPregnancySeeCover")).isEqualTo(new SingleField("householdHasPregnancySeeCover", "See cover page", null));
        assertThat(result.get("householdHasDisability")).isEqualTo(new SingleField("householdHasDisability", "No", null));
        assertThat(result.get("householdHasDisabilitySeeCover")).isNull();

        assertThat(result.get("householdPregnancyName1")).isEqualTo(
            new SingleField("householdPregnancyName1", "Alice Test", null));
        assertThat(result.get("householdPregnancyName2")).isEqualTo(
            new SingleField("householdPregnancyName2", "Benny Test", null));
        assertThat(result.get("householdPregnancyName3")).isEqualTo(
            new SingleField("householdPregnancyName3", "Carla Test", null));
        assertThat(result.get("householdPregnancyName4")).isEqualTo(
            new SingleField("householdPregnancyName4", "Damian Test", null));
        assertThat(result.get("householdPregnancyName5")).isEqualTo(
            new SingleField("householdPregnancyName5", "Eli Test", null));
        assertThat(result.get("householdPregnancyName6")).isNull();
    }

    @Test
    public void disabilityListCutsOffAtFive(){
        Submission submission = new SubmissionTestBuilder()
            .withApplicantWithDisability("Alice", "Test")
            .withHouseholdMemberWithDisability("Benny", "Test")
            .withHouseholdMemberWithDisability("Carla", "Test")
            .withHouseholdMemberWithDisability("Damian", "Test")
            .withHouseholdMemberWithDisability("Eli", "Test")
            .withHouseholdMemberWithDisability("Frankie", "Test")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("householdHasDisability")).isEqualTo(new SingleField("householdHasDisability", "Yes", null));
        assertThat(result.get("householdHasDisabilitySeeCover")).isEqualTo(new SingleField("householdHasDisabilitySeeCover", "See cover page", null));
        assertThat(result.get("householdHasPregnancy")).isEqualTo(new SingleField("householdHasPregnancy", "No", null));
        assertThat(result.get("householdHasPregnancySeeCover")).isNull();

        assertThat(result.get("householdDisabilityName1")).isEqualTo(
            new SingleField("householdDisabilityName1", "Alice Test", null));
        assertThat(result.get("householdDisabilityName2")).isEqualTo(
            new SingleField("householdDisabilityName2", "Benny Test", null));
        assertThat(result.get("householdDisabilityName3")).isEqualTo(
            new SingleField("householdDisabilityName3", "Carla Test", null));
        assertThat(result.get("householdDisabilityName4")).isEqualTo(
            new SingleField("householdDisabilityName4", "Damian Test", null));
        assertThat(result.get("householdDisabilityName5")).isEqualTo(
            new SingleField("householdDisabilityName5", "Eli Test", null));
        assertThat(result.get("householdDisabilityName6")).isNull();
    }

    @Test
    public void properlyMapsDataWhenOnlyApplicantIsPregnancy(){
        Submission submission = new SubmissionTestBuilder()
            .withApplicantWithPregnancy("Alice", "Test")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get("householdHasPregnancy")).isEqualTo(new SingleField("householdHasPregnancy", "Yes", null));
        assertThat(result.get("householdHasPregnancySeeCover")).isEqualTo(new SingleField("householdHasPregnancySeeCover", "See cover page", null));
        assertThat(result.get("householdHasDisability")).isEqualTo(new SingleField("householdHasDisability", "No", null));
        assertThat(result.get("householdHasDisabilitySeeCover")).isNull();

        assertThat(result.get("householdPregnancyName1")).isEqualTo(
            new SingleField("householdPregnancyName1", "Alice Test", null));
        assertThat(result.get("householdPregnancyName2")).isNull();
    }

    @Test
    public void properlyMapsDataWhenOnlyApplicantHasDisability(){
        Submission submission = new SubmissionTestBuilder()
            .withApplicantWithDisability("Alice", "Test")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get("householdHasDisability")).isEqualTo(new SingleField("householdHasDisability", "Yes", null));
        assertThat(result.get("householdHasDisabilitySeeCover")).isEqualTo(new SingleField("householdHasDisabilitySeeCover", "See cover page", null));
        assertThat(result.get("householdHasPregnancy")).isEqualTo(new SingleField("householdHasPregnancy", "No", null));
        assertThat(result.get("householdHasPregnancySeeCover")).isNull();

        assertThat(result.get("householdDisabilityName1")).isEqualTo(
            new SingleField("householdDisabilityName1", "Alice Test", null));
        assertThat(result.get("householdDisabilityName2")).isNull();
    }

    @Test
    public void properlyMapsWhenApplicantIsNotPregnant(){
        Submission submission = new SubmissionTestBuilder()
            .withHouseholdMemberWithPregnancy("Benny", "Test")
            .withHouseholdMemberWithPregnancy("Carla", "Test")
            .withHouseholdMemberWithPregnancy("Damian", "Test")
            .withHouseholdMemberWithPregnancy("Eli", "Test")
            .withHouseholdMemberWithPregnancy("Frankie", "Test")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.size()).isEqualTo(8);
        assertThat(result.get("householdHasPregnancy")).isEqualTo(new SingleField("householdHasPregnancy", "Yes", null));
        assertThat(result.get("householdHasPregnancySeeCover")).isEqualTo(new SingleField("householdHasPregnancySeeCover", "See cover page", null));
        assertThat(result.get("householdHasDisability")).isEqualTo(new SingleField("householdHasDisability", "No", null));
        assertThat(result.get("householdHasDisabilitySeeCover")).isNull();


        assertThat(result.get("householdPregnancyName1")).isEqualTo(
            new SingleField("householdPregnancyName1", "Benny Test", null));
        assertThat(result.get("householdPregnancyName2")).isEqualTo(
            new SingleField("householdPregnancyName2", "Carla Test", null));
        assertThat(result.get("householdPregnancyName3")).isEqualTo(
            new SingleField("householdPregnancyName3", "Damian Test", null));
        assertThat(result.get("householdPregnancyName4")).isEqualTo(
            new SingleField("householdPregnancyName4", "Eli Test", null));
        assertThat(result.get("householdPregnancyName5")).isEqualTo(
            new SingleField("householdPregnancyName5", "Frankie Test", null));
    }

    @Test
    public void properlyMapsWhenApplicantDoesNotHaveDisability(){
        Submission submission = new SubmissionTestBuilder()
            .withHouseholdMemberWithDisability("Benny", "Test")
            .withHouseholdMemberWithDisability("Carla", "Test")
            .withHouseholdMemberWithDisability("Damian", "Test")
            .withHouseholdMemberWithDisability("Eli", "Test")
            .withHouseholdMemberWithDisability("Frankie", "Test")
            .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.size()).isEqualTo(8);
        assertThat(result.get("householdHasDisability")).isEqualTo(new SingleField("householdHasDisability", "Yes", null));
        assertThat(result.get("householdHasDisabilitySeeCover")).isEqualTo(new SingleField("householdHasDisabilitySeeCover", "See cover page", null));
        assertThat(result.get("householdHasPregnancy")).isEqualTo(new SingleField("householdHasPregnancy", "No", null));
        assertThat(result.get("householdHasPregnancySeeCover")).isNull();


        assertThat(result.get("householdDisabilityName1")).isEqualTo(
            new SingleField("householdDisabilityName1", "Benny Test", null));
        assertThat(result.get("householdDisabilityName2")).isEqualTo(
            new SingleField("householdDisabilityName2", "Carla Test", null));
        assertThat(result.get("householdDisabilityName3")).isEqualTo(
            new SingleField("householdDisabilityName3", "Damian Test", null));
        assertThat(result.get("householdDisabilityName4")).isEqualTo(
            new SingleField("householdDisabilityName4", "Eli Test", null));
        assertThat(result.get("householdDisabilityName5")).isEqualTo(
            new SingleField("householdDisabilityName5", "Frankie Test", null));
    }
    
    @Test
    public void whenNoOneIsPregnantOrHasDisability() {
        Submission submission = new SubmissionTestBuilder()
            .withHouseholdMemberApplying("Betty", "White", "10", "2", "1999", "Child", "F", "NeverMarried", "firstGrade",
                "123456789", "No", "No", null, null)
            .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("householdHasPregnancy")).isEqualTo(
            new SingleField("householdHasPregnancy", "No", null));
        assertThat(result.get("householdHasPregnancySeeCover")).isNull();
        assertThat(result.get("householdHasDisability")).isEqualTo(
            new SingleField("householdHasDisability", "No", null));
        assertThat(result.get("householdHasDisabilitySeeCover")).isNull();
    }
}
