package org.mdbenefits.app.preparers;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.utils.SubmissionUtilities;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicantDetailsPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();

        Map<String, Object> inputData = submission.getInputData();

        String fullName = String.format("%s, %s", inputData.get("lastName"), inputData.get("firstName"));
        results.put("applicantFullName", new SingleField("applicantFullName", (String) fullName, null));

        var dob = Stream.of("birthMonth", "birthDay", "birthYear")
                .map(inputData::get)
                .reduce((e, c) -> e + "/" + c)
                .get();
        results.put("applicantDOB", new SingleField("applicantDOB", (String) dob, null));

        results.put("applicantSSN", new SingleField("applicantSSN", SubmissionUtilities.formatSSN((String) inputData.get("encryptedSSN")), null));
        results.put("speaksEnglish", new SingleField("speaksEnglish", (String) "Yes", null));

        prepareAnswersToSensitiveQuestions(inputData, results);
        return results;
    }

    private static void prepareAnswersToSensitiveQuestions(Map<String, Object> inputData, Map<String, SubmissionField> results) {
        for (String question : SubmissionUtilities.SENSITIVE_CONVICTION_QUESTIONS) {
            String yesField = "noOne" + question;
            String noField = "someone" + question;
            List<String> response = (List<String>) inputData.get(yesField + "[]");
            if (response != null && response.contains("true")) {
                results.put(yesField, new SingleField(yesField, "Yes", null));
            } else {
                results.put(noField, new SingleField(noField, "Yes", null));
            }
        }
    }
}
