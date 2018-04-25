package uk.gov.bis.lite.notification;

import com.google.inject.name.Names;
import uk.gov.bis.lite.notification.config.GuiceModule;
import uk.gov.bis.lite.notification.service.NotificationService;
import uk.gov.bis.lite.notification.service.NotificationServiceImpl;

public class NotificationIntegrationTestModule extends GuiceModule {

  @Override
  protected void configure() {
    bindConstant().annotatedWith(Names.named("templatePath")).to("/template-config-test.yaml");
    bind(NotificationService.class).to(NotificationServiceImpl.class);
  }
}
