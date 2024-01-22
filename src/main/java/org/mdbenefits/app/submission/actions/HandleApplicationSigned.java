package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class HandleApplicationSigned implements Action {

    @Override
    public void run(Submission submission) {
    }
}
