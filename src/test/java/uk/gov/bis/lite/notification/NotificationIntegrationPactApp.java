package uk.gov.bis.lite.notification;

import uk.gov.bis.lite.notification.config.NotificationAppConfig;

public class NotificationIntegrationPactApp extends NotificationApp {

  public NotificationIntegrationPactApp() {
    super(new NotificationIntegrationPactModule());
  }

  @Override
  protected void flywayMigrate(NotificationAppConfig configuration) {
  }

}
