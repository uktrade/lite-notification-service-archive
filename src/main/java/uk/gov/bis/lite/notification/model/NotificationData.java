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

  private int id;
  private String templateId;
  private String recipientEmail;
  private String nameValueJson;
  private int retrySend; // represents 'retry' status, 1=true, 0=false
  private int retryCount;

  public NotificationData(int id) {
    this.id = id;
  }

  public NotificationData(String templateId, String recipientEmail) {
    this.templateId = templateId;
    this.recipientEmail = recipientEmail;
    this.retrySend = 1;
    this.retryCount = 0;
  }

  public boolean isRetry() {
    return retrySend == 1;
  }

  public void setAsSent() {
    this.retrySend = 0;
  }

  public void incrementRetry(int limit) {
    if(this.retryCount >= limit) {
      this.retrySend = 0;
    } else {
      this.retryCount++;
    }
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

  public int getId() {
    return id;
  }

  public String getTemplateId() {
    return templateId;
  }

  public String getRecipientEmail() {
    return recipientEmail;
  }

  public int getRetrySend() {
    return retrySend;
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

  public void setRetrySend(int retrySend) {
    this.retrySend = retrySend;
  }

  public void setRetryCount(int retryCount) {
    this.retryCount = retryCount;
  }
}
