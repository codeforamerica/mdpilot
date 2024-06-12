package org.mdbenefits.app.preparers;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.data.enums.AdditionalIncomeType;
import org.mdbenefits.app.data.enums.MoneyOnHandType;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.mdbenefits.app.utils.SubmissionUtilities.isNoneOfAboveSelection;

@Slf4j
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
        results.putAll(prepareMoneyOnHandResources(submission));
        results.putAll(prepareHasMoreThanHundredOnHand(submission));
        return results;
    }

    private Map<String, SubmissionField> prepareIncome(Submission submission) {
        Map<String, SubmissionField> fields = new HashMap<>();
        Map<String, Object> inputData = submission.getInputData();
        var income = (List<Map<String, Object>>) inputData.get("income");
        boolean hasIncome = false;

        if (income != null) {
            int i = 1;
            for (Map<String, Object> incomeDetails : income) {
                boolean iterationIsComplete = (boolean) incomeDetails.get(Submission.ITERATION_IS_COMPLETE_KEY);
                if (!iterationIsComplete) {
                    log.info("PDF Income Preparer: Submission {}: found incomplete income iteration ({}), skipping it...",
                            submission.getId(), incomeDetails.get("uuid"));
                    continue;
                }
                hasIncome = true;
                String employeeName = (String) incomeDetails.get("householdMemberJobAdd");
                if (employeeName.equals("you")) {
                    employeeName = inputData.get("firstName") + " " + inputData.get("lastName");
                }

                String employerName = (String) incomeDetails.get("employerName");
                String payFrequency = (String) incomeDetails.get("payPeriod");
                String payAmountFor30Days = (String) incomeDetails.get("payAmountFor30Days");
                String moneyString = String.format("$%,.0f", Double.valueOf(payAmountFor30Days));

                fields.put("employeeName" + i,
                        new SingleField("employeeName" + i, employeeName, null));
                fields.put("employerName" + i,
                        new SingleField("employerName" + i, employerName, null));
                fields.put("employmentPayPerPeriod" + i,
                        new SingleField("employmentPayPerPeriod" + i, moneyString, null));
                fields.put("employmentPayFreq" + i,
                        new SingleField("employmentPayFreq" + i, preparePayPeriod(payFrequency), null));

                i++;
            }
        }

        fields.put(
                "householdHasEarnedIncome",
                new SingleField("householdHasEarnedIncome", hasIncome ? "true" : "false", null));

        return fields;
    }

    private String getPayPeriodMessage(String subtype) {
        return messagesSource.getMessage("job-pay-period." + subtype, null, Locale.ENGLISH);
    }

    private String preparePayPeriod(String value) {
        if (value.equals(getPayPeriodMessage("every-week"))) {
            return "weekly";
        } else if (value.equals(getPayPeriodMessage("every-two-weeks"))) {
            return "biweekly";
        } else if (value.equals(getPayPeriodMessage("twice-a-month"))) {
            return "semimonthly";
        } else if (value.equals(getPayPeriodMessage("every-month"))) {
            return "monthly";
        } else if (value.equals(getPayPeriodMessage("it-varies"))) {
            return "varies";
        } else {
            throw new AssertionError("Unrecognized pay period value: " + value);
        }
    }

    private Map<String, SubmissionField> prepareAdditionIncome(Submission submission) {
        Map<String, SubmissionField> fields = new HashMap<>();
        List<String> additionalIncomeTypes = (List) submission.getInputData().getOrDefault("additionalIncome[]", List.of());
        int row = 1;

        if (isNoneOfAboveSelection(additionalIncomeTypes)) {
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
                        (String) submission.getInputData().getOrDefault(type.getInputFieldName(), ""),
                        null
                ));
        addFields.put("additionalIncomeReceivedRow" + rowNum,
                new SingleField(
                        "additionalIncomeReceivedRow" + rowNum,
                        "Yes",
                        null
                ));
    }

    private Map<String, SubmissionField> prepareMoneyOnHandResources(Submission submission) {
        Map<String, SubmissionField> fields = new HashMap<>();
        var moneyOnHandSelected = (List<String>) submission.getInputData().getOrDefault("moneyOnHandTypes[]", emptyList());

        if (moneyOnHandSelected.isEmpty()) {
            return fields;
        }
        if (isNoneOfAboveSelection(moneyOnHandSelected)) {
            fields.put("householdHasResourcesOrAssets", new SingleField("householdHasResourcesOrAssets", "false", null
            ));
            return fields;
        } else {
            int i = 1;
            for (var type : MoneyOnHandType.values()) {
                if (moneyOnHandSelected.contains(type.name())) {
                    fields.put("householdHasResourcesOrAssets", new SingleField("householdHasResourcesOrAssets", "true", null
                    ));
                    fields.put("resourcesOrAssetsType" + i,
                            new SingleField("resourcesOrAssetsType" + i, messagesSource.getMessage(
                                    type.getLabelSrc(), null, Locale.ENGLISH), null));
                    i++;
                }
            }
        }
        return fields;
    }
    
    private Map<String, SubmissionField> prepareHasMoreThanHundredOnHand(Submission submission) {
        Map<String, SubmissionField> fields = new HashMap<>();
        if (submission.getInputData().containsKey("expeditedMoneyOnHandAmount")) {
            fields.put("householdHasResourcesOrAssets", new SingleField("householdHasResourcesOrAssets", "true", null));
        }
        return fields;
    }
}
