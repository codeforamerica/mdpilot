package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.mdbenefits.app.utils.SubmissionUtilities.isNoneOfAboveSelection;

@Component
public class HasAdditionalIncome implements Condition {

    @Override
    public Boolean run(Submission submission) {
        List<String> additionalIncome = (List) submission.getInputData().getOrDefault("additionalIncome[]", List.of());
        return !isNoneOfAboveSelection(additionalIncome);
    }
}