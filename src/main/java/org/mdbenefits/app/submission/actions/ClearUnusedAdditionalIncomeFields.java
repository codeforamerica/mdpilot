package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClearUnusedAdditionalIncomeFields implements Action {

    /**
     * This action will clear out the unused additional income amount fields from the submission's input data, if the choice for
     * additional income type is no longer in the income type list.
     *
     * @param formSubmission form submission object the action is associated with, not null
     * @param submission     submission object the action is associated with, not null
     */
    @Override
    public void run(FormSubmission formSubmission, Submission submission) {
        Map<String, String> submissionInputData = (Map) submission.getInputData();
        List<String> additionalIncomeTypes = (List) formSubmission.getFormData().getOrDefault("additionalIncome[]", List.of());
        boolean none = additionalIncomeTypes.contains("NONE_OF_THESE");

        if (none || !additionalIncomeTypes.contains("ALIMONY")) {
            submissionInputData.remove("additionalIncomeAlimony");
        }
        if (none || !additionalIncomeTypes.contains("CHILD_SUPPORT")) {
            submissionInputData.remove("additionalIncomeChildSupport");
        }
        if (none || !additionalIncomeTypes.contains("FRIENDS_FAMILY_CONTRIBUTION")) {
            submissionInputData.remove("additionalIncomeFriendsAndFamily");
        }
        if (none || !additionalIncomeTypes.contains("PENSION_RETIREMENT")) {
            submissionInputData.remove("additionalIncomePensionRetirement");
        }
        if (none || !additionalIncomeTypes.contains("SUPPLEMENTAL_SECURITY_INCOME")) {
            submissionInputData.remove("additionalIncomeSSI");
        }
        if (none || !additionalIncomeTypes.contains("SOCIAL_SECURITY")) {
            submissionInputData.remove("additionalIncomeSS");
        }
        if (none || !additionalIncomeTypes.contains("UNEMPLOYMENT")) {
            submissionInputData.remove("additionalIncomeUnemployment");
        }
        if (none || !additionalIncomeTypes.contains("VETERANS_BENEFITS")) {
            submissionInputData.remove("additionalIncomeVeteransBenefits");
        }
        if (none || !additionalIncomeTypes.contains("WORKERS_COMPENSATION")) {
            submissionInputData.remove("additionalIncomeWorkersComp");
        }
        if (none || !additionalIncomeTypes.contains("OTHER")) {
            submissionInputData.remove("additionalIncomeOther");
        }
    }

}
