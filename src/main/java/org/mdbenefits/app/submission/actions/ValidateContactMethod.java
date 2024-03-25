package org.mdbenefits.app.submission.actions;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateContactMethod extends AssisterAction {

    private final String TEXTING_IS_OKAY_INPUT_NAME = "textingCellNumberIsOkay";
    private final String PHONE_NUMBER_INPUT_NAME = "cellPhoneNumber";

    @Override
    public Map<String, List<String>> runValidation(FormSubmission formSubmission, Submission submission) {
        Map<String, List<String>> errorMessages = new HashMap<>();
        Map<String, Object> inputData = formSubmission.getFormData();
        String isTextOkay = (String) inputData.getOrDefault(TEXTING_IS_OKAY_INPUT_NAME, "false");
        String phoneNumber = (String) inputData.getOrDefault(PHONE_NUMBER_INPUT_NAME, "");

        if (isTextOkay.equalsIgnoreCase("true") && phoneNumber.isBlank()) {
            errorMessages.put(PHONE_NUMBER_INPUT_NAME, List.of(translateMessage("contact-info.text-error")));
        }

        return errorMessages;
    }

}
