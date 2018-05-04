package uk.gov.bis.lite.notification.service;

import java.util.Map;
import java.util.Optional;

public interface TemplateService {

  Optional<String> getTemplateId(String templateId);

  Optional<String> validatePersonalisation(String template, Map<String, String> nameValueMap);

}
