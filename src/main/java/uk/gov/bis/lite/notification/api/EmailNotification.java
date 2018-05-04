package uk.gov.bis.lite.notification.api;

import java.util.Map;

public class EmailNotification {

  private String template;
  private String emailAddress;
  private Map<String, String> personalisation;

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public Map<String, String> getPersonalisation() {
    return personalisation;
  }

  public void setPersonalisation(Map<String, String> personalisation) {
    this.personalisation = personalisation;
  }

}
