package org.mdbenefits.app.inputs;

import formflow.library.data.FlowInputs;
import formflow.library.data.annotations.DynamicField;
import formflow.library.data.annotations.Money;
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

    // Verify Address
    private String verifyAddress;

    // Select Address
    private String selectAddress;

    //Contact Info
    @Pattern(regexp = "^\\(\\d{3}\\) \\d{3}-\\d{4}$", message = "{error.invalid-phone}")
    private String cellPhoneNumber;

    @Pattern(regexp = "^\\(\\d{3}\\) \\d{3}-\\d{4}$", message = "{error.invalid-phone}")
    private String homePhoneNumber;

    @Pattern(regexp = "^\\(\\d{3}\\) \\d{3}-\\d{4}$", message = "{error.invalid-phone}")
    private String workPhoneNumber;

    private String textingCellNumberIsOkay;

    private String emailAddress;

    // Household
    private String multiplePersonHousehold;

    // Applicant Tell Us More About Yourself
    @NotBlank(message = "{error.missing-general}")
    private String isApplicantApplying;

    private String applicantSSN;

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

    @NotBlank(message = "{error.missing-general}")
    @Size(min = 11, max = 11, message = "{error.invalid-ssn}")
    private String householdMemberSsn;

    @NotBlank(message = "{error.missing-general}")
    private String householdMemberEnrolledInSchool;

    @NotBlank(message = "{error.missing-general}")
    private String householdMemberIsPregnant;

    @NotBlank(message = "{error.missing-general}")
    private String householdMemberHasDisability;

    // SNAP
    private String migrantOrSeasonalFarmWorkerInd;

    @NotBlank
    private String applicantCitizenshipStatus;

    // Income
    private String householdHasIncome;

    private String incomeLessThan150;

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
    private String payPeriodAmount;

    @NotEmpty(message = "{error.missing-general}")
    private List<String> additionalIncome;

    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String additionalIncomeAlimony;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String additionalIncomeChildSupport;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String additionalIncomeFriendsAndFamily;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String additionalIncomePensionRetirement;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String additionalIncomeSSI;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String additionalIncomeSS;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String additionalIncomeUnemployment;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String additionalIncomeVeteransBenefits;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String additionalIncomeWorkersComp;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
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
    @NotEmpty(message = "{error.missing-general}")
    private String homeExpenseRent;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String homeExpenseMortgage;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String homeExpensePhone;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String homeExpenseElectricity;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String homeExpenseWater;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String homeExpenseSewer;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String homeExpenseGarbage;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String homeExpenseGas;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String homeExpenseOil;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String homeExpenseWoodOrCoal;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String homeExpenseCondominiumFees;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String homeExpensePropertyTax;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String homeExpenseHomeownerInsurance;
    @Money(message = "{error.invalid-money}")
    @NotEmpty(message = "{error.missing-general}")
    private String homeExpenseOtherHomeExpenses;

    private List<String> ohepRentSituations;

    private String expenseHeatIncludedRent;

    private String expenseSection8Housing;

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

    // WIC / ECE
    private String interestedInEceInd;
    private String interestedInWicInd;
    private String adultsWorking;
    private String guardiansHaveDisabilityInd;

    // Final Screen
    @NotBlank(message = "{error.missing-general}")
    private String votingRegistrationRequested;


    private String householdMemberEthnicity;

    private List<String> householdMemberRace;

    @NotEmpty(message = "{error.must-accept-terms}")
    private List<String> rightsAndResponsibilitiesAgree;

    @NotBlank(message = "{error.missing-general}")
    private String signature;

    @NotBlank(message = "{final-confirmation.answer-feedback-question}")
    private String digitalAssisterFeedback;

    private String digitalAssisterFeedbackDetail;


    // Expedited Snap Start
    private String isApplyingForExpeditedSnap;

    // Household 30 Day Income
    @Money(message = "{error.invalid-money}")
    @NotBlank(message = "{error.missing-dollar-amount}")
    private String householdIncomeLast30Days;

    // Household Money on Hand
    private String householdMoneyOnHand;

    // Expedited Money on Hand Amount
    @Money(message = "{error.invalid-money}")
    @NotBlank(message = "{error.missing-dollar-amount}")
    private String expeditedMoneyOnHandAmount;

    // Household Rent
    private String householdPaysRent;

    // Household Rent Amount
    @Money(message = "{error.invalid-money}")
    @NotBlank(message = "{error.missing-dollar-amount}")
    private String householdRentAmount;

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
