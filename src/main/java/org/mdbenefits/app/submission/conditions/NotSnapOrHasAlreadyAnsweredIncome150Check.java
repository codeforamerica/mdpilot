package org.mdbenefits.app.submission.conditions;

import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NotSnapOrHasAlreadyAnsweredIncome150Check extends BasicArrayCondition {

    @Override
    public Boolean run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();
        List<String> programs = (List) inputData.getOrDefault("programs[]", List.of());

        return !programs.contains("SNAP") ||
                (inputData.containsKey("incomeLessThan150") &&
                        inputData.containsKey("isApplyingForExpeditedSnap"));
    }
}
