package uk.gov.bis.lite.notification.dao.sqlite;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import uk.gov.bis.lite.notification.model.NotificationData;

public interface NotificationInterface {

  @SqlQuery("SELECT * FROM LOCAL_NOTIFICATION WHERE ID = :id")
  @Mapper(NotificationMapper.class)
  NotificationData findById(@Bind("id") Long id);

  @SqlUpdate("INSERT INTO LOCAL_NOTIFICATION (TEMPLATE_ID, RECIPIENT_EMAIL) " +
      "VALUES (:templateId, :recipientEmail)")
  void insert(@Bind("templateId") String templateId, @Bind("recipientEmail") String recipientEmail);
}
