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

    @NotBlank(message = "{error.missing-lastname}")
    private String lastName;

    private String otherNames;

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
    private String phoneNumber;

    @Pattern(regexp = "^\\(\\d{3}\\) \\d{3}-\\d{4}$", message = "{error.invalid-phone}")
    private String cellPhoneNumber;

    @Pattern(regexp = "^\\(\\d{3}\\) \\d{3}-\\d{4}$", message = "{error.invalid-phone}")
    private String workPhoneNumber;

    private String wantsReminders;

    private String identifiesAsDeaf;

    private String preferredCommsMethod;

    private String emailAddress;

    private List<String> remindersMethod;

    // Household
    private String multiplePersonHousehold;

    // Applicant Tell Us More About Yourself
    @NotBlank(message = "{error.missing-general}")
    private String isApplicantApplying;

    private String applicantSSN;

    @NotBlank(message = "{error.missing-general}")
    private String applicantSex;

    private String isApplicantPregnant;

    private String applicantIsEnrolledInSchool;


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

    private String householdMemberMaritalStatus;

    private String householdMemberHighestEducation;
    
    // TODO this field needs to be updated where used as we are actually using applicantSSN
    @Size(min = 11, max = 11, message = "{error.invalid-ssn}")
    private String ssn;

    @Size(min = 11, max = 11, message = "{error.invalid-ssn}")
    @DynamicField
    private String householdMemberSsn;


    // SNAP
    private String buyPrepareMealsSeparateIndicator;

    private String preparesFood;

    private String migrantOrSeasonalFarmWorkerInd;

    @NotBlank
    private String allAreCitizens;

    @NotBlank
    private String applicantCitizenshipStatus;

    @DynamicField
    private String citizenshipStatus;

    // The previous field (citizenshipStatus) is used as a wildcard to help populate
    // this field below, which is put in the "household[]" members data and not left as
    // a top-level field in the json.
    private String householdMemberCitizenshipStatus;

    private String veteranInd;

    private String veterans;

    private String mealInd;

    private String meals;


    //  Sensitive Questions
    private String householdHasPersonalSituations;

    private String personalSituationsHouseholdUUID;

    private List<String> personalSituationsListed;

    private String householdHasDomesticViolenceSituation;

    private String householdHasCriminalJusticeSituation;

    @NotEmpty(message = "{error.missing-general}")
    @DynamicField
    private List<String> personalSituations;

    // Income
    private String householdSearchingForJob;

    @NotEmpty(message = "{error.missing-general}")
    private List<String> jobSearch;

    private String workDisqualificationInd;

    private String selfEmploymentIncome;

    @NotBlank(message = "{error.missing-general}")
    private String householdMemberJobAdd;

    @NotBlank(message = "{error.missing-general}")
    private String employerName;

    private String selfEmployed;

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

    @NotEmpty
    private List<String> moneyOnHandTypes;

    @DynamicField
    private String moneyOnHandOwner;

    @Money
    @DynamicField
    private String moneyOnHandValue;

    @Money(message = "{error.invalid-money}")
    @NotBlank(message = "{error.missing-dollar-amount}")
    private String monthlyHouseholdIncome;

    private String switchToIncomeByJob;

    @NotEmpty(message = "{error.missing-general}")
    private List<String> householdHomeExpenses;

    @NotBlank(message = "{error.missing-dollar-amount}")
    @Money(message = "{error.invalid-money}")
    @DynamicField
    private String householdHomeExpenseAmount;

    @NotEmpty(message = "{error.missing-general}")
    private List<String> householdUtilitiesExpenses;

    @NotBlank(message = "{error.missing-dollar-amount}")
    @Money(message = "{error.invalid-money}")
    @DynamicField
    private String householdUtilitiesExpenseAmount;

    private String receivesEnergyAssistance;

    private String assistanceThroughLiheap;

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

    private String hasElderlyCareExpenses;

    @Money(message = "{error.invalid-money}")
    @NotBlank(message = "{error.missing-dollar-amount}")
    private String expensesElderlyCare;

    // WIC / ECE
    private String interestedInEceInd;
    private String interestedInWicInd;
    private String adultsWorking;
    private String guardiansHaveDisabilityInd;

    // Final Screen
    private String needsNewEbtCard;

    private String authorizedRepresentative;

    private String authorizedRepCommsAuthorization;

    private String authorizedRepMailAuthorization;

    private String authorizedRepSpendingAuthorization;

    @NotBlank(message = "{error.missing-firstname}")
    private String authorizedRepFirstName;

    @NotBlank(message = "{error.missing-general}")
    private String votingRegistrationRequested;

    private String permissionToAskAboutRace;

    private String ethnicitySelected;

    @DynamicField
    private String householdMemberEthnicity;

    private List<String> raceSelected;

    @DynamicField
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
