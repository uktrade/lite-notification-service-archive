package uk.gov.bis.lite.notification;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.module.installer.feature.ManagedInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.ResourceInstaller;
import uk.gov.bis.lite.notification.config.GuiceModule;
import uk.gov.bis.lite.notification.config.NotificationAppConfig;
import uk.gov.bis.lite.notification.exception.NotificationServiceException;
import uk.gov.bis.lite.notification.resource.NotificationResource;
import uk.gov.bis.lite.notification.scheduler.NotificationScheduler;

public class NotificationApp extends Application<NotificationAppConfig> {

  private GuiceBundle<NotificationAppConfig> guiceBundle;

  @Override
  public void initialize(Bootstrap<NotificationAppConfig> bootstrap) {
    guiceBundle = new GuiceBundle.Builder<NotificationAppConfig>()
      .modules(new GuiceModule())
      .installers(ResourceInstaller.class, ManagedInstaller.class)
      .extensions(NotificationResource.class, NotificationScheduler.class)
      .build();
    bootstrap.addBundle(guiceBundle);
  }

  @Override
  public void run(NotificationAppConfig configuration, Environment environment) throws Exception {

    environment.jersey().register(NotificationServiceException.ServiceExceptionMapper.class);

    // Perform/validate flyway migration on startup
    DataSourceFactory dataSourceFactory = configuration.getDataSourceFactory();
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSourceFactory.getUrl(), dataSourceFactory.getUser(), dataSourceFactory.getPassword());
    flyway.migrate();
  }

  public static void main(String[] args) throws Exception {
    new NotificationApp().run(args);
  }
}

