package uk.gov.bis.lite.notification.resource;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.notification.service.NotificationService;
import uk.gov.bis.lite.notification.service.TemplateService;

import java.util.Map;
import java.util.Optional;

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
  @Consumes("application/json")
  @Produces("application/json")
  @Path("/send-email")
  public Response emailNotification(@QueryParam("template") @NotEmpty String template,
                                    @QueryParam("recipientEmail") @NotEmpty String recipientEmail,
                                    Map<String, String> nameValueMap) {

    logParams(template, recipientEmail, nameValueMap);

    // Check if we have a valid template with a valid nameValueMap
    Optional<String> optId = templateService.getTemplateId(template);
    if (optId.isPresent()) {
      if (templateService.isValidNameValueMap(template, nameValueMap)) {
        notificationService.sendEmail(optId.get(), recipientEmail, nameValueMap);
      } else {
        return badRequest("JSON body of name value pairs incorrect");
      }
    } else {
      return badRequest("Unknown notification template");
    }
    return goodRequest();
  }

  private Response badRequest(String message) {
    return Response.status(Response.Status.BAD_REQUEST)
        .entity(ImmutableMap.of("code", Response.Status.BAD_REQUEST, "message", message))
        .type(MediaType.APPLICATION_JSON_TYPE)
        .build();
  }

  private Response goodRequest() {
    return Response.ok("{\"status\": \"success\"}", MediaType.APPLICATION_JSON).build();
  }

  private static void logParams(String template, String recipientEmail, Map<String, String> nameValueMap) {
    LOGGER.info("template: " + template);
    LOGGER.info("recipientEmail: " + recipientEmail);
    nameValueMap.forEach((key, value) -> {
      LOGGER.info("Key : " + key + " Value : " + value);
    });
  }
}
