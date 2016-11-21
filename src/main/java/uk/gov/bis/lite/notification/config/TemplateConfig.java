package uk.gov.bis.lite.notification.config;


import java.util.List;
import java.util.Map;

public class TemplateConfig {

  private Map<String, String> templateIdMap;

  private Map<String, List<String>> templatePlaceholderMap;

  public Map<String, String> getTemplateIdMap() {
    return templateIdMap;
  }

  public Map<String, List<String>> getTemplatePlaceholderMap() {
    return templatePlaceholderMap;
  }
}
