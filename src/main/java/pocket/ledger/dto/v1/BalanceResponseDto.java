package pocket.ledger.dto.v1;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BalanceResponseDto(
    BigDecimal balance, Long totalTransactions, LocalDateTime asOfTimestamp) {}
