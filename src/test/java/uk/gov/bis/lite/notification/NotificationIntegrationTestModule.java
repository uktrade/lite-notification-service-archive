package uk.gov.bis.lite.notification;

import uk.gov.bis.lite.notification.config.GuiceModule;

public class NotificationIntegrationTestModule extends GuiceModule {

  @Override
  protected String templateConfigFilePath() {
    return "/template-config-test.yaml";
  }
}
