package uk.gov.bis.lite.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.bis.lite.notification.resource.NotificationResource;
import uk.gov.bis.lite.notification.service.NotificationService;
import uk.gov.bis.lite.notification.service.TemplateService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class NotificationResourceTest {


  private NotificationService notificationService = mock(NotificationService.class);
  private TemplateService templateService = mock(TemplateService.class);

  @Rule
  public final ResourceTestRule resources = ResourceTestRule.builder()
      .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
      .addResource(new NotificationResource(notificationService, templateService))
      .addProvider(new JsonProcessingExceptionMapper(true))
      .build();


  @Before
  public void setUp() throws Exception {
  }

  @Test
  @Ignore
  public void sendEmail() throws Exception {
    String requestJson = "{\"toName\":" + "\"dan1\"," +
        "\"applicationRef\":\"ref1234\"}";

    Response response = resources.client().target("http://localhost:9998/notification/send-email?template=ogelService:licenceApproved&recipientEmail=dan.haynes@digital.bis.gov.uk")
        .request()
        .post(Entity.entity(requestJson, MediaType.APPLICATION_JSON_TYPE));


    assertThat(response.getStatus()).isEqualTo(200);

  }
}
