package org.mdbenefits.app.pdf;

import static org.assertj.core.api.Assertions.assertThat;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import formflow.library.data.Submission;
import formflow.library.pdf.PdfService;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.data.SubmissionTestBuilder;
import org.mdbenefits.app.data.enums.EthnicityType;
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
                        "Child", "F", "", "", "222222222", null, null)
                .with("homeAddressStreetAddress1", "972 Mission St, 5th Floor")
                .with("homeAddressCity", "San Francisco")
                .with("homeAddressState", "CA")
                .with("homeAddressZipCode", "94103")
                .with("phoneNumber", "(510) 555-1212")
                .with("programs[]", List.of("SNAP", "RCA"))
                .with("isApplicantPregnant", "false")
                .with("applicantHasDisability", "false")
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
                .build();

        submission.setFlow("mdBenefitsFlow");

        File pdfFile = pdfService.generate(submission);

        String page1 = getPageText(pdfFile, 1);
        assertThat(page1).contains("Doe, John");

        String page2 = getPageText(pdfFile, 2);
        // applicant DOB
        assertThat(page2).contains("12/10/1999");
        // applicant Race
        assertThat(page2).contains("2,5");
    }

    private static String getPageText(File file, int page) throws IOException {
        try (PdfReader reader = new PdfReader(file.getPath())) {
            PdfTextExtractor pdfTextExtractor = new PdfTextExtractor(reader);
            return pdfTextExtractor.getTextFromPage(page);
        }
    }
}
