package org.mdbenefits.app.data.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum EthnicityType {
    HISPANIC_OR_LATINO("1", "ethnicity-selection.option1"),
    NOT_HISPANIC_OR_LATINO("2", "ethnicity-selection.option2"),
    PREFER_NO_ANSWER("", "ethnicity-selection.other");

    private final String pdfValue;
    private final String labelSrc;

    public static final Map<String, EthnicityType> MAP_BY_VALUE = new HashMap<>();

    static {
        for (EthnicityType type : EthnicityType.values()) {
            MAP_BY_VALUE.put(type.name(), type);
        }
    }

    EthnicityType(String pdfValue, String labelSrc) {
        this.pdfValue = pdfValue;
        this.labelSrc = labelSrc;
    }

    public static String getPdfValueFromValue(String value) {
        EthnicityType ethnicityType = (EthnicityType) MAP_BY_VALUE.get(value);

        return ethnicityType != null ? ethnicityType.pdfValue : null;
    }
}
