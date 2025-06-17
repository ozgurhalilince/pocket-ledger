package pocket.ledger.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@DisplayName("ErrorCode Tests")
class ErrorCodeTest {

  @ParameterizedTest
  @DisplayName("All error codes should have non-null values")
  @EnumSource(ErrorCode.class)
  void allErrorCodesShouldHaveNonNullValues(ErrorCode errorCode) {
    assertThat(errorCode.getCode()).isNotNull().isNotEmpty();
    assertThat(errorCode.getDefaultMessage()).isNotNull().isNotEmpty();
    assertThat(errorCode.getHttpStatus()).isNotNull();
  }

  @Test
  @DisplayName("Should have expected number of error codes")
  void shouldHaveExpectedNumberOfErrorCodes() {
    ErrorCode[] errorCodes = ErrorCode.values();

    assertThat(errorCodes).hasSize(10);
  }

  @Test
  @DisplayName("All error codes should have unique codes")
  void allErrorCodesShouldHaveUniqueCodes() {
    ErrorCode[] errorCodes = ErrorCode.values();

    assertThat(errorCodes).extracting(ErrorCode::getCode).doesNotHaveDuplicates();
  }

  @Test
  @DisplayName("All error codes should have unique messages")
  void allErrorCodesShouldHaveUniqueMessages() {
    ErrorCode[] errorCodes = ErrorCode.values();

    assertThat(errorCodes).extracting(ErrorCode::getDefaultMessage).doesNotHaveDuplicates();
  }
}
