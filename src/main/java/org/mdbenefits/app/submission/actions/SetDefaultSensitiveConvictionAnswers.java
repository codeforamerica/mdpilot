package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Slf4j
public class SetDefaultSensitiveConvictionAnswers implements Action {

    @Override
    public void run(Submission submission) {
        Arrays.stream(SubmissionUtilities.SENSITIVE_CONVICTION_QUESTIONS).forEach(s -> submission.getInputData().putIfAbsent("noOne" + s + "[]", "true"));
    }
}
