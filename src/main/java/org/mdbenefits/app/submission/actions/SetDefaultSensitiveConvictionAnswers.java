package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.utils.HouseholdUtilities;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Slf4j
public class SetDefaultSensitiveConvictionAnswers implements Action {
    public static final String[] SENSITIVE_CONVICTION_QUESTIONS = {
            "HasDrugKingpinFelony",
            "HasVolumeDrugDealerFelony",
            "HasSexualOffenceConviction",
            "IsViolatingParole",
            "ConvictedForLyingAboutBenefits",
            "ConvictedForTradingBenefits",
            "IsReceivingBenefitsWithFakeID"
    };

    @Override
    public void run(Submission submission) {
        Arrays.stream(SENSITIVE_CONVICTION_QUESTIONS).forEach(s -> submission.getInputData().putIfAbsent("noOne" + s + "[]", "true"));
    }
}
