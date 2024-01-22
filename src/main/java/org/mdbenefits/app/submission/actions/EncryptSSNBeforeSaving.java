package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.mdbenefits.app.submission.StringEncryptor;
import org.mdbenefits.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class EncryptSSNBeforeSaving implements Action {

    private final StringEncryptor encryptor;

    public EncryptSSNBeforeSaving(StringEncryptor stringEncryptor) {
        encryptor = stringEncryptor;
    }

    @Override
    public void run(Submission submission) {
        String ssnInput = (String) submission.getInputData().remove("ssn");
        if (ssnInput != null) {
            String encryptedSSN = encryptor.encrypt(ssnInput);
            submission.getInputData().put("encryptedSSN", encryptedSSN);
        }

        ArrayList<LinkedHashMap> householdMembers = (ArrayList) submission.getInputData().get("household");
        if (householdMembers != null) {
            for (LinkedHashMap hhmember : householdMembers) {
                String ssnKey = SubmissionUtilities.getDecryptedSSNKeyName((String) hhmember.get("uuid"));
                var householdMemberSsn = (String) submission.getInputData().remove(ssnKey);
                String encryptedSSN = encryptor.encrypt(householdMemberSsn);
                hhmember.put(SubmissionUtilities.ENCRYPTED_SSNS_INPUT_NAME, encryptedSSN);
            }
        }
    }
}
