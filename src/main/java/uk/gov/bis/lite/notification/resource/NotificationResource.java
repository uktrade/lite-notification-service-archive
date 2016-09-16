package uk.gov.bis.lite.notification.resource;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.notification.service.NotificationService;
import uk.gov.bis.lite.notification.service.TemplateService;

import java.util.Map;
import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
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
  @Path("/send-email")
  public Response emailNotification(@QueryParam("template") String template,
                                    @QueryParam("recipientEmail") String recipientEmail,
                                    Map<String, String> nameValueMap) {

    if (StringUtils.isBlank(template)) {
      badRequest("template parameter is mandatory");
    }
    if (StringUtils.isBlank(recipientEmail)) {
      badRequest("recipientEmail parameter is mandatory");
    }

    logParams(template, recipientEmail, nameValueMap);

    // We use the TemplateService to check if we have a valid template with a valid nameValueMap
    Optional<String> optId = templateService.getTemplateId(template);
    if (optId.isPresent()) {
      if (templateService.isValidNameValueMap(template, nameValueMap)) {
        notificationService.sendEmail(optId.get(), recipientEmail, nameValueMap);
      } else {
        badRequest("JSON body of name value pairs incorrect");
      }
    } else {
      badRequest("Unknown notification template");
    }
    return Response.ok("Email notification successfully received").build();
  }

  private void badRequest(String message) {
    throw new WebApplicationException(message, Response.Status.BAD_REQUEST);
  }

  private static void logParams(String template, String recipientEmail, Map<String, String> nameValueMap) {
    LOGGER.info("template: " + template);
    LOGGER.info("recipientEmail: " + recipientEmail);
    nameValueMap.forEach((key, value) -> {
      LOGGER.info("Key : " + key + " Value : " + value);
    });
  }
}
