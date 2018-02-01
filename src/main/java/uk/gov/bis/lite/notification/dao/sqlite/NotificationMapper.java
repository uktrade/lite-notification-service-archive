package uk.gov.bis.lite.notification.dao.sqlite;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import uk.gov.bis.lite.notification.model.NotificationData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationMapper implements ResultSetMapper<NotificationData> {

  @Override
  public NotificationData map(int index, ResultSet r, StatementContext ctx) throws SQLException {

    NotificationData data = new NotificationData(r.getLong("ID"));
    data.setStatus(NotificationData.Status.valueOf(r.getString("STATUS")));
    data.setType(NotificationData.Type.valueOf(r.getString("TYPE")));
    data.setTemplateId(r.getString("TEMPLATE_ID"));
    data.setRecipientEmail(r.getString("RECIPIENT_EMAIL"));
    data.setNameValueJson(r.getString("NAME_VALUE_JSON"));
    data.setRetryCount(r.getInt("RETRY_COUNT"));
    data.setCreated(r.getString("CREATED"));
    return data;
  }
}
