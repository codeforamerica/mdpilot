package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClearIncompleteIterationsFromHousehold implements Action {
    
    @Autowired
    SubmissionRepository submissionRepository;

    /**
     * This action will clear out the incomplete iterations from the household subflow. These can be created when the user goes back
     * to the beginning of the subflow before completing an iteration.
     *
     * @param submission submission object the action is associated with, not null
     */
    @Override
    public void run(Submission submission) {
        ArrayList<HashMap<String, Object>> household = (ArrayList<HashMap<String, Object>>) submission.getInputData().get("household");
        if (household != null) {
            List<HashMap<String, Object>> filteredHouseholdSubflow = household.stream()
                    .filter(iteration -> Boolean.TRUE.equals(iteration.get(Submission.ITERATION_IS_COMPLETE_KEY)))
                            .collect(Collectors.toList());
            submission.getInputData().put("household", filteredHouseholdSubflow);
            submissionRepository.save(submission);
        }
    }
}
