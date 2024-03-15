package org.mdbenefits.app.data.enums;

import lombok.Getter;

@Getter
public enum AdditionalIncomeType {

    ALIMONY("additional-income.choice.alimony"),
    CHILD_SUPPORT("additional-income.choice.child-support"),
    FRIENDS_FAMILY_CONTRIBUTION("additional-income.choice.friends-and-family"),
    PENSION_RETIREMENT("additional-income.choice.pension-retirement"),
    SUPPLEMENTAL_SECURITY_INCOME("additional-income.choice.ssi"),
    SOCIAL_SECURITY("additional-income.choice.ss"),
    UNEMPLOYMENT("additional-income.choice.unemployment"),
    VETERANS_BENEFITS("additional-income.choice.veterans-benefits"),
    WORKERS_COMPENSATION("additional-income.choice.workers-comp"),
    OTHER("additional-income.choice.other"),
    NONE_OF_THESE("additional-income.choice.none");

    private final String labelSrc;

    AdditionalIncomeType(String labelSrc) {
        this.labelSrc = labelSrc;
    }
}
