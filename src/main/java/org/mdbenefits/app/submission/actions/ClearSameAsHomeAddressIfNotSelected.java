package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Slf4j
public class ClearSameAsHomeAddressIfNotSelected implements Action {

    @Override
    public void run(FormSubmission formSubmission, Submission submission) {
        if (!formSubmission.getFormData().containsKey("sameAsHomeAddress")) {
          submission.getInputData().remove("sameAsHomeAddress");
        }
    }

}
