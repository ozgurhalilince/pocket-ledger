package pocket.ledger.dto.v1;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import pocket.ledger.enums.TransactionType;
import pocket.ledger.model.Transaction;

public record TransactionResponseDto(
    Long id,
    BigDecimal amount,
    TransactionType type,
    String description,
    LocalDateTime createdDate,
    LocalDateTime lastModifiedDate) {

  public static TransactionResponseDto fromEntity(Transaction transaction) {
    return new TransactionResponseDto(
        transaction.getId(),
        transaction.getAmount(),
        transaction.getType(),
        transaction.getDescription(),
        transaction.getCreatedDate(),
        transaction.getLastModifiedDate());
  }
}
