package pocket.ledger.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import pocket.ledger.dto.v1.ErrorCode;

class BaseBusinessExceptionTest {

  private static class TestBusinessException extends BaseBusinessException {
    public TestBusinessException(ErrorCode errorCode, Object... parameters) {
      super(errorCode, parameters);
    }

    public TestBusinessException(ErrorCode errorCode, Throwable cause, Object... parameters) {
      super(errorCode, cause, parameters);
    }
  }

  @Test
  void constructor_withErrorCodeAndParameters_shouldSetAllFields() {
    ErrorCode errorCode = ErrorCode.INSUFFICIENT_BALANCE;
    Object[] parameters = {"param1", "param2", 123};

    TestBusinessException exception = new TestBusinessException(errorCode, parameters);

    assertThat(exception.getErrorCode()).isEqualTo(errorCode);
    assertThat(exception.getParameters()).isEqualTo(parameters);
    assertThat(exception.getMessage()).isEqualTo(errorCode.getDefaultMessage());
    assertThat(exception.getCause()).isNull();
  }

  @Test
  void constructor_withErrorCodeAndNoParameters_shouldWork() {
    ErrorCode errorCode = ErrorCode.TRANSACTION_NOT_FOUND;

    TestBusinessException exception = new TestBusinessException(errorCode);

    assertThat(exception.getErrorCode()).isEqualTo(errorCode);
    assertThat(exception.getParameters()).isNotNull();
    assertThat(exception.getParameters()).isEmpty();
    assertThat(exception.getMessage()).isEqualTo(errorCode.getDefaultMessage());
  }

  @Test
  void constructor_withErrorCodeCauseAndParameters_shouldSetAllFields() {
    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    RuntimeException cause = new RuntimeException("Root cause");
    Object[] parameters = {"error", 500};

    TestBusinessException exception = new TestBusinessException(errorCode, cause, parameters);

    assertThat(exception.getErrorCode()).isEqualTo(errorCode);
    assertThat(exception.getParameters()).isEqualTo(parameters);
    assertThat(exception.getMessage()).isEqualTo(errorCode.getDefaultMessage());
    assertThat(exception.getCause()).isEqualTo(cause);
  }

  @Test
  void constructor_withErrorCodeAndCauseNoParameters_shouldWork() {
    ErrorCode errorCode = ErrorCode.SERVICE_UNAVAILABLE;
    Exception cause = new Exception("Service down");

    TestBusinessException exception = new TestBusinessException(errorCode, cause);

    assertThat(exception.getErrorCode()).isEqualTo(errorCode);
    assertThat(exception.getParameters()).isNotNull();
    assertThat(exception.getParameters()).isEmpty();
    assertThat(exception.getMessage()).isEqualTo(errorCode.getDefaultMessage());
    assertThat(exception.getCause()).isEqualTo(cause);
  }

  @Test
  void getLocalizedMessage_shouldReturnSameAsMessage() {
    ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
    TestBusinessException exception = new TestBusinessException(errorCode);

    String localizedMessage = exception.getLocalizedMessage();

    assertThat(localizedMessage).isEqualTo(exception.getMessage());
    assertThat(localizedMessage).isEqualTo(errorCode.getDefaultMessage());
  }

  @Test
  void getErrorCode_shouldReturnCorrectErrorCode() {
    ErrorCode errorCode = ErrorCode.INVALID_TRANSACTION_AMOUNT;
    TestBusinessException exception = new TestBusinessException(errorCode);

    assertThat(exception.getErrorCode()).isSameAs(errorCode);
  }

  @Test
  void getParameters_shouldReturnCorrectParameters() {
    ErrorCode errorCode = ErrorCode.REQUIRED_FIELD_MISSING;
    Object[] parameters = {"fieldName", "fieldValue"};
    TestBusinessException exception = new TestBusinessException(errorCode, parameters);

    assertThat(exception.getParameters()).isSameAs(parameters);
  }

  @Test
  void constructor_withNullParameters_shouldWork() {
    ErrorCode errorCode = ErrorCode.INVALID_REQUEST_FORMAT;
    Object[] nullParameters = null;

    TestBusinessException exception = new TestBusinessException(errorCode, nullParameters);

    assertThat(exception.getErrorCode()).isEqualTo(errorCode);
    assertThat(exception.getParameters()).isNull();
  }

  @Test
  void constructor_withCauseAndNullParameters_shouldWork() {
    ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;
    RuntimeException cause = new RuntimeException("Test cause");
    Object[] nullParameters = null;

    TestBusinessException exception = new TestBusinessException(errorCode, cause, nullParameters);

    assertThat(exception.getErrorCode()).isEqualTo(errorCode);
    assertThat(exception.getCause()).isEqualTo(cause);
    assertThat(exception.getParameters()).isNull();
  }
}
