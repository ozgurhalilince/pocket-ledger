package pocket.ledger.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ErrorResponseTest {

  @Test
  void defaultConstructor_shouldCreateEmptyErrorResponse() {
    ErrorResponse errorResponse = new ErrorResponse();

    assertThat(errorResponse.getMessage()).isNull();
    assertThat(errorResponse.getStatus()).isEqualTo(0);
    assertThat(errorResponse.getTimestamp()).isNull();
    assertThat(errorResponse.getPath()).isNull();
    assertThat(errorResponse.getValidationErrors()).isNull();
  }

  @Test
  void constructorWithoutValidationErrors_shouldSetAllFields() {
    String message = "Test error message";
    int status = 400;
    LocalDateTime timestamp = LocalDateTime.now();
    String path = "/api/test";

    ErrorResponse errorResponse = new ErrorResponse(message, status, timestamp, path);

    assertThat(errorResponse.getMessage()).isEqualTo(message);
    assertThat(errorResponse.getStatus()).isEqualTo(status);
    assertThat(errorResponse.getTimestamp()).isEqualTo(timestamp);
    assertThat(errorResponse.getPath()).isEqualTo(path);
    assertThat(errorResponse.getValidationErrors()).isNull();
  }

  @Test
  void constructorWithValidationErrors_shouldSetAllFields() {
    String message = "Validation failed";
    int status = 422;
    LocalDateTime timestamp = LocalDateTime.now();
    String path = "/api/validation";
    Map<String, String> validationErrors = new HashMap<>();
    validationErrors.put("field1", "Field1 is required");
    validationErrors.put("field2", "Field2 must be positive");

    ErrorResponse errorResponse =
        new ErrorResponse(message, status, timestamp, path, validationErrors);

    assertThat(errorResponse.getMessage()).isEqualTo(message);
    assertThat(errorResponse.getStatus()).isEqualTo(status);
    assertThat(errorResponse.getTimestamp()).isEqualTo(timestamp);
    assertThat(errorResponse.getPath()).isEqualTo(path);
    assertThat(errorResponse.getValidationErrors()).isEqualTo(validationErrors);
  }

  @Test
  void setters_shouldUpdateAllFields() {
    ErrorResponse errorResponse = new ErrorResponse();
    String newMessage = "Updated message";
    int newStatus = 500;
    LocalDateTime newTimestamp = LocalDateTime.now();
    String newPath = "/api/updated";
    Map<String, String> newValidationErrors = new HashMap<>();
    newValidationErrors.put("newField", "New validation error");

    errorResponse.setMessage(newMessage);
    errorResponse.setStatus(newStatus);
    errorResponse.setTimestamp(newTimestamp);
    errorResponse.setPath(newPath);
    errorResponse.setValidationErrors(newValidationErrors);

    assertThat(errorResponse.getMessage()).isEqualTo(newMessage);
    assertThat(errorResponse.getStatus()).isEqualTo(newStatus);
    assertThat(errorResponse.getTimestamp()).isEqualTo(newTimestamp);
    assertThat(errorResponse.getPath()).isEqualTo(newPath);
    assertThat(errorResponse.getValidationErrors()).isEqualTo(newValidationErrors);
  }

  @Test
  void setMessage_shouldAllowNullValue() {
    ErrorResponse errorResponse = new ErrorResponse();

    errorResponse.setMessage(null);

    assertThat(errorResponse.getMessage()).isNull();
  }

  @Test
  void setStatus_shouldAcceptAnyIntegerValue() {
    ErrorResponse errorResponse = new ErrorResponse();

    errorResponse.setStatus(-1);
    assertThat(errorResponse.getStatus()).isEqualTo(-1);

    errorResponse.setStatus(0);
    assertThat(errorResponse.getStatus()).isEqualTo(0);

    errorResponse.setStatus(999);
    assertThat(errorResponse.getStatus()).isEqualTo(999);
  }

  @Test
  void setTimestamp_shouldAllowNullValue() {
    ErrorResponse errorResponse = new ErrorResponse();

    errorResponse.setTimestamp(null);

    assertThat(errorResponse.getTimestamp()).isNull();
  }

  @Test
  void setPath_shouldAllowNullValue() {
    ErrorResponse errorResponse = new ErrorResponse();

    errorResponse.setPath(null);

    assertThat(errorResponse.getPath()).isNull();
  }

  @Test
  void setValidationErrors_shouldAllowNullValue() {
    ErrorResponse errorResponse = new ErrorResponse();

    errorResponse.setValidationErrors(null);

    assertThat(errorResponse.getValidationErrors()).isNull();
  }

  @Test
  void getters_shouldReturnCorrectValues() {
    String message = "Test message";
    int status = 404;
    LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
    String path = "/test/path";
    Map<String, String> validationErrors = Map.of("key", "value");

    ErrorResponse errorResponse =
        new ErrorResponse(message, status, timestamp, path, validationErrors);

    assertThat(errorResponse.getMessage()).isSameAs(message);
    assertThat(errorResponse.getStatus()).isEqualTo(status);
    assertThat(errorResponse.getTimestamp()).isSameAs(timestamp);
    assertThat(errorResponse.getPath()).isSameAs(path);
    assertThat(errorResponse.getValidationErrors()).isSameAs(validationErrors);
  }
}
