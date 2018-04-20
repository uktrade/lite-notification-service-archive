package uk.gov.bis.lite.notification;

import com.google.inject.name.Names;
import uk.gov.bis.lite.notification.config.GuiceModule;
import uk.gov.bis.lite.notification.service.NotificationService;
import uk.gov.bis.lite.notification.service.NotificationServiceMock;

public class NotificationIntegrationPactModule extends GuiceModule {
  @Override
  protected void configure() {
    bindConstant().annotatedWith(Names.named("templatePath")).to("/template-config-test.yaml");
    bind(NotificationService.class).to(NotificationServiceMock.class);
  }
}
