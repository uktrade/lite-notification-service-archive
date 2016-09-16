package uk.gov.bis.lite.notification.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.notification.config.NotificationAppConfig;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Singleton
public class TemplateService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateService.class);

  private Map<String, String> templateIdMap;
  private Map<String, List<String>> templatePlaceholderMap;

  @Inject
  public TemplateService(NotificationAppConfig config) {
    this.templateIdMap = config.getTemplateIdMap();
    this.templatePlaceholderMap = config.getTemplatePlaceholderMap();
  }

  /**
   * Returns template ID if it exists, empty Optional otherwise
   */
  public Optional<String> getTemplateId(String nameKey) {
    return templateIdMap.containsKey(nameKey) ? Optional.of(templateIdMap.get(nameKey)) : Optional.empty();
  }

  /**
   * The nameValueMap is valid for a template if it contains all the expected names from placeholder list
   * Empty and null nameValueMap and placeholder list are considered equivalent
   */
  public boolean isValidNameValueMap(String template, Map<String, String> nameValueMap) {
    boolean valid = false;
    List<String> placeholders = templatePlaceholderMap.get(template);
    if (placeholders == null && nameValueMap == null) {
      valid = true;
    } else if ((placeholders == null && nameValueMap.isEmpty()) || (nameValueMap == null && placeholders.isEmpty())) {
      valid = true;
    } else if (placeholders != null && nameValueMap != null && nameValueMap.keySet().containsAll(placeholders)) {
      valid = true;
    }
    return valid;
  }
}
