package uk.gov.bis.lite.notification.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.skife.jdbi.v2.DBI;
import ru.vyarus.dropwizard.guice.module.support.ConfigurationAwareModule;
import uk.gov.bis.lite.notification.dao.NotificationDao;
import uk.gov.bis.lite.notification.dao.NotificationDaoImpl;
import uk.gov.bis.lite.notification.service.TemplateService;

public class GuiceModule extends AbstractModule implements ConfigurationAwareModule<NotificationAppConfig> {

  private NotificationAppConfig config;

  @Override
  protected void configure() {
    bind(NotificationDao.class).to(NotificationDaoImpl.class);
  }

  @Provides
  public TemplateService providesEmailTemplateService(NotificationAppConfig config) {
    return new TemplateService(config);
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

  @Override
  public void setConfiguration(NotificationAppConfig config) {
    this.config = config;
  }

}
