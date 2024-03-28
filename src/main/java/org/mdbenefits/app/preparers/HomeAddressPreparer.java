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
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.data.enums.CitizenshipStatus;
import org.mdbenefits.app.data.enums.EthnicityType;
import org.mdbenefits.app.data.enums.RaceType;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HomeAddressPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();

        Map<String, Object> inputData = submission.getInputData();
        
        boolean useSuggestedAddress = Boolean.parseBoolean(
                inputData.getOrDefault("useSuggestedAddress", "false").toString());

        boolean homeAddressSameAsMailingAddress = Boolean.parseBoolean(
                inputData.getOrDefault("sameAsHomeAddress", "false").toString());

        List<String> homeAddressParts = List.of("homeAddressStreetAddress1", "homeAddressStreetAddress2",
                "homeAddressCity", "homeAddressState", "homeAddressZipCode");

        List<String> mailingAddressParts = List.of("mailingAddressStreetAddress1", "mailingAddressStreetAddress2",
                "mailingAddressCity", "mailingAddressState", "mailingAddressZipCode");
        // We only ever validate the mailing address so we need to replace the home address with the validated 
        // mailing address if they are the same
        if (useSuggestedAddress && homeAddressSameAsMailingAddress) {
            for (int i = 0; i < mailingAddressParts.size(); i++) {
                String mailingAddressPart = mailingAddressParts.get(i);
                if (inputData.containsKey(mailingAddressPart + "_validated")) {
                    results.put(homeAddressParts.get(i), 
                            new SingleField(homeAddressParts.get(i), inputData.get(mailingAddressPart + "_validated").toString(), null));
                }
            }
        }

        return results;
    }
}
