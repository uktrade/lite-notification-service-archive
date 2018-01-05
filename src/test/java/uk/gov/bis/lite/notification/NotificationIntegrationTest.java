package uk.gov.bis.lite.notification;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_5;

import io.dropwizard.testing.junit.DropwizardAppRule;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import uk.gov.bis.lite.common.paas.db.SchemaAwareDataSourceFactory;
import uk.gov.bis.lite.notification.config.NotificationAppConfig;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class NotificationIntegrationTest {

  private static EmbeddedPostgres postgres;

  public DropwizardAppRule<NotificationAppConfig> RULE;
  private Flyway flyway;

  @BeforeClass
  public static void beforeClass() throws Exception {
    postgres = new EmbeddedPostgres(V9_5);
    postgres.start("localhost", 5432, "dbName", "postgres", "password");
  }

  @AfterClass
  public static void afterClass() {
    postgres.stop();
  }

  @Before
  public void before() {
    RULE = new DropwizardAppRule<>(NotificationIntegrationTestApp.class, resourceFilePath("service-test.yaml"));
    RULE.getTestSupport().before();

    SchemaAwareDataSourceFactory dataSourceFactory = RULE.getConfiguration().getDataSourceFactory();
    flyway = new Flyway();
    flyway.setDataSource(dataSourceFactory.getUrl(), dataSourceFactory.getUser(), dataSourceFactory.getPassword());
    flyway.migrate();
  }

  @After
  public void after() {
    RULE.getTestSupport().after();
    flyway.clean();
  }

  @Test
  public void testNotification() throws Exception {

    JerseyClient client = new JerseyClientBuilder().build();

    String requestJson = fixture("fixture/integration/request.json");

    Response response = client.target("http://localhost:8090/notification/send-email")
        .queryParam("template", "validTemplate")
        .queryParam("recipientEmail", "dan.haynes@digital.bis.gov.uk")
        .request()
        .header("Authorization", "Basic c2VydmljZTpwYXNzd29yZA==")
        .post(Entity.entity(requestJson, MediaType.APPLICATION_JSON_TYPE));

    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  public void testNotificationUnauthorized() throws Exception {

    JerseyClient client = new JerseyClientBuilder().build();

    String requestJson = fixture("fixture/integration/request.json");

    Response response = client.target("http://localhost:8090/notification/send-email")
        .queryParam("template", "validTemplate")
        .queryParam("recipientEmail", "dan.haynes@digital.bis.gov.uk")
        .request()
        .post(Entity.entity(requestJson, MediaType.APPLICATION_JSON_TYPE));

    assertThat(response.getStatus()).isEqualTo(401);
  }
}