package uk.gov.bis.lite.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.guice.module.support.ConfigurationAwareModule;
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

public class GuiceModule extends AbstractModule implements ConfigurationAwareModule<NotificationAppConfig> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GuiceModule.class);

  private NotificationAppConfig config;

  @Override
  protected void configure() {
    bind(NotificationDao.class).to(NotificationDaoImpl.class);
    bind(NotificationService.class).to(NotificationServiceImpl.class);
  }

  @Provides
  public TemplateService providesEmailTemplateService() throws IOException {
    InputStream in = getClass().getResourceAsStream(templateConfigFilePath());
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    TemplateConfig config = mapper.readValue(reader, TemplateConfig.class);
    return new TemplateService(config);
  }

  protected String templateConfigFilePath() {
    return "/template-config.yaml";
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
  public NotificationClient provideNotificationClient() {
    return new NotificationClient(config.getNotifyApiKey(), config.getNotifyServiceId(), config.getNotifyUrl());
  }

  @Override
  public void setConfiguration(NotificationAppConfig config) {
    this.config = config;
  }

}
