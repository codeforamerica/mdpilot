package org.mdbenefits.app.preparers;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.mdbenefits.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class HouseholdDetailsPreparer implements SubmissionFieldPreparer {
    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();

        var household = (List<Map<String, Object>>) submission.getInputData().get("household");
        if (household != null) {
            for (int i = 0; i < household.size(); i++) {
                Map<String, Object> householdMember = household.get(i);
                String fullName = String.format("%s, %s", householdMember.get("householdMemberLastName"), householdMember.get("householdMemberFirstName"));
                results.put("householdMemberFullName_" + i, new SingleField("householdMemberFullName",
                        fullName, i + 1));

                var dob = Stream.of("householdMemberBirthMonth", "householdMemberBirthDay", "householdMemberBirthYear")
                        .map(householdMember::get)
                        .reduce((e, c) -> e + "/" + c)
                        .get();
                results.put("householdMemberDOB_" + i, new SingleField("householdMemberDOB", (String) dob, i + 1));

                results.put("householdMemberSSN_" + i, new SingleField("householdMemberSSN", SubmissionUtilities.formatSSN((String) householdMember.get("householdMemberEncryptedSSN")), i + 1));
            }
        }
        return results;
    }
}
