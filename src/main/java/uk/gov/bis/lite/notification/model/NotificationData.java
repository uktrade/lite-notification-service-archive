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

  private long id;
  private String templateId;
  private Type type;
  private String recipientEmail;
  private String nameValueJson;
  private Status status;
  private String created;
  private int retryCount;

  public enum Type {
    EMAIL, SMS;
  }

  public enum Status {
    CREATED, SUCCESS, RETRY, FAILED;
  }

  public NotificationData(long id) {
    this.id = id;
  }

  public NotificationData(Type type, String templateId) {
    this.type = type;
    this.templateId = templateId;
    this.status = Status.CREATED;
    this.retryCount = 0;
  }

  public void incrementRetryCount() {
    this.retryCount++;
  }

  public HashMap<String, String> getNameValueMap() {
    HashMap<String, String> notifyMap = new HashMap<>();
    try {
      ObjectMapper mapper = new ObjectMapper();
      TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {};
      notifyMap = mapper.readValue(nameValueJson, typeRef);
    } catch (IOException e) {
      LOGGER.error("IOException", e);
    }
    return notifyMap;
  }

  public void setNameValueJson(Map<String, String> nameValueMap) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      nameValueJson = mapper.writeValueAsString(nameValueMap);
    } catch (IOException e) {
      LOGGER.error("IOException", e);
    }
  }

  public String getNameValueJson() {
    return nameValueJson;
  }

  public long getId() {
    return id;
  }

  public String getTemplateId() {
    return templateId;
  }

  public String getRecipientEmail() {
    return recipientEmail;
  }

  public int getRetryCount() {
    return retryCount;
  }

  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }

  public void setRecipientEmail(String recipientEmail) {
    this.recipientEmail = recipientEmail;
  }

  public void setNameValueJson(String nameValueJson) {
    this.nameValueJson = nameValueJson;
  }

  public void setRetryCount(int retryCount) {
    this.retryCount = retryCount;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }
}
