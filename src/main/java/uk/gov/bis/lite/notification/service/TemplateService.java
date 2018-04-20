package uk.gov.bis.lite.notification.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.notification.config.TemplateConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Singleton
public class TemplateService {

  private final Map<String, String> templateIdMap;
  private final Map<String, List<String>> templatePlaceholderMap;

  @Inject
  public TemplateService(TemplateConfig config) {
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
    List<String> placeholders = templatePlaceholderMap.getOrDefault(template, new ArrayList<>());
    Set<String> keySet = nameValueMap == null ? new HashSet<>() : nameValueMap.keySet();
    return placeholders.containsAll(keySet) && keySet.containsAll(placeholders);
  }

  /**
   * Returns a String detailing any difference between the template placeholder names and the names in
   * the nameValueMap provided
   */
  public String getMismatchForPlaceholdersDetail(String template, Map<String, String> nameValueMap) {
    Set<String> placeHolderSet = new HashSet<>(templatePlaceholderMap.get(template));
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
