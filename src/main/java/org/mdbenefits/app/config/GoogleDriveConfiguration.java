package org.mdbenefits.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google.drive")
public class GoogleDriveConfiguration {
    private String baltimoreCountyDirectoryId;
    private String queenAnnesCountyDirectoryId;
}
