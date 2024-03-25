package org.mdbenefits.app.data.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;


@Getter
public enum HomeExpensesType {
//    Utilities and home expenses are mixed together now. The label string will show how the data was tagged originally.
    RENT("homeExpenseRent", "home-expenses.rent"),
    MORTGAGE("homeExpenseMortgage", "home-expenses.mortgage"),
    PHONE("homeExpensePhone", "utilities-expenses.telephone"),
    ELECTRICITY("homeExpenseElectricity", "utilities-expenses.electricity"),
    WATER("homeExpenseWater", "utilities-expenses.water"),
    SEWAGE("homeExpenseSewer", "utilities-expenses.sewage"),
    GARBAGE("homeExpenseGarbage", "utilities-expenses.garbage"),
    GAS("homeExpenseGas", "utilities-expenses.gas"),
    OIL("homeExpenseOil", "utilities-expenses.oil"),
    WOOD_OR_COAL("homeExpenseWoodOrCoal", "utilities-expenses.wood-or-coal"),
    CONDO_FEES("homeExpenseCondominiumFees", "home-expenses.condo-fees"),
    PROPERTY_TAX("homeExpensePropertyTax", "home-expenses.tax"),
    HOMEOWNERS_INSURANCE("homeExpenseHomeownerInsurance", "home-expenses.home-insurance"),
    OTHER("homeExpenseOtherHomeExpenses", "home-expenses.other");

    private final String inputFieldName;
    private final String labelSrc;

    private static final Map<String, HomeExpensesType> ENUM_BY_NAME = new HashMap<>();

    static {
        for (HomeExpensesType type : HomeExpensesType.values()) {
            ENUM_BY_NAME.put(type.name(), type);
        }
    }

    HomeExpensesType(String inputFieldName, String labelSrc) {
        this.inputFieldName = inputFieldName;
        this.labelSrc = labelSrc;
    }

    public static HomeExpensesType getEnumByName(String name) {
        return ENUM_BY_NAME.get(name);
    }
}
