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

    static private final Map<String, MoneyOnHandType> MAP_BY_VALUE = new HashMap<>();

    static {
        for (MoneyOnHandType type : MoneyOnHandType.values()) {
            MAP_BY_VALUE.put(type.value, type);
        }
    }

    MoneyOnHandType(String value, String labelSrc) {
        this.value = value;
        this.labelSrc = labelSrc;
    }

    public static String getLabelSrc(String value) {
        MoneyOnHandType moneyOnHandType = (MoneyOnHandType) MAP_BY_VALUE.get(value);
        return moneyOnHandType != null ? moneyOnHandType.labelSrc : null;
    }
}
