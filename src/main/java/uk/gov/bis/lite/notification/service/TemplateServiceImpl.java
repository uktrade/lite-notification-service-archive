package uk.gov.bis.lite.notification.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import uk.gov.bis.lite.notification.config.TemplateConfig;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TemplateServiceImpl implements TemplateService {

  private final Map<String, String> templateIdMap;
  private final Map<String, List<String>> templatePlaceholderMap;

  @Inject
  public TemplateServiceImpl(TemplateConfig config) {
    this.templateIdMap = config.getTemplateIdMap();
    this.templatePlaceholderMap = config.getTemplatePlaceholderMap();
  }

  /**
   * Returns template id if it exists
   */
  @Override
  public Optional<String> getTemplateId(String templateId) {
    return Optional.ofNullable(templateIdMap.get(templateId));
  }

  /**
   * Returns a string detailing any difference between the template placeholder names and the names in
   * the personalisation provided
   */
  @Override
  public Optional<String> validatePersonalisation(String template, Map<String, String> personalisation) {
    List<String> placeHolders = templatePlaceholderMap.get(template);
    if (placeHolders == null) {
      return Optional.of("Unknown template " + template);
    } else {
      Set<String> placeholderSet = new HashSet<>(placeHolders);
      Set<String> personalisationSet = personalisation != null ? personalisation.keySet() : new HashSet<>();

      Sets.SetView<String> missingDifference = Sets.difference(placeholderSet, personalisationSet);
      Sets.SetView<String> unrecognisedDifference = Sets.difference(personalisationSet, placeholderSet);

      if (!missingDifference.isEmpty() || !unrecognisedDifference.isEmpty()) {
        String detail = "";
        if (!missingDifference.isEmpty()) {
          detail = detail + "Missing [" + Joiner.on(", ").join(missingDifference) + "]";
        }
        if (!missingDifference.isEmpty() && !unrecognisedDifference.isEmpty()) {
          detail = detail + " ";
        }
        if (!unrecognisedDifference.isEmpty()) {
          detail = detail + "Unrecognised [" + Joiner.on(", ").join(unrecognisedDifference) + "]";
        }
        return Optional.of(detail);
      } else {
        return Optional.empty();
      }
    }
  }
}
