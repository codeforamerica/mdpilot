package org.mdbenefits.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest()
@ActiveProfiles("test")
class ReformatNameBeforeSavingTest {

    private final ReformatNameBeforeSaving action = new ReformatNameBeforeSaving();

    @Test
    public void testNamesWithSpaces() {
        Submission submission = new SubmissionTestBuilder()
            .with("firstName", "First Name   ")
            .with("middleName", " MiddleName")
            .with("lastName", " LastName With MultipleNames   ")
            .build();

        action.run(submission);

        assertThat(submission.getInputData().get("firstName")).isEqualTo("First Name");
        assertThat(submission.getInputData().get("middleName")).isEqualTo("MiddleName");
        assertThat(submission.getInputData().get("lastName")).isEqualTo("LastName With MultipleNames");
    }

    @Test
    public void testNamesWithNoSpaces() {
        Submission submission = new SubmissionTestBuilder()
            .with("firstName", "First")
            .with("middleName", "Middle")
            .with("lastName", "Last")
            .build();

        action.run(submission);

        assertThat(submission.getInputData().get("firstName")).isEqualTo("First");
        assertThat(submission.getInputData().get("middleName")).isEqualTo("Middle");
        assertThat(submission.getInputData().get("lastName")).isEqualTo("Last");
    }

}
