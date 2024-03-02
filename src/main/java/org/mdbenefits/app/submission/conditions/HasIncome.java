package org.mdbenefits.app.submission.conditions;

import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class HasIncome extends BasicCondition {

    @Override
    public Boolean run(Submission submission) {
        return run(submission, "householdHasIncome", "true");
    }
}