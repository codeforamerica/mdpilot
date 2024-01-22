package org.mdbenefits.app.preparers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.mdbenefits.app.submission.StringEncryptor;
import org.mockito.Mockito;

class SsnPreparerTest {

    private StringEncryptor encryptor;
    private SsnPreparer preparer;

    @BeforeEach
    public void setUp() {
        encryptor = Mockito.mock(StringEncryptor.class);
        preparer = new SsnPreparer(encryptor);
    }

    @Test
    public void testNoSSNs() {
        Submission submission = new Submission();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result).isEmpty();
    }

    @Test
    public void testApplicantSSN() {
        Submission submission = new SubmissionTestBuilder()
                .withPersonalInfo("", "", "", "", "", "", "", "", "",
                        "somethingencrypted")
                .build();
        when(encryptor.decrypt(eq("somethingencrypted"))).thenReturn("111223333");
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get("applicantSsn")).isEqualTo(new SingleField("applicantSsn", "111223333", null));
    }

    @Test
    public void testHouseholdSSNs() {
        Submission submission = new SubmissionTestBuilder()
                .withPersonalInfo("applicant", "test", "", "", "", "", "", "", "",
                        "")
                .withHouseholdMember("member1", "test", "", "", "", "", "", "", "",
                        "somethingencrypted", null, null)
                .withHouseholdMember("member2", "test", "", "", "", "", "", "", "",
                        "", null, null)
                .withHouseholdMember("member3", "test", "", "", "", "", "", "", "",
                        "somethingelseencrypted", null, null)
                .build();
        when(encryptor.decrypt(eq("somethingencrypted"))).thenReturn("111223333");
        when(encryptor.decrypt(eq("somethingelseencrypted"))).thenReturn("444556666");

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(submission, null);

        assertThat(result.size()).isEqualTo(4);
        assertThat(result.get("applicantSsn")).isEqualTo(new SingleField("applicantSsn", null, null));
        assertThat(result.get("ssns1")).isEqualTo(new SingleField("ssns", "111223333", 1));
        assertThat(result.get("ssns2")).isEqualTo(new SingleField("ssns", null, 2));
        assertThat(result.get("ssns3")).isEqualTo(new SingleField("ssns", "444556666", 3));
    }
}
