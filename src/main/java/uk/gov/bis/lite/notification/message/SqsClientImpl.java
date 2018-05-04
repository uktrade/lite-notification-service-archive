package uk.gov.bis.lite.notification.message;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SqsClientImpl implements SqsClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(SqsClientImpl.class);

  private final String queueUrl;
  private final int sqsWaitTimeSeconds;
  private final int sqsRetryDelaySeconds;
  private final AmazonSQS amazonSQS;

  @Inject
  public SqsClientImpl(@Named("sqsQueueUrl") String queueUrl, @Named("sqsWaitTimeSeconds") int sqsWaitTimeSeconds,
                       @Named("sqsRetryDelaySeconds") int sqsRetryDelaySeconds, AmazonSQS amazonSQS) {
    this.queueUrl = queueUrl;
    this.sqsWaitTimeSeconds = sqsWaitTimeSeconds;
    this.sqsRetryDelaySeconds = sqsRetryDelaySeconds;
    this.amazonSQS = amazonSQS;
  }

  @Override
  public List<Message> pollForNewMessages() {
    ReceiveMessageRequest receiveRequest = new ReceiveMessageRequest().withQueueUrl(queueUrl)
        .withWaitTimeSeconds(sqsWaitTimeSeconds);
    return amazonSQS.receiveMessage(receiveRequest)
        .getMessages();
  }

  @Override
  public void deleteMessage(Message message) {
    amazonSQS.deleteMessage(queueUrl, message.getReceiptHandle());
  }

  @Override
  public void retryFailedMessage(Message message) {
    LOGGER.error("Changing message visibility for message with id {} to {} seconds", message.getMessageId(),
        sqsRetryDelaySeconds);
    amazonSQS.changeMessageVisibility(queueUrl, message.getReceiptHandle(), sqsRetryDelaySeconds);
  }

}
