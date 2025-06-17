package pocket.ledger.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import pocket.ledger.dto.v1.ErrorResponse;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler exceptionHandler;

  @Mock private WebRequest request;
  @Mock private HttpInputMessage httpInputMessage;

  @BeforeEach
  void setUp() {
    exceptionHandler = new GlobalExceptionHandler();
  }

  @Test
  void handleInsufficientBalanceException_returnsUnprocessableEntity() {
    when(request.getDescription(false)).thenReturn("uri=/api/v1/transactions");
    InsufficientBalanceException ex =
        new InsufficientBalanceException(BigDecimal.valueOf(100), BigDecimal.valueOf(150));

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleInsufficientBalanceException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(body.message()).contains("Insufficient balance");
  }

  @Test
  void handleTransactionNotFoundException_returnsNotFound() {
    when(request.getDescription(false)).thenReturn("uri=/api/v1/transactions/1");
    TransactionNotFoundException ex = new TransactionNotFoundException(1L);

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleTransactionNotFoundException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertAll(
        () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
        () -> assertThat(body.message()).contains("Transaction not found"));
  }

  @Test
  void handleIllegalArgumentException_returnsBadRequest() {
    IllegalArgumentException ex = new IllegalArgumentException("Illegal argument provided");
    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleIllegalArgumentException(ex, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void handleMethodArgumentTypeMismatchException_returnsBadRequest() {
    MethodArgumentTypeMismatchException ex =
        new MethodArgumentTypeMismatchException(
            "abc",
            Long.class,
            "id",
            mock(org.springframework.core.MethodParameter.class),
            new NumberFormatException("For input string: \"abc\""));

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleMethodArgumentTypeMismatchException(ex, request);
    ErrorResponse errorResponse = Objects.requireNonNull(response.getBody());

    assertThat(errorResponse).isNotNull();
    assertAll(
        () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
        () -> assertThat(errorResponse.message()).contains("should be of type"));
  }

  @Test
  void handleNoHandlerFoundException_returnsNotFound() {
    HttpHeaders headers = new HttpHeaders();
    NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/invalid-url", headers);
    when(request.getDescription(false)).thenReturn("uri=/invalid-url");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleNoHandlerFoundException(ex, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void handleGenericException_returnsInternalServerError() {
    Exception ex = new RuntimeException("Unexpected error");
    when(request.getDescription(false)).thenReturn("uri=/api/v1/crash");

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(body.message()).isEqualTo("Internal server error");
  }

  @Test
  void handleGenericException_withNullCause_returnsInternalServerError() {
    Exception ex = new RuntimeException("Error with no cause");
    when(request.getDescription(false)).thenReturn("uri=/api/v1/test");

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(body.message()).isEqualTo("Internal server error");
  }

  @Test
  void handleMethodArgumentTypeMismatchException_withNullRequiredType_returnsBadRequest() {
    MethodArgumentTypeMismatchException ex =
        new MethodArgumentTypeMismatchException(
            "invalid",
            null,
            "param",
            mock(org.springframework.core.MethodParameter.class),
            new Exception("Type conversion error"));

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleMethodArgumentTypeMismatchException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(body.message()).isEqualTo("Invalid type for parameter param");
  }

  @Test
  void handleValidationExceptions_withNonFieldError_shouldHandleCorrectly() {
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);

    ObjectError objectError = new ObjectError("objectName", "Global error message");
    when(ex.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getAllErrors()).thenReturn(List.of(objectError));
    when(bindingResult.getFieldErrorCount()).thenReturn(0);
    when(request.getDescription(false)).thenReturn("uri=/api/v1/test");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleValidationExceptions(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(body.message()).isEqualTo("Validation failed");
    assertThat(body.validationErrors()).isEmpty();
  }

  @Test
  void handleValidationExceptions_withFieldErrors_shouldCollectFieldErrors() {
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);

    FieldError fieldError = new FieldError("objectName", "fieldName", "Field error message");
    when(ex.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
    when(bindingResult.getFieldErrorCount()).thenReturn(1);
    when(request.getDescription(false)).thenReturn("uri=/api/v1/validation");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleValidationExceptions(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(body.message()).isEqualTo("Validation failed");
    assertThat(body.validationErrors()).containsEntry("fieldName", "Field error message");
  }

  @Test
  void handleHttpMediaTypeNotSupportedException_returnsUnsupportedMediaType() {
    HttpMediaTypeNotSupportedException ex =
        new HttpMediaTypeNotSupportedException("application/xml not supported");
    when(request.getDescription(false)).thenReturn("uri=/api/v1/test");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleHttpMediaTypeNotSupportedException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    assertThat(body.message()).isEqualTo("Unsupported media type");
  }

  @Test
  void handleHttpMessageNotReadableException_withEnumError_returnsBadRequest() {
    String enumErrorMessage =
        "JSON parse error: Cannot deserialize value of type `pocket.ledger.enums.TransactionType`"
            + " from String \"WITDRAWAL\": not one of the values accepted for Enum class:"
            + " [WITHDRAWAL, DEPOSIT]";
    HttpMessageNotReadableException ex =
        new HttpMessageNotReadableException(enumErrorMessage, httpInputMessage);
    when(request.getDescription(false)).thenReturn("uri=/api/v1/transactions");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleHttpMessageNotReadableException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(body.message())
        .isEqualTo("Invalid transaction type. Valid values are: DEPOSIT, WITHDRAWAL");
  }

  @Test
  void handleHttpMessageNotReadableException_withGenericJsonError_returnsBadRequest() {
    String jsonErrorMessage = "JSON parse error: Unexpected character";
    HttpMessageNotReadableException ex =
        new HttpMessageNotReadableException(jsonErrorMessage, httpInputMessage);
    when(request.getDescription(false)).thenReturn("uri=/api/v1/transactions");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleHttpMessageNotReadableException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(body.message()).isEqualTo("Invalid JSON format in request body");
  }

  @Test
  void handleHttpMessageNotReadableException_withUnknownError_returnsBadRequest() {
    String unknownErrorMessage = "Some other parsing error";
    HttpMessageNotReadableException ex =
        new HttpMessageNotReadableException(unknownErrorMessage, httpInputMessage);
    when(request.getDescription(false)).thenReturn("uri=/api/v1/transactions");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleHttpMessageNotReadableException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(body.message()).isEqualTo("Invalid request format");
  }

  @Test
  void handleHttpMessageNotReadableException_withRootCauseValidValues_returnsBadRequest() {
    RuntimeException rootCause = new RuntimeException("Valid values are: DEPOSIT, WITHDRAWAL");
    HttpMessageNotReadableException ex =
        new HttpMessageNotReadableException("Parse error", rootCause, httpInputMessage);
    when(request.getDescription(false)).thenReturn("uri=/api/v1/transactions");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleHttpMessageNotReadableException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(body.message()).isEqualTo("Valid values are: DEPOSIT, WITHDRAWAL");
  }

  @Test
  void handleHttpMessageNotReadableException_withRootCauseInvalidType_returnsBadRequest() {
    RuntimeException rootCause = new RuntimeException("Invalid transaction type: INVALID");
    HttpMessageNotReadableException ex =
        new HttpMessageNotReadableException("Parse error", rootCause, httpInputMessage);
    when(request.getDescription(false)).thenReturn("uri=/api/v1/transactions");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleHttpMessageNotReadableException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(body.message()).isEqualTo("Invalid transaction type: INVALID");
  }

  @Test
  void handleHttpMessageNotReadableException_withNullMessage_returnsBadRequest() {
    HttpMessageNotReadableException ex =
        new HttpMessageNotReadableException("", new RuntimeException(), httpInputMessage);
    when(request.getDescription(false)).thenReturn("uri=/api/v1/transactions");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleHttpMessageNotReadableException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(body.message()).isEqualTo("Invalid request format");
  }

  @Test
  void handleHttpMessageNotReadableException_withUnexpectedCharacter_returnsBadRequest() {
    String errorMessage = "JSON parse error: Unexpected character at position 10";
    HttpMessageNotReadableException ex =
        new HttpMessageNotReadableException(errorMessage, httpInputMessage);
    when(request.getDescription(false)).thenReturn("uri=/api/v1/transactions");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleHttpMessageNotReadableException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(body.message()).isEqualTo("Invalid JSON format in request body");
  }

  @Test
  void handleHttpMessageNotReadableException_withUnexpectedToken_returnsBadRequest() {
    String errorMessage = "JSON parse error: Unexpected token START_OBJECT";
    HttpMessageNotReadableException ex =
        new HttpMessageNotReadableException(errorMessage, httpInputMessage);
    when(request.getDescription(false)).thenReturn("uri=/api/v1/transactions");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleHttpMessageNotReadableException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(body.message()).isEqualTo("Invalid JSON format in request body");
  }

  @Test
  void handleGenericException_withNonNullCause_returnsInternalServerError() {
    Exception cause = new IllegalStateException("Cause of the error");
    Exception ex = new RuntimeException("Error with cause", cause);
    when(request.getDescription(false)).thenReturn("uri=/api/v1/test");

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(ex, request);
    ErrorResponse body = Objects.requireNonNull(response.getBody());

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(body.message()).isEqualTo("Internal server error");
  }
}
