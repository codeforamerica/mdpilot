package org.mdbenefits.app.submission.conditions;

import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class IsApplicantApplyingForSelf extends BasicCondition {

    @Override
    public Boolean run(Submission submission) {
        return run(submission, "isApplicantApplying", "true");
    }
}
