package pocket.ledger.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ErrorCodeTest {

  @Test
  void insufficient_balance_shouldHaveCorrectValues() {
    ErrorCode errorCode = ErrorCode.INSUFFICIENT_BALANCE;

    assertThat(errorCode.getCode()).isEqualTo("INSUFFICIENT_BALANCE");
    assertThat(errorCode.getDefaultMessage()).isEqualTo("Insufficient balance for withdrawal");
    assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    assertThat(errorCode.getMessageKey()).isEqualTo("error.insufficient_balance");
  }

  @Test
  void transaction_not_found_shouldHaveCorrectValues() {
    ErrorCode errorCode = ErrorCode.TRANSACTION_NOT_FOUND;

    assertThat(errorCode.getCode()).isEqualTo("TRANSACTION_NOT_FOUND");
    assertThat(errorCode.getDefaultMessage()).isEqualTo("Transaction not found");
    assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(errorCode.getMessageKey()).isEqualTo("error.transaction_not_found");
  }

  @Test
  void invalid_transaction_amount_shouldHaveCorrectValues() {
    ErrorCode errorCode = ErrorCode.INVALID_TRANSACTION_AMOUNT;

    assertThat(errorCode.getCode()).isEqualTo("INVALID_TRANSACTION_AMOUNT");
    assertThat(errorCode.getDefaultMessage()).isEqualTo("Transaction amount must be positive");
    assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(errorCode.getMessageKey()).isEqualTo("error.invalid_transaction_amount");
  }

  @Test
  void invalid_transaction_type_shouldHaveCorrectValues() {
    ErrorCode errorCode = ErrorCode.INVALID_TRANSACTION_TYPE;

    assertThat(errorCode.getCode()).isEqualTo("INVALID_TRANSACTION_TYPE");
    assertThat(errorCode.getDefaultMessage()).isEqualTo("Invalid transaction type");
    assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(errorCode.getMessageKey()).isEqualTo("error.invalid_transaction_type");
  }

  @Test
  void validation_failed_shouldHaveCorrectValues() {
    ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;

    assertThat(errorCode.getCode()).isEqualTo("VALIDATION_FAILED");
    assertThat(errorCode.getDefaultMessage()).isEqualTo("Input validation failed");
    assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(errorCode.getMessageKey()).isEqualTo("error.validation_failed");
  }

  @Test
  void invalid_request_format_shouldHaveCorrectValues() {
    ErrorCode errorCode = ErrorCode.INVALID_REQUEST_FORMAT;

    assertThat(errorCode.getCode()).isEqualTo("INVALID_REQUEST_FORMAT");
    assertThat(errorCode.getDefaultMessage()).isEqualTo("Invalid request format");
    assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(errorCode.getMessageKey()).isEqualTo("error.invalid_request_format");
  }

  @Test
  void required_field_missing_shouldHaveCorrectValues() {
    ErrorCode errorCode = ErrorCode.REQUIRED_FIELD_MISSING;

    assertThat(errorCode.getCode()).isEqualTo("REQUIRED_FIELD_MISSING");
    assertThat(errorCode.getDefaultMessage()).isEqualTo("Required field is missing");
    assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(errorCode.getMessageKey()).isEqualTo("error.required_field_missing");
  }

  @Test
  void internal_server_error_shouldHaveCorrectValues() {
    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

    assertThat(errorCode.getCode()).isEqualTo("INTERNAL_SERVER_ERROR");
    assertThat(errorCode.getDefaultMessage()).isEqualTo("Internal server error");
    assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(errorCode.getMessageKey()).isEqualTo("error.internal_server_error");
  }

  @Test
  void service_unavailable_shouldHaveCorrectValues() {
    ErrorCode errorCode = ErrorCode.SERVICE_UNAVAILABLE;

    assertThat(errorCode.getCode()).isEqualTo("SERVICE_UNAVAILABLE");
    assertThat(errorCode.getDefaultMessage()).isEqualTo("Service temporarily unavailable");
    assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    assertThat(errorCode.getMessageKey()).isEqualTo("error.service_unavailable");
  }

  @Test
  void resource_not_found_shouldHaveCorrectValues() {
    ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;

    assertThat(errorCode.getCode()).isEqualTo("RESOURCE_NOT_FOUND");
    assertThat(errorCode.getDefaultMessage()).isEqualTo("Requested resource not found");
    assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(errorCode.getMessageKey()).isEqualTo("error.resource_not_found");
  }

  @Test
  void method_not_allowed_shouldHaveCorrectValues() {
    ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;

    assertThat(errorCode.getCode()).isEqualTo("METHOD_NOT_ALLOWED");
    assertThat(errorCode.getDefaultMessage()).isEqualTo("HTTP method not allowed");
    assertThat(errorCode.getHttpStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    assertThat(errorCode.getMessageKey()).isEqualTo("error.method_not_allowed");
  }

  @Test
  void getMessageKey_shouldReturnLowercaseKeyWithPrefix() {
    assertThat(ErrorCode.INSUFFICIENT_BALANCE.getMessageKey())
        .isEqualTo("error.insufficient_balance");
    assertThat(ErrorCode.TRANSACTION_NOT_FOUND.getMessageKey())
        .isEqualTo("error.transaction_not_found");
    assertThat(ErrorCode.INTERNAL_SERVER_ERROR.getMessageKey())
        .isEqualTo("error.internal_server_error");
  }

  @Test
  void allErrorCodes_shouldHaveNonNullValues() {
    for (ErrorCode errorCode : ErrorCode.values()) {
      assertThat(errorCode.getCode()).isNotNull().isNotEmpty();
      assertThat(errorCode.getDefaultMessage()).isNotNull().isNotEmpty();
      assertThat(errorCode.getHttpStatus()).isNotNull();
      assertThat(errorCode.getMessageKey()).isNotNull().isNotEmpty();
    }
  }

  @Test
  void enum_shouldHaveExpectedNumberOfValues() {
    ErrorCode[] errorCodes = ErrorCode.values();

    assertThat(errorCodes).hasSize(11);
  }
}
