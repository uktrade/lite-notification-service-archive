package uk.gov.bis.lite.notification.api;

import java.util.Map;

public class EmailNotification {

  private String template;
  private String email;
  private Map<String, String> personalisation;

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Map<String, String> getPersonalisation() {
    return personalisation;
  }

  public void setPersonalisation(Map<String, String> personalisation) {
    this.personalisation = personalisation;
  }

}
