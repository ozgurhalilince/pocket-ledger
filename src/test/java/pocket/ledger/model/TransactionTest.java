package pocket.ledger.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pocket.ledger.enums.TransactionType;

@DisplayName("Transaction Model Tests")
class TransactionTest {

  @Nested
  @DisplayName("Transaction Creation")
  class TransactionCreation {

    @Test
    @DisplayName("Should create transaction with all parameters")
    void shouldCreateTransactionWithAllParameters() {
      BigDecimal amount = BigDecimal.valueOf(100.50);
      TransactionType type = TransactionType.DEPOSIT;
      String description = "Test deposit";

      Transaction transaction = new Transaction(amount, type, description);

      assertAll(
          () -> assertThat(transaction.getAmount()).isEqualTo(amount),
          () -> assertThat(transaction.getType()).isEqualTo(type),
          () -> assertThat(transaction.getDescription()).isEqualTo(description),
          () -> assertThat(transaction.getId()).isNull(),
          () -> assertThat(transaction.getCreatedDate()).isNotNull(),
          () -> assertThat(transaction.getLastModifiedDate()).isNotNull());
    }

    @Test
    @DisplayName("Should create transaction with null description")
    void shouldCreateTransactionWithNullDescription() {
      BigDecimal amount = BigDecimal.valueOf(50);
      TransactionType type = TransactionType.WITHDRAWAL;

      Transaction transaction = new Transaction(amount, type, null);

      assertAll(
          () -> assertThat(transaction.getAmount()).isEqualTo(amount),
          () -> assertThat(transaction.getType()).isEqualTo(type),
          () -> assertThat(transaction.getDescription()).isNull());
    }

    @Test
    @DisplayName("Should create transaction with empty description")
    void shouldCreateTransactionWithEmptyDescription() {
      BigDecimal amount = BigDecimal.valueOf(25.75);
      TransactionType type = TransactionType.DEPOSIT;
      String emptyDescription = "";

      Transaction transaction = new Transaction(amount, type, emptyDescription);

      assertThat(transaction.getDescription()).isEmpty();
    }
  }

  @Nested
  @DisplayName("Transaction Properties")
  class TransactionProperties {

    @Test
    @DisplayName("Should handle very large amounts")
    void shouldHandleVeryLargeAmounts() {
      BigDecimal largeAmount = new BigDecimal("999999999999999999.99");

      Transaction transaction =
          new Transaction(largeAmount, TransactionType.DEPOSIT, "Large amount");

      assertThat(transaction.getAmount()).isEqualTo(largeAmount);
    }

    @Test
    @DisplayName("Should handle very small amounts")
    void shouldHandleVerySmallAmounts() {
      BigDecimal smallAmount = new BigDecimal("0.01");

      Transaction transaction =
          new Transaction(smallAmount, TransactionType.WITHDRAWAL, "Small amount");

      assertThat(transaction.getAmount()).isEqualTo(smallAmount);
    }

    @Test
    @DisplayName("Should handle special characters in description")
    void shouldHandleSpecialCharactersInDescription() {
      String specialDescription = "Café payment €100 - Transaction #123 @user";

      Transaction transaction =
          new Transaction(BigDecimal.valueOf(100), TransactionType.DEPOSIT, specialDescription);

      assertThat(transaction.getDescription()).isEqualTo(specialDescription);
    }
  }

  @Nested
  @DisplayName("Transaction Modification")
  class TransactionModification {

    @Test
    @DisplayName("Should allow setting ID")
    void shouldAllowSettingId() {
      Transaction transaction =
          new Transaction(BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Test");
      Long expectedId = 12345L;

      transaction.setId(expectedId);

      assertThat(transaction.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("Should update last modified date when set")
    void shouldUpdateLastModifiedDateWhenSet() {
      Transaction transaction =
          new Transaction(BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Test");
      LocalDateTime originalModifiedDate = transaction.getLastModifiedDate();
      LocalDateTime newModifiedDate = LocalDateTime.now().plusMinutes(5);

      transaction.setLastModifiedDate(newModifiedDate);

      assertAll(
          () -> assertThat(transaction.getLastModifiedDate()).isEqualTo(newModifiedDate),
          () -> assertThat(transaction.getLastModifiedDate()).isNotEqualTo(originalModifiedDate));
    }

    @Test
    @DisplayName("Should allow setting created date")
    void shouldAllowSettingCreatedDate() {
      Transaction transaction =
          new Transaction(BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Test");
      LocalDateTime customCreatedDate = LocalDateTime.of(2024, 1, 1, 10, 0, 0);

      transaction.setCreatedDate(customCreatedDate);

      assertThat(transaction.getCreatedDate()).isEqualTo(customCreatedDate);
    }
  }

  @Nested
  @DisplayName("Transaction Immutability Aspects")
  class TransactionImmutabilityAspects {

    @Test
    @DisplayName("Should maintain amount precision")
    void shouldMaintainAmountPrecision() {
      BigDecimal preciseAmount = new BigDecimal("123.456789");

      Transaction transaction =
          new Transaction(preciseAmount, TransactionType.DEPOSIT, "Precision test");

      assertThat(transaction.getAmount()).isEqualTo(preciseAmount);
      assertThat(transaction.getAmount().scale()).isEqualTo(preciseAmount.scale());
    }

    @Test
    @DisplayName("Should preserve transaction type")
    void shouldPreserveTransactionType() {
      TransactionType originalType = TransactionType.WITHDRAWAL;

      Transaction transaction = new Transaction(BigDecimal.valueOf(50), originalType, "Type test");

      assertThat(transaction.getType()).isEqualTo(originalType);
      assertThat(transaction.getType()).isNotEqualTo(TransactionType.DEPOSIT);
    }
  }
}
