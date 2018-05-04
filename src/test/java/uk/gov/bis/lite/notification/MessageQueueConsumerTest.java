package uk.gov.bis.lite.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import uk.gov.bis.lite.notification.api.EmailNotification;
import uk.gov.bis.lite.notification.message.MessageQueueConsumer;
import uk.gov.bis.lite.notification.message.MessageQueueConsumerImpl;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.Map;

public class MessageQueueConsumerTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final Map<String, String> VALID_PERSONALISATION = ImmutableMap.of("validParamOne", "valueOne",
      "validParamTwo", "valueTwo");
  private final String VALID_TEMPLATE = "validTemplate";
  private final String VALID_EMAIL_ADDRESS = "test@test.com";

  private final NotificationClient notificationClient = mock(NotificationClient.class);
  private final MessageQueueConsumer messageQueueConsumer = new MessageQueueConsumerImpl(new MockTemplateService(),
      notificationClient);

  @Test
  public void handleMessageShouldReturnTrueForMessageWithInvalidJson() {
    boolean deleteMessage = messageQueueConsumer.handleMessage("{abc");

    assertThat(deleteMessage).isTrue();
    verifyZeroInteractions(notificationClient);
  }

  @Test
  public void handleMessageShouldReturnTrueForNullMessage() {
    boolean deleteMessage = messageQueueConsumer.handleMessage("null");

    assertThat(deleteMessage).isTrue();
    verifyZeroInteractions(notificationClient);
  }

  @Test
  public void handleMessageShouldReturnTrueForMessageWithBlankEmailAddress() {
    EmailNotification emailNotification = new EmailNotification();
    emailNotification.setTemplate(VALID_TEMPLATE);
    emailNotification.setEmailAddress("   ");
    emailNotification.setPersonalisation(VALID_PERSONALISATION);

    boolean deleteMessage = messageQueueConsumer.handleMessage(toJson(emailNotification));

    assertThat(deleteMessage).isTrue();
    verifyZeroInteractions(notificationClient);
  }

  @Test
  public void handleMessageShouldReturnTrueForMessageWithInvalidTemplate() {
    EmailNotification emailNotification = new EmailNotification();
    emailNotification.setTemplate("invalidTemplate");
    emailNotification.setEmailAddress(VALID_EMAIL_ADDRESS);
    emailNotification.setPersonalisation(VALID_PERSONALISATION);

    boolean deleteMessage = messageQueueConsumer.handleMessage(toJson(emailNotification));

    assertThat(deleteMessage).isTrue();
    verifyZeroInteractions(notificationClient);
  }

  @Test
  public void handleMessageShouldReturnTrueForMessageWithInvalidPersonalisation() {
    EmailNotification emailNotification = new EmailNotification();
    emailNotification.setTemplate(VALID_TEMPLATE);
    emailNotification.setEmailAddress(VALID_EMAIL_ADDRESS);
    emailNotification.setPersonalisation(ImmutableMap.of("invalidParam", "value"));

    boolean deleteMessage = messageQueueConsumer.handleMessage(toJson(emailNotification));

    assertThat(deleteMessage).isTrue();
    verifyZeroInteractions(notificationClient);
  }

  @Test
  public void handleMessageShouldReturnFalseForMessageCausingNotificationClientException() throws NotificationClientException {
    when(notificationClient.sendEmail(eq("1"), eq(VALID_EMAIL_ADDRESS), eq(VALID_PERSONALISATION), eq(null)))
        .thenThrow(new NotificationClientException("An error occurred"));

    EmailNotification emailNotification = new EmailNotification();
    emailNotification.setTemplate(VALID_TEMPLATE);
    emailNotification.setEmailAddress(VALID_EMAIL_ADDRESS);
    emailNotification.setPersonalisation(VALID_PERSONALISATION);

    boolean deleteMessage = messageQueueConsumer.handleMessage(toJson(emailNotification));

    assertThat(deleteMessage).isFalse();
    verify(notificationClient).sendEmail(eq("1"), eq(VALID_EMAIL_ADDRESS), eq(VALID_PERSONALISATION), eq(null));
  }

  @Test
  public void handleMessageShouldReturnTrueForEmailedMessage() throws NotificationClientException {
    EmailNotification emailNotification = new EmailNotification();
    emailNotification.setTemplate(VALID_TEMPLATE);
    emailNotification.setEmailAddress(VALID_EMAIL_ADDRESS);
    emailNotification.setPersonalisation(VALID_PERSONALISATION);

    boolean deleteMessage = messageQueueConsumer.handleMessage(toJson(emailNotification));

    assertThat(deleteMessage).isTrue();
    verify(notificationClient).sendEmail(eq("1"), eq(VALID_EMAIL_ADDRESS), eq(VALID_PERSONALISATION), eq(null));
  }

  private String toJson(EmailNotification emailNotification) {
    try {
      return MAPPER.writeValueAsString(emailNotification);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Unable to convert email notification to json");
    }
  }

}
