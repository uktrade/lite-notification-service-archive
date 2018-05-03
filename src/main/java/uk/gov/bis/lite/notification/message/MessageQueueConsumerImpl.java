package uk.gov.bis.lite.notification.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.notification.api.EmailNotification;
import uk.gov.bis.lite.notification.exception.SendEmailException;
import uk.gov.bis.lite.notification.service.TemplateService;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.io.IOException;
import java.util.Optional;

import javax.inject.Inject;

public class MessageQueueConsumerImpl implements MessageQueueConsumer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageQueueConsumerImpl.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final TemplateService templateService;
  private final NotificationClient notificationClient;

  @Inject
  public MessageQueueConsumerImpl(TemplateService templateService, NotificationClient notificationClient) {
    this.templateService = templateService;
    this.notificationClient = notificationClient;
  }

  @Override
  public boolean handleMessage(String message) {
    try {
      EmailNotification emailNotification = parseMessage(message);
      validateEmail(emailNotification);
      validatePersonalisation(emailNotification);
      String templateId = getTemplateId(emailNotification);
      try {
        SendEmailResponse sendEmailResponse = notificationClient.sendEmail(templateId,
            emailNotification.getEmailAddress(), emailNotification.getPersonalisation(), null);
        LOGGER.info("Successfully sent email with message {} and received response {}", message, sendEmailResponse);
        return true;
      } catch (NotificationClientException nce) {
        LOGGER.error("Client failed to send email", nce);
        return false;
      }
    } catch (SendEmailException sendEmailException) {
      LOGGER.error("Unable to send email with message {}", message, sendEmailException);
      return true;
    }
  }

  private String getTemplateId(EmailNotification emailNotification) throws SendEmailException {
    Optional<String> templateId = templateService.getTemplateId(emailNotification.getTemplate());
    if (!templateId.isPresent()) {
      throw new SendEmailException("Unknown templateId");
    } else {
      return templateId.get();
    }
  }

  private void validateEmail(EmailNotification emailNotification) throws SendEmailException {
    if (StringUtils.isBlank(emailNotification.getEmailAddress())) {
      throw new SendEmailException("emailAddress cannot be blank");
    }
  }

  private void validatePersonalisation(EmailNotification emailNotification) throws SendEmailException {
    Optional<String> error = templateService.validatePersonalisation(emailNotification.getTemplate(),
        emailNotification.getPersonalisation());
    if (error.isPresent()) {
      throw new SendEmailException(error.get());
    }
  }

  private EmailNotification parseMessage(String message) throws SendEmailException {
    EmailNotification emailNotification;
    try {
      emailNotification = MAPPER.readValue(message, EmailNotification.class);
    } catch (IOException ioe) {
      throw new SendEmailException("Unable to read email notification");
    }
    if (emailNotification == null) {
      throw new SendEmailException("Unable to read email notification");
    } else {
      return emailNotification;
    }
  }

}
