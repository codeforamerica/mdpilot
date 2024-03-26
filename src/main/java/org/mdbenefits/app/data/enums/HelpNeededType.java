package org.mdbenefits.app.data.enums;

import static org.mdbenefits.app.data.enums.ProgramType.SNAP;
import static org.mdbenefits.app.data.enums.ProgramType.TCA;
import static org.mdbenefits.app.data.enums.ProgramType.TDAP;
import static org.mdbenefits.app.data.enums.ProgramType.RCA;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import lombok.Getter;

@Getter
public enum HelpNeededType {

    FOOD("help-needed.food", new ArrayList<>(List.of(SNAP, TCA))),
    CHILDREN("help-needed.children", new ArrayList<>(List.of(SNAP, TCA))),
    DISABILITY("help-needed.disability", new ArrayList<>(List.of(TDAP))),
    REFUGEE("help-needed.refugees", new ArrayList<>(List.of(RCA))),
    NOT_SURE("help-needed.not-sure",  new ArrayList<>(List.of(RCA, TDAP, SNAP, TCA)));

    private final String labelSrc;
    private final ArrayList<ProgramType> relevantPrograms;

    static private final Map<String, ArrayList<ProgramType>> MAP_BY_ENUM_NAME = new HashMap<>();

    static {
        for (HelpNeededType type : HelpNeededType.values()) {
            MAP_BY_ENUM_NAME.put(type.name(), type.relevantPrograms);
        };
    }

    HelpNeededType(String labelSrc, ArrayList<ProgramType> relevantPrograms) {
        this.labelSrc = labelSrc;
        this.relevantPrograms = relevantPrograms;
    }

    public static List getRelevantProgramsFromName(String name) {
        ArrayList<ProgramType> relevantPrograms = MAP_BY_ENUM_NAME.get(name);
        return relevantPrograms;
    }
}
