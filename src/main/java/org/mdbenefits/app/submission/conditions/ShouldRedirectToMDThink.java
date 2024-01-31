package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.mdbenefits.app.data.enums.ApplicationDescriptionType;
import org.mdbenefits.app.data.enums.Counties;
import org.springframework.stereotype.Component;

/**
 * If certain conditions are met, we will have the flow go to a page
 * that recommends the user visit MDTHINK instead.
 * The conditions are that they selected a county that this demo is not for,
 * or they have a situation that our pilot cannot work with.
 */
@Component
public class ShouldRedirectToMDThink implements Condition {

    private final List<String> acceptableCounties = List.of(
        Counties.BALTIMORE.getDisplayName(),
        Counties.QUEEN_ANNES.getDisplayName()
    );

    @Override
    public Boolean run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();

        String county = (String) inputData.getOrDefault("county", "");
        List<String> applicationInfoList =
            (List<String>)inputData.getOrDefault("applicationInfo[]", new ArrayList<String>());

        if (!county.isBlank() && !acceptableCounties.contains(county)) {
            return true;
        }

        if (!applicationInfoList.isEmpty() && !applicationInfoList.contains(ApplicationDescriptionType.OTHER.getValue())) {
            return true;
        }

        return false;
    }
}
