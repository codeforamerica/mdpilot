package org.mdbenefits.app.data.enums;

import lombok.Getter;

@Getter
public enum MoneyOnHandType {
    CHECKING("moneyonhand-types.checking-account"),
    SAVINGS("moneyonhand-types.savings-account"),
    BONDS("moneyonhand-types.bonds"),
    CASH_ON_HAND("moneyonhand-types.cash-on-hand"),
    MUTUAL_FUNDS("moneyonhand-types.mutual-funds"),
    STOCKS("moneyonhand-types.stocks");

    private final String labelSrc;

    MoneyOnHandType(String labelSrc) {
        this.labelSrc = labelSrc;
    }
}
