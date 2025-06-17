package pocket.ledger.exception;

import java.math.BigDecimal;
import lombok.Getter;
import pocket.ledger.dto.v1.ErrorCode;

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
