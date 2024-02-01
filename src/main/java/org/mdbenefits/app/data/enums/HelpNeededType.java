package org.mdbenefits.app.data.enums;

import static org.mdbenefits.app.data.enums.ProgramType.SNAP;
import static org.mdbenefits.app.data.enums.ProgramType.TCA;
import static org.mdbenefits.app.data.enums.ProgramType.TDAP;
import static org.mdbenefits.app.data.enums.ProgramType.OTHER;

import lombok.Getter;

@Getter
public enum HelpNeededType {

  FOOD( "help-needed.food", SNAP),
  CHILDREN("help-needed.children", TCA),
  //UTILITIES("help-needed.utilities", OHEP),
  DISABILITY("help-needed.disability", TDAP),
  NOT_SURE("help-needed.not-sure", OTHER);

  private final String labelSrc;
  private final ProgramType programType;

  HelpNeededType(String labelSrc, ProgramType programType) {
    this.labelSrc = labelSrc;
    this.programType = programType;
  }
}
