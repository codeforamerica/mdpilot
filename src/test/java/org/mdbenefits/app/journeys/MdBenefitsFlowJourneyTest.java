package org.mdbenefits.app.journeys;

import com.smartystreets.api.exceptions.SmartyException;
import formflow.library.address_validation.AddressValidationService;
import formflow.library.address_validation.ValidatedAddress;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.enums.ApplicantObjective;
import org.mdbenefits.app.data.enums.CitizenshipStatus;
import org.mdbenefits.app.data.enums.County;
import org.mdbenefits.app.data.enums.EthnicityType;
import org.mdbenefits.app.testutils.AbstractBasePageTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Slf4j
public class MdBenefitsFlowJourneyTest extends AbstractBasePageTest {

    protected static final String RANGE_ERROR_MESSAGE = "Make sure to provide a value between 1 and 100.";

    @MockBean
    private AddressValidationService addressValidationService;

    @Test
    void redirectToMyMDTHINKOnUnsupportedApplicationType() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/selectApplication");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Select application");
        assertThat(testPage.hasErrorText(message("error.missing-general")));

        // should redirect to MyMDTHINK
        testPage.clickElementById("applicationInfo-" + ApplicantObjective.COLLEGE_STUDENT_IN_APP.name());
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("redirect.mdthink.title"));

        // should not redirect
        testPage.navigateToFlowScreen("mdBenefitsFlow/selectApplication");
        testPage.clickElementById("none__checkbox-applicationInfo");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("recertification-check.title"));
    }

    @Test
    void redirectToMyMDTHINKOnUnsupportedCounty() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/county");
        // should redirect to MyMDTHINK
        testPage.selectFromDropdown("county", message(County.OTHER.getLabelSrc()));
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("redirect.mdthink.title"));

        // should not redirect
        testPage.navigateToFlowScreen("mdBenefitsFlow/county");
        testPage.selectFromDropdown("county", message(County.BALTIMORE.getLabelSrc()));
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("offboarding-information.title"));
    }

    @Test
    void personalInformationFlow() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/personalInfo");
        testPage.clickContinue();

        testPage.enter("birthMonth", "01");
        testPage.enter("birthDay", "25");
        testPage.enter("birthYear", "1985");

        assert (testPage.hasErrorText(message("error.missing-firstname")));
        assert (testPage.hasErrorText(message("error.missing-lastname")));

        testPage.enter("firstName", "test");
        testPage.enter("lastName", "test2");

        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("home-address.title"));

    }

    @Test
    void displayRentQuestionWhenRentExpense() {
        loadUserPersonalData();
        testPage.navigateToFlowScreen("mdBenefitsFlow/expensesSignPost");

        assertThat(testPage.getTitle()).isEqualTo(message("expenses-signpost.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("home-expenses.title"));
        testPage.clickElementById("householdHomeExpenses-RENT-label");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("home-expenses-amount.title"));
        testPage.enter("homeExpenseRent", "55");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("ohep-reminder.title"));
        testPage.clickButton("Ok");
    }

    @Test
    void doesNotDisplayAmountWhenMedicalExpenseIsNone() {
        loadUserPersonalData();
        testPage.navigateToFlowScreen("mdBenefitsFlow/householdMedicalExpenses");

        assertThat(testPage.getTitle()).isEqualTo(message("medical-expenses-amount.title"));
        testPage.clickElementById("none__checkbox-medicalExpenses");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("dependentcare.title"));
    }

    @Test
    void incomeFlow() {
        loadUserPersonalData();

        loadHouseHoldData("Third", "User", "12", "22", "1991", true, "123-54-2222",
                "F", true, true, true);
        loadHouseHoldData("Fourth", "User", "01", "23", "1997", true, "111-11-1111",
                "F", true, true, true);
        preloadIncomeScreen();

        assertThat(testPage.getTitle()).isEqualTo(message("income-by-job.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("income-who.title"));
        testPage.clickContinue();
        assert (testPage.hasErrorText(message("error.missing-general")));

        testPage.clickElementById("householdMemberJobAdd-you");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("employer-name.title"));
        testPage.clickContinue();

        assert (testPage.hasErrorText(message("error.missing-general")));
        testPage.enter("employerName", "job1");

        testPage.clickContinue();

        testPage.clickContinue();
        assert (testPage.hasErrorText(message("error.missing-pay-period")));

        testPage.selectRadio("payPeriod", "Every month");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("job-pay-amount.title"));

        testPage.enter("payAmountFor30Days", "a");
        testPage.clickContinue();

        assert (testPage.hasErrorText(message("error.invalid-money")));
        testPage.enter("payAmountFor30Days", "282.99");

        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("income-list.title"));
    }

    @Test
    void testEmailAddresses() {
        preloadCountyScreen(message(County.BALTIMORE.getLabelSrc()));
        loadUserPersonalData();
        loadAddressData();
        testPage.navigateToFlowScreen("mdBenefitsFlow/contactInfo");
        testPage.enter("cellPhoneNumber", "555-456-7891");
        testPage.enter("emailAddress", "test123@com");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("contact-info.title"));
        assert (testPage.hasErrorText(message("contact-info.provide-correct-email")));

        testPage.enter("emailAddress", "test.com@mail");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("contact-info.title"));
        assert (testPage.hasErrorText(message("contact-info.provide-correct-email")));

        testPage.enter("emailAddress", "test123+hello@mailinator.com");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("review-contact-info.title"));
    }

    @Test
    void minimumAppFlow() {
        loadUserPersonalData();
        loadProgramsScreen(List.of("programs-SNAP"));
        loadAddressData();
        loadContactData();
        testPage.navigateToFlowScreen("mdBenefitsFlow/contactInfoReview");
        assertThat(testPage.getTitle()).isEqualTo(message("review-contact-info.title"));

        testPage.clickLink(message("review-contact-info.submit-incomplete"));
        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-start.title"));
        testPage.clickLink(message("expedited-snap-start.no"));

        assertThat(testPage.getTitle()).isEqualTo(message("minimum-app-confirmation.title"));
        testPage.clickLink(message("minimum-app-confirmation.answer-more-questions"));

        assertThat(testPage.getTitle()).isEqualTo(message("household-signpost.title"));
        testPage.goBack();

        testPage.clickButton(message("minimum-app-confirmation.submit-app"));
        assertThat(testPage.getTitle()).isEqualTo(message("legal-stuff.title"));
    }

    @Test
    void expeditedSnapEarlyQualificationNoticeSeasonalWorkerWithLowMoneyOnHand() {
        loadUserPersonalData();
        loadAddressData();
        loadContactData();
        testPage.navigateToFlowScreen("mdBenefitsFlow/expeditedSnapStart");

        // Expedited Snap Start
        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-start.title"));
        testPage.clickButton(message("expedited-snap-start.yes"));

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-seasonal-farmworker.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-money-on-hand.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-qualification-notice.title"));
    }

    @Test
    void expeditedSnapEarlyQualificationNoticeLowMoneyOnHandAndLowIncome() {
        loadUserPersonalData();
        loadAddressData();
        loadContactData();
        testPage.navigateToFlowScreen("mdBenefitsFlow/expeditedSnapStart");
        // Expedited Snap Start
        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-start.title"));
        testPage.clickButton(message("expedited-snap-start.yes"));

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-seasonal-farmworker.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-money-on-hand.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-money-on-hand-amount.title"));
        testPage.clickLink(message("expedited-snap-optional.skip-help-text"));

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-income.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-qualification-notice.title"));
    }

    @Test
    void userCanSkipAllExpeditedSnapQuestions() {
        loadUserPersonalData();
        loadAddressData();
        loadContactData();
        testPage.navigateToFlowScreen("mdBenefitsFlow/expeditedSnapStart");
        // Expedited Snap Start
        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-start.title"));
        testPage.clickButton(message("expedited-snap-start.yes"));

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-seasonal-farmworker.title"));
        testPage.clickLink(message("expedited-snap-optional.skip-help-text"));

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-money-on-hand.title"));
        testPage.clickLink(message("expedited-snap-optional.skip-help-text"));

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-money-on-hand-amount.title"));
        testPage.clickLink(message("expedited-snap-optional.skip-help-text"));

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-income.title"));
        testPage.clickLink(message("expedited-snap-optional.skip-help-text"));

        assertThat(testPage.getTitle()).isEqualTo(message("household-income-last-30-days.title"));
        testPage.clickLink(message("expedited-snap-optional.skip-help-text"));

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-home-expenses.title"));
        testPage.clickLink(message("expedited-snap-optional.skip-help-text"));

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-deferred-notice.title"));
    }

    @Test
    void minAppLinkShowsUpForSNAPApplications() {
        loadProgramsScreen(List.of("programs-SNAP"));
        loadUserPersonalData();
        loadAddressData();
        loadContactData();
        testPage.navigateToFlowScreen("mdBenefitsFlow/contactInfoReview");

        assertThat(testPage.findLinkByText(message("review-contact-info.submit-incomplete"))).isNotNull();
    }

    @Test
    void minAppLinkDoesNotShowUpForNoNSNAPApplication() {
        loadProgramsScreen(List.of("programs-TCA", "programs-RCA"));
        loadUserPersonalData();
        loadAddressData();
        loadContactData();
        testPage.navigateToFlowScreen("mdBenefitsFlow/contactInfoReview");

        assertThat(testPage.findLinkByText(message("review-contact-info.submit-incomplete"))).isNull();
    }

    @Test
    void mainFlowMoneyOnHandQuestionDoesntShowForNonSnapApp() {
        loadProgramsScreen(List.of("programs-TCA", "programs-RCA"));
        loadUserPersonalData();
        loadAddressData();
        loadContactData();

        testPage.navigateToFlowScreen("mdBenefitsFlow/householdHomeExpenses");
        testPage.clickElementById("none__checkbox-householdHomeExpenses");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("ohep-reminder.title"));
    }

    @Test
    void mainFlowMoneyOnHandQuestionShowsUpForSnap() {
        loadProgramsScreen(List.of("programs-TCA", "programs-SNAP"));
        loadUserPersonalData();
        loadAddressData();
        loadContactData();

        testPage.navigateToFlowScreen("mdBenefitsFlow/householdHomeExpenses");
        testPage.clickElementById("none__checkbox-householdHomeExpenses");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-money-on-hand.title"));
    }

    @Test
    void mainFlowHowMuchMoneyOnHandTest() {
        loadProgramsScreen(List.of("programs-TCA", "programs-SNAP"));
        loadUserPersonalData();
        loadAddressData();
        loadContactData();

        testPage.navigateToFlowScreen("mdBenefitsFlow/incomeSignPost");
        assertThat(testPage.getTitle()).isEqualTo(message("income-signpost.title"));
        testPage.clickContinue();

        // SNAP check
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("household-income.title"));
        testPage.clickButton("Yes");

        // householdIncomeByJob
        testPage.clickContinue();

        // householdIncomeWho
        testPage.clickElementById("householdMemberJobAdd-you");
        testPage.clickContinue();

        // householdEmployerName
        testPage.enter("employerName", "test employer");
        testPage.clickContinue();

        // jobPayPeriod
        testPage.clickElementById("payPeriod-Every 2 weeks");
        testPage.clickContinue();

        // jobPayAmount
        testPage.enter("payAmountFor30Days", "200.00");
        testPage.clickContinue();

        testPage.navigateToFlowScreen("mdBenefitsFlow/householdHomeExpenses");
        testPage.clickElementById("householdHomeExpenses-RENT");
        testPage.clickElementById("householdHomeExpenses-PHONE");
        testPage.clickElementById("householdHomeExpenses-ELECTRICITY");
        testPage.clickContinue();

        testPage.enter("homeExpenseRent", "200");
        testPage.enter("homeExpensePhone", "100");
        testPage.enter("homeExpenseElectricity", "120");

        testPage.clickContinue();

        // Expedited SNAP check pages
        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-money-on-hand.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("ohep-reminder.title"));

        testPage.goBack();
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("household-expedited-snap-amount.title"));
        testPage.enter("expeditedMoneyOnHandAmount", "200");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("household-expedited-snap-check.title"));
        testPage.goBack();

        testPage.enter("expeditedMoneyOnHandAmount", "2000");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("ohep-reminder.title"));
    }

    @Test
    void maleApplicantsSkipPregnancyQuestion() {
        loadUserPersonalData();
        testPage.navigateToFlowScreen("mdBenefitsFlow/applicantSex");

        assertThat(testPage.getTitle()).isEqualTo(message("personal-info.sex.title"));
        testPage.selectRadio("applicantSex", "M");
        testPage.clickContinue();
        await().atMost(5, TimeUnit.SECONDS).until(
                () -> testPage.elementExistsById("form-submit-button")
        );

        assertThat(testPage.getTitle()).isEqualTo(message("applicant-school-enrollment.title"));
        testPage.selectRadio("applicantIsEnrolledInSchool", "Yes");

    }

    @Test
    void femaleApplicantSeesPregnancyQuestion() {
        loadUserPersonalData();
        testPage.navigateToFlowScreen("mdBenefitsFlow/applicantSex");

        assertThat(testPage.getTitle()).isEqualTo(message("personal-info.sex.title"));
        testPage.selectRadio("applicantSex", "F");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("personal-info.pregnancy.title"));
    }

    @Test
    void preferNotToAnswerApplicantSeesPregnancyQuestion() {
        loadUserPersonalData();
        testPage.navigateToFlowScreen("mdBenefitsFlow/applicantSex");

        assertThat(testPage.getTitle()).isEqualTo(message("personal-info.sex.title"));
        testPage.selectRadio("applicantSex", "Other");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("personal-info.pregnancy.title"));
    }

    @Test
    void docUploadSkipTest() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/docUploadIntro");
        assertThat(testPage.getTitle()).isEqualTo(message("doc-upload-intro.title"));
        testPage.clickButton(message("doc-upload-intro.skip"));
        assertThat(testPage.getTitle()).isEqualTo(message("legal-stuff.title"));
    }

    @Test
    void sensitiveConvictionQuestionsArePreCheckedToFalseTest() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/sensitiveConvictionQuestions");
        List<WebElement> selectedElements = driver.findElements(By.cssSelector("input[type='radio']"));
        selectedElements.stream()
                .filter(element -> element.getAttribute("value").equals("false"))
                .forEach(element -> {
                    assertThat(element.getAttribute("checked")).isEqualTo("true");
                });
        selectedElements.stream()
                .filter(element -> element.getAttribute("value").equals("true"))
                .forEach(element -> {
                    assertThat(element.getAttribute("checked")).isNull();
                });
    }

    @Test
    void showTCAConditionsIfTCASelected() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/selectHelpNeeded");
        testPage.clickElementById("helpNeeded-FOOD");
        testPage.clickElementById("helpNeeded-CHILDREN");
        testPage.clickContinue();

        testPage.clickElementById("programs-TCA");
        testPage.clickContinue();

        testPage.navigateToFlowScreen("mdBenefitsFlow/tcaOhepAgreement");
        assertThat(testPage.findElementById("tcaAcknowledgementAgree-true")).isNotNull();
    }

    @Test
    void skipTCAConditionsIfTCANotSelected() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/selectHelpNeeded");
        testPage.clickElementById("helpNeeded-FOOD");
        testPage.clickElementById("helpNeeded-CHILDREN");
        testPage.clickContinue();
        testPage.clickElementById("programs-SNAP");
        testPage.clickContinue();

        testPage.navigateToFlowScreen("mdBenefitsFlow/tcaOhepAgreement");
        assertThat(testPage.elementDoesNotExistById("programs-TCA")).isTrue();
    }

    @Test
    void completeFlowApplicantOnly() {
        assertThat(testPage.getTitle()).isEqualTo("Maryland Benefits Application");

        testPage.clickButton("Apply Now");
        assertThat(testPage.getTitle()).isEqualTo("County");

        testPage.selectFromDropdown("county", message(County.BALTIMORE.getLabelSrc()));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("offboarding-information.title"));
        testPage.clickButton(message("offboarding-information.continue-text"));

        assertThat(testPage.getTitle()).isEqualTo("Select application");

        testPage.clickElementById("none__checkbox-applicationInfo");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("recertification-check.title"));
        testPage.clickElementById("needsToReCertify-true");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("redirect.mdthink.title"));
        testPage.goBack();

        testPage.clickElementById("needsToReCertify-false");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("continue-with-site.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo("Select help");
        testPage.clickElementById("helpNeeded-FOOD");
        testPage.clickElementById("helpNeeded-CHILDREN");
        testPage.clickContinue();

        // choose program
        assertThat(testPage.getTitle()).isEqualTo(message("choose-programs.title"));
        testPage.clickElementById("programs-TCA");

        testPage.clickContinue();

        // OHEP Notice
        assertThat(testPage.getTitle()).isEqualTo(message("ohep-notice.title"));
        testPage.clickButton("Ok");

        // How this works
        assertThat(testPage.getTitle()).isEqualTo(message("how-this-works.title"));
        testPage.clickContinue();

        // Personal Info
        assertThat(testPage.getTitle()).isEqualTo(message("personal-info.title"));
        testPage.enter("firstName", "test");
        testPage.enter("lastName", "test2");
        testPage.enter("birthMonth", "12");
        testPage.enter("birthDay", "25");
        testPage.enter("birthYear", "1985");
        testPage.clickContinue();

        // Home Address
        assertThat(testPage.getTitle()).isEqualTo(message("home-address.title"));

        testPage.enter("homeAddressStreetAddress1", "test");
        testPage.enter("homeAddressCity", "test2");
        testPage.enter("homeAddressZipCode", "12");
        testPage.selectFromDropdown("homeAddressState", "CO - Colorado");
        testPage.enter("homeAddressZipCode", "12526");

        testPage.clickContinue();

        // Mailing Address
        assertThat(testPage.getTitle()).isEqualTo(message("mailing-address.title"));

        testPage.enter("mailingAddressStreetAddress1", "test");
        testPage.enter("mailingAddressCity", "test 2");
        testPage.selectFromDropdown("mailingAddressState", "CO - Colorado");
        testPage.enter("mailingAddressZipCode", "12455");

        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("verify-address.title"));

        testPage.clickButton("Use this address");

        assertThat(testPage.getTitle()).isEqualTo(message("contact-info.title"));

        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("contact-info-nudge.title"));

        testPage.clickButton(message("contact-info-nudge.add-contact-info"));

        assertThat(testPage.getTitle()).isEqualTo(message("contact-info.title"));
        testPage.enter("cellPhoneNumber", "123-456-7891");
        testPage.enter("emailAddress", "mail@mail.com");

        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("review-contact-info.title"));

        testPage.clickButton(message("review-contact-info.this-looks-correct"));

        // Individual Only Household
        assertThat(testPage.getTitle()).isEqualTo(message("household-signpost.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("applicant-applying.title"));
        testPage.selectRadio("isApplicantApplying", "Yes");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("applicant-applying.citizenship.title"));
        testPage.clickLink("Why do we ask for SSNs?");
        assertThat(testPage.getTitle()).isEqualTo(message("ssn-why.title"));
        testPage.clickLink("Go Back");
        assertThat(testPage.getTitle()).isEqualTo(message("applicant-applying.citizenship.title"));
        testPage.selectFromDropdown("applicantCitizenshipStatus", message("citizenship.types.us-citizen"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("personal-info.sex.title"));
        testPage.selectRadio("applicantSex", "F");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("personal-info.pregnancy.title"));
        testPage.selectRadio("isApplicantPregnant", "Yes");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("applicant-school-enrollment.title"));
        testPage.selectRadio("applicantIsEnrolledInSchool", "Yes");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("applicant-disability.title"));
        testPage.selectRadio("applicantHasDisability", "Yes");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo((message("race-ethnicity-selection.title")));
        testPage.clickElementById("applicantRace-ASIAN");
        testPage.clickElementById("applicantRace-WHITE");
        testPage.selectRadio("applicantEthnicity", EthnicityType.HISPANIC_OR_LATINO.name());
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("household-question.title"));
        testPage.clickButton(message("household-question.no-text"));

        assertThat(testPage.getTitle()).isEqualTo(message("seasonal-farmworker.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("income-signpost.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("household-income.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("additional-income.title"));
        testPage.clickElementById("none__checkbox-additionalIncome");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("expenses-signpost.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("home-expenses.title"));
        testPage.clickElementById("none__checkbox-householdHomeExpenses");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("ohep-reminder.title"));
        testPage.clickButton("Ok");
        
        assertThat(testPage.getTitle()).isEqualTo(message("ohep-electricity.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("ohep-heating.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("ohep-thank-you.title"));
        testPage.clickButton("Next");

        assertThat(testPage.getTitle()).isEqualTo(message("medical-expenses.title"));
        testPage.clickElementById("medicalExpenses-HEALTH_MEDICAL_INSURANCE-label");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("medical-expenses.title"));
        testPage.enter("medicalExpenseHealthMedicalInsurance", "500");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("dependentcare.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("childsupportexpenses.title"));
        testPage.clickButton("No");

        // Upload documents
        assertThat(testPage.getTitle()).isEqualTo(message("doc-upload-intro.title"));
        testPage.clickButton(message("doc-upload-intro.continue"));

        assertThat(testPage.getTitle()).isEqualTo(message("how-to-add-documents.title"));
        testPage.clickButton(message("doc-upload-instructions.add-documents"));

        assertThat(testPage.getTitle()).isEqualTo(message("upload-documents.title"));
        assertThat(testPage.findElementById("form-submit-button").getAttribute("class").contains("display-none")).isTrue();
        uploadJpgFile();
        // give the system time to remove the "display-none" class.
        await().atMost(5, TimeUnit.SECONDS).until(
                () -> !(testPage.findElementById("form-submit-button").getAttribute("class").contains("display-none"))
        );

        assertThat(driver.findElement(By.className("filename-text-name")).getText()).isEqualTo("test");
        assertThat(driver.findElement(By.className("filename-text-ext")).getText()).isEqualTo(".jpeg");

        testPage.clickButton("I'm done");

        // Confirm doc upload submit
        assertThat(testPage.getTitle()).isEqualTo(message("doc-submit-confirmation.title"));
        testPage.clickButton("Next");

        assertThat(testPage.getTitle()).isEqualTo(message("legal-stuff.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("sensitive-conviction-questions-intro.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("sensitive-conviction-questions.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("rights-and-responsibilities.title"));
        testPage.clickElementById("rightsAndResponsibilitiesAgree-true");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("tca-ohep-agreement.title"));
        testPage.clickElementById("tcaAcknowledgementAgree-true");
        testPage.clickElementById("ohepAcknowledgementAgree-true");
        testPage.clickContinue();

        // signature
        assertThat(testPage.getTitle()).isEqualTo(message("signature-title"));
        testPage.enter("signature", "My signature");
        testPage.clickButton(message("signature-submit"));

        // Confirmation page
        assertThat(testPage.getTitle()).isEqualTo(message("confirmation.title"));
    }

    @Test
    void completeFlowWithHousehold() {
        loadUserPersonalData();

        testPage.navigateToFlowScreen("mdBenefitsFlow/householdHasAdditionalMembers");
        assertThat(testPage.getTitle()).isEqualTo(message("household-question.title"));
        testPage.clickButton(message("general.inputs.yes"));

        assertThat(testPage.getTitle()).isEqualTo(message("household-list.title"));
        testPage.clickButton("Add someone");

        assertThat(testPage.getTitle()).isEqualTo(message("household-info.title"));
        testPage.enter("householdMemberFirstName", "roomy");
        testPage.enter("householdMemberLastName", "smith");
        testPage.selectFromDropdown("householdMemberRelationship", message("household-info.relationship.child"));
        testPage.selectRadio("householdMemberApplyingForBenefits", "Yes");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("household-additional-info.title"));
        testPage.selectFromDropdown("householdMemberCitizenshipStatus", message("citizenship.types.us-citizen"));

        testPage.enter("householdMemberBirthMonth", "12");
        testPage.enter("householdMemberBirthDay", "25");
        testPage.enter("householdMemberBirthYear", "1985");

        testPage.enter("householdMemberSsn", "123456789");
        testPage.selectRadio("householdMemberSex", "F");
        testPage.selectRadio("householdMemberIsPregnant", "Yes");
        testPage.selectRadio("householdMemberEnrolledInSchool", "Yes");
        testPage.selectRadio("householdMemberHasDisability", "Yes");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("household-race-and-ethnicity.title"));
        testPage.clickElementById("householdMemberRace-WHITE");
        testPage.selectRadio("householdMemberEthnicity", EthnicityType.HISPANIC_OR_LATINO.name());
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("household-list.title"));
        testPage.clickButton("I'm done");

        assertThat(testPage.getTitle()).isEqualTo(message("seasonal-farmworker.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("income-signpost.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("household-income.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("additional-income.title"));
        testPage.clickElementById("none__checkbox-additionalIncome");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("expenses-signpost.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("home-expenses.title"));
        testPage.clickElementById("none__checkbox-householdHomeExpenses");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("ohep-reminder.title"));
        testPage.clickButton("Ok");

        assertThat(testPage.getTitle()).isEqualTo(message("ohep-electricity.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("ohep-heating.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("ohep-thank-you.title"));
        testPage.clickButton("Next");

        assertThat(testPage.getTitle()).isEqualTo(message("medical-expenses.title"));
        testPage.clickElementById("medicalExpenses-HEALTH_MEDICAL_INSURANCE-label");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("medical-expenses.title"));
        testPage.enter("medicalExpenseHealthMedicalInsurance", "500");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("dependentcare.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("childsupportexpenses.title"));
        testPage.clickButton("No");

        // Upload documents
        assertThat(testPage.getTitle()).isEqualTo(message("doc-upload-intro.title"));
        testPage.clickButton(message("doc-upload-intro.continue"));
    }

    @Test
    void testValidMailingAddress() throws SmartyException, IOException, InterruptedException {
        ValidatedAddress validatedAddress = new ValidatedAddress("mailingAddressStreetAddress1_validated",
                "mailingAddressStreetAddress2_validated", "mailingAddressCity_validated", "mailingAddressState_validated",
                "mailingAddressZipCode_validated");
        when(addressValidationService.validate(any())).thenReturn(Map.of("mailingAddress", validatedAddress));
        testPage.navigateToFlowScreen("mdBenefitsFlow/mailingAddress");
        testPage.enter("mailingAddressStreetAddress1", "1445 34th Ave");
        testPage.enter("mailingAddressStreetAddress2", "Apt A");
        testPage.enter("mailingAddressCity", "Oakland");
        testPage.enter("mailingAddressZipCode", "94601");
        testPage.selectFromDropdown("mailingAddressState", "CA - California");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("verify-address.title"));
        assertThat(driver.findElements(By.className("notice--warning")).get(0).getText()).isEqualTo(
                message("select-address.notice"));
    }

    @Test
    void testInvalidMailingAddress() throws SmartyException, IOException, InterruptedException {
        when(addressValidationService.validate(any())).thenReturn(Map.of());
        testPage.navigateToFlowScreen("mdBenefitsFlow/mailingAddress");
        testPage.enter("mailingAddressStreetAddress1", "1445 34th Ave");
        testPage.enter("mailingAddressStreetAddress2", "Apt A");
        testPage.enter("mailingAddressCity", "Oakland");
        testPage.enter("mailingAddressZipCode", "94601");
        testPage.selectFromDropdown("mailingAddressState", "CA - California");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("verify-address.title"));
        assertThat(driver.findElements(By.className("notice--warning")).get(0).getText()).isEqualTo(
                message("verify-address.notice"));
    }

    @Test
    void testMailingAddressIsSameAsHomeAddress() throws SmartyException, IOException, InterruptedException {
        ValidatedAddress validatedAddress = new ValidatedAddress("mailingAddressStreetAddress1_validated",
                "mailingAddressStreetAddress2", "mailingAddressCity", "mailingAddressState", "mailingAddressZipCode");
        when(addressValidationService.validate(any())).thenReturn(Map.of("mailingAddress", validatedAddress));
        testPage.navigateToFlowScreen("mdBenefitsFlow/homeAddress");
        testPage.enter("homeAddressStreetAddress1", "1445 34th Ave");
        testPage.enter("homeAddressStreetAddress2", "Apt A");
        testPage.enter("homeAddressCity", "Oakland");
        testPage.enter("homeAddressZipCode", "94601");
        testPage.selectFromDropdown("homeAddressState", "CA - California");
        testPage.clickContinue();
        testPage.clickElementById("sameAsHomeAddress-true");
        await().atMost(4, TimeUnit.SECONDS)
                .until(() -> testPage.findElementById("mailingAddressStreetAddress1").getAttribute("value")
                        .equals("1445 34th Ave"));
        assertThat(testPage.findElementById("mailingAddressStreetAddress1").getAttribute("value")).isEqualTo("1445 34th Ave");
        assertThat(testPage.findElementById("mailingAddressStreetAddress2").getAttribute("value")).isEqualTo("Apt A");
        assertThat(testPage.findElementById("mailingAddressCity").getAttribute("value")).isEqualTo("Oakland");
        assertThat(testPage.findElementById("mailingAddressZipCode").getAttribute("value")).isEqualTo("94601");
        assertThat(testPage.findElementById("mailingAddressState").getAttribute("value")).isEqualTo("CA");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("verify-address.title"));
        assertThat(driver.findElements(By.className("notice--warning")).get(0).getText()).isEqualTo(
                message("select-address.notice"));
    }

    @Test
    void shouldValidateMailingAddressWhenNoHomeAddress() throws SmartyException, IOException, InterruptedException {
        ValidatedAddress validatedAddress = new ValidatedAddress("mailingAddressStreetAddress1_validated",
                "mailingAddressStreetAddress2", "mailingAddressCity", "mailingAddressState", "mailingAddressZipCode");
        when(addressValidationService.validate(any())).thenReturn(Map.of("mailingAddress", validatedAddress));
        testPage.navigateToFlowScreen("mdBenefitsFlow/homeAddress");
        testPage.clickElementById("noHomeAddress-true");
        testPage.clickContinue();
        testPage.enter("mailingAddressStreetAddress1", "1445 34th Ave");
        testPage.enter("mailingAddressStreetAddress2", "Apt A");
        testPage.enter("mailingAddressCity", "Oakland");
        testPage.enter("mailingAddressZipCode", "94601");
        testPage.selectFromDropdown("mailingAddressState", "CA - California");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("verify-address.title"));
        assertThat(driver.findElements(By.className("notice--warning")).get(0).getText()).isEqualTo(
                message("select-address.notice"));
    }

    @Test
    void shouldEditMailingAddressWhenNoHomeAddressAndEditRequested() throws SmartyException, IOException, InterruptedException {
        when(addressValidationService.validate(any())).thenReturn(Map.of());
        testPage.navigateToFlowScreen("mdBenefitsFlow/homeAddress");
        testPage.clickElementById("noHomeAddress-true");
        testPage.clickContinue();
        testPage.enter("mailingAddressStreetAddress1", "123 Main Street");
        testPage.enter("mailingAddressStreetAddress2", "Apt A");
        testPage.enter("mailingAddressCity", "Some City");
        testPage.enter("mailingAddressZipCode", "12345");
        testPage.selectFromDropdown("mailingAddressState", "CA - California");
        testPage.clickContinue();

        testPage.clickButton(message("address-validation.button.edit-my-address"));
        // make sure we go to the mailing address page and that there is no option
        // to click "Same as my current living address"
        assertThat(testPage.getTitle()).isEqualTo(message("mailing-address.title"));
        assertThat(testPage.elementDoesNotExistById("sameAsHomeAddress-true")).isTrue();
    }

    @Test
    void howThisWorksShouldShowCorrectEmailForCounty() {
        preloadCountyScreen(message(County.BALTIMORE.getLabelSrc()));
        testPage.navigateToFlowScreen("mdBenefitsFlow/howThisWorks");
        testPage.findAccordionByButtonText("How to add documents").click();
        assertThat(testPage.findElementById("county-document-email").getText()).contains("baltimorecounty.dmc@maryland.gov");
        preloadCountyScreen(message(County.QUEEN_ANNES.getLabelSrc()));
        testPage.navigateToFlowScreen("mdBenefitsFlow/howThisWorks");
        testPage.findAccordionByButtonText("How to add documents").click();
        assertThat(testPage.findElementById("county-document-email").getText()).contains("qacfia.customeraccount@maryland.gov");
    }

    @Test
    void allHouseholdMemberShouldBeOnOHEPPagesEvenIfNotApplying() {
        preloadCountyScreen(message(County.BALTIMORE.getLabelSrc()));
        loadUserPersonalData();
        loadHouseHoldData("Second", "User", "12", "22", "1991", true,
                "123-22-2222", "M", false, false, false);
        loadHouseHoldData("Third", "User", "01", "23", "1997", false,
                null, null, false, false, false);

        testPage.navigateToFlowScreen("mdBenefitsFlow/ohepElectricity");
        // make sure both family members are there, even those not technically applying
        // selectRadio will throw an error if it cannot find a selection on the input.
        testPage.selectRadio("electricityAccountOwner", "test test2");
        testPage.selectRadio("electricityAccountOwner", "Second User");
        testPage.selectRadio("electricityAccountOwner", "Third User");
    }

    @Test
    void shouldBeAbleToSkipOHEPUtilityPages() {
        preloadCountyScreen(message(County.BALTIMORE.getLabelSrc()));
        loadUserPersonalData();
        loadHouseHoldData("Second", "User", "12", "22", "1991", true,
                "111-11-1111", "F", true, true, true);
        loadHouseHoldData("Third", "User", "01", "23", "1997", false,
                null, null, false, false, false);

        testPage.navigateToFlowScreen("mdBenefitsFlow/ohepElectricity");

        testPage.clickLink("I don't know");
        assertThat(testPage.getTitle()).isEqualTo(message("ohep-heating.title"));

        testPage.clickLink("I don't know");
        assertThat(testPage.getTitle()).isEqualTo(message("ohep-thank-you.title"));

        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("medical-expenses-amount.title"));
    }

    @Test
    void confirmationPageWithNoFeedback() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/confirmation");
        testPage.clickButton(message("confirmation.done-button"));
        assertThat(testPage.getElementText("header")).isEqualTo(message("final-page.header.opt2"));
        await().atMost(10, TimeUnit.SECONDS).until(
                () -> testPage.elementExistsById("apply_now")
        );
    }

    @Test
    void confirmationPageWithFeedback() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/confirmation");
        testPage.selectRadio("applicationFeedback", "Very easy");
        testPage.clickButton(message("confirmation.submit-feedback"));
        assertThat(testPage.getElementText("header")).isEqualTo(message("final-page.header.opt1"));
        await().atMost(10, TimeUnit.SECONDS).until(
                () -> testPage.elementExistsById("apply_now")
        );
    }

    @Test
    void confirmationPageShouldShowEmailMsg() {
        preloadCountyScreen(message(County.BALTIMORE.getLabelSrc()));
        loadUserPersonalData();
        loadAddressData();
        loadContactData(true);

        testPage.navigateToFlowScreen("mdBenefitsFlow/signature");
        assertThat(testPage.getTitle()).isEqualTo(message("signature-title"));
        testPage.enter("signature", "My signature");
        testPage.clickButton(message("signature-submit"));
        assertThat(testPage.getTitle()).isEqualTo(message("confirmation.title"));
        assertThat(testPage.findElementTextById("email-confirmation-info")).isEqualTo(
                message("confirmation.email-info"));
    }

    @Test
    void confirmationPageShouldNotShowEmailMsg() {
        preloadCountyScreen(message(County.BALTIMORE.getLabelSrc()));
        loadUserPersonalData();
        loadAddressData();
        loadContactData(false);

        testPage.navigateToFlowScreen("mdBenefitsFlow/signature");
        assertThat(testPage.getTitle()).isEqualTo(message("signature-title"));
        testPage.enter("signature", "My signature");
        testPage.clickButton(message("signature-submit"));
        assertThat(testPage.getTitle()).isEqualTo(message("confirmation.title"));
        assertThat(testPage.elementExistsById("email-confirmation-info")).isFalse();
    }

    void loadUserPersonalData() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/personalInfo");

        testPage.enter("firstName", "test");
        testPage.enter("middleName", "MiddleName");
        testPage.enter("lastName", "test2");
        testPage.enter("birthMonth", "12");
        testPage.enter("birthDay", "25");
        testPage.enter("birthYear", "1985");
        testPage.clickContinue();
    }

    void loadHouseHoldData(String firstName, String lastName, String month,
            String day, String year, boolean isApplying, String ssn, String sex,
            boolean isPregnant, boolean isEnrolledInSchool, boolean hasDisability) {
        testPage.navigateToFlowScreen("mdBenefitsFlow/householdInfo");
        testPage.enter("householdMemberFirstName", firstName);
        testPage.enter("householdMemberLastName", lastName);
        testPage.selectFromDropdown("householdMemberRelationship", message("household-info.relationship.child"));
        testPage.selectRadio("householdMemberApplyingForBenefits", isApplying ? "Yes" : "No");
        testPage.clickContinue();

        if (isApplying == false) {
            return;
        }

        testPage.selectFromDropdown("householdMemberCitizenshipStatus", message(CitizenshipStatus.US_CITIZEN.getLabelSrc()));
        testPage.enter("householdMemberSsn", ssn);
        testPage.enter("householdMemberBirthMonth", month);
        testPage.enter("householdMemberBirthDay", day);
        testPage.enter("householdMemberBirthYear", year);

        testPage.selectRadio("householdMemberSex", sex);
        testPage.selectRadio("householdMemberIsPregnant", isPregnant ? "Yes" : "No");
        testPage.selectRadio("householdMemberEnrolledInSchool", isEnrolledInSchool ? "Yes" : "No");
        testPage.selectRadio("householdMemberHasDisability", hasDisability ? "Yes" : "No");
        testPage.clickContinue();
        // race/ethnicity (all optional)
        testPage.clickContinue();
    }

    void loadAddressData() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/homeAddress");
        testPage.enter("homeAddressStreetAddress1", "123 Test St");
        testPage.enter("homeAddressCity", "Testland");
        testPage.enter("homeAddressZipCode", "12345");
        testPage.selectFromDropdown("homeAddressState", "LA - Louisiana");
        testPage.clickContinue();
        testPage.clickElementById("sameAsHomeAddress-true");
        testPage.clickContinue();
    }

    void loadContactData() {
        loadContactData(true);
    }

    void loadContactData(boolean includeEmail) {
        testPage.navigateToFlowScreen("mdBenefitsFlow/contactInfo");
        if (includeEmail) {
            testPage.enter("emailAddress", "test123@mailinator.com");
        }
        testPage.enter("cellPhoneNumber", "555-456-7891");
        testPage.clickContinue();
    }

    void preloadIncomeScreen() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/incomeSignPost");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("household-income.title"));
        testPage.clickButton("Yes");
    }

    void preloadCountyScreen(String county) {
        testPage.navigateToFlowScreen("mdBenefitsFlow/county");
        testPage.selectFromDropdown("county", county);
        testPage.clickContinue();
    }

    void loadProgramsScreen(List<String> programs) {
        testPage.navigateToFlowScreen("mdBenefitsFlow/selectHelpNeeded");
        testPage.clickElementById("helpNeeded-FOOD");
        testPage.clickElementById("helpNeeded-REFUGEE");
        testPage.clickContinue();
        for (String program : programs) {
            testPage.clickElementById(program);
        }
        testPage.clickContinue();
    }
}
