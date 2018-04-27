package uk.gov.bis.lite.notification.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AwsConfig {

  @NotNull
  @Valid
  @JsonProperty("credentials")
  private AwsCredentialsConfig awsCredentialsConfig;

  @NotNull
  private String region;

  @NotNull
  private String sqsQueueUrl;

  @NotNull
  private Integer sqsWaitTimeSeconds;

  @NotNull
  private Integer sqsRetryDelaySeconds;

  public AwsCredentialsConfig getAwsCredentialsConfig() {
    return awsCredentialsConfig;
  }

  public String getRegion() {
    return region;
  }

  public String getSqsQueueUrl() {
    return sqsQueueUrl;
  }

  public Integer getSqsWaitTimeSeconds() {
    return sqsWaitTimeSeconds;
  }

  public Integer getSqsRetryDelaySeconds() {
    return sqsRetryDelaySeconds;
  }

}
