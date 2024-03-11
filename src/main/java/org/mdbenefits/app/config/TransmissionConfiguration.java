package org.mdbenefits.app.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "transmission")
public class TransmissionConfiguration {

    Map<String, String> googleDriveDirectoryId;
    Map<String, String> emailRecipients;
}
