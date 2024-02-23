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
        assert(testPage.hasErrorText(message("error.missing-general")));

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
        assert(testPage.hasErrorText(message("error.missing-general")));

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
        assert(testPage.hasErrorText(message("error.missing-general")));

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
        assert(testPage.hasErrorText(message("error.missing-general")));

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
        assert(testPage.hasErrorText(message("error.missing-general")));

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
        assert(testPage.hasErrorText(message("error.missing-general")));

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

        assertThat(testPage.getTitle()).isEqualTo(message("self-employment.title"));
        testPage.clickButton("No");

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

        assertThat(testPage.getTitle()).isEqualTo(message("income-confirmation.title"));
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

        assertThat(testPage.getTitle()).isEqualTo(message("self-employment.title"));
        testPage.clickButton("No");

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

        assertThat(testPage.getTitle()).isEqualTo(message("income-confirmation.title"));
    }

    @Test
    void socialSecurityFlow() {
        loadUserPersonalData();

        testPage.navigateToFlowScreen("mdBenefitsFlow/ssnForm");

        assertThat(testPage.getTitle()).isEqualTo(message("ssn-form.title"));

        testPage.enter("ssn", "1234");
        testPage.clickContinue();

        assert (testPage.hasErrorText(message("error.invalid-ssn")));
        testPage.enter("ssn", "");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("special-situations.title"));
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
    void raceEthnicityFlow() {
        loadUserPersonalData();
        loadHouseHoldData("Person", "One", "12", "12", "1995", true);
        loadHouseHoldData("Person", "Two", "12", "12", "2016", true);
        loadHouseHoldData("Person", "Three", "12", "12", "2017", true);

        testPage.navigateToFlowScreen("mdBenefitsFlow/ethnicitySelection");
        // the titles don't seem to render correctly in test
        // assertThat(testPage.getTitle()).isEqualTo("L\u1ef1a ch\u1ecdn dân t\u1ed9c");

        // set for the applicant
        testPage.clickElementById("ethnicitySelected-Hispanic or Latino");

        List<WebElement> ethnicityInputs = driver.findElements(By.cssSelector("input[id*='householdMemberEthnicity_wildcard_']"));

        ethnicityInputs.stream()
                .filter(ei -> ei.getAttribute("value").equals("Hispanic or Latino"))
                .forEach(ei -> {
                    ei.click();
                });

        testPage.clickContinue();
        testPage.goBack();

        //assertThat(testPage.getTitle()).isEqualTo("L\u1ef1a ch\u1ecdn dân t\u1ed9c");
        assertThat(testPage.findElementById("ethnicitySelected-Hispanic or Latino").isSelected()).isTrue();

        List<WebElement> selectedElements = driver.findElements(By.cssSelector("input[checked='checked']"));
        selectedElements.forEach(element -> {
            assertThat(element.getAttribute("value")).isEqualTo("Hispanic or Latino");
        });

        testPage.clickContinue();
        //assertThat(testPage.getTitle()).isEqualTo("L\u1ef1a ch\u1ecdn ch\u1ee7ng t\u1ed9c");

        // set for the applicant
        testPage.clickElementById("raceSelected-Alaskan Native");
        testPage.clickElementById("raceSelected-Black or African American");

        // now set for household members
        List<WebElement> raceInputs = driver.findElements(By.cssSelector("input[id*='householdMemberRace_wildcard_'"));

        // choose a few for each
        raceInputs.stream()
                .filter(ri -> {
                    String value = ri.getAttribute("value");
                    return value.equals("Alaskan Native") || value.equals("Black or African American");
                })
                .forEach(ri -> {
                    ri.click();
                });

        testPage.clickContinue();
        testPage.goBack();

        //assertThat(testPage.getTitle()).isEqualTo("L\u1ef1a ch\u1ecdn ch\u1ee7ng t\u1ed9c");
        assertThat(testPage.findElementById("raceSelected-Alaskan Native").isSelected()).isTrue();
        assertThat(testPage.findElementById("raceSelected-Black or African American").isSelected()).isTrue();
        assertThat(testPage.findElementById("raceSelected-Asian").isSelected()).isFalse();

        List<WebElement> selectedRaceElements = driver.findElements(By.cssSelector("input[checked='checked']"));
        selectedRaceElements.forEach(element -> {
            String value = element.getAttribute("value");
            assertThat(value.equals("Alaskan Native") || value.equals("Black or African American")).isTrue();
            assertThat(value).isNotEqualTo("White");
            assertThat(value).isNotEqualTo("Asian");
            assertThat(value).isNotEqualTo("Native Hawaiian or Other Pacific Islander");
            assertThat(value).isNotEqualTo("American Indian");
        });

        assertThat(testPage.getTitle()).isEqualTo("Race Selection");
    }

    @Test
    void allCitizensSkipSelectStatusPage() {
        loadUserPersonalData();
        loadHouseHoldData("First", "User", "12", "22", "1991", true);
        loadHouseHoldData("Second", "User", "01", "23", "1997", true);

        testPage.navigateToFlowScreen("mdBenefitsFlow/citizenshipIntro");
        testPage.clickContinue();

        testPage.clickButton(message("general.inputs.yes"));
        assertThat(testPage.getTitle()).isEqualTo(message("veteran.title"));
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
    void someHouseholdMembersNotApplyingPresetsCitizenshipStatus() {
        loadUserPersonalData();
        loadHouseHoldData("First", "User", "12", "22", "1991", true);
        loadHouseHoldData("Second", "User", "01", "23", "1997", false);

        testPage.navigateToFlowScreen("mdBenefitsFlow/citizenshipIntro");
        testPage.clickContinue();

        testPage.clickButton(message("general.inputs.no"));
        assertThat(testPage.getTitle()).isEqualTo(message("citizenship-select-status.title"));

        List<WebElement> selectGroups = driver.findElements(By.className("form-group"));
        selectGroups.forEach(groupElement -> {
            String memberName = groupElement.findElement(By.tagName("label")).getText();
            WebElement select = groupElement.findElement(By.className("select__element"));
            WebElement option = testPage.getSelectedOption(select.getAttribute("id"));

            if (memberName.equalsIgnoreCase("Second User")) {
                assertThat(option.getAttribute("value")).isEqualTo(CitizenshipStatus.NOT_APPLYING.name());
            } else {
                assertThat(option.getText()).isEqualTo(message("general.select.placeholder"));
            }
        });
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
        testPage.selectRadio("isApplicantApplying", "true");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("personal-info.sex.title"));
        testPage.selectRadio("applicantSex", "F");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("personal-info.pregnancy.title"));
        testPage.selectRadio("isApplicantPregnant", "true");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("applicant-school-enrollment.title"));

       // TODO: remove this navigation in future work
        testPage.navigateToFlowScreen("mdBenefitsFlow/applicantApplying");

        assertThat(testPage.getTitle()).isEqualTo(message("applicant-applying.title"));
        testPage.selectRadio("isApplicantApplying", "false");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("household-question.title"));
        testPage.clickButton(message("household-question.no-text"));

        assertThat(testPage.getTitle()).isEqualTo(message("seasonal-farmworker.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("citizenship.title"));
        testPage.clickButton(message("general.inputs.continue"));

        assertThat(testPage.getTitle()).isEqualTo(message("citizenship-question.title"));
        testPage.clickButton(message("general.inputs.no"));

        assertThat(testPage.getTitle()).isEqualTo(message("citizenship-select-status.title"));
        testPage.selectFromDropdown("applicantCitizenshipStatus", message("citizenship-select-status.types.us-citizen"));

        List<WebElement> elementList = driver.findElements(By.className("select__element"));
        elementList.forEach(e -> {
            testPage.selectFromDropdown(e, message(CitizenshipStatus.US_CITIZEN.getLabelSrc()));
        });
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("veteran.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("household-meals.title"));
        testPage.goBack();
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("veteran-who.title"));
        testPage.clickElementById("veterans-you");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("household-meals.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("sensitive-questions.title"));
        testPage.goBack();
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("household-meals-who.title"));
        testPage.clickElementById("meals-you");
        testPage.clickContinue();

        // Sensitive Questions
        assertThat(testPage.getTitle()).isEqualTo(message("sensitive-questions.title"));
        testPage.clickContinue();

        //    Case when no personal situations apply
        assertThat(testPage.getTitle()).isEqualTo(message("personal-situations.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("criminal-justice-warning.title"));
        testPage.goBack();

        assertThat(testPage.getTitle()).isEqualTo(message("personal-situations.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("personal-situations-who.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("personal-situations-which.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("criminal-justice-warning.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("criminal-justice.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("income-signpost.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("job-search.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("job-search-who.title"));
        testPage.clickElementById("jobSearch-you");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("work-disqualifications.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("employment-status.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("income-by-job.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("income-who.title"));
        testPage.clickElementById("householdMemberJobAdd-you");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("employer-name.title"));
        testPage.enter("employerName", "job1");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("self-employment.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("job-paid-by-hour.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("job-hourly-wage.title"));
        testPage.enter("hourlyWage", "15");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("job-hours-per-week.title"));
        testPage.enter("hoursPerWeek", "10");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("income-confirmation.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("income-list.title"));
        testPage.clickButton(message("income-list.continue"));

        assertThat(testPage.getTitle()).isEqualTo(message("additional-income.title"));
        testPage.clickElementById("none__checkbox");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("moneyonhand-types.title"));
        testPage.clickElementById("moneyOnHandTypes-Savings bond-label");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("moneyonhand-details.title"));
        testPage.clickContinue();

        //    Expenses SignPost
        assertThat(testPage.getTitle()).isEqualTo(message("expenses-signpost.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("home-expenses.title"));
        testPage.clickElementById("householdHomeExpenses-rent-label");
        testPage.clickElementById("householdHomeExpenses-otherHomeExpenses-label");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("home-expenses-amount.title"));

        testPage.enter("householdHomeExpenseAmount_wildcard_rent", "500");
        testPage.enter("householdHomeExpenseAmount_wildcard_otherHomeExpenses", "100");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("utilities.title"));

        testPage.clickElementById("householdUtilitiesExpenses-water-label");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("utilities-expenses-amount.title"));
        testPage.goBack();

        testPage.clickElementById("none__checkbox-label"); // none selected
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("energy-assistance.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("medical-expenses-amount.title"));
        testPage.goBack();

        assertThat(testPage.getTitle()).isEqualTo(message("energy-assistance.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("liheap.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("medical-expenses.title"));

        testPage.clickElementById("householdMedicalExpenses-dentalBills-label");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("medical-expenses-amount.title"));
        testPage.enter("householdMedicalExpenseAmount_wildcard_dentalBills", "200");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("dependentcare.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("childsupportexpenses.title"));
        testPage.goBack();
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("dependentcare-expenses.title"));
        testPage.enter("expensesDependentCare", "15");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("childsupportexpenses.title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("elderlycare.title"));
        testPage.goBack();

        testPage.clickButton("Yes");
        assertThat(testPage.getTitle()).isEqualTo(message("childsupportexpenses-amount.title"));
        testPage.enter("expensesChildSupport", "150");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("elderlycare.title"));
        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("elderlycare-amount.title"));
        testPage.enter("expensesElderlyCare", "123");
        testPage.clickContinue();
        ;

        // Final SignPost
        assertThat(testPage.getTitle()).isEqualTo(message("final-signpost.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("ebtcard-title"));
        testPage.clickButton("No");

        assertThat(testPage.getTitle()).isEqualTo(message("voter-registration.title"));
        testPage.selectRadio("votingRegistrationRequested", "Yes");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("race-ethnicity.title"));
        testPage.clickButton("No, skip this question");

        assertThat(testPage.getTitle()).isEqualTo(message("doc-upload-intro.title"));
        testPage.goBack();

        assertThat(testPage.getTitle()).isEqualTo(message("race-ethnicity.title"));
        testPage.clickButton("Yes, continue");

        assertThat(testPage.getTitle()).isEqualTo(message("ethnicity-selection.title"));
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("race-selection.title"));
        testPage.clickContinue();

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
        testPage.selectRadio("householdMemberApplyingForBenefits", "yes");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("household-list.title"));
        testPage.clickButton("I'm done");

        assertThat(testPage.getTitle()).isEqualTo(message("seasonal-farmworker.title"));
    }

    @Test
    void testValidMailingAddress() throws SmartyException, IOException, InterruptedException {
        ValidatedAddress validatedAddress = new ValidatedAddress("mailingAddressStreetAddress1_validated", "mailingAddressStreetAddress2_validated", "mailingAddressCity_validated", "mailingAddressState_validated", "mailingAddressZipCode_validated");
        when(addressValidationService.validate(any())).thenReturn(Map.of("mailingAddress", validatedAddress));
        testPage.navigateToFlowScreen("mdBenefitsFlow/mailingAddress");
        testPage.enter("mailingAddressStreetAddress1", "1445 34th Ave");
        testPage.enter("mailingAddressStreetAddress2", "Apt A");
        testPage.enter("mailingAddressCity", "Oakland");
        testPage.enter("mailingAddressZipCode", "94601");
        testPage.selectFromDropdown("mailingAddressState", "CA - California");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("verify-address.title"));
        assertThat(driver.findElements(By.className("notice--warning")).get(0).getText()).isEqualTo(message("select-address.notice"));
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
        assertThat(driver.findElements(By.className("notice--warning")).get(0).getText()).isEqualTo(message("verify-address.notice"));
    }

    @Test
    void testMailingAddressIsSameAsHomeAddress() throws SmartyException, IOException, InterruptedException {
        ValidatedAddress validatedAddress = new ValidatedAddress("mailingAddressStreetAddress1_validated", "mailingAddressStreetAddress2", "mailingAddressCity", "mailingAddressState", "mailingAddressZipCode");
        when(addressValidationService.validate(any())).thenReturn(Map.of("mailingAddress", validatedAddress));
        testPage.navigateToFlowScreen("mdBenefitsFlow/homeAddress");
        testPage.enter("homeAddressStreetAddress1", "1445 34th Ave");
        testPage.enter("homeAddressStreetAddress2", "Apt A");
        testPage.enter("homeAddressCity", "Oakland");
        testPage.enter("homeAddressZipCode", "94601");
        testPage.selectFromDropdown("homeAddressState", "CA - California");
        testPage.clickContinue();
        testPage.clickElementById("sameAsHomeAddress-true");
        await().atMost(4, TimeUnit.SECONDS).until(() -> testPage.findElementById("mailingAddressStreetAddress1").getAttribute("value").equals("1445 34th Ave"));
        assertThat(testPage.findElementById("mailingAddressStreetAddress1").getAttribute("value")).isEqualTo("1445 34th Ave");
        assertThat(testPage.findElementById("mailingAddressStreetAddress2").getAttribute("value")).isEqualTo("Apt A");
        assertThat(testPage.findElementById("mailingAddressCity").getAttribute("value")).isEqualTo("Oakland");
        assertThat(testPage.findElementById("mailingAddressZipCode").getAttribute("value")).isEqualTo("94601");
        assertThat(testPage.findElementById("mailingAddressState").getAttribute("value")).isEqualTo("CA");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(message("verify-address.title"));
        assertThat(driver.findElements(By.className("notice--warning")).get(0).getText()).isEqualTo(message("select-address.notice"));
    }

    @Test
    void shouldValidateMailingAddressWhenNoHomeAddress() throws SmartyException, IOException, InterruptedException {
        ValidatedAddress validatedAddress = new ValidatedAddress("mailingAddressStreetAddress1_validated", "mailingAddressStreetAddress2", "mailingAddressCity", "mailingAddressState", "mailingAddressZipCode");
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
        assertThat(driver.findElements(By.className("notice--warning")).get(0).getText()).isEqualTo(message("select-address.notice"));
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
        testPage.selectRadio("householdMemberApplyingForBenefits", isApplying ? "yes" : "no");
        testPage.clickContinue();
//        testPage.enter("householdMemberBirthMonth", month);
//        testPage.enter("householdMemberBirthDay", day);
//        testPage.enter("householdMemberBirthYear", year);
//        testPage.selectRadio("householdMemberSex", "F");
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

        testPage.clickButton("Yes");

        assertThat(testPage.getTitle()).isEqualTo(message("job-search-who.title"));
        testPage.clickContinue();

        assert (testPage.hasErrorText(message("error.missing-general")));

        testPage.clickElementById("jobSearch-you");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(message("work-disqualifications.title"));
        testPage.clickButton("No");

        testPage.clickButton("No");
    }
}
