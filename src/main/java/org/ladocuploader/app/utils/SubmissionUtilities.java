package org.ladocuploader.app.utils;

import formflow.library.data.Submission;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SubmissionUtilities {

  public static DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
  public static final String APPLICANT = "applicant";
  public static final String REPORTED_TOTAL_ANNUAL_HOUSEHOLD_INCOME = "reportedTotalAnnualHouseholdIncome";
  public static final String HOUSEHOLD_MEMBER = "householdMember";
  public static final String HOUSEHOLD = "household";
  public static final String INCOME = "income";
  public static final String ITERATION_UUID = "uuid";

  public static List<Map<String, Object>> sortIncomeNamesWithApplicantFirst(Submission submission) {
    Map<String, Object> inputData = submission.getInputData();
    if (inputData.containsKey(INCOME)) {
      List<Map<String, Object>> subflow =
          (List<Map<String, Object>>) submission.getInputData().get(INCOME);
      Stream<Map<String, Object>> applicantEntry = subflow.stream()
          .filter(entry -> entry.get(HOUSEHOLD_MEMBER).equals(APPLICANT));
      Stream<Map<String, Object>> nonApplicantEntries = subflow.stream()
          .filter(entry -> !entry.get(HOUSEHOLD_MEMBER).equals(APPLICANT));
      return Stream.concat(applicantEntry, nonApplicantEntries).toList();
    }

    return null;
  }

  /**
   * Returns the total income for a specific individual (iteration) identified by uuid parameter.
   *
   * @param submission submission containing input data to use
   * @param uuid       UUID of the iteration to pull the data from
   * @return a String containing an individuals total income.
   */
  public static String getIndividualsTotalIncome(Submission submission, String uuid) {
    DecimalFormat df = new DecimalFormat("0.00");

    if (submission.getInputData().containsKey(INCOME)) {
      List<Map<String, Object>> incomeSubflow =
          (List<Map<String, Object>>) submission.getInputData().get(INCOME);
      Map<String, Object> individualsIncomeEntry =
          incomeSubflow.stream()
              .filter(entry -> entry.get(ITERATION_UUID)
                  .equals(uuid))
              .toList()
              .get(0);
      List<String> incomeTypes = (List<String>) individualsIncomeEntry.get("incomeTypes[]");
      List<BigDecimal> incomeTypeAmounts = incomeTypes.stream()
          .map(type -> new BigDecimal((String) individualsIncomeEntry.get(type + "Amount")))
          .toList();
      return df.format(incomeTypeAmounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    return null;
  }

  /**
   * Returns the value of income for the whole family as a Double value. If the user has set the
   * {@link SubmissionUtilities#REPORTED_TOTAL_ANNUAL_HOUSEHOLD_INCOME} value that will be returned to the user instead of the
   * total of the individual income entries.
   *
   * @param submission submission containing input data to use
   * @return total income amount as a Double
   */
  public static Double getHouseholdTotalIncomeValue(Submission submission) {
    Double total = 0.0;

    // if they entered it by hand that takes precedence over individual totals.
    if (submission.getInputData().containsKey(REPORTED_TOTAL_ANNUAL_HOUSEHOLD_INCOME)) {
      total = Double.valueOf((String) submission.getInputData().get(REPORTED_TOTAL_ANNUAL_HOUSEHOLD_INCOME));
    } else if (submission.getInputData().containsKey(INCOME)) {
      List<Map<String, Object>> incomeSubflow =
          (List<Map<String, Object>>) submission.getInputData().get(INCOME);
      List<Double> amounts = new ArrayList<>();
      incomeSubflow.forEach(iteration -> {
        iteration.forEach((key, value) -> {
          if (key.contains("Amount")) {
            amounts.add(Double.valueOf((String) value));
          }
        });
      });
      total = amounts.stream().reduce(0.0, (subtotal, element) -> subtotal + element);
    }

    return total;
  }

  /**
   * Returns the value of income for the whole family as a String value. If the user has set the
   * {@link SubmissionUtilities#REPORTED_TOTAL_ANNUAL_HOUSEHOLD_INCOME} value that will be returned to the user instead of the
   * total of the individual income entries. The number 12000 will be returned formatted like so: 12,000.00
   *
   * @param submission submission containing input data to use
   * @return total income amount as a formatted numerical value
   */
  public static String getHouseholdTotalIncome(Submission submission) {
    return decimalFormat.format(getHouseholdTotalIncomeValue(submission));
  }

  /**
   * Returns the number of members in a family, including the applicant.
   *
   * @param submission submission containing input data to use
   * @return String containing family size
   */
  public static String getFamilySize(Submission submission) {
    //Add all household member and the applicant to get total family size
    int familySize = 1;
    if (submission.getInputData().get(HOUSEHOLD) != null) {
      var household = (List<LinkedHashMap<String, String>>) submission.getInputData().get(HOUSEHOLD);
      familySize = household.size() + familySize;
    }
    return (Integer.toString(familySize));
  }
}
