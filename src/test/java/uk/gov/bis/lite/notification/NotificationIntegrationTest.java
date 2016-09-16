package uk.gov.bis.lite.notification;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.bis.lite.notification.config.NotificationAppConfig;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.assertj.core.api.Assertions.assertThat;

public class NotificationIntegrationTest {

  @ClassRule
  public static final WireMockRule wireMockRule = new WireMockRule(9000);

  @Rule
  public final DropwizardAppRule<NotificationAppConfig> RULE =
      new DropwizardAppRule<>(NotificationApp.class, resourceFilePath("service-test.yaml"));

  @Test
  @Ignore
  public void testNotification() throws Exception {

    JerseyClient client = new JerseyClientBuilder().build();

    String requestJson = "{\"toName\":" + "\"dan1\"," +
        "\"applicationRef\":\"ref1234\"}";

    Response response = client.target("http://localhost:8090/notification/send-email?template=ogelService:licenceApproved&recipientEmail=dan.haynes@digital.bis.gov.uk")
        .request()
        .post(Entity.entity(requestJson, MediaType.APPLICATION_JSON_TYPE));


    assertThat(response.getStatus()).isEqualTo(200);

  }

}