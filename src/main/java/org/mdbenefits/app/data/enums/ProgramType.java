package org.mdbenefits.app.data.enums;

import lombok.Getter;

@Getter
public enum ProgramType {

  SNAP("choose-programs.snap", "choose-programs.snap.short-desc", "choose-programs.snap.desc"),
  TCA("choose-programs.tca","choose-programs.tca.short-desc","choose-programs.tca.desc"),
  OHEP("choose-programs.ohep", "choose-programs.ohep.short-desc", "choose-programs.ohep.desc"),
  TDAP("choose-programs.tdap", "choose-programs.tdap.short-desc", "choose-programs.tdap.desc"),
  OTHER("choose-programs.other", "", "choose-programs.other.desc");

  private final String nameSrc;
  private final String shortDescSrc;
  private final String descSrc;

  ProgramType(String nameSrc, String shortDescSrc, String descSrc) {
    this.nameSrc = nameSrc;
    this.shortDescSrc = shortDescSrc;
    this.descSrc = descSrc;
  }
}
