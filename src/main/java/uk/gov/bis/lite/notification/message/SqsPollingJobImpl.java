package uk.gov.bis.lite.notification.message;

import com.amazonaws.services.sqs.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.notification.util.ThreadUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class SqsPollingJobImpl implements SqsPollingJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(SqsPollingJobImpl.class);

  private final ExecutorService executorService = Executors.newSingleThreadExecutor();
  private final MessageQueueConsumer messageQueueConsumer;
  private final SqsClient sqsClient;

  private volatile boolean running = true;

  @Inject
  public SqsPollingJobImpl(MessageQueueConsumer messageQueueConsumer, SqsClient sqsClient) {
    this.messageQueueConsumer = messageQueueConsumer;
    this.sqsClient = sqsClient;
  }

  @Override
  public void start() {
    Runnable runnable = () -> {
      while (running && !Thread.interrupted()) {
        try {
          LOGGER.info("Starting sqs poll");
          List<Message> messages = sqsClient.pollForNewMessages();
          messages.forEach(this::handleMessage);
        } catch (Exception exception) {
          LOGGER.error("An exception occurred receiving messages from sqs", exception);
          ThreadUtil.sleep(5000);
        }
      }
    };
    LOGGER.info("Starting sqs polling job");
    executorService.submit(runnable);
  }

  private void handleMessage(Message message) {
    if (running && !Thread.interrupted()) {
      String messageString = message.getBody();
      LOGGER.info("Received message: {}", messageString);

      boolean success = messageQueueConsumer.handleMessage(messageString);
      if (success) {
        sqsClient.deleteMessage(message);
      } else {
        sqsClient.retryFailedMessage(message);
      }
    }
  }

  @Override
  public void stop() throws InterruptedException {
    running = false;
    executorService.shutdownNow();
    executorService.awaitTermination(10, TimeUnit.SECONDS);
  }

}
