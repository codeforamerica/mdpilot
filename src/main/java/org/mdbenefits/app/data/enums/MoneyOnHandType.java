package org.mdbenefits.app.data.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum MoneyOnHandType {
    CHECKING("checking", "moneyonhand-types.checking-account"),
    SAVINGS("savings", "moneyonhand-types.savings-account"),
    BONDS("bonds", "moneyonhand-types.bonds"),
    CASH_ON_HAND("cash", "moneyonhand-types.cash-on-hand"),
    MUTUAL_FUNDS("mutual funds", "moneyonhand-types.mutual-funds"),
    STOCKS("stocks", "moneyonhand-types.stocks");

    private final String value;
    private final String labelSrc;

    MoneyOnHandType(String value, String labelSrc) {
        this.value = value;
        this.labelSrc = labelSrc;
    }
}
