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
import org.mdbenefits.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicantDetailsPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();

        Map<String, Object> inputData = submission.getInputData();

        String fullName = String.format("%s, %s", inputData.get("lastName"), inputData.get("firstName"));
        if (inputData.get("middleName") != null) {
            fullName += ", " + inputData.get("middleName");
        }

        results.put("applicantFullName", new SingleField("applicantFullName", (String) fullName, null));

        var dob = Stream.of("birthMonth", "birthDay", "birthYear")
                .map(inputData::get)
                .reduce((e, c) -> e + "/" + c)
                .get();

        String applyingStatus = (String) inputData.get("isApplicantApplying");
        if (applyingStatus.equalsIgnoreCase("yes")) {

            results.put("applicantDOB", new SingleField("applicantDOB", (String) dob, null));

            results.put("speaksEnglish", new SingleField("speaksEnglish", (String) "true", null));

            // unset the applicantSex field if the value is unset or "other"
            if (inputData.getOrDefault("applicantSex", "other").toString().equalsIgnoreCase("other")) {
                results.put("applicantSex", new SingleField("applicantSex", "", null));
            }


            prepareCitizenshipStatus(inputData, results);

            prepareRaceEthnicityInfo(inputData, results);

            results.put("applicantMailingAddressFull",
                    new SingleField("applicantMailingAddressFull", formatMailingAddress(inputData), null));
        }

        return results;
    }

    public String formatMailingAddress(Map<String, Object> inputData) {
        String mailingAddressFull;
        if (inputData.getOrDefault("sameAsHomeAddress", "false").equals("true")) {
            mailingAddressFull = "";
        } else {
            if (inputData.get("mailingAddressStreetAddress2") != null) {
                mailingAddressFull = String.format("%s, %s, %s, %s %s",
                        inputData.get("mailingAddressStreetAddress1"),
                        inputData.get("mailingAddressStreetAddress2"),
                        inputData.get("mailingAddressCity"),
                        inputData.get("mailingAddressState"),
                        inputData.get("mailingAddressZipCode")
                );
            } else {
                mailingAddressFull = String.format("%s, %s, %s %s",
                        inputData.get("mailingAddressStreetAddress1"),
                        inputData.get("mailingAddressCity"),
                        inputData.get("mailingAddressState"),
                        inputData.get("mailingAddressZipCode")
                );

            }
        }
        return mailingAddressFull;
    }

    private void prepareCitizenshipStatus(Map<String, Object> inputData, Map<String, SubmissionField> results) {
        String citizenshipStatus = (String) inputData.getOrDefault("applicantCitizenshipStatus","");

        if (!citizenshipStatus.isBlank()) {
            var status = citizenshipStatus.equals(CitizenshipStatus.US_CITIZEN.name()) ? "Yes" : "No";
            results.put("applicantCitizenshipStatus", new SingleField("applicantCitizenshipStatus", status, null));
        }
    }

    private void prepareRaceEthnicityInfo(Map<String, Object> inputData, Map<String, SubmissionField> results) {
        List<String> race = (List) inputData.getOrDefault("applicantRace[]", List.of());
        String ethnicity = (String) inputData.getOrDefault("applicantEthnicity", "");

        String raceCode = RaceType.getRaceCodeStringFromValue(race);

        String ethnicityCode = EthnicityType.getPdfValueFromValue(ethnicity);

        results.put("applicantRace", new SingleField("applicantRace", raceCode, null));
        results.put("applicantEthnicity", new SingleField("applicantEthnicity", ethnicityCode, null));
    }
}
