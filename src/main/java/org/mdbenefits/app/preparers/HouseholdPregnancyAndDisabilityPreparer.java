package org.mdbenefits.app.preparers;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.stereotype.Component;

@Component
public class HouseholdPregnancyAndDisabilityPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();

        var household = (List<Map<String, Object>>) submission.getInputData().get("household");

        if (household != null) {
            AtomicBoolean householdMemberIsPregnant = new AtomicBoolean(false);
            AtomicBoolean householdMemberHasDisability = new AtomicBoolean(false);
            household.forEach(householdMember -> {
                if (householdMember.get("householdMemberIsPregnant").equals("Yes")) {
                    householdMemberIsPregnant.set(true);
                }
                if (householdMember.get("householdMemberHasDisability").equals("Yes")) {
                    householdMemberHasDisability.set(true);
                }
            });
            
            results.put("householdMemberIsPregnant", new SingleField("householdMemberIsPregnant", 
                householdMemberIsPregnant.get() ? "Yes" : "No", null));
            if (householdMemberIsPregnant.get()) {
                results.put("householdMemberIsPregnantSeeCover", 
                    new SingleField("householdMemberIsPregnantSeeCover", "See cover page", null));
            }
            results.put("householdMemberHasDisability", new SingleField("householdMemberHasDisability", 
                householdMemberHasDisability.get() ? "Yes" : "No", null));
            if (householdMemberHasDisability.get()) {
                results.put("householdMemberHasDisabilitySeeCover",
                    new SingleField("householdMemberHasDisabilitySeeCover", "See cover page",null));
            }
            
        }
        
        return results;
    }
}
