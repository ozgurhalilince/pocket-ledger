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
      Long sourceAccountId = 1L;
      Long destinationAccountId = 2L;
      BigDecimal amount = BigDecimal.valueOf(100.50);
      TransactionType type = TransactionType.DEPOSIT;
      String description = "Test deposit";

      Transaction transaction =
          new Transaction(sourceAccountId, destinationAccountId, amount, type, description);

      assertAll(
          () -> assertThat(transaction.getSourceAccountId()).isEqualTo(sourceAccountId),
          () -> assertThat(transaction.getDestinationAccountId()).isEqualTo(destinationAccountId),
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
      Long sourceAccountId = 1L;
      Long destinationAccountId = 2L;

      Transaction transaction =
          new Transaction(sourceAccountId, destinationAccountId, amount, type, null);

      assertAll(
          () -> assertThat(transaction.getSourceAccountId()).isEqualTo(sourceAccountId),
          () -> assertThat(transaction.getDestinationAccountId()).isEqualTo(destinationAccountId),
          () -> assertThat(transaction.getAmount()).isEqualTo(amount),
          () -> assertThat(transaction.getType()).isEqualTo(type),
          () -> assertThat(transaction.getDescription()).isNull());
    }

    @Test
    @DisplayName("Should create transaction with empty description")
    void shouldCreateTransactionWithEmptyDescription() {
      Long sourceAccountId = 3L;
      Long destinationAccountId = 4L;
      BigDecimal amount = BigDecimal.valueOf(25.75);
      TransactionType type = TransactionType.DEPOSIT;
      String emptyDescription = "";

      Transaction transaction =
          new Transaction(sourceAccountId, destinationAccountId, amount, type, emptyDescription);

      assertAll(
          () -> assertThat(transaction.getSourceAccountId()).isEqualTo(sourceAccountId),
          () -> assertThat(transaction.getDestinationAccountId()).isEqualTo(destinationAccountId),
          () -> assertThat(transaction.getDescription()).isEmpty());
    }

    @Test
    @DisplayName("Should create transaction with null account IDs")
    void shouldCreateTransactionWithNullAccountIds() {
      BigDecimal amount = BigDecimal.valueOf(75.25);
      TransactionType type = TransactionType.DEPOSIT;
      String description = "Transaction with null accounts";

      Transaction transaction = new Transaction(null, null, amount, type, description);

      assertAll(
          () -> assertThat(transaction.getSourceAccountId()).isNull(),
          () -> assertThat(transaction.getDestinationAccountId()).isNull(),
          () -> assertThat(transaction.getAmount()).isEqualTo(amount),
          () -> assertThat(transaction.getType()).isEqualTo(type),
          () -> assertThat(transaction.getDescription()).isEqualTo(description));
    }

    @Test
    @DisplayName("Should create transaction with same source and destination account")
    void shouldCreateTransactionWithSameSourceAndDestinationAccount() {
      Long accountId = 1L;
      BigDecimal amount = BigDecimal.valueOf(100);
      TransactionType type = TransactionType.DEPOSIT;
      String description = "Internal transaction";

      Transaction transaction = new Transaction(accountId, accountId, amount, type, description);

      assertAll(
          () -> assertThat(transaction.getSourceAccountId()).isEqualTo(accountId),
          () -> assertThat(transaction.getDestinationAccountId()).isEqualTo(accountId),
          () ->
              assertThat(transaction.getSourceAccountId())
                  .isEqualTo(transaction.getDestinationAccountId()));
    }
  }

  @Nested
  @DisplayName("Transaction Properties")
  class TransactionProperties {

    @Test
    @DisplayName("Should handle very large amounts")
    void shouldHandleVeryLargeAmounts() {
      Long sourceAccountId = 1L;
      Long destinationAccountId = 2L;
      BigDecimal largeAmount = new BigDecimal("999999999999999999.99");

      Transaction transaction =
          new Transaction(
              sourceAccountId,
              destinationAccountId,
              largeAmount,
              TransactionType.DEPOSIT,
              "Large amount");

      assertThat(transaction.getAmount()).isEqualTo(largeAmount);
    }

    @Test
    @DisplayName("Should handle very small amounts")
    void shouldHandleVerySmallAmounts() {
      Long sourceAccountId = 1L;
      Long destinationAccountId = 2L;
      BigDecimal smallAmount = new BigDecimal("0.01");

      Transaction transaction =
          new Transaction(
              sourceAccountId,
              destinationAccountId,
              smallAmount,
              TransactionType.WITHDRAWAL,
              "Small amount");

      assertThat(transaction.getAmount()).isEqualTo(smallAmount);
    }

    @Test
    @DisplayName("Should handle special characters in description")
    void shouldHandleSpecialCharactersInDescription() {
      Long sourceAccountId = 1L;
      Long destinationAccountId = 2L;
      String specialDescription = "Café payment €100 - Transaction #123 @user";

      Transaction transaction =
          new Transaction(
              sourceAccountId,
              destinationAccountId,
              BigDecimal.valueOf(100),
              TransactionType.DEPOSIT,
              specialDescription);

      assertThat(transaction.getDescription()).isEqualTo(specialDescription);
    }

    @Test
    @DisplayName("Should handle large account IDs")
    void shouldHandleLargeAccountIds() {
      Long largeSourceAccountId = Long.MAX_VALUE;
      Long largeDestinationAccountId = Long.MAX_VALUE - 1;
      BigDecimal amount = BigDecimal.valueOf(100);

      Transaction transaction =
          new Transaction(
              largeSourceAccountId,
              largeDestinationAccountId,
              amount,
              TransactionType.DEPOSIT,
              "Large account IDs");

      assertAll(
          () -> assertThat(transaction.getSourceAccountId()).isEqualTo(largeSourceAccountId),
          () ->
              assertThat(transaction.getDestinationAccountId())
                  .isEqualTo(largeDestinationAccountId));
    }
  }

  @Nested
  @DisplayName("Transaction Modification")
  class TransactionModification {

    @Test
    @DisplayName("Should allow setting ID")
    void shouldAllowSettingId() {
      Transaction transaction =
          new Transaction(1L, 2L, BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Test");
      Long expectedId = 12345L;

      transaction.setId(expectedId);

      assertThat(transaction.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("Should update last modified date when set")
    void shouldUpdateLastModifiedDateWhenSet() {
      Transaction transaction =
          new Transaction(1L, 2L, BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Test");
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
          new Transaction(1L, 2L, BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Test");
      LocalDateTime customCreatedDate = LocalDateTime.of(2024, 1, 1, 10, 0, 0);

      transaction.setCreatedDate(customCreatedDate);

      assertThat(transaction.getCreatedDate()).isEqualTo(customCreatedDate);
    }

    @Test
    @DisplayName("Should allow updating source account ID")
    void shouldAllowUpdatingSourceAccountId() {
      Transaction transaction =
          new Transaction(1L, 2L, BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Test");
      Long newSourceAccountId = 999L;

      transaction.setSourceAccountId(newSourceAccountId);

      assertThat(transaction.getSourceAccountId()).isEqualTo(newSourceAccountId);
    }

    @Test
    @DisplayName("Should allow updating destination account ID")
    void shouldAllowUpdatingDestinationAccountId() {
      Transaction transaction =
          new Transaction(1L, 2L, BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Test");
      Long newDestinationAccountId = 888L;

      transaction.setDestinationAccountId(newDestinationAccountId);

      assertThat(transaction.getDestinationAccountId()).isEqualTo(newDestinationAccountId);
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
          new Transaction(1L, 2L, preciseAmount, TransactionType.DEPOSIT, "Precision test");

      assertThat(transaction.getAmount()).isEqualTo(preciseAmount);
      assertThat(transaction.getAmount().scale()).isEqualTo(preciseAmount.scale());
    }

    @Test
    @DisplayName("Should preserve transaction type")
    void shouldPreserveTransactionType() {
      TransactionType originalType = TransactionType.WITHDRAWAL;

      Transaction transaction =
          new Transaction(1L, 2L, BigDecimal.valueOf(50), originalType, "Type test");

      assertThat(transaction.getType()).isEqualTo(originalType);
      assertThat(transaction.getType()).isNotEqualTo(TransactionType.DEPOSIT);
    }

    @Test
    @DisplayName("Should preserve account IDs")
    void shouldPreserveAccountIds() {
      Long originalSourceAccountId = 100L;
      Long originalDestinationAccountId = 200L;

      Transaction transaction =
          new Transaction(
              originalSourceAccountId,
              originalDestinationAccountId,
              BigDecimal.valueOf(50),
              TransactionType.DEPOSIT,
              "Account preservation test");

      assertAll(
          () -> assertThat(transaction.getSourceAccountId()).isEqualTo(originalSourceAccountId),
          () ->
              assertThat(transaction.getDestinationAccountId())
                  .isEqualTo(originalDestinationAccountId),
          () ->
              assertThat(transaction.getSourceAccountId())
                  .isNotEqualTo(transaction.getDestinationAccountId()));
    }
  }

  @Nested
  @DisplayName("Account Relationships")
  class AccountRelationships {

    @Test
    @DisplayName("Should handle transfer between different accounts")
    void shouldHandleTransferBetweenDifferentAccounts() {
      Long fromAccount = 101L;
      Long toAccount = 102L;
      BigDecimal transferAmount = BigDecimal.valueOf(250.50);

      Transaction transaction =
          new Transaction(
              fromAccount,
              toAccount,
              transferAmount,
              TransactionType.WITHDRAWAL,
              "Transfer between accounts");

      assertAll(
          () -> assertThat(transaction.getSourceAccountId()).isEqualTo(fromAccount),
          () -> assertThat(transaction.getDestinationAccountId()).isEqualTo(toAccount),
          () ->
              assertThat(transaction.getSourceAccountId())
                  .isNotEqualTo(transaction.getDestinationAccountId()));
    }

    @Test
    @DisplayName("Should handle deposit to single account")
    void shouldHandleDepositToSingleAccount() {
      Long accountId = 101L;
      BigDecimal depositAmount = BigDecimal.valueOf(500);

      Transaction transaction =
          new Transaction(
              null, accountId, depositAmount, TransactionType.DEPOSIT, "External deposit");

      assertAll(
          () -> assertThat(transaction.getSourceAccountId()).isNull(),
          () -> assertThat(transaction.getDestinationAccountId()).isEqualTo(accountId));
    }

    @Test
    @DisplayName("Should handle withdrawal from single account")
    void shouldHandleWithdrawalFromSingleAccount() {
      Long accountId = 101L;
      BigDecimal withdrawalAmount = BigDecimal.valueOf(150);

      Transaction transaction =
          new Transaction(
              accountId, null, withdrawalAmount, TransactionType.WITHDRAWAL, "External withdrawal");

      assertAll(
          () -> assertThat(transaction.getSourceAccountId()).isEqualTo(accountId),
          () -> assertThat(transaction.getDestinationAccountId()).isNull());
    }
  }
}
