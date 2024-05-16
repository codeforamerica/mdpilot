package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class HasMoreThan100OnHand implements Condition {

    @Override
    public Boolean run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();
        return inputData.getOrDefault("householdMoneyOnHandLessThan100", "false")
                .toString()
                .equalsIgnoreCase("false");
    }
}
