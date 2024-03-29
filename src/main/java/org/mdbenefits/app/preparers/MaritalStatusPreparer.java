package org.mdbenefits.app.preparers;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.Collections;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MaritalStatusPreparer implements SubmissionFieldPreparer {

    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        var maritalStatus = (String) submission.getInputData().getOrDefault("maritalStatus", "");
        if (maritalStatus.startsWith("Married")) {
            return Map.of("maritalStatus", new SingleField("maritalStatus", "Married", null));
        }
        return Collections.emptyMap();
    }
}
