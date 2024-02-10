package org.mdbenefits.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import formflow.library.inputs.FieldNameMarkers;
import org.springframework.stereotype.Component;

@Component
public class SmartySuggestionNotFound implements Condition {

  @Override
  public Boolean run(Submission submission) {
    return submission.getInputData().get(FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATE_ADDRESS + "mailingAddress")
        .equals("true") && !submission.getInputData()
        .containsKey("mailingAddressStreetAddress1" + FieldNameMarkers.UNVALIDATED_FIELD_MARKER_VALIDATED);
  }
}
