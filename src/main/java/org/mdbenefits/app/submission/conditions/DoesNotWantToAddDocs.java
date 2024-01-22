package org.mdbenefits.app.submission.conditions;

import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class DoesNotWantToAddDocs extends BasicCondition {

    @Override
    public Boolean run(Submission submission) {
        return run(submission, "addDocuments", "false");
    }
}
