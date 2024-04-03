package org.mdbenefits.app.utils;

import static org.junit.jupiter.api.Assertions.*;

import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class IncomeCalculatorTest {

    @Test
    void totalFutureEarnedIncomeShouldFilterIncompleteSubflowIterations() {
        // given
        var submission = new Submission();
        var income = new ArrayList<Map<String, Object>>();
        var job1 = new HashMap<String, Object>();
        job1.put(Submission.ITERATION_IS_COMPLETE_KEY, true);
        job1.put("employerName", "ACME Inc");
        job1.put("payPeriod", "It varies");
        job1.put("payPeriodAmount", 400.0);
        var job2 = new HashMap<String, Object>();
        job2.put(Submission.ITERATION_IS_COMPLETE_KEY, false);
        job2.put("employerName", "Monsters Inc");
        job2.put("payPeriodAmount", 200.0);
        income.add(job1);
        income.add(job2);
        submission.setInputData(Map.of("income", income));
        var calculator = new IncomeCalculator(submission);

        var total = calculator.totalFutureEarnedIncome();

        // Amount will exclude amount from incomplete iteration
        assertEquals(400.0, total);
    }
}