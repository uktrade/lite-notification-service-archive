package uk.gov.bis.lite.notification.scheduler;

import com.google.inject.Inject;
import io.dropwizard.lifecycle.Managed;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.notification.config.NotificationAppConfig;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class NotificationScheduler implements Managed {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationScheduler.class);

  private final Scheduler scheduler;
  private final NotificationAppConfig config;

  @Inject
  public NotificationScheduler(Scheduler scheduler, NotificationAppConfig config) {
    this.scheduler = scheduler;
    this.config = config;
  }

  @Override
  public void start() throws Exception {
    LOGGER.info("NotificationScheduler start...");

    JobKey jobKey = JobKey.jobKey("notificationJob");
    JobDetail jobDetail = newJob(NotificationRetryJob.class)
        .withIdentity(jobKey)
        .build();

    CronTrigger trigger = newTrigger()
        .withIdentity(TriggerKey.triggerKey("notificationRetryJobTrigger"))
        .withSchedule(cronSchedule(config.getNotificationRetryJobCron()))
        .build();

    scheduler.scheduleJob(jobDetail, trigger);
    scheduler.start();
    scheduler.triggerJob(jobKey);
  }

  @Override
  public void stop() throws Exception {
    scheduler.shutdown(true);
  }
}
