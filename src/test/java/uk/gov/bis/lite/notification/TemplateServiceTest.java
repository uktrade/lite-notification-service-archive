package uk.gov.bis.lite.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import uk.gov.bis.lite.notification.config.TemplateConfig;
import uk.gov.bis.lite.notification.service.TemplateService;
import uk.gov.bis.lite.notification.service.TemplateServiceImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TemplateServiceTest {

  private TemplateService templateService;

  @Before
  public void setup() {
    Map<String, String> templateIdMap = ImmutableMap.of("validTemplate", "1");
    Map<String, List<String>> templatePlaceholderMap = new HashMap<>();
    templatePlaceholderMap.put("validTemplate", Arrays.asList("validParamOne", "validParamTwo"));
    TemplateConfig templateConfig = new TemplateConfig();
    templateConfig.setTemplateIdMap(templateIdMap);
    templateConfig.setTemplatePlaceholderMap(templatePlaceholderMap);
    templateService = new TemplateServiceImpl(templateConfig);
  }

  @Test
  public void getTemplateIdShouldReturnIdForValidTemplate() {
    Optional<String> templateId = templateService.getTemplateId("validTemplate");

    assertThat(templateId).contains("1");
  }

  @Test
  public void getTemplateIdShouldReturnEmptyOptionalForInvalidTemplate() {
    Optional<String> templateId = templateService.getTemplateId("invalidTemplate");

    assertThat(templateId).isEmpty();
  }

  @Test
  public void validatePersonalisationShouldReturnErrorForInvalidTemplate() {
    Optional<String> error = templateService.validatePersonalisation("invalidTemplate", new HashMap<>());

    assertThat(error).contains("Unknown template invalidTemplate");
  }

  @Test
  public void validatePersonalisationShouldReturnErrorForInvalidPersonalisation() {
    Optional<String> error = templateService.validatePersonalisation("validTemplate", ImmutableMap.of("invalidParamOne", "value"));

    assertThat(error).contains("Missing [validParamOne, validParamTwo] Unrecognised [invalidParamOne]");
  }

  @Test
  public void validatePersonalisationShouldReturnEmptyOptionalForValidPersonalisation() {
    Optional<String> error = templateService.validatePersonalisation("validTemplate",
        ImmutableMap.of("validParamOne", "valueOne", "validParamTwo", "valueTwo"));

    assertThat(error).isEmpty();
  }

}
