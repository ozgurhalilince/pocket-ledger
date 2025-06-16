package pocket.ledger.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import pocket.ledger.enums.TransactionType;
import pocket.ledger.model.Transaction;

@Schema(description = "Request to create a new transaction")
public record TransactionRequestDto(
    @Schema(description = "Transaction amount", example = "100.50", required = true)
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        @DecimalMax(
            value = "9999999999.99",
            message = "Amount must not exceed 10 digits with 2 decimal places")
        BigDecimal amount,
    @Schema(
            description = "Type of transaction",
            example = "DEPOSIT",
            required = true,
            allowableValues = {"DEPOSIT", "WITHDRAWAL"})
        @NotNull(message = "Transaction type is required")
        TransactionType type,
    @Schema(
            description = "Optional description for the transaction",
            example = "Monthly salary",
            maxLength = 255)
        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description) {

  public Transaction toEntity() {
    return new Transaction(amount, type, description);
  }
}
