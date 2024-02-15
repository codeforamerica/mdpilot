package org.mdbenefits.app.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mdbenefits.app.data.enums.HelpNeededType;

public class ApplicationUtilities {

    public static Set<String> flattenHouseholdNeedsPrograms(ArrayList<String> needTypes) {
        List<List<String>> relevantPrograms = new ArrayList<>();

        needTypes.forEach((String programType) -> {
                relevantPrograms.add(HelpNeededType.getRelevantProgramsFromName(programType));
        });

        return relevantPrograms.stream()
            .flatMap(List::stream)
            .collect(Collectors.toSet());
    }
}
