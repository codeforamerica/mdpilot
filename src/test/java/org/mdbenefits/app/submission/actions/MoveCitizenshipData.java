package org.mdbenefits.app.submission.actions;

import static formflow.library.inputs.FieldNameMarkers.DYNAMIC_FIELD_MARKER;
import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.mdbenefits.app.data.enums.CitizenStatusTypes;

class MoveCitizenshipDataTest {

    private final MoveCitizenshipData action = new MoveCitizenshipData();
    private final String INPUT_FIELD_NAME = "citizenshipStatus";
    private final String HOUSEHOLD_FIELD_NAME = "householdMemberCitizenshipStatus";
    private final String APPLICANT_FIELD_NAME = "applicantCitizenshipStatus";

    @Test
    public void moveDataIntoHouseholdMembersData() {

        Submission submission = new SubmissionTestBuilder()
            .withPersonalInfo("Test", "Person",
                "10", "10", "2010",
                "", "", "", "", "")
            .withHouseholdMember("Child1", "Person",
                "10", "10", "2019",
                "", "", "", "", "", null, "")
            .withHouseholdMember("Child2", "Person",
                "10", "10", "2019",
                "", "", "", "", "", null, "")
            .withHouseholdMember("Child3", "Person",
                "10", "10", "2019",
                "", "", "", "", "", null, "")
            .withHouseholdMember("Child4", "Person",
                "10", "10", "2019",
                "", "", "", "", "", null, "")
            .build();

        // first part of map is uuid as String
        Map<String, String> citizenshipStatusMap = Map.of(
            "child1-person", CitizenStatusTypes.ASYLEE.name(),
            "child2-person", CitizenStatusTypes.NOT_APPLYING.name(),
            "child3-person", CitizenStatusTypes.REFUGEE.name(),
            "child4-person", CitizenStatusTypes.PERM_RESIDENT_OR_GREEN_CARD.name()
        );

        citizenshipStatusMap.forEach((key, value) -> {
            String fieldName = INPUT_FIELD_NAME + DYNAMIC_FIELD_MARKER + key;
            submission.getInputData().put(fieldName, value);
        });

        // add applicant's data
        submission.getInputData().put(APPLICANT_FIELD_NAME, CitizenStatusTypes.US_CITIZEN.name());

        action.run(submission);

        // make sure the dynamic field data is gone for household members
        citizenshipStatusMap.forEach((key, value) -> {
            String fieldName = INPUT_FIELD_NAME + DYNAMIC_FIELD_MARKER + key;
            assertThat(submission.getInputData().containsKey(fieldName)).isFalse();
        });

        List<Map<String, Object>> household = (List) submission.getInputData().get("household");

        household.forEach(member -> {
            String uuid = (String) member.get("uuid");
            assertThat(member.get(HOUSEHOLD_FIELD_NAME)).isEqualTo(citizenshipStatusMap.get(uuid));
        });

        assertThat(submission.getInputData().get(APPLICANT_FIELD_NAME)).isEqualTo(CitizenStatusTypes.US_CITIZEN.name());
    }
}