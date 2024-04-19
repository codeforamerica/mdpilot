package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.mdbenefits.app.submission.StringEncryptor;
import org.mdbenefits.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class ReformatNameBeforeSaving implements Action {
    @Override
    public void run(Submission submission) {
        String firstName = (String) submission.getInputData().getOrDefault("firstName", "");
        String middleName = (String) submission.getInputData().getOrDefault("middleName", "");
        String lastName = (String) submission.getInputData().getOrDefault("lastName", "");

        if(!firstName.isBlank()){
            submission.getInputData().put("firstName", firstName.trim());
        }
        if(!middleName.isBlank()){
            submission.getInputData().put("middleName", middleName.trim());
        }
        if(!lastName.isBlank()){
            submission.getInputData().put("lastName", lastName.trim());
        }
    }
}