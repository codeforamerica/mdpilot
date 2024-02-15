package org.mdbenefits.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.mdbenefits.app.data.enums.HelpNeededType;
import org.springframework.stereotype.Component;

/**
 * This method stores the program associated with the help needed so that the field appears checked on the choose programs page.
 */
@Component
@Slf4j
public class PreCheckPrograms implements Action {

  @Override
  public void run(Submission submission) {
    List<String> helpNeeded = (List<String>) submission.getInputData().get("helpNeeded[]");
    List<String> programsToAdd = new ArrayList<>();

    helpNeeded.forEach((String need) -> {
      if(need.equals(HelpNeededType.NOT_SURE.name())){
        submission.getInputData().remove("programs[]");
      } else {
        programsToAdd.add(HelpNeededType.getProgramTypeFromName(need));
      }
    });

    submission.getInputData().put("programs[]", programsToAdd);
  }
}
