package uk.gov.bis.lite.notification;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Before;
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

  private Flyway flyway = new Flyway();

  @Before
  public void setupDatabase() {
    DataSourceFactory f = RULE.getConfiguration().getDataSourceFactory();
    flyway.setDataSource(f.getUrl(), f.getUser(), f.getPassword());
    flyway.migrate();
  }

  @Test
  @Ignore
  public void testNotification() throws Exception {

    JerseyClient client = new JerseyClientBuilder().build();

    String requestJson = "{\"name1\": \"value1\"}";

    Response response = client.target("http://localhost:8090/notification/send-email")
        .queryParam("template", "template1")
        .queryParam("recipientEmail", "dan.haynes@digital.bis.gov.uk")
        .request()
        .post(Entity.entity(requestJson, MediaType.APPLICATION_JSON_TYPE));


    assertThat(response.getStatus()).isEqualTo(200);

  }

}