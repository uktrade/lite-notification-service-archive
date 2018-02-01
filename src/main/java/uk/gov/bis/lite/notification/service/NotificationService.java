package uk.gov.bis.lite.notification.service;

import java.util.Map;

public interface NotificationService {
  void sendEmail(String templateId, String recipientEmail, Map<String, String> nameValueMap);

  void retryUnsentEmails();
}
