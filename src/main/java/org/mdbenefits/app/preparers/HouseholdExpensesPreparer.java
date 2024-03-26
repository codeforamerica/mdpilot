package org.mdbenefits.app.preparers;

import static java.util.Collections.emptyList;
import static org.mdbenefits.app.utils.SubmissionUtilities.isNoneOfAboveSelection;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.data.enums.HomeExpensesType;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HouseholdExpensesPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();

        var householdExpenses = (List<String>) submission.getInputData().getOrDefault("householdHomeExpenses[]", emptyList());
        if (!householdExpenses.isEmpty()) {
            for (String expense : householdExpenses) {
                var householdExpenseEnum = HomeExpensesType.getEnumByName(expense);
                if (householdExpenseEnum != null) {
                    String frequencyFieldName = householdExpenseEnum.getInputFieldName() + "Frequency";
                    results.put(frequencyFieldName, new SingleField(frequencyFieldName, "Monthly", null));
                }
            }
        }

        var section8 = (List<String>) submission.getInputData().getOrDefault("expenseSection8Housing[]", emptyList());
        if(!section8.isEmpty()){
            results.put("expenseSection8Housing", new SingleField("expenseSection8Housing",String.valueOf(section8.contains("true")), null));
        }

        var expenseIncludesHeat = (List<String>) submission.getInputData().getOrDefault("expenseHeatIncludedRent[]", emptyList());
        if(!expenseIncludesHeat.isEmpty()){
            results.put("expenseHeatIncludedRent", new SingleField("expenseHeatIncludedRent",String.valueOf(expenseIncludesHeat.contains("true")), null));
        }

        var hasMedicalExpenses = (List<String>) submission.getInputData().getOrDefault("medicalExpenses[]", emptyList());
        var hasMedicalExpensesBool = !hasMedicalExpenses.isEmpty() && !isNoneOfAboveSelection(hasMedicalExpenses);

        results.put("hasMedicalExpenses", new SingleField("hasMedicalExpenses",String.valueOf(hasMedicalExpensesBool), null));


        var hasDependentCareExpenses = (String) submission.getInputData().getOrDefault("hasDependentCareExpenses", "");

        if(!hasDependentCareExpenses.isEmpty()){
            if(hasDependentCareExpenses.equals("true")){
                results.put("hasDependentCareExpenses", new SingleField("hasDependentCareExpenses", "household", null));

                var dependentCareExpensesAmount = (String) submission.getInputData().getOrDefault("expensesDependentCare", "");
                results.put("expensesDependentCare", new SingleField("expensesDependentCare", dependentCareExpensesAmount + " /month", null));

            }
        }


        return results;
    }
}