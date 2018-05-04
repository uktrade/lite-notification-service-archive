package uk.gov.bis.lite.notification.message;

import com.amazonaws.services.sqs.model.Message;

import java.util.List;

public interface SqsClient {

  List<Message> pollForNewMessages();

  void deleteMessage(Message message);

  void retryFailedMessage(Message message);

}
