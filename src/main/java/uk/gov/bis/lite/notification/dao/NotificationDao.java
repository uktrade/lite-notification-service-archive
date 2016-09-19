package uk.gov.bis.lite.notification.dao;

import uk.gov.bis.lite.notification.model.NotificationData;

import java.util.List;

public interface NotificationDao {

  void updateForRetry(NotificationData notification);

  void create(NotificationData notification);

  List<NotificationData> getRetries();
}
