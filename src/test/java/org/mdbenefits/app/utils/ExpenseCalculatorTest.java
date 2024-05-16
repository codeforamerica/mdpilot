package org.mdbenefits.app.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import formflow.library.data.Submission;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.enums.HomeExpensesType;

class ExpenseCalculatorTest {

    @Test
    void totalMonthlyExpenseCalculation() {
        Submission submission = new Submission();
        List<String> expenses = new ArrayList<String>();
        expenses.add(HomeExpensesType.RENT.name());
        expenses.add(HomeExpensesType.PHONE.name());
        expenses.add(HomeExpensesType.ELECTRICITY.name());
        expenses.add(HomeExpensesType.OIL.name());

        Map<String, Object> inputData = new HashMap<>();
        inputData.put("householdHomeExpenses[]", expenses);
        inputData.put(HomeExpensesType.RENT.getInputFieldName(), "1200.50");
        inputData.put(HomeExpensesType.PHONE.getInputFieldName(), "100");
        inputData.put(HomeExpensesType.ELECTRICITY.getInputFieldName(), "120");
        inputData.put(HomeExpensesType.OIL.getInputFieldName(), "200");

        submission.setInputData(inputData);

        ExpenseCalculator calculator = new ExpenseCalculator(submission);

        var totalExpenses = calculator.totalUtilitiesExpenses();

        assert (totalExpenses.compareTo(new BigDecimal("1620.50")) == 0);
    }

    @Test
    void totalMonthlyExpenseWithExpenseNotIncludedInHomeExpenseList() {
        Submission submission = new Submission();
        List<String> expenses = new ArrayList<String>();
        expenses.add(HomeExpensesType.RENT.name());
        expenses.add(HomeExpensesType.PHONE.name());
        expenses.add(HomeExpensesType.ELECTRICITY.name());
        expenses.add(HomeExpensesType.OIL.name());

        Map<String, Object> inputData = new HashMap<>();
        inputData.put("householdHomeExpenses[]", expenses);
        inputData.put(HomeExpensesType.RENT.getInputFieldName(), "1200.50");
        inputData.put(HomeExpensesType.PHONE.getInputFieldName(), "100");
        inputData.put(HomeExpensesType.ELECTRICITY.getInputFieldName(), "120");
        inputData.put(HomeExpensesType.OIL.getInputFieldName(), "200");
        // should not get counted, as it's not in the expense list
        inputData.put(HomeExpensesType.PROPERTY_TAX.getInputFieldName(), "1200");

        submission.setInputData(inputData);

        ExpenseCalculator calculator = new ExpenseCalculator(submission);

        var totalExpenses = calculator.totalUtilitiesExpenses();

        assert (totalExpenses.compareTo(new BigDecimal("1620.50")) == 0);
    }

    @Test
    void totalMonthlyExpenseZeroValues() {
        Submission submission = new Submission();
        List<String> expenses = new ArrayList<String>();
        expenses.add(HomeExpensesType.RENT.name());
        expenses.add(HomeExpensesType.PHONE.name());
        expenses.add(HomeExpensesType.ELECTRICITY.name());
        expenses.add(HomeExpensesType.OIL.name());

        Map<String, Object> inputData = new HashMap<>();
        inputData.put("householdHomeExpenses[]", expenses);
        inputData.put(HomeExpensesType.RENT.getInputFieldName(), "1200.5");
        inputData.put(HomeExpensesType.PHONE.getInputFieldName(), "0");
        inputData.put(HomeExpensesType.ELECTRICITY.getInputFieldName(), "0.00");
        inputData.put(HomeExpensesType.OIL.getInputFieldName(), "200");

        submission.setInputData(inputData);

        ExpenseCalculator calculator = new ExpenseCalculator(submission);

        var totalExpenses = calculator.totalUtilitiesExpenses();

        assert (totalExpenses.compareTo(new BigDecimal("1400.50")) == 0);
    }
}