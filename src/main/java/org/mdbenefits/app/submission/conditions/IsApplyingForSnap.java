package org.mdbenefits.app.submission.conditions;

import formflow.library.data.Submission;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class IsApplyingForSnap extends BasicCondition {

    @Override
    public Boolean run(Submission submission) {
        List<String> programs = (List) submission.getInputData().getOrDefault("programs[]", List.of());
        return programs.contains("SNAP");
    }
}
