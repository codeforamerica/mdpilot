package org.mdbenefits.app.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TestUserFileUtilities {

    @CsvSource(delimiter = ';', value = {
            // passed in value; expected value
            ".jpg, .jpeg, .csv,.pdf;.jpg, .jpeg, .csv, and .pdf",
            ".jpg,.jpeg,.csv,.pdf,.doc;.jpg, .jpeg, .csv, .pdf, and .doc",
            " .jpg ;.jpg",
            ".jpg, .gif;.jpg and .gif",
            "'';''"
    })
    @ParameterizedTest
    public void shouldParseFileTypesSuccessfully(String acceptedFileTypeString, String expectedString) {
        assertThat(UserFileUtilities.formatAcceptedFileTypeString(acceptedFileTypeString)).isEqualTo(expectedString);
    }
}
