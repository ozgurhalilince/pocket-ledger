package pocket.ledger.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // Balance related errors
  INSUFFICIENT_BALANCE(
      "INSUFFICIENT_BALANCE",
      "Insufficient balance for withdrawal",
      HttpStatus.UNPROCESSABLE_ENTITY),

  // Transaction related errors
  TRANSACTION_NOT_FOUND("TRANSACTION_NOT_FOUND", "Transaction not found", HttpStatus.NOT_FOUND),
  INVALID_TRANSACTION_AMOUNT(
      "INVALID_TRANSACTION_AMOUNT", "Transaction amount must be positive", HttpStatus.BAD_REQUEST),
  INVALID_TRANSACTION_TYPE(
      "INVALID_TRANSACTION_TYPE", "Invalid transaction type", HttpStatus.BAD_REQUEST),

  // Validation errors
  VALIDATION_FAILED("VALIDATION_FAILED", "Input validation failed", HttpStatus.BAD_REQUEST),
  INVALID_REQUEST_FORMAT(
      "INVALID_REQUEST_FORMAT", "Invalid request format", HttpStatus.BAD_REQUEST),
  REQUIRED_FIELD_MISSING(
      "REQUIRED_FIELD_MISSING", "Required field is missing", HttpStatus.BAD_REQUEST),

  // System errors
  INTERNAL_SERVER_ERROR(
      "INTERNAL_SERVER_ERROR", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
  SERVICE_UNAVAILABLE(
      "SERVICE_UNAVAILABLE", "Service temporarily unavailable", HttpStatus.SERVICE_UNAVAILABLE),

  // Resource errors
  RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "Requested resource not found", HttpStatus.NOT_FOUND),
  METHOD_NOT_ALLOWED(
      "METHOD_NOT_ALLOWED", "HTTP method not allowed", HttpStatus.METHOD_NOT_ALLOWED);

  private final String code;
  private final String defaultMessage;
  private final HttpStatus httpStatus;

  ErrorCode(String code, String defaultMessage, HttpStatus httpStatus) {
    this.code = code;
    this.defaultMessage = defaultMessage;
    this.httpStatus = httpStatus;
  }

  public String getMessageKey() {
    return "error." + code.toLowerCase();
  }
}
