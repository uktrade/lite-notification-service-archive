package uk.gov.bis.lite.notification.resource;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.notification.service.NotificationService;
import uk.gov.bis.lite.notification.service.TemplateService;

import java.util.Map;
import java.util.Optional;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/notification")
public class NotificationResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationResource.class);

  private final NotificationService notificationService;
  private final TemplateService templateService;

  @Inject
  public NotificationResource(NotificationService notificationService, TemplateService templateService) {
    this.notificationService = notificationService;
    this.templateService = templateService;
  }

  @POST
  @PermitAll
  @Consumes("application/json")
  @Produces("application/json")
  @Path("/send-email")
  public Response emailNotification(@QueryParam("template") String template,
                                    @QueryParam("recipientEmail") String recipientEmail,
                                    Map<String, String> nameValueMap) {

    if (StringUtils.isBlank(template)) {
      return badRequest("template parameter is mandatory");
    }
    if (StringUtils.isBlank(recipientEmail)) {
      return badRequest("recipientEmail parameter is mandatory");
    }
    logParams(template, recipientEmail, nameValueMap);

    // Check if we have a valid template with a matched nameValueMap/placeholder list
    Optional<String> optId = templateService.getTemplateId(template);
    if (optId.isPresent()) {
      if (templateService.isMatchedForPlaceholders(template, nameValueMap)) {
        notificationService.sendEmail(optId.get(), recipientEmail, nameValueMap);
      } else {
        return badRequest(templateService.getMismatchForPlaceholdersDetail(template, nameValueMap));
      }
    } else {
      return badRequest("Unknown notification template: " + template);
    }
    return goodRequest();
  }

  private Response badRequest(String message) {
    LOGGER.error("Invalid request: {}", message);
    return Response.status(Response.Status.BAD_REQUEST)
        .entity(ImmutableMap.of("code", Response.Status.BAD_REQUEST.getStatusCode(), "message", message))
        .type(MediaType.APPLICATION_JSON_TYPE)
        .build();
  }

  private Response goodRequest() {
    return Response.ok("{\"status\": \"success\"}", MediaType.APPLICATION_JSON).build();
  }

  private static void logParams(String template, String recipientEmail, Map<String, String> nameValueMap) {
    LOGGER.info("template: {}", template);
    LOGGER.info("recipientEmail: {}", recipientEmail);
    if (nameValueMap != null) {
      nameValueMap.forEach((key, value) -> LOGGER.info("Key : {}  Value : {}", key, value));
    }
  }
}
