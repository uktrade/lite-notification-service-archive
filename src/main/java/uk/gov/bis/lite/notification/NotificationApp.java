package uk.gov.bis.lite.notification;

import com.google.inject.Module;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.module.installer.feature.ManagedInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.ResourceInstaller;
import uk.gov.bis.lite.common.jersey.filter.ContainerCorrelationIdFilter;
import uk.gov.bis.lite.notification.auth.SimpleAuthenticator;
import uk.gov.bis.lite.notification.config.GuiceModule;
import uk.gov.bis.lite.notification.config.NotificationAppConfig;
import uk.gov.bis.lite.notification.resource.NotificationResource;
import uk.gov.bis.lite.notification.scheduler.NotificationScheduler;

public class NotificationApp extends Application<NotificationAppConfig> {

  private GuiceBundle<NotificationAppConfig> guiceBundle;
  private final Module module;

  public NotificationApp(Module module) {
    this.module = module;
  }

  @Override
  public void initialize(Bootstrap<NotificationAppConfig> bootstrap) {
    guiceBundle = new GuiceBundle.Builder<NotificationAppConfig>()
      .modules(module)
      .installers(ResourceInstaller.class, ManagedInstaller.class)
      .extensions(NotificationResource.class, NotificationScheduler.class)
      .build();
    bootstrap.addBundle(guiceBundle);
  }

  @Override
  public void run(NotificationAppConfig configuration, Environment environment) throws Exception {

    environment.jersey().register(new AuthDynamicFeature(
        new BasicCredentialAuthFilter.Builder<PrincipalImpl>()
            .setAuthenticator(new SimpleAuthenticator(configuration.getServiceLogin(), configuration.getServicePassword()))
            .setRealm("Notification Service Admin Authentication")
            .buildAuthFilter()));

    environment.jersey().register(ContainerCorrelationIdFilter.class);

    // Perform/validate flyway migration on startup
    DataSourceFactory dataSourceFactory = configuration.getDataSourceFactory();
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSourceFactory.getUrl(), dataSourceFactory.getUser(), dataSourceFactory.getPassword());
    flyway.migrate();
  }

  public static void main(String[] args) throws Exception {
    new NotificationApp(new GuiceModule()).run(args);
  }
}

