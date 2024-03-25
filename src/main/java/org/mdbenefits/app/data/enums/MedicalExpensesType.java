package org.mdbenefits.app.data.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum MedicalExpensesType {
    HEALTH_MEDICAL_INSURANCE("medicalExpenseHealthMedicalInsurance", "medical-expenses.options.insurance-premiums"),
    DENTURES_GLASSES_HEARING_AIDS("medicalExpenseDenturesGlassesEtc", "medical-expenses.options.dentures-glasses-etc"),
    HOSPITAL_BILLS("medicalExpenseHospitalBills", "medical-expenses.options.hospital-bills"),
    ATTENDANT_CARE("medicalExpenseAttendantCare", "medical-expenses.options.attendant-care"),
    MEDICAL_DENTAL_INSURANCE("medicalExpenseMedicalDentalInsurance", "medical-expenses.options.dental-insurance"),
    TRANSPORTATION_COSTS("medicalExpenseTransportationCosts", "medical-expenses.options.transporation-costs"),
    NURSING("medicalExpenseNursing", "medical-expenses.options.nursing"),
    PHARMACY("medicalExpensePharmacy", "medical-expenses.options.pharmacy-expenses"),
    OTHER("medicalExpenseOther", "medical-expenses.options.other");

    private final String inputFieldName;
    private final String labelSrc;

    private static final Map<String, MedicalExpensesType> ENUM_BY_NAME = new HashMap<>();

    static {
        for (MedicalExpensesType type : MedicalExpensesType.values()) {
            ENUM_BY_NAME.put(type.name(), type);
        }
    }

    MedicalExpensesType(String inputFieldName, String labelSrc) {
        this.inputFieldName = inputFieldName;
        this.labelSrc = labelSrc;
    }

    public static MedicalExpensesType getEnumByName(String name) {
        return ENUM_BY_NAME.get(name);
    }
}
