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
    public void run(Submission submission) {
        String firstName = (String) submission.getInputData().remove("firstName");
        String middleName = (String) submission.getInputData().remove("middleName");
        String lastName = (String) submission.getInputData().remove("lastName");

        submission.getInputData().put("firstName", firstName.trim());
        submission.getInputData().put("middleName", middleName.trim());
        submission.getInputData().put("lastName", lastName.trim());
    }
}