package org.mdbenefits.app.data.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum AdditionalIncomeType {

    ALIMONY("additionalIncomeAlimony", "additional-income.choice.alimony"),
    CHILD_SUPPORT("additionalIncomeChildSupport", "additional-income.choice.child-support"),
    FRIENDS_FAMILY_CONTRIBUTION("additionalIncomeFriendsAndFamily", "additional-income.choice.friends-and-family"),
    PENSION_RETIREMENT("additionalIncomePensionRetirement", "additional-income.choice.pension-retirement"),
    SUPPLEMENTAL_SECURITY_INCOME("additionalIncomeSSI", "additional-income.choice.ssi"),
    SOCIAL_SECURITY("additionalIncomeSS", "additional-income.choice.ss"),
    UNEMPLOYMENT("additionalIncomeUnemployment", "additional-income.choice.unemployment"),
    VETERANS_BENEFITS("additionalIncomeVeteransBenefits", "additional-income.choice.veterans-benefits"),
    WORKERS_COMPENSATION("additionalIncomeWorkersComp", "additional-income.choice.workers-comp"),
    OTHER("additionalIncomeOther", "additional-income.choice.other");

    private final String inputFieldName;
    private final String labelSrc;
    private static final Map<String, AdditionalIncomeType> ENUM_BY_NAME = new HashMap<>();

    static {
        for (AdditionalIncomeType type : AdditionalIncomeType.values()) {
            ENUM_BY_NAME.put(type.name(), type);
        }
    }

    AdditionalIncomeType(String inputFieldName, String labelSrc) {
        this.labelSrc = labelSrc;
        this.inputFieldName = inputFieldName;
    }

    public static AdditionalIncomeType getEnumByName(String name) {
        return ENUM_BY_NAME.get(name);
    }
}
