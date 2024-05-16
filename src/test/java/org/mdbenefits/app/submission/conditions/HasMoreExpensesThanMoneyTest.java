package org.mdbenefits.app.submission.conditions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.enums.HomeExpensesType;

public class HasMoreExpensesThanMoneyTest {

    HasMoreExpensesThanMoney hasMoreExpensesThanMoney = new HasMoreExpensesThanMoney();

    @Test
    void hasMoreExpensesThanMoney() {
        Submission submission = new Submission();
        Map<String, Object> inputData = new HashMap<>();
        List<String> expenses = new ArrayList<>();
        List<Map<String, Object>> income = new ArrayList<>();

        expenses.add(HomeExpensesType.RENT.name());
        expenses.add(HomeExpensesType.PHONE.name());
        expenses.add(HomeExpensesType.ELECTRICITY.name());
        expenses.add(HomeExpensesType.OIL.name());

        inputData.put("householdHomeExpenses[]", expenses);
        inputData.put(HomeExpensesType.RENT.getInputFieldName(), "1200.5");
        inputData.put(HomeExpensesType.PHONE.getInputFieldName(), "100");
        inputData.put(HomeExpensesType.ELECTRICITY.getInputFieldName(), "50");
        inputData.put(HomeExpensesType.OIL.getInputFieldName(), "200");

        Map<String, Object> job1 = new HashMap<>();
        job1.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        job1.put("employerName", "ACME Inc");
        job1.put("payPeriod", "It varies");
        job1.put("payPeriodAmount", 400.0);

        Map<String, Object> job2 = new HashMap<>();
        job2.put(Submission.ITERATION_IS_COMPLETE_KEY, false);
        job2.put("employerName", "Monsters Inc");
        job2.put("payPeriodAmount", 200.0);

        income.add(job1);
        income.add(job2);
        inputData.put("income", income);

        submission.setInputData(inputData);

        assertTrue(hasMoreExpensesThanMoney.run(submission));
    }

    @Test
    void hasMoreMoneyThanExpenses() {
        Submission submission = new Submission();
        Map<String, Object> inputData = new HashMap<>();
        List<String> expenses = new ArrayList<>();
        List<Map<String, Object>> income = new ArrayList<>();

        expenses.add(HomeExpensesType.RENT.name());
        expenses.add(HomeExpensesType.PHONE.name());
        expenses.add(HomeExpensesType.ELECTRICITY.name());
        expenses.add(HomeExpensesType.OIL.name());

        inputData.put("householdHomeExpenses[]", expenses);
        inputData.put(HomeExpensesType.RENT.getInputFieldName(), "1200.5");
        inputData.put(HomeExpensesType.PHONE.getInputFieldName(), "100");
        inputData.put(HomeExpensesType.ELECTRICITY.getInputFieldName(), "50");
        inputData.put(HomeExpensesType.OIL.getInputFieldName(), "200");

        Map<String, Object> job1 = new HashMap<>();
        job1.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        job1.put("employerName", "ACME Inc");
        job1.put("payPeriod", "It varies");
        job1.put("payPeriodAmount", 400.0);

        Map<String, Object> job2 = new HashMap<>();
        job2.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        job2.put("employerName", "Monsters Inc");
        job1.put("payPeriod", "It varies");
        job2.put("payPeriodAmount", 1500.0);

        income.add(job1);
        income.add(job2);
        inputData.put("income", income);

        submission.setInputData(inputData);

        assertFalse(hasMoreExpensesThanMoney.run(submission));
    }
}
