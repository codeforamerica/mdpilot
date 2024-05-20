package org.mdbenefits.app.data.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum County {

    BALTIMORE("county.option.baltimore_county", true),
    QUEEN_ANNES("county.option.queenannes_county", true),
    OTHER("county.option.other", false);

    static private final Map<String, County> MAP_BY_ENUM_NAME = new HashMap<>();

    static {
        for (County county : County.values()) {
            MAP_BY_ENUM_NAME.put(county.name(), county);
        }
    }

    private final String labelSrc;
    private final boolean inPilot;

    County(String labelSrc, boolean inPilot) {
        this.labelSrc = labelSrc;
        this.inPilot = inPilot;
    }

    public static County getCountyByName(String name) {
        return MAP_BY_ENUM_NAME.get(name);
    }
}
