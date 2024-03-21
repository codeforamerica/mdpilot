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
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HouseholdExpensesPreparer implements SubmissionFieldPreparer {

    private static final Map<String, String> EXPENSES = new HashMap<>();

    static {
        EXPENSES.put("householdHomeExpenseAmount_wildcard_rent", "Rent");
        EXPENSES.put("householdHomeExpenseAmount_wildcard_mortgage", "Mortgage");
        EXPENSES.put("householdHomeExpenseAmount_wildcard_homeownerInsurance", "Homeowner's Insurance");
        EXPENSES.put("householdHomeExpenseAmount_wildcard_propertyTax", "Property Tax");
        EXPENSES.put("householdHomeExpenseAmount_wildcard_condominiumFees", "Condo Fees");
        EXPENSES.put("householdHomeExpenseAmount_wildcard_lotRent", "Lot Rental");
        EXPENSES.put("householdHomeExpenseAmount_wildcard_floodInsurance", "Flood Insurance");
    }

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();

        var householdExpenses = (List) submission.getInputData().getOrDefault("householdHomeExpenses[]", emptyList());
        if (!householdExpenses.isEmpty()) {
            List<String> sortedExpenses = EXPENSES.keySet().stream().sorted().toList();
            int i = 1;
            for (String expense : sortedExpenses) {
                var expenseInput = submission.getInputData().get(expense);
                if (expenseInput != null) {
                    results.put("householdExpensesType" + i, new SingleField("householdExpensesType", EXPENSES.get(expense), i));
                    results.put("householdExpensesAmount" + i,
                            new SingleField("householdExpensesAmount", (String) expenseInput, i));
                    results.put("householdExpensesFreq" + i, new SingleField("householdExpensesFreq", "Monthly", i));
                    i++;
                }
            }

            // Check for "Other" - theres only one spot for this in the PDF so just combine them
            var totalOtherAmount = 0.0;
            try {
                var expensesOther = submission.getInputData().get("householdHomeExpenseAmount_wildcard_otherHomeExpenses");
                if (expensesOther != null) {
                    totalOtherAmount += Double.parseDouble((String) expensesOther);
                }
            } catch (NumberFormatException e) {
                // We should have validations in place for this already
                log.warn("Could not parse amount", e);
            }

            if (totalOtherAmount > 0) {
                results.put("Other", new SingleField("Other", "Yes", null));

                results.put("householdExpensesType_" + i, new SingleField("householdExpensesType_" + i, "Other", null));
                results.put("householdExpensesAmount_" + i,
                        new SingleField("householdExpensesAmount_" + i, Double.toString(totalOtherAmount), null));
                results.put("householdExpensesFreq_" + i, new SingleField("householdExpensesFreq_" + i, "Monthly", null));
            }
        }

        return results;
    }
}
