package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class HasAdditionalIncome implements Condition {

    @Override
    public Boolean run(Submission submission) {
        List<String> additionalIncome = (List) submission.getInputData().getOrDefault("additionalIncome[]", List.of());
        return (additionalIncome != null && !additionalIncome.contains("NONE"));
    }
}