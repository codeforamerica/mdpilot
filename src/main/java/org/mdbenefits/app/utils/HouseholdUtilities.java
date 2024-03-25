package org.mdbenefits.app.utils;

import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

import org.mdbenefits.app.data.enums.CitizenshipStatus;

public class HouseholdUtilities {

    /**
     * Checks the birthdate against the current calendar date (now) to see if the birthdate is 60 years or older.
     *
     * @param day   day of Birth
     * @param month month of Birth
     * @param year  year of Birth
     * @return boolean - true if member is 60 or older, otherwise false
     * @throws NumberFormatException throws this if the day, month or year passed in are invalid
     */
    public static boolean isMember60orOlder(int year, int month, int day) throws NumberFormatException {
        return isMember60orOlder(year, month, day, Calendar.getInstance());
    }

    /**
     * Checks the birthdate against the calendar date passed in to see if the birthdate is 60 years old or older.
     *
     * @param day      day of Birth
     * @param month    month of Birth
     * @param year     year of Birth
     * @param calendar calendar to compare the birthdate to
     * @return boolean - true if member is 60 or older, otherwise false
     * @throws NumberFormatException throws this if the day, month or year passed in are invalid
     */
    public static boolean isMember60orOlder(int year, int month, int day, Calendar calendar) throws NumberFormatException {

        if (day <= 0 || month <= 0 || year <= 0) {
            throw new NumberFormatException("cannot analyze birthdate as fields are missing");
        }

        LocalDate currentDate = LocalDate.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
        LocalDate memberBirthDate = LocalDate.of(year, month, day);
        LocalDate sixtiethBirthday = memberBirthDate.plusYears(60);

        return sixtiethBirthday.isBefore(currentDate) || sixtiethBirthday.isEqual(currentDate);
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
