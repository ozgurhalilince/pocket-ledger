package pocket.ledger.init;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pocket.ledger.dto.v1.BalanceResponseDto;
import pocket.ledger.dto.v1.TransactionRequestDto;
import pocket.ledger.service.LedgerService;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataSeedComponent Unit Tests")
class DataSeedComponentTest {

  @Mock private LedgerService ledgerService;

  @Mock private DataSeedProperties dataSeedProperties;

  @InjectMocks private DataSeedComponent dataSeedComponent;

  @Test
  @DisplayName("Should run data seeding when enabled")
  void shouldRunDataSeedingWhenEnabled() {
    when(dataSeedProperties.isEnabled()).thenReturn(true);
    when(dataSeedProperties.getCount()).thenReturn(3);
    when(ledgerService.getBalance()).thenReturn(new BalanceResponseDto(BigDecimal.ZERO, 0L, null));

    dataSeedComponent.run();

    verify(ledgerService, times(2)).createTransaction(any(TransactionRequestDto.class));
  }

  @Test
  @DisplayName("Should not run data seeding when disabled")
  void shouldNotRunDataSeedingWhenDisabled() {
    when(dataSeedProperties.isEnabled()).thenReturn(false);

    dataSeedComponent.run();

    verify(ledgerService, never()).createTransaction(any(TransactionRequestDto.class));
  }

  @Test
  @DisplayName("Should create correct number of transactions")
  void shouldCreateCorrectNumberOfTransactions() {
    int transactionCount = 5;
    when(dataSeedProperties.isEnabled()).thenReturn(true);
    when(dataSeedProperties.getCount()).thenReturn(transactionCount);
    when(ledgerService.getBalance()).thenReturn(new BalanceResponseDto(BigDecimal.ZERO, 0L, null));

    dataSeedComponent.run();

    verify(ledgerService, times(transactionCount - 1))
        .createTransaction(any(TransactionRequestDto.class));
  }

  @Test
  @DisplayName("Should create more deposits when balance is low")
  void shouldCreateMoreDepositsWhenBalanceIsLow() {
    when(dataSeedProperties.isEnabled()).thenReturn(true);
    when(dataSeedProperties.getCount()).thenReturn(10);
    when(ledgerService.getBalance())
        .thenReturn(new BalanceResponseDto(new BigDecimal("500"), 5L, null));

    dataSeedComponent.run();

    verify(ledgerService, times(9)).createTransaction(any(TransactionRequestDto.class));
  }

  @Test
  @DisplayName("Should handle high balance scenario")
  void shouldHandleHighBalanceScenario() {
    when(dataSeedProperties.isEnabled()).thenReturn(true);
    when(dataSeedProperties.getCount()).thenReturn(5);
    when(ledgerService.getBalance())
        .thenReturn(new BalanceResponseDto(new BigDecimal("2000"), 10L, null));

    dataSeedComponent.run();

    verify(ledgerService, times(4)).createTransaction(any(TransactionRequestDto.class));
  }

  @Test
  @DisplayName("Should handle transaction creation exception")
  void shouldHandleTransactionCreationException() {
    when(dataSeedProperties.isEnabled()).thenReturn(true);
    when(dataSeedProperties.getCount()).thenReturn(3);
    when(ledgerService.getBalance())
        .thenReturn(new BalanceResponseDto(new BigDecimal("2000"), 10L, null));
    when(ledgerService.createTransaction(any(TransactionRequestDto.class)))
        .thenThrow(new RuntimeException("Transaction failed"));

    dataSeedComponent.run();

    verify(ledgerService, times(2)).createTransaction(any(TransactionRequestDto.class));
  }

  @Test
  @DisplayName("Should handle exact boundary balance")
  void shouldHandleExactBoundaryBalance() {
    when(dataSeedProperties.isEnabled()).thenReturn(true);
    when(dataSeedProperties.getCount()).thenReturn(4);
    when(ledgerService.getBalance())
        .thenReturn(new BalanceResponseDto(new BigDecimal("1000"), 5L, null));

    dataSeedComponent.run();

    verify(ledgerService, times(3)).createTransaction(any(TransactionRequestDto.class));
  }

  @Test
  @DisplayName("Should handle zero count")
  void shouldHandleZeroCount() {
    when(dataSeedProperties.isEnabled()).thenReturn(true);
    when(dataSeedProperties.getCount()).thenReturn(0);

    dataSeedComponent.run();

    verify(ledgerService, never()).createTransaction(any(TransactionRequestDto.class));
  }

  @Test
  @DisplayName("Should handle count equals 1")
  void shouldHandleCountEqualsOne() {
    when(dataSeedProperties.isEnabled()).thenReturn(true);
    when(dataSeedProperties.getCount()).thenReturn(1);

    dataSeedComponent.run();

    verify(ledgerService, never()).createTransaction(any(TransactionRequestDto.class));
  }

  @Test
  @DisplayName("Should call getBalance for each transaction when balance is high")
  void shouldCallGetBalanceForEachTransactionWhenBalanceIsHigh() {
    when(dataSeedProperties.isEnabled()).thenReturn(true);
    when(dataSeedProperties.getCount()).thenReturn(3);
    when(ledgerService.getBalance())
        .thenReturn(new BalanceResponseDto(new BigDecimal("2000"), 10L, null));

    dataSeedComponent.run();

    verify(ledgerService, times(2)).getBalance();
    verify(ledgerService, times(2)).createTransaction(any(TransactionRequestDto.class));
  }
}
