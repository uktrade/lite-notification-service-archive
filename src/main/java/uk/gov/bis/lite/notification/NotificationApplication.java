package uk.gov.bis.lite.notification;

import com.codahale.metrics.servlets.AdminServlet;
import com.google.inject.Module;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.module.installer.feature.ManagedInstaller;
import uk.gov.bis.lite.common.auth.admin.AdminConstraintSecurityHandler;
import uk.gov.bis.lite.notification.config.GuiceModule;
import uk.gov.bis.lite.notification.config.NotificationConfiguration;
import uk.gov.bis.lite.notification.message.SqsPollingJob;

public class NotificationApplication extends Application<NotificationConfiguration> {

  private final Module module;

  private GuiceBundle<NotificationConfiguration> guiceBundle;

  public NotificationApplication(Module module) {
    this.module = module;
  }

  @Override
  public void initialize(Bootstrap<NotificationConfiguration> bootstrap) {
    bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
        new ResourceConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));

    guiceBundle = new GuiceBundle.Builder<NotificationConfiguration>()
        .modules(module)
        .installers(ManagedInstaller.class)
        .build();
    bootstrap.addBundle(guiceBundle);
  }

  @Override
  public void run(NotificationConfiguration configuration, Environment environment) throws Exception {
    SqsPollingJob sqsPollingJob = guiceBundle.getInjector().getInstance(SqsPollingJob.class);
    environment.lifecycle().manage(sqsPollingJob);
    environment.admin().addServlet("admin", new AdminServlet()).addMapping("/admin");
    environment.admin().setSecurityHandler(new AdminConstraintSecurityHandler(configuration.getLogin(), configuration.getPassword()));
  }

  public static void main(String[] args) throws Exception {
    new NotificationApplication(new GuiceModule()).run(args);
  }
}

