package pocket.ledger.exception;

public class TransactionNotFoundException extends BaseBusinessException {
  public TransactionNotFoundException(Long id) {
    super(ErrorCode.TRANSACTION_NOT_FOUND, id);
  }
}
