package pocket.ledger.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import pocket.ledger.dto.v1.TransactionQueryDto;
import pocket.ledger.enums.TransactionType;
import pocket.ledger.model.Transaction;
import pocket.ledger.repository.TransactionRepository;
import pocket.ledger.util.Page;
import pocket.ledger.util.PageRequest;

class TypeStrategyTest {

  private final TypeStrategy strategy = new TypeStrategy();
  private final TransactionRepository repository = mock(TransactionRepository.class);

  @Test
  void canHandle_shouldReturnTrueWhenHasTypeOnly() {
    TransactionQueryDto query =
        new TransactionQueryDto(null, null, TransactionType.DEPOSIT, PageRequest.of(0, 10));

    boolean result = strategy.canHandle(query);

    assertThat(result).isTrue();
  }

  @Test
  void canHandle_shouldReturnFalseWhenNoType() {
    TransactionQueryDto query = new TransactionQueryDto(null, null, null, PageRequest.of(0, 10));

    boolean result = strategy.canHandle(query);

    assertThat(result).isFalse();
  }

  @Test
  void canHandle_shouldReturnFalseWhenHasDateRange() {
    LocalDateTime startDate = LocalDateTime.now();
    LocalDateTime endDate = LocalDateTime.now().plusDays(1);
    TransactionQueryDto query =
        new TransactionQueryDto(startDate, endDate, TransactionType.DEPOSIT, PageRequest.of(0, 10));

    boolean result = strategy.canHandle(query);

    assertThat(result).isFalse();
  }

  @Test
  void canHandle_shouldReturnFalseWhenHasStartDateOnly() {
    LocalDateTime startDate = LocalDateTime.now();
    TransactionQueryDto query =
        new TransactionQueryDto(startDate, null, TransactionType.DEPOSIT, PageRequest.of(0, 10));

    boolean result = strategy.canHandle(query);

    assertThat(result).isTrue();
  }

  @Test
  void executeQuery_shouldCallFindByType() {
    PageRequest pageRequest = PageRequest.of(0, 10);
    TransactionQueryDto query =
        new TransactionQueryDto(null, null, TransactionType.WITHDRAWAL, pageRequest);
    Page<Transaction> expectedPage = new Page<>(List.of(), pageRequest, 0);
    when(repository.findByType(TransactionType.WITHDRAWAL, pageRequest)).thenReturn(expectedPage);

    Page<Transaction> result = strategy.executeQuery(query, repository);

    assertThat(result).isEqualTo(expectedPage);
    verify(repository).findByType(TransactionType.WITHDRAWAL, pageRequest);
  }
}
