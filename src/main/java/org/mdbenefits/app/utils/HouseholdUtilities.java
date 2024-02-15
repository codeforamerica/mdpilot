package org.mdbenefits.app.utils;

import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.mdbenefits.app.data.enums.CitizenshipStatus;

public class HouseholdUtilities {

    public static boolean isMember18orOlder(int day, int month, int year) throws NumberFormatException {

        if (day <= 0 || month <= 0 || year <= 0) {
            throw new NumberFormatException("cannot analyze birthdate as fields are missing");
        }

        Calendar memberBirthDayCal = Calendar.getInstance();
        memberBirthDayCal.set(Calendar.YEAR, year);
        memberBirthDayCal.set(Calendar.MONTH, month);
        memberBirthDayCal.set(Calendar.DAY_OF_MONTH, day);

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        cal.add(Calendar.YEAR, -18);

        // these are converted to milliseconds since Epoch and then compared.
        // if the memberBirthDayCal is < or == the cal, then they are 18+ years old.
        return memberBirthDayCal.compareTo(cal) <= 0;
    }

    public static List<Map<String, Object>> formattedHouseholdData(Submission submission, String key) {
        Map<String, Object> inputData = submission.getInputData();

        List<Map<String, Object>> householdMembers = (List) inputData.get("household");
        List<String> hasPersonalSituations = (List) inputData.get(key);
        List<Map<String, Object>> householdDataObject = new ArrayList<>();

        hasPersonalSituations.forEach((String id) -> {
                    Map<String, Object> user = new LinkedHashMap<>();
                    if (id.equals("you")) {
                        user.put("uuid", id);
                        user.put("firstName", inputData.get("firstName"));

                        householdDataObject.add(user);
                    } else {
                        householdDataObject.add(householdData(householdMembers, id));
                    }
                }
        );

        return householdDataObject;
    }

    /**
     * Returns a household member's CitizenStatusType enum name as a string. This will check to see if the applicant indicated
     * whether the person is actually applying. If they are not applying for benefits, then the value will default to
     * NOT_APPLYING, unless the client overrides it.
     *
     * @param memberData A hash map of data about the household member.
     * @return String value of CitizenStatusType enum associated with their citizenship status
     */
    public static String householdCitizenshipStatus(Map<String, Object> memberData) {
        String status = (String) memberData.getOrDefault("householdMemberCitizenshipStatus", "");
        String applying = (String) memberData.getOrDefault("householdMemberApplyingForBenefits", "no");

        if (status.isBlank() && applying.equalsIgnoreCase("no")) {
            return CitizenshipStatus.NOT_APPLYING.name();
        }
        return status;
    }

    private static Map<String, Object> householdData(List<Map<String, Object>> household, String uuid) {
        Map<String, Object> user = new LinkedHashMap<>();
        for (Map<String, Object> hhmember : household) {
            if (hhmember.get("uuid").equals(uuid)) {
                user.put("uuid", uuid);
                user.put("firstName", hhmember.get("householdMemberFirstName"));
                break;
            }
        }

        return user;
    }
}
