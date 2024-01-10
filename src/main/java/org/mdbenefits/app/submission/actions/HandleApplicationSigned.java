package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.data.TransmissionRepositoryService;
import org.mdbenefits.app.utils.SubmissionUtilities;
import org.mdbenefits.app.data.enums.TransmissionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.mdbenefits.app.submission.actions.SetExperimentGroups.ExperimentGroup.APPLY;


@Slf4j
@Component
public class HandleApplicationSigned implements Action {

  @Autowired
  private TransmissionRepositoryService transmissionRepositoryService;

  @Override
  public void run(Submission submission) {
    // Create WIC + ECE records if they don't exist
    if (!transmissionRepositoryService.transmissionExists(submission, TransmissionType.WIC)) {
      // TODO: should we also check if any questions were answered? Or is this enough?
      if (SubmissionUtilities.inExperimentGroup(APPLY.name(), submission)){
        transmissionRepositoryService.createTransmissionRecord(submission, TransmissionType.WIC);
      }

    }

    if (!transmissionRepositoryService.transmissionExists(submission, TransmissionType.ECE)) {
      if (SubmissionUtilities.inExperimentGroup(APPLY.name(), submission)) {
        transmissionRepositoryService.createTransmissionRecord(submission, TransmissionType.ECE);
      }
    }

    if(!transmissionRepositoryService.transmissionExists(submission, TransmissionType.SNAP)) {
      transmissionRepositoryService.createTransmissionRecord(submission, TransmissionType.SNAP);
    }
  }
}
