package org.mdbenefits.app.data.enums;

import lombok.Getter;

@Getter
public enum Counties {

    BALTIMORE("county.option.baltimore_county"),
    QUEEN_ANNES("county.option.queenannes_county"),
    OTHER("county.option.other");

    /*
    // we may need these again if we switch gears to include them.
    // we may also want to switch back to hard coded text like below.
    ALLEGANY("Allegany County"),
    ANNE_ARUNDEL("Anne Arundel County"),
    BALTIMORE_CITY("Baltimore City"),
    CALVERT("Calvert County"),
    CAROLINE("Caroline County"),
    CARROLL("Carroll County"),
    CECIL("Cecil County"),
    CHARLES("Charles County"),
    DORCHESTER("Dorchester County"),
    FREDERICK("Frederick County"),
    GARRETT("Garrett County"),
    HARFORD("Harford County"),
    HOWARD("Howard County"),
    KENT("Kent County"),
    MONTGOMERY("Montgomery County"),
    PRINCE_GEORGES("Prince George's County"),
    SOMERSET("Somerset County"),
    ST_MARYS("St. Mary's County"),
    TALBOT("Talbot County"),
    WASHINGTON("Washington County"),
    WICOMICO("Wicomico County"),
    WORCESTER("Worcester County");
     */

    private final String labelSrc;

    Counties(String labelSrc) {
        this.labelSrc = labelSrc;
    }

}
