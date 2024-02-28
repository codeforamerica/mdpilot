package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class IsHouseholdMemberApplying implements Condition {

    @Override
    public Boolean run(Submission submission, String uuid) {
        Map<String, Object> currentSubflowIteration = submission.getSubflowEntryByUuid("household", uuid);
        if (currentSubflowIteration == null) {
            return false;
        }
        String isHouseholdMemberApplying = (String) currentSubflowIteration.getOrDefault("householdMemberApplyingForBenefits", "No");
        return isHouseholdMemberApplying.equals("Yes");
    }
}
