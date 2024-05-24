package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MaybeClearMoneyOnHandAmount implements Action {

    /**
     * This action will clear out the expeditedMoneyOnHandAmount field, if the householdMoneyOnHandLessThan100 is equal to "true";
     * This is useful in the case that the user goes forward and enters an amount and then goes back and indicates that they have
     * less than the money on hand threshold.
     *
     * @param formSubmission the data being submitted.
     * @param submission     submission object the action is associated with, not null
     */
    @Override
    public void run(FormSubmission formSubmission, Submission submission) {
        boolean isMoneyOnHandLessThan100 = ((String) formSubmission.getFormData()
                .get("householdMoneyOnHandLessThan100")).equalsIgnoreCase("true");

        if (isMoneyOnHandLessThan100) {
            formSubmission.getFormData().put("expeditedMoneyOnHandAmount", "0");
        }
    }
}
