package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

import static org.mdbenefits.app.utils.SubmissionUtilities.isNolaParish;

@Component
public class EligibleForNolaWicEceLinks implements Condition {

  @Override
  public Boolean run(Submission submission) {
    return isNolaParish(submission);
  }
}
