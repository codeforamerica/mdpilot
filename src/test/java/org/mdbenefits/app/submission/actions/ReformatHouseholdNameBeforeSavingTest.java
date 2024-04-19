package org.mdbenefits.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest()
@ActiveProfiles("test")
class ReformatHouseholdNameBeforeSavingTest {

    private final ReformatHouseholdNameBeforeSaving action = new ReformatHouseholdNameBeforeSaving();

    @Test
    public void testNamesWithSpaces() {
        Submission submission = new SubmissionTestBuilder()
            .withHouseholdMemberWithPregnancy("First Name   ", " LastName With MultipleNames   ")
            .build();

        ArrayList<HashMap<String, Object>> household = (ArrayList<HashMap<String, Object>>) submission.getInputData().get("household");
        var householdMember = household.get(0);

        action.run(submission, householdMember.get("uuid").toString());

        assertThat(householdMember.get("householdMemberFirstName")).isEqualTo("First Name");
        assertThat(householdMember.get("householdMemberMiddleName")).isNull();
        assertThat(householdMember.get("householdMemberLastName")).isEqualTo("LastName With MultipleNames");
    }

    @Test
    public void testNamesWithNoSpaces() {
        Submission submission = new SubmissionTestBuilder()
            .withHouseholdMemberWithPregnancy("First", "Last")
            .build();

        ArrayList<HashMap<String, Object>> household = (ArrayList<HashMap<String, Object>>) submission.getInputData().get("household");
        var householdMember = household.get(0);

        action.run(submission, householdMember.get("uuid").toString());
        
        assertThat(householdMember.get("householdMemberFirstName")).isEqualTo("First");
        assertThat(householdMember.get("householdMemberMiddleName")).isNull();
        assertThat(householdMember.get("householdMemberLastName")).isEqualTo("Last");
    }

}
