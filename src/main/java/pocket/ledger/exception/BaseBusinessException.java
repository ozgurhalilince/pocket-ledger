package pocket.ledger.exception;

import lombok.Getter;
import pocket.ledger.dto.v1.ErrorCode;

@Getter
public abstract class BaseBusinessException extends RuntimeException {
  private final ErrorCode errorCode;
  private final Object[] parameters;

  protected BaseBusinessException(ErrorCode errorCode, Object... parameters) {
    super(errorCode.getDefaultMessage());
    this.errorCode = errorCode;
    this.parameters = parameters;
  }

  protected BaseBusinessException(ErrorCode errorCode, Throwable cause, Object... parameters) {
    super(errorCode.getDefaultMessage(), cause);
    this.errorCode = errorCode;
    this.parameters = parameters;
  }

  public String getLocalizedMessage() {
    return getMessage();
  }
}
