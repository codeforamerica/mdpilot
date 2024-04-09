package org.mdbenefits.app.preparers;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import formflow.library.pdf.SubmissionFieldPreparer;
import java.util.List;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ApplicantNeedsPreparer implements SubmissionFieldPreparer {
    @Override
    public Map<String, SubmissionField> prepareSubmissionFields(Submission submission, PdfMap pdfMap) {
        Map<String, SubmissionField> results = new HashMap<>();

        Map<String, Object> inputData = submission.getInputData();

        List<String> programs = (List) inputData.get("programs[]");

        if (programs.isEmpty()) {
            return results;
        }

        results.put("needsSNAP",
            new SingleField("needsSNAP", String.valueOf(programs.contains("SNAP")), null));

        boolean needsCash = programs.contains("TDAP") ||  programs.contains("RCA");
        boolean needsTCA = programs.contains("TCA");

        results.put("needsCashAssistance",
            new SingleField("needsCashAssistance", String.valueOf(needsCash || needsTCA), null));

        if(needsTCA){
            results.put("tcaSignature", new SingleField("tcaSignature", inputData.getOrDefault("signature", "").toString(), null));
            results.put("tcaSubmissionData", new SingleField("tcaSubmissionDate", inputData.getOrDefault("submittedAt", "").toString(), null));
        }

        return results;
    }
}
