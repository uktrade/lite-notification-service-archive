package uk.gov.bis.lite.notification.dao.sqlite;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import uk.gov.bis.lite.notification.model.NotificationData;

import java.util.List;

public interface NotificationInterface {

  @SqlQuery("SELECT * FROM LOCAL_NOTIFICATION WHERE ID = :id")
  @Mapper(NotificationMapper.class)
  NotificationData findById(@Bind("id") int id);

  @SqlUpdate("INSERT INTO LOCAL_NOTIFICATION (TEMPLATE_ID, RECIPIENT_EMAIL, NAME_VALUE_JSON, RETRY_SEND, RETRY_COUNT) " +
      "VALUES (:templateId, :recipientEmail, :nameValueJson, :retrySend, :retryCount)")
  void insert(@Bind("templateId") String templateId,
              @Bind("recipientEmail") String recipientEmail,
              @Bind("nameValueJson") String nameValueJson,
              @Bind("retrySend") int retrySend,
              @Bind("retryCount") int retryCount);


  @SqlQuery("SELECT * FROM LOCAL_NOTIFICATION WHERE RETRY_SEND = 1")
  @Mapper(NotificationMapper.class)
  List<NotificationData> getRetries();

  @SqlUpdate("UPDATE LOCAL_NOTIFICATION " +
              "SET    RETRY_SEND = :retrySend, " +
              "       RETRY_COUNT = :retryCount " +
              "WHERE  ID = :id")
  Integer updateForRetry(@Bind("retrySend") int retrySend,
                         @Bind("retryCount") int retryCount,
                         @Bind("id") int id);
}
