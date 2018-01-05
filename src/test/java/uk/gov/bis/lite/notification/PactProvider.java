package uk.gov.bis.lite.notification;

import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;

import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import uk.gov.bis.lite.notification.config.NotificationAppConfig;


@RunWith(PactRunner.class)
@Provider("lite-notification-service")
@PactBroker(host = "pact-broker.mgmt.licensing.service.trade.gov.uk.test", port = "80")
public class PactProvider {

  @ClassRule
  public static DropwizardAppRule<NotificationAppConfig> RULE =
      new DropwizardAppRule<>(NotificationIntegrationPactApp.class, resourceFilePath("service-test-pact.yaml"));

  @TestTarget
  public final Target target = new HttpTarget(RULE.getLocalPort());

  @State("provided template name and parameters are valid")
  public void validTemplateDetails() {
    //Relies on template-config-test.yaml defining a template called "validTemplate" (slight hack)
  }

  @State("provided template information is invalid")
  public void invalidTemplateDetails() {
    //Relies on template-config-test.yaml not defining a template called "invalidTemplate" (slight hack)
  }

}



