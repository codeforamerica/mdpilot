package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Action that will unset the minimum flow flag, if it was set.
 */
@Component
@Slf4j
public class UnsetMinimumFlowFlag implements Action {

    private static final String MINIMAL_FLOW_FLAG = "isMinimumApplication";

    @Override
    public void run(FormSubmission formSubmission, Submission submission) {
        formSubmission.getFormData().put(MINIMAL_FLOW_FLAG, "false");
    }
}
