package org.mdbenefits.app.preparers;

import static org.assertj.core.api.Assertions.assertThat;
import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.mdbenefits.app.testutils.TestUtils;

class ApplicantNeedsPreparerTest {
    private final ApplicantNeedsPreparer preparer = new ApplicantNeedsPreparer();

    private final OffsetDateTime NovTwelve = TestUtils.makeOffsetDateTime("2024-11-12");

    @Test
    public void tdapProgramSelectionGeneratesNeedsCashAssistance() {
        Submission submission = new SubmissionTestBuilder()
            .withPersonalInfo("John", "Doe", "10", "12", "1999",
                "", "", "", "", "")
            .with("programs[]", List.of("TDAP"))
            .with("signature", "My Signature")
            .build();

        submission.setSubmittedAt(NovTwelve);

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("needsSNAP"))
            .isEqualTo(new SingleField("needsSNAP", "false", null));
        assertThat(result.get("needsCashAssistance"))
            .isEqualTo(new SingleField("needsCashAssistance", "true", null));
        assertThat(result.get("tcaSignature"))
            .isEqualTo(new SingleField("tcaSignature", "My Signature", null));
        assertThat(result.get("tcaSubmissionDate")).isEqualTo(new SingleField("tcaSubmissionDate", "11/12/2024", null));
    }

    @Test
    public void rcaProgramSelectionGeneratesNeedsCashAssistance() {
        Submission submission = new SubmissionTestBuilder()
            .withPersonalInfo("John", "Doe", "10", "12", "1999",
                "", "", "", "", "")
            .with("programs[]", List.of("RCA"))
            .with("signature", "My Signature")
            .build();

        submission.setSubmittedAt(NovTwelve);

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("needsSNAP"))
            .isEqualTo(new SingleField("needsSNAP", "false", null));
        assertThat(result.get("needsCashAssistance"))
            .isEqualTo(new SingleField("needsCashAssistance", "true", null));
        assertThat(result.get("tcaSignature"))
            .isEqualTo(new SingleField("tcaSignature", "My Signature", null));
        assertThat(result.get("tcaSubmissionDate")).isEqualTo(new SingleField("tcaSubmissionDate", "11/12/2024", null));
    }

    @Test
    public void snapProgramSelectionGeneratesNeedsCashAssistance() {
        Submission submission = new SubmissionTestBuilder()
            .withPersonalInfo("John", "Doe", "10", "12", "1999",
                "", "", "", "", "")
            .with("programs[]", List.of("SNAP"))
            .with("signature", "My Signature")
            .build();

        submission.setSubmittedAt(NovTwelve);

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("needsSNAP"))
            .isEqualTo(new SingleField("needsSNAP", "true", null));
        assertThat(result.get("needsCashAssistance"))
            .isEqualTo(new SingleField("needsCashAssistance", "false", null));
        assertThat(result.get("tcaSignature"))
            .isEqualTo(null);
        assertThat(result.get("tcaSubmissionDate")).isEqualTo(null);
    }

    @Test
    public void tcaProgramSelectionGeneratesNeedsCashAssistance() {
        Submission submission = new SubmissionTestBuilder()
            .withPersonalInfo("John", "Doe", "10", "12", "1999",
                "", "", "", "", "")
            .with("programs[]", List.of("TCA"))
            .with("signature", "My Signature")
            .build();

        submission.setSubmittedAt(NovTwelve);

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);
        assertThat(result.get("needsSNAP"))
            .isEqualTo(new SingleField("needsSNAP", "false", null));
        assertThat(result.get("needsCashAssistance"))
            .isEqualTo(new SingleField("needsCashAssistance", "true", null));
        assertThat(result.get("tcaSignature"))
            .isEqualTo(new SingleField("tcaSignature", "My Signature", null));
        assertThat(result.get("tcaSubmissionDate")).isEqualTo(new SingleField("tcaSubmissionDate", "11/12/2024", null));
    }
}