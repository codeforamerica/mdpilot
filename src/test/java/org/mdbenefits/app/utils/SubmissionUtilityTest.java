package org.mdbenefits.app.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class SubmissionUtilityTest {

    @CsvSource(value = {
            "12,10,2010,'12/10/2010'",
            "1,2,2010,'01/02/2010'",
            "2,04,2011,'02/04/2011'",
    })
    @ParameterizedTest
    public void shouldBeCorrectlyFormatted(String month, String day, String year, String fullDate) {
        Map<String, Object> inputData = Map.of(
                "birthMonth", month,
                "birthDay", day,
                "birthYear", year);
        assertThat(SubmissionUtilities.getFormattedBirthdate(inputData)).isEqualTo(fullDate);
    }

}
