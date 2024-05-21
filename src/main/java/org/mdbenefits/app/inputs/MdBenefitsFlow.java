package org.mdbenefits.app.inputs;

import formflow.library.data.FlowInputs;
import formflow.library.data.annotations.DynamicField;
import formflow.library.data.annotations.Money;
import formflow.library.utils.RegexUtils;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

@Data
@EqualsAndHashCode(callSuper = false)
public class MdBenefitsFlow extends FlowInputs {

    MultipartFile uploadDocuments;

    // selectApplication information
    @NotEmpty(message = "{error.missing-general}")
    private List<String> applicationInfo;

    @NotEmpty(message = "{error.missing-general}")
    private String needsToReCertify;

    @NotEmpty(message = "{error.missing-general}")
    private String county;

    // Choose help
    @NotEmpty(message = "{error.missing-general}")
    private List<String> helpNeeded;

    // Choose programs
    @NotEmpty(message = "{error.missing-general}")
    private List<String> programs;

    // Personal Information
    @NotBlank(message = "{error.missing-firstname}")
    private String firstName;

    private String middleName;

    @NotBlank(message = "{error.missing-lastname}")
    private String lastName;

    private String birthDay;
    private String birthMonth;
    private String birthYear;

    private String maritalStatus;

    private String highestEducation;

    // home address
    private String noHomeAddress;

    private String homeAddressStreetAddress1;

    private String homeAddressStreetAddress2;

    private String homeAddressCity;

    private String homeAddressState;

    private String homeAddressZipCode;

    //Mailing Address
    private String sameAsHomeAddress;

    private String mailingAddressStreetAddress1;

    private String mailingAddressStreetAddress2;

    private String mailingAddressCity;

    private String mailingAddressState;

    private String mailingAddressZipCode;

    // Verify Address & Select Address
    private String useSuggestedAddress;

    //Contact Info
    @Pattern(regexp = "^\\(\\d{3}\\) \\d{3}-\\d{4}$", message = "{error.invalid-phone}")
    private String cellPhoneNumber;

    @Pattern(regexp = "^\\(\\d{3}\\) \\d{3}-\\d{4}$", message = "{error.invalid-phone}")
    private String homePhoneNumber;

    @Pattern(regexp = "^\\(\\d{3}\\) \\d{3}-\\d{4}$", message = "{error.invalid-phone}")
    private String workPhoneNumber;

    private String textingCellNumberIsOkay;

    @Email(message = "{contact-info.provide-correct-email}", regexp = RegexUtils.EMAIL_REGEX)
    private String emailAddress;

    // Household
    private String multiplePersonHousehold;

    // Applicant Tell Us More About Yourself
    @NotBlank(message = "{error.missing-general}")
    private String isApplicantApplying;

    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{4}$", message = "{error.invalid-ssn}")
    private String applicantSSN;

    @NotBlank(message = "{error.missing-general}")
    private String applicantCitizenshipStatus;

    @NotBlank(message = "{error.missing-general}")
    private String applicantSex;

    @NotBlank(message = "{error.missing-general}")
    private String isApplicantPregnant;

    @NotBlank(message = "{error.missing-general}")
    private String applicantIsEnrolledInSchool;

    @NotBlank(message = "{error.missing-general}")
    private String applicantHasDisability;

    private String applicantEthnicity;

    private List<String> applicantRace;

    @NotBlank(message = "{error.missing-firstname}")
    private String householdMemberFirstName;

    private String householdMemberMiddleName;

    @NotBlank(message = "{error.missing-lastname}")
    private String householdMemberLastName;

    @NotBlank(message = "{error.missing-general}")
    private String householdMemberRelationship;

    @NotBlank(message = "{error.missing-general}")
    private String householdMemberApplyingForBenefits;

    private String householdMemberBirthDay;

    private String householdMemberBirthMonth;

    private String householdMemberBirthYear;

    @NotBlank(message = "{error.missing-general}")
    private String householdMemberSex;

    @NotBlank(message = "{error.missing-general}")
    private String householdMemberCitizenshipStatus;

    @Pattern(regexp = "^\\d{3}-\\d{2}-\\d{4}$", message = "{error.invalid-ssn}")
    private String householdMemberSsn;

    @NotBlank(message = "{error.missing-general}")
    private String householdMemberEnrolledInSchool;

    @NotBlank(message = "{error.missing-general}")
    private String householdMemberIsPregnant;

    @NotBlank(message = "{error.missing-general}")
    private String householdMemberHasDisability;

    // SNAP
    private String migrantOrSeasonalFarmWorkerInd;

    // Income
    private String householdHasIncome;

    @NotBlank(message = "{error.missing-general}")
    private String householdMemberJobAdd;

    @NotBlank(message = "{error.missing-general}")
    private String employerName;

    private String jobPaidByHour;

    @Money(message = "{error.invalid-money}")
    @NotBlank(message = "{error.missing-dollar-amount}")
    private String hourlyWage;

    @Range(message = "{error.invalid-range}", min = 1, max = 100)
    @NotBlank(message = "{error.missing-general}")
    private String hoursPerWeek;

    @NotBlank(message = "{error.missing-pay-period}")
    private String payPeriod;

    @Money(message = "{error.invalid-money}")
    @NotBlank(message = "{error.missing-dollar-amount}")
    private String payAmountFor30Days;

    @NotEmpty(message = "{error.missing-general}")
    private List<String> additionalIncome;

    @Money(message = "{error.invalid-money}")
    private String additionalIncomeAlimony;
    @Money(message = "{error.invalid-money}")
    private String additionalIncomeChildSupport;
    @Money(message = "{error.invalid-money}")
    private String additionalIncomeFriendsAndFamily;
    @Money(message = "{error.invalid-money}")
    private String additionalIncomePensionRetirement;
    @Money(message = "{error.invalid-money}")
    private String additionalIncomeSSI;
    @Money(message = "{error.invalid-money}")
    private String additionalIncomeSS;
    @Money(message = "{error.invalid-money}")
    private String additionalIncomeUnemployment;
    @Money(message = "{error.invalid-money}")
    private String additionalIncomeVeteransBenefits;
    @Money(message = "{error.invalid-money}")
    private String additionalIncomeWorkersComp;
    @Money(message = "{error.invalid-money}")
    private String additionalIncomeOther;

    @NotEmpty
    private List<String> moneyOnHandTypes;

    @Money(message = "{error.invalid-money}")
    @NotBlank(message = "{error.missing-dollar-amount}")
    private String monthlyHouseholdIncome;

    private String switchToIncomeByJob;

    @NotEmpty(message = "{error.missing-general}")
    private List<String> householdHomeExpenses;

    @Money(message = "{error.invalid-money}")
    private String homeExpenseRent;
    @Money(message = "{error.invalid-money}")
    private String homeExpenseMortgage;
    @Money(message = "{error.invalid-money}")
    private String homeExpensePhone;
    @Money(message = "{error.invalid-money}")
    private String homeExpenseElectricity;
    @Money(message = "{error.invalid-money}")
    private String homeExpenseWater;
    @Money(message = "{error.invalid-money}")
    private String homeExpenseSewer;
    @Money(message = "{error.invalid-money}")
    private String homeExpenseGarbage;
    @Money(message = "{error.invalid-money}")
    private String homeExpenseGas;
    @Money(message = "{error.invalid-money}")
    private String homeExpenseOil;
    @Money(message = "{error.invalid-money}")
    private String homeExpenseWoodOrCoal;
    @Money(message = "{error.invalid-money}")
    private String homeExpenseCondominiumFees;
    @Money(message = "{error.invalid-money}")
    private String homeExpensePropertyTax;
    @Money(message = "{error.invalid-money}")
    private String homeExpenseHomeownerInsurance;
    @Money(message = "{error.invalid-money}")
    private String homeExpenseOtherHomeExpenses;

    private List<String> ohepRentSituations;

    private String expenseHeatIncludedRent;

    private String expenseSection8Housing;

    @Size(max = 30, message = "{error.character-length}")
    private String electricityAccountNumber;

    private String heatingCompanyName;

    @Size(max = 30, message = "{error.character-length}")
    private String heatingAccountNumber;

    // medical expenses for 60+ or disability
    @NotEmpty(message = "{error.missing-general}")
    private List<String> medicalExpenses;

    @Money(message = "{error.invalid-money}")
    private String medicalExpenseHealthMedicalInsurance;
    @Money(message = "{error.invalid-money}")
    private String medicalExpenseDenturesGlassesEtc;
    @Money(message = "{error.invalid-money}")
    private String medicalExpenseHospitalBills;
    @Money(message = "{error.invalid-money}")
    private String medicalExpenseAttendantCare;
    @Money(message = "{error.invalid-money}")
    private String medicalExpenseMedicalDentalInsurance;
    @Money(message = "{error.invalid-money}")
    private String medicalExpenseTransportationCosts;
    @Money(message = "{error.invalid-money}")
    private String medicalExpenseNursing;
    @Money(message = "{error.invalid-money}")
    private String medicalExpensePharmacy;
    @Money(message = "{error.invalid-money}")
    private String medicalExpenseOther;

    private String hasDependentCareExpenses;

    @Money(message = "{error.invalid-money}")
    @NotBlank(message = "{error.missing-dollar-amount}")
    private String expensesDependentCare;

    private String hasChildSupportExpenses;

    @Money(message = "{error.invalid-money}")
    @NotBlank(message = "{error.missing-dollar-amount}")
    private String expensesChildSupport;

    @NotEmpty(message = "{error.missing-general}")
    private List<String> householdMedicalExpenses;

    @NotBlank(message = "{error.missing-dollar-amount}")
    @Money(message = "{error.invalid-money}")
    @DynamicField
    private String householdMedicalExpenseAmount;

    private String householdMemberEthnicity;

    private List<String> householdMemberRace;

    @NotEmpty(message = "{error.must-accept-terms}")
    private List<String> rightsAndResponsibilitiesAgree;

    @NotBlank(message = "{error.missing-general}")
    private String signature;

    @NotBlank(message = "{confirmation.answer-feedback-question}")
    private String applicationFeedback;

    private String applicationFeedbackDetail;

    // Expedited Snap Start
    private String isApplyingForExpeditedSnap;

    // Minimum App marker
    private String isMinimumApplication;

    // Household Money on Hand
    private String householdMoneyOnHandLessThan100;

    // Expedited Money on Hand Amount
    @Money(message = "{error.invalid-money}")
    @NotBlank(message = "{error.missing-dollar-amount}")
    private String expeditedMoneyOnHandAmount;

    // Expedited Snap Income
    private String incomeLessThan150;

    // Expedited SNAP confirmation Income < Expenses
    private String clientConfirmsInputLessThanExpenses;

    // Household 30 Day Income
    @Money(message = "{error.invalid-money}")
    @NotBlank(message = "{error.missing-dollar-amount}")
    private String householdIncomeLast30Days;

    private String addDocuments;

    @NotBlank(message = "{doc-type.select-a-type}")
    @DynamicField
    private String docType;

    private String someoneHasDrugKingpinFelony;
    private String someoneHasVolumeDrugDealerFelony;
    private String someoneHasSexualOffenceConviction;
    private String someoneIsViolatingParole;
    private String someoneConvictedForLyingAboutBenefits;
    private String someoneConvictedForTradingBenefits;
    private String someoneIsReceivingBenefitsWithFakeID;

    @NotEmpty(message = "{error.must-accept-terms}")
    private List<String> tcaAcknowledgementAgree;

    @NotEmpty(message = "{error.must-accept-terms}")
    private List<String> ohepAcknowledgementAgree;
}
