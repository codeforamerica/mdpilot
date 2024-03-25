package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.data.enums.HomeExpensesType;
import org.mdbenefits.app.data.enums.MedicalExpensesType;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClearUnusedMedicalExpensesFields implements Action {

    /**
     * This action will clear out the unused medical expenses amount fields from the submission's input data, if the choice for
     * additional income type is no longer in the income type list.
     *
     * @param submission submission object the action is associated with, not null
     */
    @Override
    public void run(Submission submission) {
        Map<String, String> submissionInputData = (Map) submission.getInputData();
        List<String> medicalExpenses = (List) submission.getInputData().getOrDefault("medicalExpenses[]", List.of());
        boolean none = medicalExpenses.contains("NONE");

        for (MedicalExpensesType type : MedicalExpensesType.values()) {
            if (none || !medicalExpenses.contains(type.name())) {
                submissionInputData.remove(type.getInputFieldName());
            }
        }
    }

}
