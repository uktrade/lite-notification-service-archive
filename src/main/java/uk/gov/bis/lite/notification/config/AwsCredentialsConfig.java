package uk.gov.bis.lite.notification.config;

public class AwsCredentialsConfig {

  private String profileName;

  private String accessKey;
  private String secretKey;

  public String getProfileName() {
    return profileName;
  }

  public String getAccessKey() {
    return accessKey;
  }

  public String getSecretKey() {
    return secretKey;
  }
}
