package uk.gov.bis.lite.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.skife.jdbi.v2.DBI;
import uk.gov.bis.lite.notification.dao.NotificationDao;
import uk.gov.bis.lite.notification.dao.NotificationDaoImpl;
import uk.gov.bis.lite.notification.service.NotificationService;
import uk.gov.bis.lite.notification.service.NotificationServiceImpl;
import uk.gov.bis.lite.notification.service.TemplateService;
import uk.gov.service.notify.NotificationClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GuiceModule extends AbstractModule {

  @Override
  protected void configure() {
    bindConstant().annotatedWith(Names.named("templatePath")).to("/template-config.yaml");
    bind(NotificationService.class).to(NotificationServiceImpl.class);
  }

  @Provides
  public NotificationDao provideNotificationDao(@Named("jdbi") DBI jdbi) {
    return new NotificationDaoImpl(jdbi);
  }

  @Provides
  public TemplateService providesEmailTemplateService(@Named("templatePath") String templatePath) throws IOException {
    InputStream in = getClass().getResourceAsStream(templatePath);
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    TemplateConfig templateConfig = mapper.readValue(reader, TemplateConfig.class);
    return new TemplateService(templateConfig);
  }

  @Provides
  @Named("jdbi")
  public DBI provideDataSourceJdbi(Environment environment, NotificationAppConfig config) {
    final DBIFactory factory = new DBIFactory();
    return factory.build(environment, config.getDataSourceFactory(), "sqlite");
  }

  @Provides
  public Scheduler provideScheduler() throws SchedulerException {
    return new StdSchedulerFactory().getScheduler();
  }

  @Provides
  public NotificationClient provideNotificationClient(NotificationAppConfig notificationAppConfig) {
    return new NotificationClient(notificationAppConfig.getNotifyApiKey(), notificationAppConfig.getNotifyServiceId(),
        notificationAppConfig.getNotifyUrl());
  }

}
