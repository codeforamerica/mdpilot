package org.mdbenefits.app.utils;

import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bouncycastle.util.Strings;
import org.mdbenefits.app.data.enums.HelpNeededType;
import org.springframework.shell.Command.Help;

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
