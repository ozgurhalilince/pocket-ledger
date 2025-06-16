package pocket.ledger.dto.v1;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.*;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pocket.ledger.enums.TransactionType;

class TransactionRequestDtoTest {

  private Validator validator;

  @BeforeEach
  void setup() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void validDto_shouldPassValidation() {
    TransactionRequestDto dto =
        new TransactionRequestDto(
            new BigDecimal("100.00"), TransactionType.DEPOSIT, "Salary deposit");

    Set<ConstraintViolation<TransactionRequestDto>> violations = validator.validate(dto);
    assertTrue(violations.isEmpty(), "Valid DTO should not produce violations");
  }

  @Test
  void nullAmount_shouldFailValidation() {
    TransactionRequestDto dto =
        new TransactionRequestDto(null, TransactionType.WITHDRAWAL, "ATM withdrawal");

    Set<ConstraintViolation<TransactionRequestDto>> violations = validator.validate(dto);
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("amount")));
  }

  @Test
  void zeroAmount_shouldFailMinValidation() {
    TransactionRequestDto dto =
        new TransactionRequestDto(
            new BigDecimal("0.00"), TransactionType.DEPOSIT, "Invalid amount");

    Set<ConstraintViolation<TransactionRequestDto>> violations = validator.validate(dto);
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("greater than 0")));
  }

  @Test
  void tooLargeAmount_shouldFailMaxValidation() {
    TransactionRequestDto dto =
        new TransactionRequestDto(
            new BigDecimal("10000000000.00"), TransactionType.DEPOSIT, "Too much");

    Set<ConstraintViolation<TransactionRequestDto>> violations = validator.validate(dto);
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("must not exceed")));
  }

  @Test
  void nullType_shouldFailValidation() {
    TransactionRequestDto dto =
        new TransactionRequestDto(new BigDecimal("50.00"), null, "Missing type");

    Set<ConstraintViolation<TransactionRequestDto>> violations = validator.validate(dto);
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("type")));
  }

  @Test
  void longDescription_shouldFailSizeValidation() {
    String longDesc = "a".repeat(256);
    TransactionRequestDto dto =
        new TransactionRequestDto(new BigDecimal("100.00"), TransactionType.WITHDRAWAL, longDesc);

    Set<ConstraintViolation<TransactionRequestDto>> violations = validator.validate(dto);
    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
  }
}
