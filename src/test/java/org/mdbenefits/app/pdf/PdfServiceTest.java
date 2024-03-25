package org.mdbenefits.app.pdf;

import static org.assertj.core.api.Assertions.assertThat;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import formflow.library.data.Submission;
import formflow.library.pdf.PdfService;
import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.mdbenefits.app.data.enums.RaceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class PDFServiceTest {

    @Autowired
    PdfService pdfService;

    @Test
    void generate() throws IOException {
        Submission submission = new SubmissionTestBuilder()
                .withPersonalInfo("John", "Doe", "10", "12", "1999",
                        "", "F", "", "", "111111111")
                .withHouseholdMemberApplying("Jane", "Doe", "1", "5", "2000",
                        "Child", "F", "", "", "222222222", "Yes", "Yes", null, null)
                .with("homeAddressStreetAddress1", "972 Mission St")
                .with("homeAddressStreetAddress2", "5th Floor")
                .with("homeAddressCity", "San Francisco")
                .with("homeAddressState", "CA")
                .with("homeAddressZipCode", "94103")
                .with("mailingAddressStreetAddress1", "916 Mission St")
//                .with("mailingAddressStreetAddress2", "4th Floor")
                .with("mailingAddressCity", "San Francisco")
                .with("mailingAddressState", "CA")
                .with("mailingAddressZipCode", "94086")
                .with("cellPhoneNumber", "(510) 555-1111")
                .with("homePhoneNumber", "(510) 555-2222")
                .with("workPhoneNumber", "(510) 555-3333")
                .with("programs[]", List.of("SNAP", "RCA"))
                .with("isApplicantPregnant", "No")
                .with("applicantHasDisability", "No")
                .with("signature", "John Hancock")
                .with("someoneInHHIsPregnant", "true")
                .with("someoneHasDrugKingpinFelony", "false")
                .with("someoneHasVolumeDrugDealerFelony", "true")
                .with("someoneHasSexualOffenceConviction", "false")
                .with("someoneIsViolatingParole", "true")
                .with("someoneConvictedForLyingAboutBenefits", "false")
                .with("someoneConvictedForTradingBenefits", "true")
                .with("someoneIsReceivingBenefitsWithFakeID", "false")
                .with("applicantRace[]", List.of(RaceType.ASIAN.name(), RaceType.WHITE.name()))
                .with("confirmationNumber", "M123456789")
                .build();

        submission.setFlow("mdBenefitsFlow");
        submission.setSubmittedAt(OffsetDateTime.parse("2024-01-01T00:00:00Z"));

        File pdfFile = pdfService.generate(submission);
        // Cover page 1
        String page1 = getPageText(pdfFile, 1);
        // Confirmation number
        assertThat(page1).contains("M123456789");
        // Applicant full name
        assertThat(page1).contains("Doe, John");
        // Submission Date
        assertThat(page1).contains("01/01/2024");
        // Applicant Phone
        assertThat(page1).contains("(510) 555-1111");
        assertThat(page1).contains("(510) 555-2222");
        assertThat(page1).contains("(510) 555-3333");
        // 9701 Page 1
        String page4 = getPageText(pdfFile, 4);
        assertThat(page4).contains("Doe, John");

        String page5 = getPageText(pdfFile, 5);
        // applicant DOB
        assertThat(page5).contains("12/10/1999");
        // applicant Race
        assertThat(page5).contains("2,5");
    }

    private static String getPageText(File file, int page) throws IOException {
        try (PdfReader reader = new PdfReader(file.getPath())) {
            PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(reader);
            return pdfTextExtractor.getTextFromPage(page);
        }
    }
}
