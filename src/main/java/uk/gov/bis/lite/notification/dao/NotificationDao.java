package uk.gov.bis.lite.notification.dao;

import uk.gov.bis.lite.notification.model.NotificationData;

public interface NotificationDao {

  void create(NotificationData notification);

}
