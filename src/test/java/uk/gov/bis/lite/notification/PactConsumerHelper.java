package uk.gov.bis.lite.notification;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.MessagePactProviderRule;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.model.v3.messaging.MessagePact;
import com.google.common.collect.ImmutableMap;
import uk.gov.bis.lite.notification.message.MessageQueueConsumer;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;


class PactConsumerHelper {

  static MessagePact createValidEmailNotification(MessagePactBuilder builder) {
    PactDslJsonBody body = new PactDslJsonBody()
        .stringType("template", "validTemplate")
        .stringType("email", "user@test.com")
        .object("personalisation")
        .stringType("validParamOne", "valueOne")
        .stringType("validParamTwo", "valueTwo")
        .closeObject()
        .asBody();
    return builder.expectsToReceive("a valid email notification")
        .withContent(body)
        .toPact();
  }

  static void verifyPact(MessagePactProviderRule mockProvider, MessageQueueConsumer messageQueueConsumer,
                         NotificationClient notificationClient) throws NotificationClientException {
    String message = new String(mockProvider.getMessage());
    messageQueueConsumer.handleMessage(message);

    verify(notificationClient).sendEmail(eq("1"), eq("user@test.com"),
        eq(ImmutableMap.of("validParamOne", "valueOne", "validParamTwo", "valueTwo")), eq(null));
  }

}
