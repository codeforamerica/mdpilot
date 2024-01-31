package org.mdbenefits.app.data.enums;

import lombok.Getter;

@Getter
public enum ApplicantObjective {

  NEED_HELP_IN_OTHER_LANGUAGE("I need help in a language that isn’t English ((Español, 中文, Tiếng Việt)", "select-app.help-in-other-language"),
  COLLEGE_STUDENT_IN_APP("I am a college student or have a student who will be on my aplication", "select-app.college-student-in-home"),
  VETERAN_IN_APP("I am a veteran or have a veteran who will be on my application", "select-app.veteran-in-app"),
  HELP_WITH_HEALTHCARE("I am applying for help with healthcare", "select-app.help-with-healthcare"),
  AUTHORIZED_REP_FOR_SOMEONE("I am an authorized representative applying for someone else", "select-app.authorized-rep"),
  OTHER("None of these are true for me", "select-app.other");

  private final String value;
  private final String labelSrc;

  ApplicantObjective(String value, String labelSrc) {
    this.value = value;
    this.labelSrc = labelSrc;
  }
}
