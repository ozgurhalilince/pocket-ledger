package pocket.ledger.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transaction type", example = "DEPOSIT")
public enum TransactionType {
  @Schema(description = "Money deposit (positive amount)")
  DEPOSIT(1),

  @Schema(description = "Money withdrawal (negative amount)")
  WITHDRAWAL(-1);

  private final int multiplier;

  TransactionType(int multiplier) {
    this.multiplier = multiplier;
  }

  public int getMultiplier() {
    return multiplier;
  }

  @JsonValue
  public String getValue() {
    return this.name();
  }

  @JsonCreator
  public static TransactionType fromString(String value) {
    if (value == null) {
      throw new IllegalArgumentException("Transaction type cannot be null");
    }

    String normalizedValue = value.trim().toUpperCase();

    try {
      return TransactionType.valueOf(normalizedValue);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          String.format(
              "Invalid transaction type: '%s'. Valid values are: DEPOSIT, WITHDRAWAL", value));
    }
  }
}
