package uk.gov.bis.lite.notification.exception;

import javax.ws.rs.WebApplicationException;

public class NotificationServiceException extends WebApplicationException {

  public NotificationServiceException(String message) {
    super(message);
  }

  public NotificationServiceException(String message, Throwable cause) {
    super(message, cause);
  }

}