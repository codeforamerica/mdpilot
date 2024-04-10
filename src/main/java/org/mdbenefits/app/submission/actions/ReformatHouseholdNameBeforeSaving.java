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
            List<HashMap<String, Object>> householdMember = householdSubflow.stream()
                .filter(iteration -> id.equals(iteration.get("uuid"))).toList();
            submission.getInputData().put("income", householdMember);
        }
    }

//        submission.getInputData();
//        Map<String, Object> inputData = formSubmission.getFormData();
//
//        String firstName = (String) inputData.remove("householdMemberFirstName");
//        String middleName = (String) inputData.remove("householdMemberMiddleName");
//        String lastName = (String) inputData.remove("householdMemberLastName");
//
//        submission.getInputData().put("householdMemberFirstName", firstName.trim());
//        submission.getInputData().put("householdMemberMiddleName", middleName.trim());
//        submission.getInputData().put("householdMemberLastName", lastName.trim());

}
