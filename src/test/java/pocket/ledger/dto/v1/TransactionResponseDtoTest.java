package pocket.ledger.dto.v1;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import pocket.ledger.enums.TransactionType;
import pocket.ledger.model.Transaction;

class TransactionResponseDtoTest {

  @Test
  void constructor_shouldSetAllFields() {
    Long id = 123L;
    BigDecimal amount = BigDecimal.valueOf(100.50);
    TransactionType type = TransactionType.DEPOSIT;
    String description = "Test transaction";
    LocalDateTime createdDate = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
    LocalDateTime lastModifiedDate = LocalDateTime.of(2024, 1, 1, 10, 5, 0);

    TransactionResponseDto dto =
        new TransactionResponseDto(id, amount, type, description, createdDate, lastModifiedDate);

    assertThat(dto.id()).isEqualTo(id);
    assertThat(dto.amount()).isEqualTo(amount);
    assertThat(dto.type()).isEqualTo(type);
    assertThat(dto.description()).isEqualTo(description);
    assertThat(dto.createdDate()).isEqualTo(createdDate);
    assertThat(dto.lastModifiedDate()).isEqualTo(lastModifiedDate);
  }

  @Test
  void fromEntity_shouldMapAllFieldsCorrectly() {
    Transaction transaction =
        new Transaction(BigDecimal.valueOf(250.75), TransactionType.WITHDRAWAL, "Test withdrawal");
    transaction.setId(456L);
    LocalDateTime customCreatedDate = LocalDateTime.of(2024, 2, 1, 15, 30, 0);
    LocalDateTime customModifiedDate = LocalDateTime.of(2024, 2, 1, 15, 35, 0);
    transaction.setCreatedDate(customCreatedDate);
    transaction.setLastModifiedDate(customModifiedDate);

    TransactionResponseDto dto = TransactionResponseDto.fromEntity(transaction);

    assertThat(dto.id()).isEqualTo(456L);
    assertThat(dto.amount()).isEqualTo(BigDecimal.valueOf(250.75));
    assertThat(dto.type()).isEqualTo(TransactionType.WITHDRAWAL);
    assertThat(dto.description()).isEqualTo("Test withdrawal");
    assertThat(dto.createdDate()).isEqualTo(customCreatedDate);
    assertThat(dto.lastModifiedDate()).isEqualTo(customModifiedDate);
  }

  @Test
  void fromEntity_shouldHandleNullDescription() {
    Transaction transaction =
        new Transaction(BigDecimal.valueOf(100), TransactionType.DEPOSIT, null);
    transaction.setId(789L);

    TransactionResponseDto dto = TransactionResponseDto.fromEntity(transaction);

    assertThat(dto.description()).isNull();
    assertThat(dto.id()).isEqualTo(789L);
  }

  @Test
  void fromEntity_shouldHandleNullId() {
    Transaction transaction =
        new Transaction(BigDecimal.valueOf(50), TransactionType.DEPOSIT, "Test");

    TransactionResponseDto dto = TransactionResponseDto.fromEntity(transaction);

    assertThat(dto.id()).isNull();
    assertThat(dto.amount()).isEqualTo(BigDecimal.valueOf(50));
  }

  @Test
  void fromEntity_shouldPreservePrecision() {
    BigDecimal preciseAmount = new BigDecimal("123.456789");
    Transaction transaction =
        new Transaction(preciseAmount, TransactionType.DEPOSIT, "Precision test");

    TransactionResponseDto dto = TransactionResponseDto.fromEntity(transaction);

    assertThat(dto.amount()).isEqualTo(preciseAmount);
    assertThat(dto.amount().scale()).isEqualTo(preciseAmount.scale());
  }
}
