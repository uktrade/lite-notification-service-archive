package uk.gov.bis.lite.notification.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class NotificationAppConfig extends Configuration {

  @NotEmpty
  private String notifyUrl;

  @NotEmpty
  private String notifyApiKey;

  @NotEmpty
  private String notifyServiceId;

  @NotEmpty
  private String maxRetryCount;

  @NotEmpty
  private String notificationRetryJobCron;

  @Valid
  @NotNull
  @JsonProperty("database")
  private DataSourceFactory database = new DataSourceFactory();

  @NotEmpty
  private String adminLogin;

  @NotEmpty
  private String adminPassword;

  public DataSourceFactory getDataSourceFactory() {
    return database;
  }

  public String getNotificationRetryJobCron() {
    return notificationRetryJobCron;
  }

  public String getNotifyUrl() {
    return notifyUrl;
  }

  public String getNotifyApiKey() {
    return notifyApiKey;
  }

  public String getNotifyServiceId() {
    return notifyServiceId;
  }

  public int getMaxRetryCount() {
    return Integer.parseInt(maxRetryCount);
  }

  public String getAdminLogin() {
    return adminLogin;
  }

  public String getAdminPassword() {
    return adminPassword;
  }
}
