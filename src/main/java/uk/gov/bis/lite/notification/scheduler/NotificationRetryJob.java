package uk.gov.bis.lite.notification.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.notification.service.NotificationService;

public class NotificationRetryJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationRetryJob.class);

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    LOGGER.info("Starting NotificationRetryJob...");
    NotificationService service = (NotificationService) context.getMergedJobDataMap().get("notificationService");
    service.retryUnsentEmails();
  }
}
