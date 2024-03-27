package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Tests whether the record has a phone number or email address and if missing those, returns true;
 */
@Component
public class ContactInfoMissing implements Condition {

    @Override
    public Boolean run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();
        String cellPhoneNumber = (String) inputData.getOrDefault("cellPhoneNumber", "");
        String homePhoneNumber = (String) inputData.getOrDefault("homePhoneNumber", "");
        String workPhoneNumber = (String) inputData.getOrDefault("workPhoneNumber", "");
        return cellPhoneNumber.isBlank() && homePhoneNumber.isBlank() &&
                workPhoneNumber.isBlank();
    }
}
