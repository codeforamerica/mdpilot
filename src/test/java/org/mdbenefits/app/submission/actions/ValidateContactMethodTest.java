package org.mdbenefits.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.FormSubmission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest()
@ActiveProfiles("test")
class ValidateContactMethodTest {

    private final String VALID_PHONE = "(123) 459-0392";
    private final String VALID_EMAIL = "test@mail.com";
    private final String BLANK_VALUE = "";
    private final String PHONE_NUMBER_INPUT_NAME = "cellPhoneNumber";
    private final String EMAIL_ADDRESS_INPUT_NAME = "emailAddress";
    private final String IS_TEXT_OKAY_INPUT_NAME = "textingCellNumberIsOkay";

    @Autowired
    private ValidateContactMethod validator;

    @Test
    public void testPhoneMissingDoesNotRaiseErrorWhenPhoneNotExpected() {
        FormSubmission form = new FormSubmission(Map.of(
                PHONE_NUMBER_INPUT_NAME, BLANK_VALUE,
                EMAIL_ADDRESS_INPUT_NAME, VALID_EMAIL,
                IS_TEXT_OKAY_INPUT_NAME, "false"
        ));

        Map<String, List<String>> result = validator.runValidation(form, null);
        assertThat(result.get(PHONE_NUMBER_INPUT_NAME)).isNullOrEmpty();
    }

    @Test
    public void testPhoneMissingRaisesErrorWhenPhoneExpected() {
        FormSubmission form = new FormSubmission(Map.of(
                PHONE_NUMBER_INPUT_NAME, BLANK_VALUE,
                EMAIL_ADDRESS_INPUT_NAME, VALID_EMAIL,
                IS_TEXT_OKAY_INPUT_NAME, "true"
        ));

        Map<String, List<String>> result = validator.runValidation(form, null);
        assertThat(result.get(PHONE_NUMBER_INPUT_NAME)).containsAll(
                List.of("If you'd like to receive texts please provide a cell phone number."));
    }

    @Test
    public void testPhoneHappyPath() {
        FormSubmission form = new FormSubmission(Map.of(
                PHONE_NUMBER_INPUT_NAME, VALID_PHONE,
                EMAIL_ADDRESS_INPUT_NAME, VALID_EMAIL,
                IS_TEXT_OKAY_INPUT_NAME, "true"
        ));

        Map<String, List<String>> result = validator.runValidation(form, null);
        assertThat(result.get(PHONE_NUMBER_INPUT_NAME)).isNullOrEmpty();
    }
}
