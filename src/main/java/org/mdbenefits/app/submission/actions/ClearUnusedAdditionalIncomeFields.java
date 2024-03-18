package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.data.enums.AdditionalIncomeType;
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
        boolean none = additionalIncomeTypes.contains("NONE");

        for (AdditionalIncomeType type : AdditionalIncomeType.values()) {
            if (none || !additionalIncomeTypes.contains(type.name())) {
                submissionInputData.remove(type.getInputFieldName());
            }
        }
    }

}
