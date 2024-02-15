package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.utils.HouseholdUtilities;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Slf4j
public class ClearNoHomeAddressIfNotSelected implements Action {


    /**
     * @param formSubmission the form data that was submitted when posting the screen this action corresponds to.
     * @param submission the submission object that contains the input data submitted so far for the application.
     * 
     * This action is used to clear the noHomeAddress field from the submission's input data if the screen is submitted with the
     * checkbox unchecked.
     */
    @Override
    public void run(FormSubmission formSubmission, Submission submission) {
        if (!formSubmission.getFormData().containsKey("noHomeAddress")) {
          submission.getInputData().remove("noHomeAddress");
        }
    }

}
