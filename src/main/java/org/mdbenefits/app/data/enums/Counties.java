package org.mdbenefits.app.data.enums;

public enum Counties {

  ALLEGANY("Allegany"),
  ANNE_ARUNDEL("Anne Arundel"),
  BALTIMORE_CITY("Baltimore City"),
  BALITMORE("Baltimore"),
  CALVERT("Calvert"),
  CAROLINE("Caroline"),
  CARROLL("Carroll"),
  CECIL("Cecil"),
  CHARLES("Charles"),
  DORCHESTER("Dorchester"),
  FREDERICK("Frederick"),
  GARRETT("Garrett"),
  HARFORD("Harford"),
  HOWARD("Howard"),
  KENT("Kent"),
  MONTGOMERY("Montgomery"),
  PRINCE_GEORGES("Prince George's"),
 QUEEN_ANNES("Queen Anne's"),
 SOMERSET("Somerset"),
 ST_MARYS("St. Mary's"),
 TALBOT("Talbot"),
 WASHINGTON("Washington"),
 WICOMICO("Wicomico"),
 WORCESTER("Worcester");

  private final String displayName;

  Counties(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
