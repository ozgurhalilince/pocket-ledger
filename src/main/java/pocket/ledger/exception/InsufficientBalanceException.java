package pocket.ledger.exception;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class InsufficientBalanceException extends BaseBusinessException {
  private final BigDecimal currentBalance;
  private final BigDecimal requestedAmount;

  public InsufficientBalanceException(BigDecimal currentBalance, BigDecimal requestedAmount) {
    super(ErrorCode.INSUFFICIENT_BALANCE, currentBalance, requestedAmount);
    this.currentBalance = currentBalance;
    this.requestedAmount = requestedAmount;
  }
}
