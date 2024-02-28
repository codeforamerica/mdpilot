package org.mdbenefits.app.data.enums;

import lombok.Getter;

@Getter
public enum CitizenshipStatus {

    US_CITIZEN("citizenship.types.us-citizen"),
    PERM_RESIDENT_OR_GREEN_CARD("citizenship.types.perm-resident-or-green-card"),
    REFUGEE("citizenship.types.refugee"),
    ASYLEE("citizenship.types.asylee"),
    NOT_LISTED("citizenship.types.not-listed");

    private final String labelSrc;

    CitizenshipStatus(String labelSrc) {
        this.labelSrc = labelSrc;
    }
}
