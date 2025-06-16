package pocket.ledger.dto.v1;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import pocket.ledger.enums.TransactionType;
import pocket.ledger.util.PageRequest;

class TransactionQueryDtoTest {

  @Test
  void constructor_shouldSetAllFields() {
    LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59, 59);
    TransactionType type = TransactionType.DEPOSIT;
    PageRequest pageRequest = PageRequest.of(0, 10);

    TransactionQueryDto dto = new TransactionQueryDto(startDate, endDate, type, pageRequest);

    assertThat(dto.startDate()).isEqualTo(startDate);
    assertThat(dto.endDate()).isEqualTo(endDate);
    assertThat(dto.type()).isEqualTo(type);
    assertThat(dto.pageRequest()).isEqualTo(pageRequest);
  }

  @Test
  void hasDateRange_shouldReturnTrueWhenBothDatesPresent() {
    LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59, 59);
    TransactionQueryDto dto = new TransactionQueryDto(startDate, endDate, null, null);

    assertThat(dto.hasDateRange()).isTrue();
  }

  @Test
  void hasDateRange_shouldReturnFalseWhenStartDateNull() {
    LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59, 59);
    TransactionQueryDto dto = new TransactionQueryDto(null, endDate, null, null);

    assertThat(dto.hasDateRange()).isFalse();
  }

  @Test
  void hasDateRange_shouldReturnFalseWhenEndDateNull() {
    LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    TransactionQueryDto dto = new TransactionQueryDto(startDate, null, null, null);

    assertThat(dto.hasDateRange()).isFalse();
  }

  @Test
  void hasDateRange_shouldReturnFalseWhenBothDatesNull() {
    TransactionQueryDto dto = new TransactionQueryDto(null, null, null, null);

    assertThat(dto.hasDateRange()).isFalse();
  }

  @Test
  void hasType_shouldReturnTrueWhenTypePresent() {
    TransactionQueryDto dto = new TransactionQueryDto(null, null, TransactionType.WITHDRAWAL, null);

    assertThat(dto.hasType()).isTrue();
  }

  @Test
  void hasType_shouldReturnFalseWhenTypeNull() {
    TransactionQueryDto dto = new TransactionQueryDto(null, null, null, null);

    assertThat(dto.hasType()).isFalse();
  }

  @Test
  void hasDateRangeAndType_shouldReturnTrueWhenBothPresent() {
    LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59, 59);
    TransactionType type = TransactionType.DEPOSIT;
    TransactionQueryDto dto = new TransactionQueryDto(startDate, endDate, type, null);

    assertThat(dto.hasDateRangeAndType()).isTrue();
  }

  @Test
  void hasDateRangeAndType_shouldReturnFalseWhenOnlyDateRangePresent() {
    LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59, 59);
    TransactionQueryDto dto = new TransactionQueryDto(startDate, endDate, null, null);

    assertThat(dto.hasDateRangeAndType()).isFalse();
  }

  @Test
  void hasDateRangeAndType_shouldReturnFalseWhenOnlyTypePresent() {
    TransactionQueryDto dto = new TransactionQueryDto(null, null, TransactionType.WITHDRAWAL, null);

    assertThat(dto.hasDateRangeAndType()).isFalse();
  }

  @Test
  void hasDateRangeAndType_shouldReturnFalseWhenNeitherPresent() {
    TransactionQueryDto dto = new TransactionQueryDto(null, null, null, null);

    assertThat(dto.hasDateRangeAndType()).isFalse();
  }

  @Test
  void constructor_shouldHandleAllNullValues() {
    TransactionQueryDto dto = new TransactionQueryDto(null, null, null, null);

    assertThat(dto.startDate()).isNull();
    assertThat(dto.endDate()).isNull();
    assertThat(dto.type()).isNull();
    assertThat(dto.pageRequest()).isNull();
    assertThat(dto.hasDateRange()).isFalse();
    assertThat(dto.hasType()).isFalse();
    assertThat(dto.hasDateRangeAndType()).isFalse();
  }

  @Test
  void constructor_shouldHandleCompleteQuery() {
    LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59, 59);
    TransactionType type = TransactionType.DEPOSIT;
    PageRequest pageRequest = PageRequest.of(2, 20);

    TransactionQueryDto dto = new TransactionQueryDto(startDate, endDate, type, pageRequest);

    assertThat(dto.hasDateRange()).isTrue();
    assertThat(dto.hasType()).isTrue();
    assertThat(dto.hasDateRangeAndType()).isTrue();
    assertThat(dto.pageRequest().getPageNumber()).isEqualTo(2);
    assertThat(dto.pageRequest().getPageSize()).isEqualTo(20);
  }
}
