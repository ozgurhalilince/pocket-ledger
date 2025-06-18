package pocket.ledger.model;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import pocket.ledger.enums.TransactionType;

@Getter
@Setter
public class Transaction extends BaseModel {
  private Long id;
  private Long sourceAccountId;
  private Long destinationAccountId;
  private BigDecimal amount;
  private TransactionType type;
  private String description;

  public Transaction(
      Long sourceAccountId,
      Long destinationAccountId,
      BigDecimal amount,
      TransactionType type,
      String description) {
    this.sourceAccountId = sourceAccountId;
    this.destinationAccountId = destinationAccountId;
    this.amount = amount;
    this.type = type;
    this.description = description;
  }
}
