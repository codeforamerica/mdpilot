package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.mdbenefits.app.data.enums.ApplicantObjective;
import org.mdbenefits.app.data.enums.County;
import org.springframework.stereotype.Component;

/**
 * If certain conditions are met, we will have the flow go to a page that recommends the user visit myMDTHINK instead.
 * <p>
 * This will return true (for a redirect) if:
 * <ol>
 *   <li>they are in a county this pilot is not working in, or</li>
 *   <li>they have a need that this pilot is unable to work with</li>
 *   <li>they are re-certifying versus applying for the first time</li>
 * </ol>
 * </p>
 */
@Component
public class ShouldRedirectToMDThink implements Condition {

    @Override
    public Boolean run(Submission submission) {
        Map<String, Object> inputData = submission.getInputData();

        String county = (String) inputData.getOrDefault("county", "");
        List<String> applicationInfoList =
                (List<String>) inputData.getOrDefault("applicationInfo[]", new ArrayList<String>());

        return (!county.isBlank() && !County.getCountyByName(county).isInPilot()) ||
                (!applicationInfoList.isEmpty() && !applicationInfoList.contains(ApplicantObjective.OTHER.name())) ||
                (inputData.getOrDefault("needsToReCertify", "false")
                        .toString().equalsIgnoreCase("true"));

    }
}
