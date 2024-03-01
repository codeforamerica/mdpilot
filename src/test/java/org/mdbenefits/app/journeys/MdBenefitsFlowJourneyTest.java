package org.mdbenefits.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.smartystreets.api.exceptions.SmartyException;
import formflow.library.address_validation.AddressValidationService;
import formflow.library.address_validation.ValidatedAddress;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.enums.ApplicantObjective;
import org.mdbenefits.app.data.enums.EthnicityType;
import org.mdbenefits.app.testutils.AbstractBasePageTest;
import org.mdbenefits.app.data.enums.CitizenshipStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.mock.mockito.MockBean;

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
        testPage.clickElementById("none__checkbox");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("help-needed.title"));
    }

    @Test
    void redirectToMyMDTHINKOnUnsupportedCounty() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/county");
        // should redirect to MyMDTHINK
        testPage.selectFromDropdown("county", "Frederick County");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("redirect.mdthink.title"));

        // should not redirect
        testPage.navigateToFlowScreen("mdBenefitsFlow/county");
        testPage.selectFromDropdown("county", "Baltimore County");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("select-app.title"));
    }

    @Test
    void selectNotSureHelpNeededAndChooseProgramFlow() {
        // select help needed
        testPage.navigateToFlowScreen("mdBenefitsFlow/selectHelpNeeded");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Select help");
        assert (testPage.hasErrorText(message("error.missing-general")));

        testPage.clickElementById("helpNeeded-CHILDREN");
        // this line unchecks helpNeeded-Children because none__checkbox is a noneOfTheAbove=true
        testPage.clickElementById("none__checkbox");

        testPage.clickContinue();

        // choose program flow
        assertThat(testPage.getTitle()).isEqualTo("Choose programs");
        assertThat(testPage.findElementById("programs-SNAP")).isNotNull();
        assertThat(testPage.findElementById("programs-SNAP").getAttribute("Checked")).isNull();
        assertThat(testPage.findElementById("programs-TDAP")).isNotNull();
        assertThat(testPage.findElementById("programs-TDAP").getAttribute("Checked")).isNull();
        assertThat(testPage.findElementById("programs-TCA")).isNotNull();
        assertThat(testPage.findElementById("programs-TCA").getAttribute("Checked")).isNull();
        assertThat(testPage.findElementById("programs-RCA")).isNotNull();
        assertThat(testPage.findElementById("programs-RCA").getAttribute("Checked")).isNull();

        testPage.clickContinue();
        assert (testPage.hasErrorText(message("error.missing-general")));

        testPage.clickElementById("programs-SNAP");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("signpost.title"));
    }

    @Test
    void selectSnapOrTcaHelpAndChooseProgramFlow() {
        // select help needed
        testPage.navigateToFlowScreen("mdBenefitsFlow/selectHelpNeeded");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Select help");
        assert (testPage.hasErrorText(message("error.missing-general")));

        testPage.clickElementById("helpNeeded-FOOD");

        testPage.clickContinue();

        // choose program flow
        assertThat(testPage.getTitle()).isEqualTo("Choose programs");
        assertThat(testPage.findElementById("programs-SNAP")).isNotNull();
        assertThat(testPage.findElementById("programs-SNAP").getAttribute("Checked")).isNotNull();
        assertThat(testPage.findElementById("programs-TCA")).isNotNull();
        assertThat(testPage.findElementById("programs-TCA").getAttribute("Checked")).isNull();
        assertThat(testPage.elementDoesNotExistById("programs-TDAP")).isTrue();
        assertThat(testPage.elementDoesNotExistById("programs-RCA")).isTrue();

        testPage.clickElementById("programs-SNAP");

        testPage.clickContinue();
        assert (testPage.hasErrorText(message("error.missing-general")));

        testPage.clickElementById("programs-SNAP");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("signpost.title"));
    }

    @Test
    void selectNonSnapOrTcaAndChooseProgramFlow() {
        // select help needed
        testPage.navigateToFlowScreen("mdBenefitsFlow/selectHelpNeeded");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Select help");
        assert (testPage.hasErrorText(message("error.missing-general")));

        testPage.clickElementById("helpNeeded-REFUGEE");

        testPage.clickContinue();

        // choose program flow
        assertThat(testPage.getTitle()).isEqualTo("Choose programs");
        assertThat(testPage.elementDoesNotExistById("programs-SNAP")).isTrue();
        assertThat(testPage.elementDoesNotExistById("programs-TCA")).isTrue();
        assertThat(testPage.elementDoesNotExistById("programs-TDAP")).isTrue();
        assertThat(testPage.findElementById("programs-RCA")).isNotNull();
        assertThat(testPage.findElementById("programs-RCA").getAttribute("Checked")).isNotNull();

        testPage.clickElementById("programs-RCA");

        testPage.clickContinue();
        assert (testPage.hasErrorText(message("error.missing-general")));

        testPage.clickElementById("programs-RCA");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("signpost.title"));
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
    void hourlyIncomeFlow() {
        loadUserPersonalData();
        loadHouseHoldData("First", "User", "12", "22", "1991", true);
        loadHouseHoldData("Second", "User", "01", "23", "1997", true);
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

        assertThat(testPage.getTitle()).isEqualTo(message("job-paid-by-hour.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("job-hourly-wage.title"));
        testPage.clickContinue();

        assert (testPage.hasErrorText(message("error.missing-dollar-amount")));
        testPage.enter("hourlyWage", "a");
        testPage.clickContinue();

        assert (testPage.hasErrorText(message("error.invalid-money")));
        testPage.enter("hourlyWage", ".99");

        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("job-hours-per-week.title"));
        testPage.clickContinue();

        assert (testPage.hasErrorText(RANGE_ERROR_MESSAGE));
        assert (testPage.hasErrorText(message("error.missing-general")));

        testPage.enter("hoursPerWeek", "100000");
        testPage.clickContinue();

        assert (testPage.hasErrorText(RANGE_ERROR_MESSAGE));

        testPage.enter("hoursPerWeek", "10");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("income-list.title"));
    }

    @Test
    void otherIncomeFlow() {
        loadUserPersonalData();
        loadHouseHoldData("Third", "User", "12", "22", "1991", true);
        loadHouseHoldData("Fourth", "User", "01", "23", "1997", true);
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

        assertThat(testPage.getTitle()).isEqualTo(message("job-paid-by-hour.title"));
        testPage.clickButton("No");

        testPage.clickContinue();
        assert (testPage.hasErrorText(message("error.missing-pay-period")));

        testPage.selectRadio("payPeriod", "Every month");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("job-pay-amount.title"));

        testPage.enter("payPeriodAmount", "a");
        testPage.clickContinue();

        assert (testPage.hasErrorText(message("error.invalid-money")));
        testPage.enter("payPeriodAmount", "282.99");

        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("income-list.title"));
    }

    // TODO: re-enable once we implement the expedited SNAP flow
    @Test
    @Disabled
    void expeditedSnapFlow() {
        loadUserPersonalData();
        loadAddressData();
        loadContactData();
        testPage.navigateToFlowScreen("mdBenefitsFlow/reviewContactInfo");
        assertThat(testPage.getTitle()).isEqualTo(message("review-contact-info.title"));
        testPage.clickLink(message("review-contact-info.submit-incomplete"));
        // Expedited Snap Start
        assertThat(testPage.getTitle()).isEqualTo(message("expedited-snap-start.title"));
        testPage.clickButton("Yes, I want to see if I qualify");
        // Multiple Person Household
        assertThat(testPage.getTitle()).isEqualTo(message("multiple-person-household.title"));
        testPage.clickButton("Yes");
        // Household Income Last 30 Days
        assertThat(testPage.getTitle()).isEqualTo(message("household-income-last-30-days.title"));
        testPage.enter("householdIncomeLast30Days", "0");
        testPage.clickContinue();
        // Expedited Money on Hand Amount
        assertThat(testPage.getTitle()).isEqualTo(message("expedited-money-on-hand-amount.title"));
        testPage.enter("expeditedMoneyOnHandAmount", "0");
        testPage.clickContinue();
        // Household Rent
        assertThat(testPage.getTitle()).isEqualTo(message("household-rent.title"));
        testPage.clickButton("Yes");
        // Household Rent Amount
        assertThat(testPage.getTitle()).isEqualTo(message("household-rent-amount.title"));
        testPage.enter("householdRentAmount", "1200");
        testPage.clickContinue();
        // Utilities
        assertThat(testPage.getTitle()).isEqualTo(message("utilities.title"));
        testPage.clickElementById("none__checkbox");
        testPage.clickContinue();
        // Seasonal Farm Worker
        assertThat(testPage.getTitle()).isEqualTo(message("seasonal-farmworker.title"));
        testPage.clickButton("No");
        // Expedited Snap Qualification Notice
        assertThat(testPage.getTitle()).isEqualTo(message("expedited-qualification-notice.title"));
    }

    @Test
    void maleApplicantsSkipPregnancyQuestion() {
        loadUserPersonalData();
        testPage.navigateToFlowScreen("mdBenefitsFlow/applicantSex");

        assertThat(testPage.getTitle()).isEqualTo(message("personal-info.sex.title"));
        testPage.selectRadio("applicantSex", "M");
        testPage.clickContinue();

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

        testPage.selectFromDropdown("county", "Baltimore County");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Select application");

        testPage.clickElementById("none__checkbox");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo("Select help");

        testPage.clickElementById("helpNeeded-FOOD");
        testPage.clickElementById("helpNeeded-CHILDREN");
        testPage.clickContinue();

        // choose program
        assertThat(testPage.getTitle()).isEqualTo(message("choose-programs.title"));

        testPage.clickContinue();

        // Signpost
        assertThat(testPage.getTitle()).isEqualTo(message("signpost.title"));
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

        testPage.clickElementById("noHomeAddress-true");
        testPage.clickContinue();

        // Where to send mail
        assertThat(testPage.getTitle()).isEqualTo(message("where-to-send-mail.title"));

        testPage.goBack();
        testPage.clickElementById("noHomeAddress-true");
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
        testPage.clickElementById("remindersMethod-By email-label");
        testPage.enter("emailAddress", "mail@mail.com");

        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("phone-number-nudge.title"));

        testPage.clickButton("Add a phone number");

        assertThat(testPage.getTitle()).isEqualTo(message("contact-info.title"));
        testPage.enter("phoneNumber", "123-456-7891");

        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("review-contact-info.title"));

        testPage.clickButton("This looks correct");

        // Individual Only Household
        assertThat(testPage.getTitle()).isEqualTo(message("household-signpost.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("applicant-applying.title"));
        testPage.clickLink("Why do we ask for SSNs?");
        assertThat(testPage.getTitle()).isEqualTo(message("ssn-why.title"));
        testPage.clickLink("Go Back");
        assertThat(testPage.getTitle()).isEqualTo(message("applicant-applying.title"));

        testPage.selectRadio("isApplicantApplying", "Yes");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("personal-info.sex.title"));
        testPage.selectRadio("applicantSex", "F");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("personal-info.pregnancy.title"));
        testPage.selectRadio("isApplicantPregnant", "true");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("applicant-school-enrollment.title"));
        testPage.selectRadio("applicantIsEnrolledInSchool", "Yes");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("applicant-disability.title"));
        testPage.selectRadio("applicantHasDisability", "false");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo((message("race-ethnicity-selection.title")));
        testPage.clickElementById("applicantRace-ASIAN");
        testPage.clickElementById("applicantRace-WHITE");
        testPage.selectRadio("applicantEthnicity", EthnicityType.HISPANIC_OR_LATINO.name());
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("household-question.title"));
        testPage.clickButton(message("household-question.no-text"));

        assertThat(testPage.getTitle()).isEqualTo(message("seasonal-farmworker.title"));

        // PLACEHOLDER FOR ADDITIONAL TESTS

        testPage.navigateToFlowScreen("mdBenefitsFlow/docUploadIntro");

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

        assertThat(testPage.getTitle()).isEqualTo(message("household-list.title"));
        testPage.clickButton("I'm done");

        assertThat(testPage.getTitle()).isEqualTo(message("seasonal-farmworker.title"));
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
        assertThat(testPage.getTitle()).isEqualTo(message("where-to-send-mail.title"));
        testPage.clickButton("Add a mailing address");
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

    void loadUserPersonalData() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/personalInfo");

        testPage.enter("firstName", "test");
        testPage.enter("lastName", "test2");
        testPage.enter("birthMonth", "12");
        testPage.enter("birthDay", "25");
        testPage.enter("birthYear", "1985");
        testPage.clickContinue();
    }

    void loadHouseHoldData(String firstName, String lastName, String month, String day, String year, boolean isApplying) {
        testPage.navigateToFlowScreen("mdBenefitsFlow/householdInfo");
        testPage.enter("householdMemberFirstName", firstName);
        testPage.enter("householdMemberLastName", lastName);
        testPage.selectFromDropdown("householdMemberRelationship", message("household-info.relationship.child"));
        testPage.selectRadio("householdMemberApplyingForBenefits", isApplying ? "Yes" : "No");
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
        testPage.navigateToFlowScreen("mdBenefitsFlow/contactInfo");
        testPage.enter("emailAddress", "test@gmail.com");
        testPage.enter("phoneNumber", "555-456-7891");
        testPage.clickElementById("remindersMethod-By email-label");
        testPage.clickContinue();
    }

    void preloadIncomeScreen() {
        testPage.navigateToFlowScreen("mdBenefitsFlow/incomeSignPost");
        testPage.clickContinue();

    }
}
