package org.mdbenefits.app.submission.conditions;

import static org.mdbenefits.app.utils.HouseholdUtilities.isMember60orOlder;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class HasHouseholdMemberDisabledOrOver60 implements Condition {

    @Override
    public Boolean run(Submission submission) {
        var inputData = submission.getInputData();
        List<Map<String, Object>> householdMembers = (List) inputData.get("household");
        int applicantBirthdateDay = Integer.parseInt((String) inputData.get("birthDay"));
        int applicantBirthdateMonth = Integer.parseInt((String) inputData.get("birthMonth"));
        int applicantBirthdateYear = Integer.parseInt((String) inputData.get("birthYear"));
        boolean applicantIsDisabled = ((String) inputData.getOrDefault("applicantHasDisability", "No")).equalsIgnoreCase("Yes");

        if (isMember60orOlder(applicantBirthdateYear, applicantBirthdateMonth, applicantBirthdateDay) ||
                applicantIsDisabled) {
            return true;
        }

        for (Map<String, Object> householdMember : householdMembers) {

            int hhMemberBirthDay = Integer.parseInt((String) householdMember.get("householdMemberBirthDay"));
            int hhMemberBirthMonth = Integer.parseInt((String) householdMember.get("householdMemberBirthMonth"));
            int hhMemberBirthYear = Integer.parseInt((String) householdMember.get("householdMemberBirthYear"));
            boolean hhMemberIsDisabled = ((String) householdMember.getOrDefault("householdMemberHasDisability",
                    "No")).equalsIgnoreCase("Yes");
            if (isMember60orOlder(hhMemberBirthYear, hhMemberBirthMonth, hhMemberBirthDay) || hhMemberIsDisabled) {
                return true;
            }
        }

        return false;
    }

}
