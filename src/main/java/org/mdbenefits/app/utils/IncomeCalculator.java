package org.mdbenefits.app.utils;

import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IncomeCalculator {

    Submission submission;

    public IncomeCalculator(Submission submission) {
        this.submission = submission;
    }

    public Double totalFutureEarnedIncome() {
        var jobs = (List<Map<String, Object>>) submission.getInputData()
                .getOrDefault("income", new ArrayList<Map<String, Object>>());
        var completedJobs = jobs.stream().filter(job -> job.get(Submission.ITERATION_IS_COMPLETE_KEY).equals(true)).toList();

        return completedJobs.stream()
                .map(job -> Double.parseDouble(job.getOrDefault("payAmountFor30Days", "0").toString()))
                .reduce(0.0d, Double::sum);
    }
}
