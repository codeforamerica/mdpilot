package org.mdbenefits.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mdbenefits.app.testutils.AbstractBasePageTest;
import org.mdbenefits.app.testutils.Page;

@Tag("staticPagesJourney")
public class StaticPagesJourneyTest extends AbstractBasePageTest {

    @Override
    protected void initTestPage() {
        testPage = new Page(driver, localServerPort, messageSource);
    }

    @Test
    void staticPagesJourney() {
        // Landing screen
        assertThat(testPage.getTitle()).isEqualTo("Maryland Benefits Application");
        assertThat(driver.getWindowHandles().size()).isEqualTo(1);
        String originalWindow = driver.getWindowHandle();
        // Go to privacy policy tab
        testPage.clickLink(message("footer.privacy"));
        assertThat(driver.getWindowHandles().size()).isEqualTo(2);
        switchAwayFromOriginalWindow(originalWindow);
        await().atMost(5, TimeUnit.SECONDS).until(
                () -> testPage.getTitle().equals(message("privacy.title"))
        );
        assertThat(testPage.getTitle()).isEqualTo(message("privacy.title"));
    }

    void switchAwayFromOriginalWindow(String originalWindow) {
        for (String windowHandle : driver.getWindowHandles()) {
            if (!originalWindow.contentEquals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
    }
}
