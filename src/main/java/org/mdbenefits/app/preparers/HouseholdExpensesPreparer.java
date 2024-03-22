package org.mdbenefits.app.preparers;

import static java.util.Collections.emptyList;

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

        return results;
    }
}