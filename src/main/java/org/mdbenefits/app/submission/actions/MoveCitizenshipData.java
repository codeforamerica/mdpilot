package org.mdbenefits.app.submission.actions;

import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

/**
 * A beforeSaveAction that moves citizen status information into a `household` member's data, removing it from the higher level in
 * the Map/JSON.
 */
@Component
public class MoveCitizenshipData extends MoveDataIntoSubflow {

    @Override
    public void run(Submission submission) {
        moveDataToSubflow(submission, "household", "citizenshipStatus", "householdMemberCitizenshipStatus");
    }
}
