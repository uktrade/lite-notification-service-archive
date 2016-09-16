package uk.gov.bis.lite.notification.scheduler;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationRetryJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationRetryJob.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LOGGER.info("Starting NotificationRetryJob...");

  }
}
