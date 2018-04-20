package uk.gov.bis.lite.notification.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.notification.config.NotificationAppConfig;
import uk.gov.bis.lite.notification.dao.NotificationDao;
import uk.gov.bis.lite.notification.model.NotificationData;
import uk.gov.service.notify.Notification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.NotificationResponse;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class NotificationServiceImpl implements NotificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceImpl.class);

  private final int maxRetryCount;

  private final NotificationClient client;
  private final NotificationDao dao;

  @Inject
  public NotificationServiceImpl(NotificationAppConfig config, NotificationClient client, NotificationDao dao) {
    this.maxRetryCount = config.getMaxRetryCount();
    this.client = client;
    this.dao = dao;
  }

  /**
   * Send an Email
   */
  @Override
  public void sendEmail(String templateId, String recipientEmail, Map<String, String> nameValueMap) {
    HashMap<String, String> notifyMap = new HashMap<>(); // NotificationClient requires a HashMap<String, String>
    if (nameValueMap != null) {
      notifyMap = new HashMap<>(nameValueMap);
    }
    NotificationData data = initEmailNotificationData(templateId, recipientEmail, notifyMap);
    boolean success = doSendNotification(data);
    if (success) {
      data.setStatus(NotificationData.Status.SUCCESS);
    } else {
      data.setStatus(NotificationData.Status.RETRY);
    }
    dao.create(data);
  }

  /**
   * Get any unsent notifications, retry, and update
   */
  @Override
  public void retryUnsentEmails() {
    dao.getRetries().forEach(this::doRetryNotification);
  }

  /**
   * Attempts send, updates retry data accordingly
   */
  private void doRetryNotification(NotificationData data) {
    LOGGER.error("doRetryNotification: {}", data.getId());
    boolean success = doSendNotification(data);
    if (success) {
      data.setStatus(NotificationData.Status.SUCCESS);
    } else {
      data.incrementRetryCount();
      if (data.getRetryCount() >= maxRetryCount) {
        data.setStatus(NotificationData.Status.FAILED);
      }
    }
    dao.updateForRetry(data);
  }

  /**
   * Uses NotificationClient to send notification.
   * We returns TRUE for a non null Response - false otherwise
   */
  private boolean doSendNotification(NotificationData data) {
    boolean sent = false;
    try {
      NotificationResponse response = client.sendEmail(data.getTemplateId(), data.getRecipientEmail(), data.getNameValueMap());
      if (response != null) {
        sent = true;
        logResponse(response);
      } else {
        LOGGER.warn("Notification is null");
      }
    } catch (NotificationClientException e) {
      LOGGER.error("NotificationClientException", e);
    }
    return sent;
  }

  /**
   * We currently do not use data retrieved from Response - this is here for future reference
   * and test retrieving Notification status
   */
  private void logResponse(NotificationResponse response) {
    try {
      Notification notifyNotification = client.getNotificationById(response.getNotificationId());
      String status = notifyNotification.getStatus();
      if (status != null) {
        if (!(status.equals("created") || status.equals("sending") || status.equals("delivered"))) {
          // status probably "failed"
          LOGGER.warn("Notification with status: {}", status);
        }
      } else {
        LOGGER.warn("Notification with NULL status");
      }
    } catch (NotificationClientException e) {
      LOGGER.warn("NotificationClientException", e);
    } catch (JSONException e) {
      // this exception gets thrown quite frequently, so not logging full stack trace
      LOGGER.warn("JSONException");
    }
  }

  /**
   * Create and return new instance of NotificationData
   */
  private NotificationData initEmailNotificationData(String templateId, String recipientEmail,
                                                     HashMap<String, String> notifyMap) {
    NotificationData data = new NotificationData(NotificationData.Type.EMAIL, templateId);
    data.setRecipientEmail(recipientEmail);
    data.setNameValueJson(notifyMap);
    return data;
  }
}
