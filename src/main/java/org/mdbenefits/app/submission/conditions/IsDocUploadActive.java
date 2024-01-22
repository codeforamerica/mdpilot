package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.mdbenefits.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class IsDocUploadActive implements Condition {


    @Override
    public Boolean run(Submission submission) {
        return SubmissionUtilities.isDocUploadActive(submission);
    }
}
