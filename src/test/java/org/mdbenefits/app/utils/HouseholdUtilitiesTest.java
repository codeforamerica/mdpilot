package org.mdbenefits.app.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HouseholdUtilitiesTest {

    // Because we manually set the date here, and Calendar uses 0-based months, this is January 10, 2024
    // Note that in the actual code, this is set to Calendar.getInstance() and LocalDate handles the conversion to
    // non 0 based months
    private Calendar pretendCurrentDate = new Calendar.Builder().setDate(2024, 0, 10).build();

    @ParameterizedTest
    @CsvSource({
            // year, month, day, expected response
            "1964, 1, 10, true",  // 60th birthday
            "1964, 1, 11, false", // one day too young! hmmm.... weird boundary case
            "1935, 1, 10, true",  // older than 60
            "2019, 1, 10, false", // def not 60+
    })
    void test60orOlderDates(int year, int month, int day, boolean expectedResponse) {
        assertThat(HouseholdUtilities.isMember60orOlder(year, month, day, pretendCurrentDate)).isEqualTo(expectedResponse);
    }
}
