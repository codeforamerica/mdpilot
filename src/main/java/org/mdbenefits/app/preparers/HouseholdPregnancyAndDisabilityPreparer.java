package org.mdbenefits.app.preparers;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

import org.mdbenefits.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
public class HouseholdPregnancyAndDisabilityPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();

        var inputData = submission.getInputData();

        if (inputData.getOrDefault("isMinimumApplication", "false").toString().equalsIgnoreCase("true")) {
            // they used the minimum app flow, don't assert anything about pregnancy / disability
            // since we didn't collect it
            return results;
        }

        ArrayList<String> householdMembersPregnant = new ArrayList<String>();
        ArrayList<String> householdMembersWithDisability = new ArrayList<String>();

        // only looking for a yes here, so it doesn't matter if we default to "No" in the case of this being unset
        if (inputData.getOrDefault("isApplicantPregnant", "No").toString().equalsIgnoreCase("Yes")) {
            householdMembersPregnant.add(SubmissionUtilities.applicantFullNameFormatted(submission));
        }

        // only looking for a yes here, so it doesn't matter if we default to "No" in the case of this being unset
        if (inputData.getOrDefault("applicantHasDisability", "No").toString().equalsIgnoreCase("Yes")) {
            householdMembersWithDisability.add(SubmissionUtilities.applicantFullNameFormatted(submission));
        }

        var household = (List<Map<String, Object>>) inputData.getOrDefault("household", emptyList());

        if (!household.isEmpty()) {
            householdMembersPregnant.addAll(householdMembersWithCondition(household, "householdMemberIsPregnant"));
            householdMembersWithDisability.addAll(householdMembersWithCondition(household, "householdMemberHasDisability"));
        }

        results.put("householdHasDisability",
                new SingleField("householdHasDisability", !householdMembersWithDisability.isEmpty() ? "Yes" : "No", null));
        results.put("householdHasPregnancy",
                new SingleField("householdHasPregnancy", !householdMembersPregnant.isEmpty() ? "Yes" : "No", null));

        if (!householdMembersWithDisability.isEmpty()) {
            results.put("householdHasDisabilitySeeCover",
                    new SingleField("householdHasDisabilitySeeCover", "See cover page", null));
            for (int i = 0; (i < 5 && i < householdMembersWithDisability.size()); i++) {
                var index = i + 1;
                results.put("householdDisabilityName" + index,
                        new SingleField("householdDisabilityName" + index, householdMembersWithDisability.get(i), null));
            }
        }

        if (!householdMembersPregnant.isEmpty()) {
            results.put("householdHasPregnancySeeCover",
                    new SingleField("householdHasPregnancySeeCover", "See cover page", null));
            for (int i = 0; (i < 5 && i < householdMembersPregnant.size()); i++) {
                var index = i + 1;
                results.put("householdPregnancyName" + index,
                        new SingleField("householdPregnancyName" + index, householdMembersPregnant.get(i), null));
            }
        }

        return results;
    }


    private List<String> householdMembersWithCondition(List<Map<String, Object>> householdSubflow, String fieldName) {
        return householdSubflow.stream()
                .filter(hhMember -> "yes".equalsIgnoreCase(String.valueOf(hhMember.get(fieldName))))
                .map(hhMember -> SubmissionUtilities.householdMemberFullNameFormatted(hhMember)).toList();
    }
}
