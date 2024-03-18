package org.mdbenefits.app.preparers;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.mdbenefits.app.data.enums.AdditionalIncomeType;
import org.springframework.stereotype.Component;

@Component
public class IncomeDetailsPreparer implements SubmissionFieldPreparer {

    private final MessageSource messagesSource;

    IncomeDetailsPreparer(MessageSource messageSource) {
        this.messagesSource = messageSource;
    }

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();

        results.putAll(prepareIncome(submission));
        results.putAll(prepareAdditionIncome(submission));
        return results;
    }

    private Map<String, SubmissionField> prepareIncome(Submission submission) {
        Map<String, SubmissionField> fields = new HashMap<>();
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
                fields.put("employeeName" + i, new SingleField("employeeName", (String) employeeName, iterator));
                fields.put("employerName" + i, new SingleField("employerName", (String) employerName, iterator));
                fields.put("employmentPayFreq" + i,
                        new SingleField("employmentPayFreq", (String) payPeriod, iterator));
                fields.put("employeeHoursPerWeek" + i,
                        new SingleField("employeeHoursPerWeek", (String) hoursPerWeek, iterator));
                fields.put("employeeHourlyWage" + i,
                        new SingleField("employeeHourlyWage", (String) hourlyWage, iterator));
                iterator++;

            }
        }
        return fields;
    }

    private Map<String, SubmissionField> prepareAdditionIncome(Submission submission) {
        Map<String, SubmissionField> fields = new HashMap<>();
        List<String> additionalIncomeTypes = (List) submission.getInputData().getOrDefault("additionalIncome[]", List.of());
        int row = 1;

        if (additionalIncomeTypes.isEmpty() ||
                (additionalIncomeTypes.size() == 1 && additionalIncomeTypes.contains("NONE"))) {
            return fields;
        }

        for (String typeName : additionalIncomeTypes) {
            writeAdditionalIncomeFields(submission, AdditionalIncomeType.getEnumByName(typeName), fields, row);
            row++;
        }

        return fields;
    }

    private void writeAdditionalIncomeFields(Submission submission, AdditionalIncomeType type,
            Map<String, SubmissionField> addFields,
            int rowNum) {
        addFields.put("additionalIncomeTypeOfBenefitRow" + rowNum,
                new SingleField(
                        "additionalIncomeTypeOfBenefitRow" + rowNum,
                        messagesSource.getMessage(type.getLabelSrc(), null, Locale.ENGLISH),
                        null
                ));
        addFields.put("additionalIncomeAmountRow" + rowNum,
                new SingleField(
                        "additionalIncomeAmountRow" + rowNum,
                        "$" + submission.getInputData().get(type.getInputFieldName()),
                        null
                ));
        addFields.put("additionalIncomeReceivedRow" + rowNum,
                new SingleField(
                        "additionalIncomeReceivedRow" + rowNum,
                        "Yes",
                        null
                ));
    }
}
