package uk.gov.bis.lite.notification.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NotificationData {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationData.class);

  private Long id;
  private String templateId;
  private String recipientEmail;
  private String nameValueJson;

  public NotificationData(Long id, String templateId, String recipientEmail) {
    this.id = id;
    this.templateId = templateId;
    this.recipientEmail = recipientEmail;
  }

  public NotificationData(String templateId, String recipientEmail) {
    this.templateId = templateId;
    this.recipientEmail = recipientEmail;
  }

  public HashMap<String, String> getNameValueMap() {
    HashMap<String, String> notifyMap = new HashMap<>();
    try {
      ObjectMapper mapper = new ObjectMapper();
      TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {};
      notifyMap = mapper.readValue(nameValueJson, typeRef);
    } catch (IOException e) {
      LOGGER.error("getNameValueMap IOException", e);
    }
    return notifyMap;
  }

  public void setNameValueJson(Map<String, String> nameValueMap) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      nameValueJson = mapper.writeValueAsString(nameValueMap);
    } catch (IOException e) {
      LOGGER.error("setNameValueJson IOException", e);
    }
  }

  public String getNameValueJson() {
    return nameValueJson;
  }

  public Long getId() {
    return id;
  }

  public String getTemplateId() {
    return templateId;
  }

  public String getRecipientEmail() {
    return recipientEmail;
  }
}
