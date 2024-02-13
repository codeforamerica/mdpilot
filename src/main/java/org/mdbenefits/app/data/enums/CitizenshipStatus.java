package org.mdbenefits.app.data.enums;

import lombok.Getter;

@Getter
public enum CitizenshipStatus {

    US_CITIZEN("citizenship-select-status.types.us-citizen"),
    PERM_RESIDENT_OR_GREEN_CARD("citizenship-select-status.types.perm-resident-or-green-card"),
    REFUGEE("citizenship-select-status.types.refugee"),
    ASYLEE("citizenship-select-status.types.asylee"),
    NOT_APPLYING("citizenship-select-status.types.not-applying");

    private final String labelSrc;

    CitizenshipStatus(String labelSrc) {
        this.labelSrc = labelSrc;
    }
}
