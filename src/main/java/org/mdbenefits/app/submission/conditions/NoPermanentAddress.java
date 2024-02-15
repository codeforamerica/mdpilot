package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class NoPermanentAddress implements Condition {

    @Override
    public Boolean run(Submission submission) {
        return submission.getInputData().containsKey("noHomeAddress") && submission.getInputData().get("noHomeAddress").equals("true");
    }
}
