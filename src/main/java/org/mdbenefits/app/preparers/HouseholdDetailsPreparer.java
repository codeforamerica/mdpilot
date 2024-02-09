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
import org.mdbenefits.app.data.enums.CitizenStatusTypes;
import org.mdbenefits.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class HouseholdDetailsPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();

        var household = (List<Map<String, Object>>) submission.getInputData().get("household");
        boolean allAreCitizens = ((String) submission.getInputData().getOrDefault("allAreCitizens", "false")).equalsIgnoreCase(
                "true");

        if (household != null) {
            int iteration = 1;  // start at one!
            for (Map<String, Object> householdMember : household) {
                String fullName = String.format("%s, %s", householdMember.get("householdMemberLastName"),
                        householdMember.get("householdMemberFirstName"));
                results.put("householdMemberFullName_" + iteration, new SingleField("householdMemberFullName",
                        fullName, iteration));

                var dob = Stream.of("householdMemberBirthMonth", "householdMemberBirthDay", "householdMemberBirthYear")
                        .map(householdMember::get)
                        .reduce((e, c) -> e + "/" + c)
                        .get();

                results.put("householdMemberDOB_" + iteration,
                        new SingleField("householdMemberDOB", (String) dob, iteration));

                results.put("householdMemberSSN_" + iteration, new SingleField("householdMemberSSN",
                        SubmissionUtilities.formatSSN((String) householdMember.get("householdMemberEncryptedSSN")), iteration));

                String applyingStatus = (String) householdMember.getOrDefault("householdMemberApplyingForBenefits", "");
                results.put("householdMemberApplyingForBenefits_" + iteration,
                        new SingleField(
                                "householdMemberApplyingForBenefits",
                                applyingStatus.equalsIgnoreCase("yes") ? "Y" : "N",
                                iteration));
                if (applyingStatus.equalsIgnoreCase("yes")) {
                    String citizen = "Y";

                    if (!allAreCitizens) {
                        String citizenshipStatus = (String) householdMember.get("householdMemberCitizenshipStatus");
                        if (!citizenshipStatus.equals(CitizenStatusTypes.US_CITIZEN.name())) {
                            citizen = "N";
                        }
                    }
                    results.put("householdMemberCitizen_" + iteration,
                            new SingleField("householdMemberCitizen", citizen, iteration));
                }
                iteration++;
            }
        }
        return results;
    }
}
