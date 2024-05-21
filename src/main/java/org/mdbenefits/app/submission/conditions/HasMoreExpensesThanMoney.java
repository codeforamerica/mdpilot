package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.utils.ExpenseCalculator;
import org.mdbenefits.app.utils.IncomeCalculator;
import org.springframework.stereotype.Component;

/**
 * Checks to see if there are more expense than income and liquid assets.  Returns true if there are more expenses than income.
 */

@Slf4j
@Component
public class HasMoreExpensesThanMoney implements Condition {

    @Override
    public Boolean run(Submission submission) {
        IncomeCalculator incomeCalculator = new IncomeCalculator(submission);
        ExpenseCalculator expenseCalculator = new ExpenseCalculator(submission);
        String moneyOnHandStr = (String) submission.getInputData().get("expeditedMoneyOnHandAmount");
        double moneyOnHand = 0.0;

        if (moneyOnHandStr != null) {
            try {
                moneyOnHand = Double.parseDouble(moneyOnHandStr);
            } catch (NumberFormatException e) {
                log.warn("Unable to parse `expeditedMoneyOnHandAmount`: `{}`. Not using money on hand in calculations.",
                        moneyOnHandStr);
            }
        }

        return expenseCalculator.totalUtilitiesExpenses()
                .compareTo(BigDecimal.valueOf(incomeCalculator.totalFutureEarnedIncome() + moneyOnHand)) > 0;
    }
}
