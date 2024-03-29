package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.mdbenefits.app.submission.StringEncryptor;
import org.mdbenefits.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class DecryptSSNBeforeDisplaying implements Action {

    private final StringEncryptor encryptor;

    public DecryptSSNBeforeDisplaying(StringEncryptor stringEncryptor) {
        encryptor = stringEncryptor;
    }

    private StringEncryptor getEncryptor() {
        return encryptor;
    }

    @Override
    public void run(Submission submission) {
        String encryptedSSN = (String) submission.getInputData().remove("encryptedSSN");
        if (encryptedSSN != null) {
            String decryptedSSN = getEncryptor().decrypt(encryptedSSN);
            submission.getInputData().put("ssn", decryptedSSN);
        }

        ArrayList<LinkedHashMap> householdMembers = (ArrayList) submission.getInputData().get("household");

        for (LinkedHashMap hhmember : householdMembers) {
            String ssnKey = SubmissionUtilities.getDecryptedSSNKeyName((String) hhmember.get("uuid"));
            encryptedSSN = (String) hhmember.remove(SubmissionUtilities.ENCRYPTED_SSNS_INPUT_NAME);
            String decryptedSSN = getEncryptor().decrypt(encryptedSSN);
            submission.getInputData().put(ssnKey, decryptedSSN);
        }
    }
}
