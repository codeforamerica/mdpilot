package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.math.BigDecimal;
import org.mdbenefits.app.utils.ExpenseCalculator;
import org.mdbenefits.app.utils.IncomeCalculator;
import org.springframework.stereotype.Component;

/**
 * Checks to see if there are more expense than income and liquid assets.  Returns true if there are more expenses than income.
 */

@Component
public class HasMoreExpensesThanMoney implements Condition {

    @Override
    public Boolean run(Submission submission) {
        IncomeCalculator incomeCalculator = new IncomeCalculator(submission);
        ExpenseCalculator expenseCalculator = new ExpenseCalculator(submission);
        Double moneyOnHand = Double.parseDouble(
                submission.getInputData().getOrDefault("expeditedMoneyOnHandAmount", "0.0").toString());
        BigDecimal incomeAndMoneyOnHand = BigDecimal.valueOf(incomeCalculator.totalFutureEarnedIncome() + moneyOnHand);

        return incomeAndMoneyOnHand.compareTo(expenseCalculator.totalUtilitiesExpenses()) < 0;
    }
}
