package uk.gov.bis.lite.notification.service;

import java.util.Map;

public class NotificationServiceMock implements NotificationService {

  @Override
  public void sendEmail(String templateId, String recipientEmail, Map<String, String> nameValueMap) {

  }

  @Override
  public void retryUnsentEmails() {

  }
}