package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ReformatHouseholdNameBeforeSaving implements Action {
    @Override
    public void run(Submission submission, String id) {
        ArrayList<HashMap<String, Object>> householdSubflow = (ArrayList<HashMap<String, Object>>) submission.getInputData().get("household");
        if (householdSubflow != null) {
            HashMap<String, Object> householdMember = householdSubflow.stream()
                .filter(iteration -> id.equals(iteration.get("uuid"))).toList().get(0);
            String firstName = (String) householdMember.getOrDefault("householdMemberFirstName", "");
            String middleName = (String) householdMember.getOrDefault("householdMemberMiddleName", "");
            String lastName = (String) householdMember.getOrDefault("householdMemberLastName", "");

            if(!firstName.isBlank()){
                householdMember.put("householdMemberFirstName", firstName.trim());
            }
            if(!middleName.isBlank()){
                householdMember.put("householdMemberMiddleName", middleName.trim());
            }
            if(!lastName.isBlank()){
                householdMember.put("householdMemberLastName", lastName.trim());
            }
        }
    }
}
