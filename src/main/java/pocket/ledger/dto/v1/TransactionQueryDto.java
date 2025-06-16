package pocket.ledger.dto.v1;

import java.time.LocalDateTime;
import pocket.ledger.enums.TransactionType;
import pocket.ledger.util.PageRequest;

public record TransactionQueryDto(
    LocalDateTime startDate, LocalDateTime endDate, TransactionType type, PageRequest pageRequest) {

  public boolean hasDateRange() {
    return startDate != null && endDate != null;
  }

  public boolean hasType() {
    return type != null;
  }

  public boolean hasDateRangeAndType() {
    return hasDateRange() && hasType();
  }
}
