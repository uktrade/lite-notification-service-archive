package uk.gov.bis.lite.notification.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Transaction;
import uk.gov.bis.lite.notification.dao.sqlite.NotificationInterface;
import uk.gov.bis.lite.notification.model.NotificationData;

import java.util.List;

@Singleton
public class NotificationDaoImpl implements NotificationDao {

  private final DBI jdbi;

  @Inject
  public NotificationDaoImpl(@Named("jdbi") DBI jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  @Transaction
  public void updateForRetry(NotificationData notification) {
    try (final Handle handle = jdbi.open()) {
      NotificationInterface notificationInterface = handle.attach(NotificationInterface.class);
      notificationInterface.updateForRetry(
          notification.getRetrySend(),
          notification.getRetryCount(),
          notification.getId());
    }
  }

  @Override
  @Transaction
  public List<NotificationData> getRetries() {
    try (final Handle handle = jdbi.open()) {
      NotificationInterface notificationInterface = handle.attach(NotificationInterface.class);
      return notificationInterface.getRetries();
    }
  }

  @Override
  @Transaction
  public void create(NotificationData notification) {
    try (final Handle handle = jdbi.open()) {
      NotificationInterface notificationInterface = handle.attach(NotificationInterface.class);
      notificationInterface.insert(
          notification.getTemplateId(),
          notification.getRecipientEmail(),
          notification.getNameValueJson(),
          notification.getRetrySend(),
          notification.getRetryCount());
    }
  }
}
