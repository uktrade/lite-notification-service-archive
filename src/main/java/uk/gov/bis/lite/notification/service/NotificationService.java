package uk.gov.bis.lite.notification.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
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
public class NotificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

  private String notifyUrl;
  private String notifyApiKey;
  private String notifyServiceId;
  private NotificationClient notificationClient;
  private NotificationDao notificationDao;

  private String NOTIFY_DELIVERED = "delivered";
  private String NOTIFY_SENDING = "sending";
  private String NOTIFY_FAILED = "failed";

  @Inject
  public NotificationService(NotificationAppConfig config, NotificationDao notificationDao) {
    this.notifyUrl = config.getNotifyUrl();
    this.notifyApiKey = config.getNotifyApiKey();
    this.notifyServiceId = config.getNotifyServiceId();
    this.notificationDao = notificationDao;
  }

  @Inject
  public void init() {
    notificationClient = new NotificationClient(notifyApiKey, notifyServiceId, notifyUrl);
  }

  /**
   * The NotificationClient sendEmail method requires a HashMap<String, String>
   */
  public void sendEmail(String templateId, String recipientEmail, Map<String, String> nameValueMap) {
    HashMap<String, String> notifyMap = new HashMap<>();
    if (nameValueMap != null) {
      notifyMap = new HashMap<>(nameValueMap);
    }

    try {
      NotificationResponse response = notificationClient.sendEmail(templateId, recipientEmail, notifyMap);

      if (response != null) {
        Notification notifyNotification = notificationClient.getNotificationById(response.getNotificationId());
        String status = notifyNotification.getStatus();
        if (status != null) {
          if (!(status.equals(NOTIFY_SENDING) || status.equals(NOTIFY_DELIVERED))) {
            LOGGER.info("Saving NotificationData with status: " + status);
            saveNotificationForRetry(templateId, recipientEmail, notifyMap);
          }
        } else {
          LOGGER.info("Saving NotificationData with NULL status");
          saveNotificationForRetry(templateId, recipientEmail, notifyMap);
        }
      } else {
        LOGGER.info("NotificationResponse NULL - saving NotificationData");
        saveNotificationForRetry(templateId, recipientEmail, notifyMap);
      }

    } catch (NotificationClientException e) {
      LOGGER.error("sendEmail NotificationClientException", e);
    }
  }

  /**
   * We save notification data in order that the retry mechanism can attempt to resend notification later
   */
  private void saveNotificationForRetry(String templateId, String recipientEmail, HashMap<String, String> notifyMap) {
    NotificationData data = new NotificationData(templateId, recipientEmail);
    data.setNameValueJson(notifyMap);
    notificationDao.create(data);

    LOGGER.info("saveNotificationForRetry: " + data.getNameValueJson());
    data.getNameValueMap().forEach((key, value) -> {
      LOGGER.info("Key : " + key + " Value : " + value);
    });
  }

  private static void logRequest(String templateId, String recipientEmail, HashMap<String, String> notifyMap) {
    LOGGER.info("Sending email request: " + templateId + "/" + recipientEmail);
    notifyMap.forEach((key, value) -> {
      LOGGER.info("Key : " + key + " Value : " + value);
    });
  }

  private static void logResponse(NotificationResponse response) {
    LOGGER.info("Response notificationId/templateVersion: " + response.getNotificationId() + "/" + response.getTemplateVersion());
    LOGGER.info("Response body: " + response.getBody());
  }
}
