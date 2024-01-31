package org.mdbenefits.app.data.enums;

import static org.mdbenefits.app.data.enums.ProgramType.SNAP;
import static org.mdbenefits.app.data.enums.ProgramType.TCA;
import static org.mdbenefits.app.data.enums.ProgramType.OHEP;
import static org.mdbenefits.app.data.enums.ProgramType.TDAP;
import static org.mdbenefits.app.data.enums.ProgramType.OTHER;

import lombok.Getter;

@Getter
public enum HelpNeededType {

  FOOD("Help with food", "help-needed.food", SNAP),
  CHILDREN("Help with money for children", "help-needed.children", TCA),
  UTILITIES("Help with utilities", "help-needed.utilities", OHEP),
  DISABILITY("Help for a disability", "help-needed.disability", TDAP),
  NOT_SURE("I'm not sure", "help-needed.not-sure", OTHER);

  private final String value;
  private final String labelSrc;
  private final ProgramType programType;

  HelpNeededType(String value, String labelSrc, ProgramType programType) {
    this.value = value;
    this.labelSrc = labelSrc;
    this.programType = programType;
  }

}
