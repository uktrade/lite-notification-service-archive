package uk.gov.bis.lite.notification;

import static org.mockito.Mockito.mock;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.MessagePactProviderRule;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.model.v3.messaging.MessagePact;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.bis.lite.notification.message.MessageQueueConsumer;
import uk.gov.bis.lite.notification.message.MessageQueueConsumerImpl;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

public class OgelRegistrationPactConsumer {

  // Added suffix message to lite-ogel-registration due to https://github.com/DiUS/pact-jvm/issues/610
  private final static String PROVIDER = "lite-ogel-registration-message";
  private final static String CONSUMER = "lite-notification-service";

  private final NotificationClient notificationClient = mock(NotificationClient.class);
  private final MessageQueueConsumer messageQueueConsumer = new MessageQueueConsumerImpl(new MockTemplateService(),
      notificationClient);

  @Rule
  public final MessagePactProviderRule mockProvider = new MessagePactProviderRule(PROVIDER, this);

  @Pact(provider = PROVIDER, consumer = CONSUMER)
  public MessagePact createValidEmailNotification(MessagePactBuilder builder) {
    return PactConsumerHelper.createValidEmailNotification(builder);
  }

  @Test
  @PactVerification(value = PROVIDER, fragment = "createValidEmailNotification")
  public void receiveValidEmailNotification() throws NotificationClientException {
    PactConsumerHelper.verifyPact(mockProvider, messageQueueConsumer, notificationClient);
  }

}
