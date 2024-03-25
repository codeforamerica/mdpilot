package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.utils.SubmissionUtilities;

import java.util.List;

@Slf4j
public abstract class BasicArrayCondition implements Condition {

    /**
     * Return true if the array corresponding to the key exists and ( is not empty or does not have "None" as its value)
     */
    public Boolean checkHasValidSelection(Submission submission, String key) {
        var inputData = submission.getInputData();
        if (inputData.containsKey(key)) {
            if (submission.getInputData().get(key) instanceof List<?> array) {
                if (array.isEmpty()) {
                    return false;
                }
                boolean noneOfAboveSelected = array.stream().anyMatch(SubmissionUtilities::isNoneOfAboveSelection);
                if (noneOfAboveSelected && array.size() > 1) {
                    // This should never happen, it represents a misconfiguration
                    // Either the one and only selection should be NONE, or NONE should not be in the list
                    log.error(
                            "Submission {} contains both {} and valid values in {} key",
                            submission.getId(),
                            SubmissionUtilities.NONE_OF_ABOVE_SELECTION_VALUE,
                            key);
                }
                return !noneOfAboveSelected;
            } else {
                throw new IllegalArgumentException(
                        String.format("Non-list value stored in input with key '%s'", key));
            }
        } else {
            return false;
        }
    }
}
