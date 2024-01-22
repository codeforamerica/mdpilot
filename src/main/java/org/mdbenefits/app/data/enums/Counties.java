package org.mdbenefits.app.data.enums;

public enum Counties {
    ALLEGANY("Allegany County"),
    ANNE_ARUNDEL("Anne Arundel County"),
    BALTIMORE_CITY("Baltimore City"),
    BALTIMORE("Baltimore County"),
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
    QUEEN_ANNES("Queen Anne's County"),
    SOMERSET("Somerset County"),
    ST_MARYS("St. Mary's County"),
    TALBOT("Talbot County"),
    WASHINGTON("Washington County"),
    WICOMICO("Wicomico County"),
    WORCESTER("Worcester County");

    private final String displayName;

    Counties(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
