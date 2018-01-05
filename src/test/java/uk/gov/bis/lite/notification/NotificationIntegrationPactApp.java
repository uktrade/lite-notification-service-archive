package uk.gov.bis.lite.notification;

import com.google.inject.util.Modules;
import uk.gov.bis.lite.notification.config.GuiceModule;
import uk.gov.bis.lite.notification.config.NotificationAppConfig;

public class NotificationIntegrationPactApp extends NotificationApp {

  public NotificationIntegrationPactApp() {
    super(Modules.override(new GuiceModule()).with(new NotificationIntegrationPactModule()));
  }

  @Override
  protected void flywayMigrate(NotificationAppConfig configuration) {

  }
}
