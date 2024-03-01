package org.mdbenefits.app.preparers;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mdbenefits.app.utils.IncomeCalculator;
import org.springframework.stereotype.Component;

@Component
public class IncomeDetailsPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();

        var income = (List<Map<String, Object>>) submission.getInputData().get("income");
        if (income != null) {
            int iterator = 1;
            for (int i = 0; i < income.size(); i++) {
                Map<String, Object> incomeDetails = income.get(i);

                var employeeName = incomeDetails.get("householdMemberJobAdd");
                var employerName = incomeDetails.get("employerName");
                var hoursPerWeek = incomeDetails.get("hoursPerWeek");

                var payPeriod = incomeDetails.get("payPeriod");
                var hourlyWage = incomeDetails.get("hourlyWage");
                results.put("employeeName" + i, new SingleField("employeeName", (String) employeeName, iterator));
                results.put("employerName" + i, new SingleField("employerName", (String) employerName, iterator));
                results.put("employmentPayFreq" + i,
                    new SingleField("employmentPayFreq", (String) payPeriod, iterator));
                results.put("employeeHoursPerWeek" + i,
                    new SingleField("employeeHoursPerWeek", (String) hoursPerWeek, iterator));
                results.put("employeeHourlyWage" + i,
                    new SingleField("employeeHourlyWage", (String) hourlyWage, iterator));
                iterator++;

            }
        }
        return results;
    }
}
