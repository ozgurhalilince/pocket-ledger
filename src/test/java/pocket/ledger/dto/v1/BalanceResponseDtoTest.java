package pocket.ledger.dto.v1;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class BalanceResponseDtoTest {

  @Test
  void constructor_shouldSetAllFields() {
    BigDecimal balance = BigDecimal.valueOf(1000.50);
    Long totalTransactions = 25L;
    LocalDateTime timestamp = LocalDateTime.of(2024, 1, 1, 10, 0, 0);

    BalanceResponseDto dto = new BalanceResponseDto(balance, totalTransactions, timestamp);

    assertThat(dto.balance()).isEqualTo(balance);
    assertThat(dto.totalTransactions()).isEqualTo(totalTransactions);
    assertThat(dto.asOfTimestamp()).isEqualTo(timestamp);
  }

  @Test
  void constructor_shouldHandleZeroBalance() {
    BigDecimal zeroBalance = BigDecimal.ZERO;
    Long noTransactions = 0L;
    LocalDateTime timestamp = LocalDateTime.now();

    BalanceResponseDto dto = new BalanceResponseDto(zeroBalance, noTransactions, timestamp);

    assertThat(dto.balance()).isEqualTo(BigDecimal.ZERO);
    assertThat(dto.totalTransactions()).isEqualTo(0L);
    assertThat(dto.asOfTimestamp()).isEqualTo(timestamp);
  }

  @Test
  void constructor_shouldHandleNegativeBalance() {
    BigDecimal negativeBalance = BigDecimal.valueOf(-500.25);
    Long totalTransactions = 10L;
    LocalDateTime timestamp = LocalDateTime.now();

    BalanceResponseDto dto = new BalanceResponseDto(negativeBalance, totalTransactions, timestamp);

    assertThat(dto.balance()).isEqualTo(negativeBalance);
    assertThat(dto.totalTransactions()).isEqualTo(totalTransactions);
  }

  @Test
  void constructor_shouldHandleLargeBalance() {
    BigDecimal largeBalance = new BigDecimal("9999999999.99");
    Long manyTransactions = 1000000L;
    LocalDateTime timestamp = LocalDateTime.now();

    BalanceResponseDto dto = new BalanceResponseDto(largeBalance, manyTransactions, timestamp);

    assertThat(dto.balance()).isEqualTo(largeBalance);
    assertThat(dto.totalTransactions()).isEqualTo(manyTransactions);
  }

  @Test
  void constructor_shouldPreservePrecision() {
    BigDecimal preciseBalance = new BigDecimal("123.456789");
    Long totalTransactions = 5L;
    LocalDateTime timestamp = LocalDateTime.now();

    BalanceResponseDto dto = new BalanceResponseDto(preciseBalance, totalTransactions, timestamp);

    assertThat(dto.balance()).isEqualTo(preciseBalance);
    assertThat(dto.balance().scale()).isEqualTo(preciseBalance.scale());
  }

  @Test
  void constructor_shouldHandleNullValues() {
    BalanceResponseDto dto = new BalanceResponseDto(null, null, null);

    assertThat(dto.balance()).isNull();
    assertThat(dto.totalTransactions()).isNull();
    assertThat(dto.asOfTimestamp()).isNull();
  }
}
