package pocket.ledger.dto.v1;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ErrorResponse Tests")
public class ErrorResponseTest {
  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Should create ErrorResponse with valid parameters.")
    void shouldCreateErrorResponseWithValidParameters() {
      String message = "OK";
      int status = 200;
      String errorId = "error-id";
      LocalDateTime timestamp = LocalDateTime.now();
      Map<String, String> validationErrors = new HashMap<>();

      ErrorResponse errorResponse =
          new ErrorResponse(message, status, errorId, timestamp, validationErrors);

      assertThat(errorResponse.message()).isEqualTo(message);
      assertThat(errorResponse.status()).isEqualTo(status);
      assertThat(errorResponse.errorId()).isEqualTo(errorId);
      assertThat(errorResponse.timestamp()).isEqualTo(timestamp);
      assertThat(errorResponse.validationErrors()).isEqualTo(validationErrors);
    }

    @Test
    @DisplayName("Should create ErrorResponse with null parameters")
    void shouldCreateErrorResponseWithNullParameters() {
      String message = null;
      int status = 200;
      String errorId = null;
      LocalDateTime timestamp = null;
      Map<String, String> validationErrors = null;

      ErrorResponse errorResponse =
          new ErrorResponse(message, status, errorId, timestamp, validationErrors);

      assertThat(errorResponse.message()).isNull();
      assertThat(errorResponse.status()).isEqualTo(status);
      assertThat(errorResponse.errorId()).isNull();
      assertThat(errorResponse.timestamp()).isNull();
      assertThat(errorResponse.validationErrors()).isNull();
    }
  }

  @Nested
  @DisplayName("Static Factory Method Tests")
  class StaticFactoryMethodTests {

    @Test
    @DisplayName("Should create ErrorResponse using of(message, status, errorId)")
    void shouldCreateErrorResponseWithSimpleOf() {
      String message = "Not found";
      int status = 404;
      String errorId = "ERR-001";

      ErrorResponse errorResponse = ErrorResponse.of(message, status, errorId);

      assertThat(errorResponse.message()).isEqualTo(message);
      assertThat(errorResponse.status()).isEqualTo(status);
      assertThat(errorResponse.errorId()).isEqualTo(errorId);
      assertThat(errorResponse.timestamp()).isNotNull();
      assertThat(errorResponse.validationErrors()).isNull();
    }

    @Test
    @DisplayName("Should create ErrorResponse using of(message, status, errorId, validationErrors)")
    void shouldCreateErrorResponseWithValidationErrors() {
      String message = "Validation failed";
      int status = 400;
      String errorId = "ERR-002";
      Map<String, String> validationErrors =
          Map.of(
              "field1", "Field is required",
              "field2", "Invalid format");

      ErrorResponse errorResponse = ErrorResponse.of(message, status, errorId, validationErrors);

      assertThat(errorResponse.message()).isEqualTo(message);
      assertThat(errorResponse.status()).isEqualTo(status);
      assertThat(errorResponse.errorId()).isEqualTo(errorId);
      assertThat(errorResponse.timestamp()).isNotNull();
      assertThat(errorResponse.validationErrors()).isEqualTo(validationErrors);
      assertThat(errorResponse.validationErrors()).hasSize(2);
    }

    @Test
    @DisplayName("Should create ErrorResponse with empty validation errors map")
    void shouldCreateErrorResponseWithEmptyValidationErrors() {
      String message = "Bad request";
      int status = 400;
      String errorId = "ERR-003";
      Map<String, String> emptyValidationErrors = new HashMap<>();

      ErrorResponse errorResponse =
          ErrorResponse.of(message, status, errorId, emptyValidationErrors);

      assertThat(errorResponse.message()).isEqualTo(message);
      assertThat(errorResponse.status()).isEqualTo(status);
      assertThat(errorResponse.errorId()).isEqualTo(errorId);
      assertThat(errorResponse.timestamp()).isNotNull();
      assertThat(errorResponse.validationErrors()).isEmpty();
    }

    @Test
    @DisplayName("Should set current timestamp in factory methods")
    void shouldSetCurrentTimestampInFactoryMethods() {
      LocalDateTime beforeCall = LocalDateTime.now();

      ErrorResponse errorResponse1 = ErrorResponse.of("Message", 400, "ERR-001");
      ErrorResponse errorResponse2 = ErrorResponse.of("Message", 400, "ERR-002", null);

      LocalDateTime afterCall = LocalDateTime.now();

      assertThat(errorResponse1.timestamp()).isBetween(beforeCall, afterCall);
      assertThat(errorResponse2.timestamp()).isBetween(beforeCall, afterCall);
    }
  }
}
