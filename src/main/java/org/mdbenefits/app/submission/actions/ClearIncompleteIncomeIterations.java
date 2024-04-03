package org.mdbenefits.app.submission.actions;


import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClearIncompleteIncomeIterations implements Action {

    /**
     * This action will clear out the incomplete iterations from the income subflow. These can be created when the user goes back
     * to the beginning of the subflow before completing an iteration.
     *
     * @param submission submission object the action is associated with, not null
     * @param id the id of the current iteration being submitted when this action is run, not null
     */
    @Override
    public void run(Submission submission, String id) {
        ArrayList<HashMap<String, Object>> incomeSubflow = (ArrayList<HashMap<String, Object>>) submission.getInputData().get("income");
        if (incomeSubflow != null) {
            List<HashMap<String, Object>> filteredIncomeSubflow = incomeSubflow.stream()
                    .filter(iteration -> id.equals(iteration.get("uuid")) ||
                            Boolean.TRUE.equals(iteration.get(Submission.ITERATION_IS_COMPLETE_KEY)))
                            .collect(Collectors.toList());
            submission.getInputData().put("income", filteredIncomeSubflow);
        }
    }
}
