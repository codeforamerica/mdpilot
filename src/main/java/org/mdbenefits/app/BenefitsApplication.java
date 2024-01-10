package org.mdbenefits.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"org.mdbenefits.app", "formflow.library"})
@EntityScan(basePackages = {"org.mdbenefits.app", "formflow.library"})
@EnableConfigurationProperties
public class BenefitsApplication {

  public static void main(String[] args) {

    SpringApplication.run(BenefitsApplication.class, args);
  }

}
