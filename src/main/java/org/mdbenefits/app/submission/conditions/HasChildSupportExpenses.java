package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class HasChildSupportExpenses implements Condition {

    @Override
    public Boolean run(Submission submission) {
        var inputData = submission.getInputData();
        if (inputData.containsKey("hasChildSupportExpenses")) {
            return submission.getInputData().get("hasChildSupportExpenses").equals("true");
        }
        return false;
    }
}
