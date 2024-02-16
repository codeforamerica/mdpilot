package org.mdbenefits.app.data.enums;

import lombok.Getter;

@Getter
public enum ProgramType {

    SNAP("choose-programs.snap", "choose-programs.snap.short-desc", "choose-programs.snap.desc"),
    TCA("choose-programs.tca", "choose-programs.tca.short-desc", "choose-programs.tca.desc"),
    TDAP("choose-programs.tdap", "choose-programs.tdap.short-desc", "choose-programs.tdap.desc"),
    RCA("choose-programs.rca", "choose-programs.rca.short-desc", "choose-programs.rca.desc");

    private final String nameSrc;
    private final String shortDescSrc;
    private final String descSrc;

    ProgramType(String nameSrc, String shortDescSrc, String descSrc) {
        this.nameSrc = nameSrc;
        this.shortDescSrc = shortDescSrc;
        this.descSrc = descSrc;
    }
}
