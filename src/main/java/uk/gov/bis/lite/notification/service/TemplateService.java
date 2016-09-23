package uk.gov.bis.lite.notification.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.notification.config.NotificationAppConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
   * Does the nameValueMap match the placeholder list exactly?
   * Empty and null nameValueMap and placeholder list are considered equivalent
   */
  public boolean isMatchedForPlaceholders(String template, Map<String, String> nameValueMap) {
    boolean valid = false;
    List<String> placeholders = templatePlaceholderMap.get(template);
    if (placeholders == null && nameValueMap == null) {
      valid = true;
    } else if ((placeholders == null && nameValueMap.isEmpty()) || (nameValueMap == null && placeholders.isEmpty())) {
      valid = true;
    } else if (placeholders != null && nameValueMap != null && nameValueMap.keySet().containsAll(placeholders)
        && placeholders.containsAll(nameValueMap.keySet())) {
      valid = true;
    }
    return valid;
  }

  /**
   * Returns a String detailing any difference between the template placeholder names and the names in
   * the nameValueMap provided
   */
  public String getMismatchForPlaceholdersDetail(String template, Map<String, String> nameValueMap) {
    Set<String> placeHolderSet = templatePlaceholderMap.get(template).stream().collect(Collectors.toSet());
    Set<String> nameValueSet = nameValueMap != null ? nameValueMap.keySet() : new HashSet<>();

    Sets.SetView<String> missingDifference = Sets.difference(placeHolderSet, nameValueSet);
    Sets.SetView<String> unrecognisedDifference = Sets.difference(nameValueSet, placeHolderSet);

    String detail = "";
    if (!missingDifference.isEmpty()) {
      detail = "Missing [" + Joiner.on(", ").join(missingDifference) + "]";
    }
    if (!unrecognisedDifference.isEmpty()) {
      detail = detail + " Unrecognised [" + Joiner.on(", ").join(unrecognisedDifference) + "]";
    }
    return detail;
  }
}
