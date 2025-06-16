package pocket.ledger.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String ERROR_ID_PREFIX = "Error ID: ";
  private static final String CONTACT_SUPPORT_PREFIX = "Contact support with Error ID: ";

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex, WebRequest request) {
    String errorId = generateErrorId();
    String requestUri = request.getDescription(false);

    log.error(
        "Validation error [{}]: {} at {} - Field errors: {}",
        errorId,
        ex.getClass().getSimpleName(),
        requestUri,
        ex.getBindingResult().getFieldErrorCount());
    Map<String, String> validationErrors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              if (error instanceof FieldError fieldError) {
                validationErrors.put(fieldError.getField(), error.getDefaultMessage());
              }
            });

    ErrorResponse errorResponse =
        createErrorResponseWithValidation(
            "Validation failed", HttpStatus.BAD_REQUEST, errorId, validationErrors);

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InsufficientBalanceException.class)
  public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(
      InsufficientBalanceException ex, WebRequest request) {
    String errorId = generateErrorId();
    String requestUri = request.getDescription(false);

    log.error(
        "Insufficient balance error [{}]: {} at {} - Current: {}, Requested: {}",
        errorId,
        ex.getMessage(),
        requestUri,
        ex.getCurrentBalance(),
        ex.getRequestedAmount());
    ErrorResponse errorResponse =
        createErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY, errorId);
    return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler(TransactionNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleTransactionNotFoundException(
      TransactionNotFoundException ex, WebRequest request) {
    String errorId = generateErrorId();
    String requestUri = request.getDescription(false);

    log.error("Transaction not found error [{}]: {} at {}", errorId, ex.getMessage(), requestUri);
    ErrorResponse errorResponse =
        createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, errorId);
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    String errorId = generateErrorId();
    log.error("Illegal argument error [{}]: {}", errorId, ex.getMessage());
    ErrorResponse errorResponse =
        createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, errorId);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, WebRequest request) {
    String errorId = generateErrorId();
    String requestUri = request.getDescription(false);

    String errorMessage = "Invalid request format";
    String originalMessage = ex.getMessage();
    Throwable rootCause = ex.getRootCause();

    log.info(
        "JSON parse error [{}]: {} at {} - Root cause: {}",
        errorId,
        originalMessage,
        requestUri,
        rootCause != null ? rootCause.getMessage() : "none");

    if (rootCause != null && rootCause.getMessage() != null) {
      String rootMessage = rootCause.getMessage();
      if (rootMessage.contains("Valid values are: DEPOSIT, WITHDRAWAL")) {
        errorMessage = rootMessage;
      } else if (rootMessage.contains("Invalid transaction type")) {
        errorMessage = rootMessage;
      }
    }

    if (errorMessage.equals("Invalid request format") && originalMessage != null) {
      if (originalMessage.contains("TransactionType")) {
        errorMessage = "Invalid transaction type. Valid values are: DEPOSIT, WITHDRAWAL";
      } else if (originalMessage.contains("JSON parse error")) {
        errorMessage = "Invalid JSON format in request body";
      } else if (originalMessage.contains("Unexpected character")
          || originalMessage.contains("Unexpected token")) {
        errorMessage = "Invalid JSON format in request body";
      }
    }

    ErrorResponse errorResponse =
        createErrorResponse(errorMessage, HttpStatus.BAD_REQUEST, errorId);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    String errorId = generateErrorId();
    Class<?> requiredType = ex.getRequiredType();
    String errorMessage =
        requiredType != null
            ? ex.getName() + " should be of type " + requiredType.getSimpleName()
            : "Invalid type for parameter " + ex.getName();
    log.info("Type mismatch error [{}]: {}", errorId, errorMessage);
    ErrorResponse errorResponse =
        createErrorResponse(errorMessage, HttpStatus.BAD_REQUEST, errorId);
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(
      HttpMediaTypeNotSupportedException ex, WebRequest request) {
    String errorId = generateErrorId();
    String requestUri = request.getDescription(false);

    log.info("Unsupported media type error [{}]: {} at {}", errorId, ex.getMessage(), requestUri);
    ErrorResponse errorResponse =
        createErrorResponse("Unsupported media type", HttpStatus.UNSUPPORTED_MEDIA_TYPE, errorId);
    return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
      NoHandlerFoundException ex, WebRequest request) {
    String errorId = generateErrorId();
    String requestUri = request.getDescription(false);

    log.warn(
        "No handler found [{}]: {} {} at {}",
        errorId,
        ex.getHttpMethod(),
        ex.getRequestURL(),
        requestUri);
    ErrorResponse errorResponse =
        createErrorResponse("Endpoint not found", HttpStatus.NOT_FOUND, errorId);
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
    String errorId = generateErrorId();
    String requestUri = request.getDescription(false);

    log.error(
        "Unexpected error [{}]: {} at {} - Message: {} - Cause: {}",
        errorId,
        ex.getClass().getSimpleName(),
        requestUri,
        ex.getMessage(),
        ex.getCause() != null ? ex.getCause().getClass().getSimpleName() : "Unknown",
        ex);

    ErrorResponse errorResponse =
        createErrorResponseWithContactSupport(
            "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, errorId);

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private String generateErrorId() {
    return UUID.randomUUID().toString();
  }

  private ErrorResponse createErrorResponse(String message, HttpStatus status, String errorId) {
    return new ErrorResponse(
        message, status.value(), LocalDateTime.now(), ERROR_ID_PREFIX + errorId);
  }

  private ErrorResponse createErrorResponseWithValidation(
      String message, HttpStatus status, String errorId, Map<String, String> validationErrors) {
    return new ErrorResponse(
        message, status.value(), LocalDateTime.now(), ERROR_ID_PREFIX + errorId, validationErrors);
  }

  private ErrorResponse createErrorResponseWithContactSupport(
      String message, HttpStatus status, String errorId) {
    return new ErrorResponse(
        message, status.value(), LocalDateTime.now(), CONTACT_SUPPORT_PREFIX + errorId);
  }
}
