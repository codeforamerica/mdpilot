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
import org.mdbenefits.app.data.enums.RelationshipType;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HouseholdDetailsPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();

        var household = (List<Map<String, Object>>) submission.getInputData().get("household");

        if (household != null) {
            int iteration = 1;  // start at one!
            for (Map<String, Object> householdMember : household) {
                boolean iterationIsComplete = (boolean) householdMember.get("iterationIsComplete");
                if (!iterationIsComplete) {
                    log.info(
                            "PDF Household Preparer: Submission {}: found incomplete household member iteration ({}), skipping...",
                            submission.getId(), householdMember.get("uuid"));
                    continue;
                }
                String fullName = removeTrailingCommaAndSpace(String.format("%s, %s, %s",
                        householdMember.get("householdMemberLastName"),
                        householdMember.get("householdMemberFirstName"),
                        householdMember.getOrDefault("householdMemberMiddleName", "")));
                results.put("householdMemberFullName_" + iteration, new SingleField("householdMemberFullName",
                        fullName, iteration));

                String applyingStatus = (String) householdMember.get("householdMemberApplyingForBenefits");

                String householdMemberRelationship = RelationshipType.getPDFValueFromValue(
                        householdMember.get("householdMemberRelationship").toString());
                results.put("householdMemberRelationship_" + iteration,
                        new SingleField("householdMemberRelationship", householdMemberRelationship, iteration));

                if (applyingStatus.equalsIgnoreCase("yes")) {
                    String citizen = "Yes";

                    String citizenshipStatus = (String) householdMember.get("householdMemberCitizenshipStatus");
                    if (!citizenshipStatus.equals(CitizenshipStatus.US_CITIZEN.name())) {
                        citizen = "No";
                    }

                    results.put("householdMemberCitizen_" + iteration,
                            new SingleField("householdMemberCitizen", citizen, iteration));

                    var dob = Stream.of("householdMemberBirthMonth", "householdMemberBirthDay", "householdMemberBirthYear")
                            .map(householdMember::get)
                            .reduce((e, c) -> e + "/" + c)
                            .get();

                    results.put("householdMemberDOB_" + iteration,
                            new SingleField("householdMemberDOB", (String) dob, iteration));

                    String householdMemberSex = householdMember.get("householdMemberSex").toString();

                    if (!householdMemberSex.equalsIgnoreCase("other")) {
                        results.put("householdMemberSex_" + iteration,
                                new SingleField("householdMemberSex", householdMemberSex, iteration));
                    }

                    prepareRaceEthnicityInfo(householdMember, results, iteration);
                }
                iteration++;
            }
        }
        return results;
    }

    /**
     * @param fullName the string to remove the trailing comma and space from. Such as "First, Last, "
     * @return the string with the trailing comma and space removed.
     */
    protected String removeTrailingCommaAndSpace(String fullName) {
        return fullName.replaceAll(", $", "");
    }

    private void prepareRaceEthnicityInfo(Map<String, Object> householdMember, Map<String, SubmissionField> results,
            int iteration) {
        List<String> householdMemberRace = (List) householdMember.getOrDefault("householdMemberRace[]", List.of());
        String householdMemberEthnicity = (String) householdMember.getOrDefault("householdMemberEthnicity", "");

        String raceCode = RaceType.getRaceCodeStringFromValue(householdMemberRace);

        String ethnicityCode = EthnicityType.getPdfValueFromValue(householdMemberEthnicity);

        results.put("householdMemberRace_" + iteration, new SingleField("householdMemberRace", raceCode, iteration));
        results.put("householdMemberEthnicity_" + iteration,
                new SingleField("householdMemberEthnicity", ethnicityCode, iteration));
    }
}
