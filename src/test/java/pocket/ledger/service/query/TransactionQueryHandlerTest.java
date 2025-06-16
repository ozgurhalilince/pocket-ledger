package pocket.ledger.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pocket.ledger.dto.v1.TransactionQueryDto;
import pocket.ledger.enums.TransactionType;
import pocket.ledger.model.Transaction;
import pocket.ledger.repository.TransactionRepository;
import pocket.ledger.util.Page;
import pocket.ledger.util.PageRequest;

@DisplayName("TransactionQueryHandler Tests")
class TransactionQueryHandlerTest {

  @Test
  @DisplayName("Should execute query successfully when strategy can handle")
  void executeQuery_strategyCanHandle_executesSuccessfully() {
    var mockStrategy = new TestTransactionQueryStrategy(true);
    List<TransactionQueryStrategy> strategies = List.of(mockStrategy);
    var queryHandler = new TransactionQueryHandler(strategies);
    var now = LocalDateTime.now();
    var queryDto =
        new TransactionQueryDto(
            now.minusDays(7), now, TransactionType.DEPOSIT, PageRequest.of(0, 10));
    var repository = mock(TransactionRepository.class);

    var result = queryHandler.executeQuery(queryDto, repository);

    assertThat(result).isNotNull();
    assertThat(mockStrategy.wasExecuted()).isTrue();
    assertThat(mockStrategy.wasCanHandleCalled()).isTrue();
  }

  @Test
  @DisplayName("Should execute query with second strategy when first cannot handle")
  void executeQuery_firstStrategyCannotHandle_usesSecondStrategy() {
    var strategy1 = new TestTransactionQueryStrategy(false);
    var strategy2 = new TestTransactionQueryStrategy(true);
    List<TransactionQueryStrategy> strategies = List.of(strategy1, strategy2);
    var queryHandler = new TransactionQueryHandler(strategies);
    var now = LocalDateTime.now();
    var queryDto =
        new TransactionQueryDto(
            now.minusDays(7), now, TransactionType.DEPOSIT, PageRequest.of(0, 10));
    var repository = mock(TransactionRepository.class);

    var result = queryHandler.executeQuery(queryDto, repository);

    assertThat(result).isNotNull();
    assertThat(strategy1.wasExecuted()).isFalse();
    assertThat(strategy1.wasCanHandleCalled()).isTrue();
    assertThat(strategy2.wasExecuted()).isTrue();
    assertThat(strategy2.wasCanHandleCalled()).isTrue();
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when no strategy can handle query")
  void executeQuery_noStrategyCanHandle_throwsIllegalArgumentException() {
    var strategy1 = new TestTransactionQueryStrategy(false);
    var strategy2 = new TestTransactionQueryStrategy(false);
    List<TransactionQueryStrategy> strategies = List.of(strategy1, strategy2);
    var queryHandler = new TransactionQueryHandler(strategies);
    var now = LocalDateTime.now();
    var queryDto =
        new TransactionQueryDto(
            now.minusDays(7), now, TransactionType.DEPOSIT, PageRequest.of(0, 10));
    var repository = mock(TransactionRepository.class);

    assertThatThrownBy(() -> queryHandler.executeQuery(queryDto, repository))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No strategy found for query");

    assertThat(strategy1.wasExecuted()).isFalse();
    assertThat(strategy2.wasExecuted()).isFalse();
  }

  @Test
  @DisplayName("Should handle empty strategy list")
  void executeQuery_emptyStrategyList_throwsIllegalArgumentException() {
    var emptyHandler = new TransactionQueryHandler(List.of());
    var now = LocalDateTime.now();
    var queryDto =
        new TransactionQueryDto(
            now.minusDays(7), now, TransactionType.DEPOSIT, PageRequest.of(0, 10));
    var repository = mock(TransactionRepository.class);

    assertThatThrownBy(() -> emptyHandler.executeQuery(queryDto, repository))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("No strategy found for query");
  }

  @Test
  @DisplayName("Should use first matching strategy when multiple strategies can handle")
  void executeQuery_multipleStrategiesCanHandle_usesFirstMatchingStrategy() {
    var strategy1 = new TestTransactionQueryStrategy(true);
    var strategy2 = new TestTransactionQueryStrategy(true);
    List<TransactionQueryStrategy> strategies = List.of(strategy1, strategy2);
    var queryHandler = new TransactionQueryHandler(strategies);
    var now = LocalDateTime.now();
    var queryDto =
        new TransactionQueryDto(
            now.minusDays(7), now, TransactionType.DEPOSIT, PageRequest.of(0, 10));
    var repository = mock(TransactionRepository.class);

    var result = queryHandler.executeQuery(queryDto, repository);

    assertThat(result).isNotNull();
    assertThat(strategy1.wasExecuted()).isTrue();
    assertThat(strategy1.wasCanHandleCalled()).isTrue();
    assertThat(strategy2.wasExecuted()).isFalse();
    assertThat(strategy2.wasCanHandleCalled()).isFalse();
  }

  @Test
  @DisplayName("Constructor should accept strategy list")
  void constructor_withStrategies_initializesCorrectly() {
    var strategy1 = new TestTransactionQueryStrategy(true);
    var strategy2 = new TestTransactionQueryStrategy(true);
    List<TransactionQueryStrategy> strategies = List.of(strategy1, strategy2);

    var handler = new TransactionQueryHandler(strategies);

    assertThat(handler).isNotNull();
  }

  private static class TestTransactionQueryStrategy implements TransactionQueryStrategy {
    private final boolean canHandle;
    private boolean canHandleCalled = false;
    private boolean executed = false;

    public TestTransactionQueryStrategy(boolean canHandle) {
      this.canHandle = canHandle;
    }

    @Override
    public boolean canHandle(TransactionQueryDto query) {
      this.canHandleCalled = true;
      return canHandle;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Page<Transaction> executeQuery(
        TransactionQueryDto query, TransactionRepository repository) {
      this.executed = true;
      Page<Transaction> page = Mockito.mock(Page.class);

      return page;
    }

    public boolean wasCanHandleCalled() {
      return canHandleCalled;
    }

    public boolean wasExecuted() {
      return executed;
    }
  }
}
