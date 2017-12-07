package uk.gov.bis.lite.notification.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Optional;

public class SimpleAuthenticator implements Authenticator<BasicCredentials, PrincipalImpl> {

  private final String login;
  private final String password;

  public SimpleAuthenticator(String login, String password) {
    this.login = login;
    this.password = password;
  }

  @Override
  public Optional<PrincipalImpl> authenticate(BasicCredentials credentials) throws AuthenticationException {
    if (password.equals(credentials.getPassword()) && login.equals(credentials.getUsername())) {
      return Optional.of(new PrincipalImpl(credentials.getUsername()));
    }
    return Optional.empty();
  }
}
