package org.mdbenefits.app.submission.conditions;

import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class IsEligibleForExpeditedSnap extends BasicCondition {

    @Override
    public Boolean run(Submission submission) {
        return run(submission, "isEligibleForExpeditedSnap", "true");
    }
}
