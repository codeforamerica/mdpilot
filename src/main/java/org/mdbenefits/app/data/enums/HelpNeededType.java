package org.mdbenefits.app.data.enums;

import static org.mdbenefits.app.data.enums.ProgramType.SNAP;
import static org.mdbenefits.app.data.enums.ProgramType.TCA;
import static org.mdbenefits.app.data.enums.ProgramType.TDAP;
import static org.mdbenefits.app.data.enums.ProgramType.RCA;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import lombok.Getter;

@Getter
public enum HelpNeededType {

    FOOD("help-needed.food", SNAP, new ArrayList<>(List.of(SNAP, TCA))),
    CHILDREN("help-needed.children", TCA,  new ArrayList<>(List.of(SNAP, TCA))),
    DISABILITY("help-needed.disability", TDAP, new ArrayList<>(List.of(TDAP))),
    REFUGEE("help-needed.refugees", RCA, new ArrayList<>(List.of(RCA))),
    NOT_SURE("help-needed.not-sure", null, new ArrayList<>(List.of(RCA, TDAP, SNAP, TCA)));

    private final String labelSrc;
    private final ProgramType programType;
    private final ArrayList<ProgramType> relevantPrograms;

    static private final Map<String, ArrayList<ProgramType>> MAP_BY_ENUM_NAME = new HashMap<>();
    static private final Map<String, ProgramType> MAP_PROGRAM_BY_ENUM_NAME = new HashMap<>();

    static {
        for (HelpNeededType type : HelpNeededType.values()) {
            MAP_BY_ENUM_NAME.put(type.name(), type.relevantPrograms);
        }
    }
    static {
        for (HelpNeededType type : HelpNeededType.values()) {
            MAP_PROGRAM_BY_ENUM_NAME.put(type.name(), type.programType);
        }
    }

    HelpNeededType(String labelSrc, ProgramType programType, ArrayList<ProgramType> relevantPrograms) {
        this.labelSrc = labelSrc;
        this.programType = programType;
        this.relevantPrograms = relevantPrograms;
    }

    public static ArrayList getRelevantProgramsFromName(String name) {
        ArrayList<ProgramType> relevantPrograms = MAP_BY_ENUM_NAME.get(name);
        return relevantPrograms;
    }

    public static String getProgramTypeFromName(String name) {
        String programType = MAP_PROGRAM_BY_ENUM_NAME.get(name).toString();
        return programType;
    }
}
