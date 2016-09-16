package uk.gov.bis.lite.notification.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.sqlobject.Transaction;
import uk.gov.bis.lite.notification.dao.sqlite.NotificationInterface;
import uk.gov.bis.lite.notification.model.NotificationData;

@Singleton
public class NotificationDaoImpl implements NotificationDao {

  private final DBI jdbi;

  @Inject
  public NotificationDaoImpl(@Named("jdbi") DBI jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  @Transaction
  public void create(NotificationData notification) {

    try (final Handle handle = jdbi.open()) {
      NotificationInterface notificationInterface = handle.attach(NotificationInterface.class);
      notificationInterface.insert(notification.getTemplateId(), notification.getRecipientEmail());
    }

  }

}
