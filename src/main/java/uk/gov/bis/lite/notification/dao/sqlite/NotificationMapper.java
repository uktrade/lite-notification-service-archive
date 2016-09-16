package uk.gov.bis.lite.notification.dao.sqlite;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import uk.gov.bis.lite.notification.model.NotificationData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationMapper implements ResultSetMapper<NotificationData> {

  @Override
  public NotificationData map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    return new NotificationData(r.getLong("ID"), r.getString("TEMPLATE_ID"), r.getString("RECIPIENT_EMAIL"));
  }
}
