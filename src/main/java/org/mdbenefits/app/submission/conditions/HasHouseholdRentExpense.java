package org.mdbenefits.app.submission.conditions;

import formflow.library.data.Submission;
import formflow.library.config.submission.Condition;
import java.util.ArrayList;
import org.mdbenefits.app.data.enums.HomeExpensesType;
import org.springframework.stereotype.Component;

@Component
public class HasHouseholdRentExpense implements Condition {

    @Override
    public Boolean run(Submission submission) {
        var inputData = submission.getInputData();
        if (inputData.containsKey("householdHomeExpenses[]")) {
            var expenses = (ArrayList<String>) submission.getInputData().get("householdHomeExpenses[]");
            return expenses.contains(HomeExpensesType.RENT.name());
        }
        return false;
    }
}
