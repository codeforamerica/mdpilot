package org.mdbenefits.app.data.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum RelationshipType {
    AUNT_OR_UNCLE("AuntOrUncle", "Aunt or Uncle", "household-info.relationship.aunt-or-uncle"),
    CHILD("Child", "Child", "household-info.relationship.child"),
    FATHER("Father", "Father", "household-info.relationship.father"),
    FRIEND("Friend", "Friend", "household-info.relationship.friend"),
    GRANDCHILD("Grandchild", "Grandchild", "household-info.relationship.grandchild"),
    GRANDPARENT("Grandparent", "Grandparent", "household-info.relationship.grandparent"),
    MOTHER("Mother", "Mother", "household-info.relationship.mother"),
    NIECE_OR_NEPHEW("NieceOrNephew", "Niece or Nephew", "household-info.relationship.niece-or-nephew"),
    OTHER_RELATIVE("OtherRelative", "Other", "household-info.relationship.other-relative"),
    PARTNER("Partner", "Partner", "household-info.relationship.partner"),
    ROOMMATE("Roommate", "Roommate", "household-info.relationship.roommate"),
    SIBLING("Sibling", "Sibling", "household-info.relationship.sibling"),
    SPOUSE("Spouse", "Spouse", "household-info.relationship.spouse"),
    OTHER_GENERAL("OtherGeneral", "Other", "household-info.relationship.other-general");

    private final String value;
    private final String pdfValue;
    private final String labelSrc;

    static private final Map<String, RelationshipType> MAP_BY_VALUE = new HashMap<>();

    static {
        for (RelationshipType type : RelationshipType.values()) {
            MAP_BY_VALUE.put(type.value, type);
        }
    }

    RelationshipType(String value, String pdfValue, String labelSrc) {
        this.value = value;
        this.pdfValue = pdfValue;
        this.labelSrc = labelSrc;
    }

    public static String getPDFValueFromValue(String value) {
        RelationshipType relationshipType = MAP_BY_VALUE.get(value);
        return relationshipType != null ? relationshipType.pdfValue : "";
    }
}
