package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.data.enums.HomeExpensesType;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClearUnusedExpensesFields implements Action {

    /**
     * This action will clear out the unused additional income amount fields from the submission's input data, if the choice for
     * additional income type is no longer in the income type list.
     *
     * @param submission submission object the action is associated with, not null
     */
    @Override
    public void run(Submission submission) {
        Map<String, String> submissionInputData = (Map) submission.getInputData();
        List<String> householdHomeExpenses = (List) submission.getInputData().getOrDefault("householdHomeExpenses[]", List.of());
        boolean none = householdHomeExpenses.contains("NONE");

        for (HomeExpensesType type : HomeExpensesType.values()) {
            if (none || !householdHomeExpenses.contains(type.name())) {
                submissionInputData.remove(type.getInputFieldName());
            }
        }
    }

}
