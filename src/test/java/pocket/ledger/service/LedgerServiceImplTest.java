package pocket.ledger.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pocket.ledger.dto.v1.BalanceResponseDto;
import pocket.ledger.dto.v1.TransactionQueryDto;
import pocket.ledger.dto.v1.TransactionRequestDto;
import pocket.ledger.dto.v1.TransactionResponseDto;
import pocket.ledger.enums.TransactionType;
import pocket.ledger.exception.InsufficientBalanceException;
import pocket.ledger.exception.TransactionNotFoundException;
import pocket.ledger.model.Transaction;
import pocket.ledger.repository.TransactionRepository;
import pocket.ledger.service.query.TransactionQueryHandler;
import pocket.ledger.util.Page;
import pocket.ledger.util.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("LedgerServiceImpl Unit Tests")
class LedgerServiceImplTest {

  @Mock private TransactionRepository transactionRepository;

  @Mock private TransactionQueryHandler queryHandler;

  @InjectMocks private LedgerServiceImpl ledgerService;

  @Nested
  @DisplayName("Create Transaction Tests")
  class CreateTransactionTests {

    @Test
    @DisplayName("Should create deposit transaction successfully")
    void shouldCreateDepositTransactionSuccessfully() {
      TransactionRequestDto request =
          new TransactionRequestDto(
              BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Salary deposit");

      Transaction savedTransaction =
          new Transaction(BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Salary deposit");
      savedTransaction.setId(1L);
      savedTransaction.setCreatedDate(LocalDateTime.now());
      savedTransaction.setLastModifiedDate(LocalDateTime.now());

      when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

      TransactionResponseDto result = ledgerService.createTransaction(request);

      assertAll(
          () -> assertThat(result.id()).isEqualTo(1L),
          () -> assertThat(result.amount()).isEqualTo(BigDecimal.valueOf(100)),
          () -> assertThat(result.type()).isEqualTo(TransactionType.DEPOSIT),
          () -> assertThat(result.description()).isEqualTo("Salary deposit"),
          () -> assertThat(result.createdDate()).isNotNull(),
          () -> verify(transactionRepository).save(any(Transaction.class)));
    }

    @Test
    @DisplayName("Should create withdrawal transaction when balance is sufficient")
    void shouldCreateWithdrawalTransactionWhenBalanceIsSufficient() {
      TransactionRequestDto request =
          new TransactionRequestDto(
              BigDecimal.valueOf(50), TransactionType.WITHDRAWAL, "ATM withdrawal");

      when(transactionRepository.calculateBalance()).thenReturn(BigDecimal.valueOf(100));

      Transaction savedTransaction =
          new Transaction(BigDecimal.valueOf(50), TransactionType.WITHDRAWAL, "ATM withdrawal");
      savedTransaction.setId(2L);
      savedTransaction.setCreatedDate(LocalDateTime.now());
      savedTransaction.setLastModifiedDate(LocalDateTime.now());

      when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

      TransactionResponseDto result = ledgerService.createTransaction(request);

      assertAll(
          () -> assertThat(result.id()).isEqualTo(2L),
          () -> assertThat(result.amount()).isEqualTo(BigDecimal.valueOf(50)),
          () -> assertThat(result.type()).isEqualTo(TransactionType.WITHDRAWAL),
          () -> verify(transactionRepository).calculateBalance(),
          () -> verify(transactionRepository).save(any(Transaction.class)));
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when withdrawal amount exceeds balance")
    void shouldThrowInsufficientBalanceExceptionWhenWithdrawalAmountExceedsBalance() {
      TransactionRequestDto request =
          new TransactionRequestDto(
              BigDecimal.valueOf(150), TransactionType.WITHDRAWAL, "Large withdrawal");

      BigDecimal currentBalance = BigDecimal.valueOf(100);
      when(transactionRepository.calculateBalance()).thenReturn(currentBalance);

      assertThatThrownBy(() -> ledgerService.createTransaction(request))
          .isInstanceOf(InsufficientBalanceException.class)
          .satisfies(
              exception -> {
                InsufficientBalanceException ex = (InsufficientBalanceException) exception;
                assertThat(ex.getCurrentBalance()).isEqualTo(currentBalance);
                assertThat(ex.getRequestedAmount()).isEqualTo(BigDecimal.valueOf(150));
              });

      verify(transactionRepository).calculateBalance();
    }

    @Test
    @DisplayName(
        "Should throw InsufficientBalanceException when withdrawal amount equals zero balance")
    void shouldThrowInsufficientBalanceExceptionWhenWithdrawalAmountEqualsZeroBalance() {
      TransactionRequestDto request =
          new TransactionRequestDto(
              BigDecimal.valueOf(50), TransactionType.WITHDRAWAL, "Withdrawal from empty account");

      when(transactionRepository.calculateBalance()).thenReturn(BigDecimal.ZERO);

      assertThatThrownBy(() -> ledgerService.createTransaction(request))
          .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    @DisplayName("Should allow withdrawal when amount exactly equals balance")
    void shouldAllowWithdrawalWhenAmountExactlyEqualsBalance() {
      BigDecimal exactBalance = BigDecimal.valueOf(100);
      TransactionRequestDto request =
          new TransactionRequestDto(
              exactBalance, TransactionType.WITHDRAWAL, "Complete withdrawal");

      when(transactionRepository.calculateBalance()).thenReturn(exactBalance);

      Transaction savedTransaction =
          new Transaction(exactBalance, TransactionType.WITHDRAWAL, "Complete withdrawal");
      savedTransaction.setId(3L);
      when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

      TransactionResponseDto result = ledgerService.createTransaction(request);

      assertThat(result.amount()).isEqualTo(exactBalance);
      verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should handle decimal withdrawal amounts correctly")
    void shouldHandleDecimalWithdrawalAmountsCorrectly() {
      BigDecimal currentBalance = new BigDecimal("100.50");
      BigDecimal withdrawalAmount = new BigDecimal("50.25");

      TransactionRequestDto request =
          new TransactionRequestDto(
              withdrawalAmount, TransactionType.WITHDRAWAL, "Partial withdrawal");

      when(transactionRepository.calculateBalance()).thenReturn(currentBalance);

      Transaction savedTransaction =
          new Transaction(withdrawalAmount, TransactionType.WITHDRAWAL, "Partial withdrawal");
      savedTransaction.setId(4L);
      when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

      TransactionResponseDto result = ledgerService.createTransaction(request);

      assertThat(result.amount()).isEqualTo(withdrawalAmount);
    }
  }

  @Nested
  @DisplayName("Get Transaction By ID Tests")
  class GetTransactionByIdTests {

    @Test
    @DisplayName("Should return transaction when ID exists")
    void shouldReturnTransactionWhenIdExists() {
      Long transactionId = 1L;
      Transaction transaction =
          new Transaction(BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Test transaction");
      transaction.setId(transactionId);
      transaction.setCreatedDate(LocalDateTime.now());
      transaction.setLastModifiedDate(LocalDateTime.now());

      when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

      TransactionResponseDto result = ledgerService.getTransactionById(transactionId);

      assertAll(
          () -> assertThat(result.id()).isEqualTo(transactionId),
          () -> assertThat(result.amount()).isEqualTo(BigDecimal.valueOf(100)),
          () -> assertThat(result.type()).isEqualTo(TransactionType.DEPOSIT),
          () -> assertThat(result.description()).isEqualTo("Test transaction"),
          () -> verify(transactionRepository).findById(transactionId));
    }

    @Test
    @DisplayName("Should throw TransactionNotFoundException when ID does not exist")
    void shouldThrowTransactionNotFoundExceptionWhenIdDoesNotExist() {
      Long nonExistentId = 999L;
      when(transactionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> ledgerService.getTransactionById(nonExistentId))
          .isInstanceOf(TransactionNotFoundException.class);

      verify(transactionRepository).findById(nonExistentId);
    }
  }

  @Nested
  @DisplayName("Get Balance Tests")
  class GetBalanceTests {

    @Test
    @DisplayName("Should return current balance with transaction count")
    void shouldReturnCurrentBalanceWithTransactionCount() {
      BigDecimal expectedBalance = BigDecimal.valueOf(250.75);
      Long expectedCount = 5L;

      when(transactionRepository.calculateBalance()).thenReturn(expectedBalance);
      when(transactionRepository.countTransactions()).thenReturn(expectedCount);

      BalanceResponseDto result = ledgerService.getBalance();

      assertAll(
          () -> assertThat(result.balance()).isEqualTo(expectedBalance),
          () -> assertThat(result.totalTransactions()).isEqualTo(expectedCount),
          () -> assertThat(result.asOfTimestamp()).isNotNull(),
          () -> assertThat(result.asOfTimestamp()).isBeforeOrEqualTo(LocalDateTime.now()),
          () -> verify(transactionRepository).calculateBalance(),
          () -> verify(transactionRepository).countTransactions());
    }

    @Test
    @DisplayName("Should return zero balance and zero count for empty repository")
    void shouldReturnZeroBalanceAndZeroCountForEmptyRepository() {
      when(transactionRepository.calculateBalance()).thenReturn(BigDecimal.ZERO);
      when(transactionRepository.countTransactions()).thenReturn(0L);

      BalanceResponseDto result = ledgerService.getBalance();

      assertAll(
          () -> assertThat(result.balance()).isEqualTo(BigDecimal.ZERO),
          () -> assertThat(result.totalTransactions()).isEqualTo(0L));
    }

    @Test
    @DisplayName("Should handle negative balance correctly")
    void shouldHandleNegativeBalanceCorrectly() {
      BigDecimal negativeBalance = BigDecimal.valueOf(-50.25);
      when(transactionRepository.calculateBalance()).thenReturn(negativeBalance);
      when(transactionRepository.countTransactions()).thenReturn(3L);

      BalanceResponseDto result = ledgerService.getBalance();

      assertThat(result.balance()).isEqualTo(negativeBalance);
    }
  }

  @Nested
  @DisplayName("Get Transactions Tests")
  class GetTransactionsTests {

    @Test
    @DisplayName("Should delegate to query handler and map results correctly")
    void shouldDelegateToQueryHandlerAndMapResultsCorrectly() {
      LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
      LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59);
      PageRequest pageRequest = PageRequest.of(0, 10);

      TransactionQueryDto query =
          new TransactionQueryDto(startDate, endDate, TransactionType.DEPOSIT, pageRequest);

      Transaction transaction1 =
          new Transaction(BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Transaction 1");
      transaction1.setId(1L);
      transaction1.setCreatedDate(LocalDateTime.now());

      Transaction transaction2 =
          new Transaction(BigDecimal.valueOf(200), TransactionType.DEPOSIT, "Transaction 2");
      transaction2.setId(2L);
      transaction2.setCreatedDate(LocalDateTime.now());

      List<Transaction> transactions = List.of(transaction1, transaction2);
      Page<Transaction> transactionPage = new Page<>(transactions, pageRequest, 2);

      when(queryHandler.executeQuery(query, transactionRepository)).thenReturn(transactionPage);

      pocket.ledger.util.Page<TransactionResponseDto> result = ledgerService.getTransactions(query);

      assertAll(
          () -> assertThat(result.getContent()).hasSize(2),
          () -> assertThat(result.getContent().get(0).id()).isEqualTo(1L),
          () -> assertThat(result.getContent().get(0).amount()).isEqualTo(BigDecimal.valueOf(100)),
          () -> assertThat(result.getContent().get(1).id()).isEqualTo(2L),
          () -> assertThat(result.getContent().get(1).amount()).isEqualTo(BigDecimal.valueOf(200)),
          () -> assertThat(result.getTotalElements()).isEqualTo(2),
          () -> verify(queryHandler).executeQuery(query, transactionRepository));
    }

    @Test
    @DisplayName("Should handle empty query results")
    void shouldHandleEmptyQueryResults() {
      PageRequest pageRequest = PageRequest.of(0, 10);
      TransactionQueryDto query = new TransactionQueryDto(null, null, null, pageRequest);

      Page<Transaction> emptyPage = new Page<>(List.of(), pageRequest, 0);
      when(queryHandler.executeQuery(query, transactionRepository)).thenReturn(emptyPage);

      pocket.ledger.util.Page<TransactionResponseDto> result = ledgerService.getTransactions(query);

      assertAll(
          () -> assertThat(result.getContent()).isEmpty(),
          () -> assertThat(result.getTotalElements()).isEqualTo(0),
          () -> assertThat(result.getTotalPages()).isEqualTo(0));
    }

    @Test
    @DisplayName("Should preserve page metadata from query handler")
    void shouldPreservePageMetadataFromQueryHandler() {
      PageRequest pageRequest = PageRequest.of(1, 5);
      TransactionQueryDto query = new TransactionQueryDto(null, null, null, pageRequest);

      Transaction transaction =
          new Transaction(BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Transaction");
      transaction.setId(1L);
      transaction.setCreatedDate(LocalDateTime.now());

      Page<Transaction> pagedResult = new Page<>(List.of(transaction), pageRequest, 12);
      when(queryHandler.executeQuery(query, transactionRepository)).thenReturn(pagedResult);

      pocket.ledger.util.Page<TransactionResponseDto> result = ledgerService.getTransactions(query);

      assertAll(
          () -> assertThat(result.getContent()).hasSize(1),
          () -> assertThat(result.getTotalElements()).isEqualTo(12),
          () -> assertThat(result.getTotalPages()).isEqualTo(3),
          () -> assertThat(result.getPageable().getPageNumber()).isEqualTo(1),
          () -> assertThat(result.getPageable().getPageSize()).isEqualTo(5),
          () -> assertThat(result.isFirst()).isFalse(),
          () -> assertThat(result.isLast()).isFalse());
    }
  }

  @Nested
  @DisplayName("Integration and Edge Cases")
  class IntegrationAndEdgeCases {

    @Test
    @DisplayName("Should handle very large transaction amounts")
    void shouldHandleVeryLargeTransactionAmounts() {
      BigDecimal largeAmount = new BigDecimal("999999999999999999.99");
      TransactionRequestDto request =
          new TransactionRequestDto(largeAmount, TransactionType.DEPOSIT, "Large deposit");

      Transaction savedTransaction =
          new Transaction(largeAmount, TransactionType.DEPOSIT, "Large deposit");
      savedTransaction.setId(1L);
      when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

      TransactionResponseDto result = ledgerService.createTransaction(request);

      assertThat(result.amount()).isEqualTo(largeAmount);
    }

    @Test
    @DisplayName("Should handle minimum transaction amounts")
    void shouldHandleMinimumTransactionAmounts() {
      BigDecimal minAmount = new BigDecimal("0.01");
      TransactionRequestDto request =
          new TransactionRequestDto(minAmount, TransactionType.DEPOSIT, "Minimum deposit");

      Transaction savedTransaction =
          new Transaction(minAmount, TransactionType.DEPOSIT, "Minimum deposit");
      savedTransaction.setId(1L);
      when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

      TransactionResponseDto result = ledgerService.createTransaction(request);

      assertThat(result.amount()).isEqualTo(minAmount);
    }

    @Test
    @DisplayName("Should handle transactions with special characters in description")
    void shouldHandleTransactionsWithSpecialCharactersInDescription() {
      String specialDescription = "Café payment €100 - Transaction #123 @user";
      TransactionRequestDto request =
          new TransactionRequestDto(
              BigDecimal.valueOf(100), TransactionType.WITHDRAWAL, specialDescription);

      when(transactionRepository.calculateBalance()).thenReturn(BigDecimal.valueOf(200));

      Transaction savedTransaction =
          new Transaction(BigDecimal.valueOf(100), TransactionType.WITHDRAWAL, specialDescription);
      savedTransaction.setId(1L);
      when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

      TransactionResponseDto result = ledgerService.createTransaction(request);

      assertThat(result.description()).isEqualTo(specialDescription);
    }

    @Test
    @DisplayName("Should handle null description gracefully")
    void shouldHandleNullDescriptionGracefully() {
      TransactionRequestDto request =
          new TransactionRequestDto(BigDecimal.valueOf(100), TransactionType.DEPOSIT, null);

      Transaction savedTransaction =
          new Transaction(BigDecimal.valueOf(100), TransactionType.DEPOSIT, null);
      savedTransaction.setId(1L);
      when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

      TransactionResponseDto result = ledgerService.createTransaction(request);

      assertThat(result.description()).isNull();
    }

    @Test
    @DisplayName("Should preserve exact decimal precision in calculations")
    void shouldPreserveExactDecimalPrecisionInCalculations() {
      BigDecimal currentBalance = new BigDecimal("100.33");
      BigDecimal withdrawalAmount = new BigDecimal("50.17");

      TransactionRequestDto request =
          new TransactionRequestDto(withdrawalAmount, TransactionType.WITHDRAWAL, "Precision test");

      when(transactionRepository.calculateBalance()).thenReturn(currentBalance);

      Transaction savedTransaction =
          new Transaction(withdrawalAmount, TransactionType.WITHDRAWAL, "Precision test");
      savedTransaction.setId(1L);
      when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

      TransactionResponseDto result = ledgerService.createTransaction(request);

      assertThat(result.amount()).isEqualTo(withdrawalAmount);
    }
  }
}
