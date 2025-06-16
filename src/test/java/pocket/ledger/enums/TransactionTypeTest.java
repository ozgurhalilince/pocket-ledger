package pocket.ledger.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class TransactionTypeTest {

  @Test
  void fromString_withValidDeposit_shouldReturnDeposit() {
    TransactionType result = TransactionType.fromString("DEPOSIT");

    assertThat(result).isEqualTo(TransactionType.DEPOSIT);
  }

  @Test
  void fromString_withValidWithdrawal_shouldReturnWithdrawal() {
    TransactionType result = TransactionType.fromString("WITHDRAWAL");

    assertThat(result).isEqualTo(TransactionType.WITHDRAWAL);
  }

  @Test
  void fromString_withLowerCase_shouldReturnCorrectType() {
    TransactionType result = TransactionType.fromString("deposit");

    assertThat(result).isEqualTo(TransactionType.DEPOSIT);
  }

  @Test
  void fromString_withMixedCase_shouldReturnCorrectType() {
    TransactionType result = TransactionType.fromString("WiThDrAwAl");

    assertThat(result).isEqualTo(TransactionType.WITHDRAWAL);
  }

  @Test
  void fromString_withWhitespace_shouldReturnCorrectType() {
    TransactionType result = TransactionType.fromString("  DEPOSIT  ");

    assertThat(result).isEqualTo(TransactionType.DEPOSIT);
  }

  @Test
  void fromString_withInvalidValue_shouldThrowException() {
    assertThatThrownBy(() -> TransactionType.fromString("INVALID"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid transaction type: 'INVALID'. Valid values are: DEPOSIT, WITHDRAWAL");
  }

  @Test
  void fromString_withTypo_shouldThrowException() {
    assertThatThrownBy(() -> TransactionType.fromString("WITDRAWAL"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid transaction type: 'WITDRAWAL'. Valid values are: DEPOSIT, WITHDRAWAL");
  }

  @Test
  void fromString_withNull_shouldThrowException() {
    assertThatThrownBy(() -> TransactionType.fromString(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Transaction type cannot be null");
  }

  @Test
  void fromString_withEmptyString_shouldThrowException() {
    assertThatThrownBy(() -> TransactionType.fromString(""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Invalid transaction type: ''. Valid values are: DEPOSIT, WITHDRAWAL");
  }

  @Test
  void getValue_shouldReturnEnumName() {
    assertThat(TransactionType.DEPOSIT.getValue()).isEqualTo("DEPOSIT");
    assertThat(TransactionType.WITHDRAWAL.getValue()).isEqualTo("WITHDRAWAL");
  }

  @Test
  void getMultiplier_shouldReturnCorrectValues() {
    assertThat(TransactionType.DEPOSIT.getMultiplier()).isEqualTo(1);
    assertThat(TransactionType.WITHDRAWAL.getMultiplier()).isEqualTo(-1);
  }
}
