package uk.gov.bis.lite.notification.message;

public interface MessageQueueConsumer {

  boolean handleMessage(String message);

}
