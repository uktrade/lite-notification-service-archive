package uk.gov.bis.lite.notification.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class NotificationConfiguration extends Configuration {

  @NotEmpty
  private String login;

  @NotEmpty
  private String password;

  @NotEmpty
  private String notifyUrl;

  @NotEmpty
  private String notifyApiKey;

  @NotNull
  @Valid
  @JsonProperty("aws")
  private AwsConfig awsConfig;

  public NotificationConfiguration() {
  }

  public String getLogin() {
    return login;
  }

  public String getPassword() {
    return password;
  }

  public String getNotifyUrl() {
    return notifyUrl;
  }

  public String getNotifyApiKey() {
    return notifyApiKey;
  }

  public AwsConfig getAwsConfig() {
    return awsConfig;
  }

}
