package pocket.ledger.model;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import pocket.ledger.enums.TransactionType;

@Getter
@Setter
public class Transaction extends BaseModel {
  private Long id;
  private BigDecimal amount;
  private TransactionType type;
  private String description;

  public Transaction(BigDecimal amount, TransactionType type, String description) {
    this.amount = amount;
    this.type = type;
    this.description = description;
  }
}
