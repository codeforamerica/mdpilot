package org.mdbenefits.app.submission.conditions;

import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class HasMedicalExpenses extends BasicArrayCondition {

    @Override
    public Boolean run(Submission submission) {
        return checkHasValidSelection(submission, "medicalExpenses[]");
    }
}
