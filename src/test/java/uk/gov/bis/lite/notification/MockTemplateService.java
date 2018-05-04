package uk.gov.bis.lite.notification;

import org.apache.commons.collections4.CollectionUtils;
import uk.gov.bis.lite.notification.service.TemplateService;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class MockTemplateService implements TemplateService {

  @Override
  public Optional<String> getTemplateId(String templateId) {
    if ("validTemplate".equals(templateId)) {
      return Optional.of("1");
    } else {
      return Optional.empty();
    }
  }

  @Override
  public Optional<String> validatePersonalisation(String template, Map<String, String> personalisation) {
    if ("validTemplate".equals(template) && CollectionUtils.isEqualCollection(personalisation.keySet(),
        Arrays.asList("validParamOne", "validParamTwo"))) {
      return Optional.empty();
    } else {
      return Optional.of("An error occurred");
    }
  }

}
