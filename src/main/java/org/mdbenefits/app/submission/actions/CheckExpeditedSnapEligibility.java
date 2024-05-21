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
import org.mdbenefits.app.utils.ExpenseCalculator;
import org.mdbenefits.app.utils.IncomeCalculator;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CheckExpeditedSnapEligibility implements Action {

    @Override
    public void run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();
        boolean isApplyingForExpeditedSnap = Boolean.parseBoolean(
                inputData.getOrDefault("isApplyingForExpeditedSnap", "false").toString());

        ExpenseCalculator expenseCalculator = new ExpenseCalculator(submission);

        BigDecimal moneyOnHandAmount = convertToBigDecimal(
                inputData.getOrDefault("expeditedMoneyOnHandAmount", "0").toString());
        BigDecimal householdIncomeAmount = convertToBigDecimal(
                inputData.getOrDefault("householdIncomeLast30Days", "0").toString());
        if (!isApplyingForExpeditedSnap) {
            // they are in the main flow, so we need to use the income they entered there.
            IncomeCalculator incomeCalculator = new IncomeCalculator(submission);
            householdIncomeAmount = BigDecimal.valueOf(incomeCalculator.totalFutureEarnedIncome())
                    .setScale(2, RoundingMode.HALF_UP);
        }

        boolean isMigrantOrSeasonalFarmWorker = inputData.getOrDefault("migrantOrSeasonalFarmWorkerInd", "false").toString()
                .equals("true");
        boolean isBelowMoneyOnHandThreshhold = inputData.getOrDefault("householdMoneyOnHandLessThan100", "false").toString()
                .equals("true");
        boolean isBelowIncomeThreshhold = inputData.getOrDefault("incomeLessThan150", "false").toString().equals("true");

        boolean isEligibleByIncomeAndCashOnHandLessThanExpenses =
                householdIncomeAmount.add(moneyOnHandAmount).compareTo(expenseCalculator.totalUtilitiesExpenses()) <= 0;

        boolean isEligibleForExpeditedSnap =
                (isBelowMoneyOnHandThreshhold && isBelowIncomeThreshhold) ||
                        (isMigrantOrSeasonalFarmWorker && isBelowMoneyOnHandThreshhold) ||
                        (expenseCalculator.totalUtilitiesExpenses().compareTo(BigDecimal.ZERO) > 0) &&
                                isEligibleByIncomeAndCashOnHandLessThanExpenses;

        submission.getInputData().put("isEligibleForExpeditedSnap", String.valueOf(isEligibleForExpeditedSnap));
    }

    private BigDecimal convertToBigDecimal(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }
}
