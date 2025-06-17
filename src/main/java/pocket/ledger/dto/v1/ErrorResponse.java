package pocket.ledger.dto.v1;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
    String message,
    int status,
    String errorId,
    LocalDateTime timestamp,
    Map<String, String> validationErrors) {
  public static ErrorResponse of(String message, int status, String errorId) {
    return new ErrorResponse(message, status, errorId, LocalDateTime.now(), null);
  }

  public static ErrorResponse of(
      String message, int status, String errorId, Map<String, String> validationErrors) {
    return new ErrorResponse(message, status, errorId, LocalDateTime.now(), validationErrors);
  }
}
