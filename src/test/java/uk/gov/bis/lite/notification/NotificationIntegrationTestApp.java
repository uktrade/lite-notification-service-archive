package uk.gov.bis.lite.notification;

import uk.gov.bis.lite.notification.config.NotificationAppConfig;

public class NotificationIntegrationTestApp extends NotificationApp {

  public NotificationIntegrationTestApp() {
    super(new NotificationIntegrationTestModule());
  }

  @Override
  protected void flywayMigrate(NotificationAppConfig configuration) {

  }
}
