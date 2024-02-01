package org.mdbenefits.app.data.enums;

import lombok.Getter;

@Getter
public enum ApplicantObjective {

  NEED_HELP_IN_OTHER_LANGUAGE("select-app.help-in-other-language"),
  COLLEGE_STUDENT_IN_APP("select-app.college-student-in-home"),
  VETERAN_IN_APP("select-app.veteran-in-app"),
  HELP_WITH_HEALTHCARE("select-app.help-with-healthcare"),
  AUTHORIZED_REP_FOR_SOMEONE("select-app.authorized-rep"),
  OTHER("select-app.other");

  private final String labelSrc;

  ApplicantObjective(String labelSrc) {
    this.labelSrc = labelSrc;
  }
}
