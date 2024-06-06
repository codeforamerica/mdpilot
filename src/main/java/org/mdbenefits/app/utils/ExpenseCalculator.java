package org.mdbenefits.app.utils;

import static org.mdbenefits.app.utils.SubmissionUtilities.isNoneOfAboveSelection;

import formflow.library.data.Submission;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.data.enums.HomeExpensesType;

@Slf4j
public class ExpenseCalculator {

    Submission submission;

    public ExpenseCalculator(Submission submission) {
        this.submission = submission;
    }

    public BigDecimal totalUtilitiesExpenses() {
        List<BigDecimal> expenseAmounts = new java.util.ArrayList<>();
        List<String> expenses = (List) submission.getInputData().getOrDefault("householdHomeExpenses[]", List.of());
        Map<String, Object> inputData = submission.getInputData();

        expenses.forEach(val -> {
            if (!val.equalsIgnoreCase("None")) {
                String inputFieldName = HomeExpensesType.getEnumByName(val).getInputFieldName();
                // expense values are not required and may be blank
                String expenseValue = inputData.getOrDefault(inputFieldName, "0").toString();
                if (expenseValue.isBlank()) {
                    return;
                }
                expenseAmounts.add(new BigDecimal(expenseValue).setScale(2, RoundingMode.HALF_UP));
            }
        });

        return expenseAmounts.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
