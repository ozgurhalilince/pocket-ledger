package pocket.ledger.exception;

import pocket.ledger.dto.v1.ErrorCode;

public class TransactionNotFoundException extends BaseBusinessException {
  public TransactionNotFoundException(Long id) {
    super(ErrorCode.TRANSACTION_NOT_FOUND, id);
  }
}
