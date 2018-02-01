package uk.gov.bis.lite.notification;

import uk.gov.bis.lite.notification.config.GuiceModule;
import uk.gov.bis.lite.notification.service.NotificationService;
import uk.gov.bis.lite.notification.service.NotificationServiceMock;

public class NotificationIntegrationPactModule extends GuiceModule {

  @Override
  protected void configure() {
    bind(NotificationService.class).to(NotificationServiceMock.class);
  }

  @Override
  protected String templateConfigFilePath() {
    return "/template-config-test.yaml";
  }
}
