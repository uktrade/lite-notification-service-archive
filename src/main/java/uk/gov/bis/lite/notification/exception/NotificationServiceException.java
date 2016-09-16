package uk.gov.bis.lite.notification.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class NotificationServiceException extends RuntimeException {

  public NotificationServiceException(String message) {
    super(message);
  }

  public NotificationServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public static class ServiceExceptionMapper
    implements ExceptionMapper<NotificationServiceException>, ErrorResponse {

    private static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    @Override
    public Response toResponse(NotificationServiceException exception) {
      return buildResponse(exception.getMessage(), STATUS_INTERNAL_SERVER_ERROR);
    }

  }

}