package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.math.BigDecimal;

import static org.mdbenefits.app.utils.SubmissionUtilities.isNoneOfAboveSelection;

import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.data.enums.HomeExpensesType;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CheckExpeditedSnapEligibility implements Action {

    BigDecimal INCOME_LIMIT = new BigDecimal("150.00");
    BigDecimal MONEY_ON_HAND_LIMIT = new BigDecimal("100.00");

    private static List<String> utilities = List.of("PHONE", "ELECTRICITY", "WATER", "SEWAGE", "GARBAGE", "GAS", "OIL",
        "WOOD_OR_COAL");

    @Override
    public void run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();
        if (inputData.getOrDefault("isApplyingForExpeditedSnap", "false").toString().equals("true")) {

            BigDecimal moneyOnHandAmount = convertToBigDecimal(
                inputData.getOrDefault("expeditedMoneyOnHandAmount", "0").toString());
            BigDecimal householdIncomeAmount = convertToBigDecimal(
                inputData.getOrDefault("householdIncomeLast30Days", "0").toString());

            boolean isMigrantOrSeasonalFarmWorker = inputData.get("migrantOrSeasonalFarmWorkerInd").toString().equals("true");

            boolean isEligibleByIncomeAndCashOnHandLessThanExpenses =
                householdIncomeAmount.add(moneyOnHandAmount).compareTo(calculateUtilitiesExpenses(inputData)) <= 0;

            boolean isEligibleForExpeditedSnap =
                (moneyOnHandAmount.compareTo(MONEY_ON_HAND_LIMIT) <= 0 && householdIncomeAmount.compareTo(INCOME_LIMIT) <= 0) || (
                    isMigrantOrSeasonalFarmWorker && moneyOnHandAmount.compareTo(MONEY_ON_HAND_LIMIT) <= 0)
                    || isEligibleByIncomeAndCashOnHandLessThanExpenses;
            submission.getInputData().put("isEligibleForExpeditedSnap", String.valueOf(isEligibleForExpeditedSnap));
        }
    }

    private BigDecimal calculateUtilitiesExpenses(Map<String, Object> inputData) {
        List<String> allExpenses = new java.util.ArrayList<>();
        allExpenses.add(inputData.getOrDefault("householdRentAmount", "0").toString());

        if (!isNoneOfAboveSelection(inputData.get("householdHomeExpenses[]"))) {
            List<String> utilityTypes = (List<String>) inputData.get("householdHomeExpenses[]");
            List<String> relevantUtilities = utilities.stream().filter(expense -> utilityTypes.contains(expense)).toList();
            relevantUtilities.forEach(val -> {
                String inputFieldName = HomeExpensesType.getEnumByName(val).getInputFieldName();
                allExpenses.add(inputData.getOrDefault(inputFieldName, "0").toString());
            });
        }
        return allExpenses.stream().map(n -> convertToBigDecimal(n)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal convertToBigDecimal(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }
}
