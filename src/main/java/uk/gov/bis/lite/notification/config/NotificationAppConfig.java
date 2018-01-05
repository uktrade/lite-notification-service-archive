package uk.gov.bis.lite.notification.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;
import uk.gov.bis.lite.common.paas.db.SchemaAwareDataSourceFactory;

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
  private SchemaAwareDataSourceFactory dataSourceFactory;

  @NotEmpty
  private String serviceLogin;

  @NotEmpty
  private String servicePassword;

  public SchemaAwareDataSourceFactory getDataSourceFactory() {
    return dataSourceFactory;
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

  public String getServiceLogin() {
    return serviceLogin;
  }

  public String getServicePassword() {
    return servicePassword;
  }
}
