package org.mdbenefits.app.data.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum RaceType {
    AMERICAN_INDIAN("1", "race-selection.option1"),
    ASIAN("2", "race-selection.option2"),
    BLACK_OR_AFRICAN_AMERICA("3", "race-selection.option3"),
    NATIVE_HAWAIIAN_OR_PACIFIC_ISLANDER("4", "race-selection.option4"),
    WHITE("5", "race-selection.option5"),
    PREFER_NO_ANSWER("", "race-selection.other");

    private final String pdfValue;
    private final String labelSrc;

    static private final Map<String, RaceType> MAP_BY_VALUE = new HashMap<>();

    static {
        for (RaceType type : RaceType.values()) {
            MAP_BY_VALUE.put(type.name(), type);
        }
    }

    RaceType(String pdfValue, String labelSrc) {
        this.pdfValue = pdfValue;
        this.labelSrc = labelSrc;
    }

    public static String getPdfValueFromValue(String value) {
        RaceType raceType = (RaceType) MAP_BY_VALUE.get(value);
        return raceType != null ? raceType.pdfValue : "";
    }
}
