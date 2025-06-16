package pocket.ledger.service.query;

import java.util.List;
import org.springframework.stereotype.Component;
import pocket.ledger.dto.v1.TransactionQueryDto;
import pocket.ledger.model.Transaction;
import pocket.ledger.repository.TransactionRepository;
import pocket.ledger.util.Page;

@Component
public class TransactionQueryHandler {

  private final List<TransactionQueryStrategy> strategies;

  public TransactionQueryHandler(List<TransactionQueryStrategy> strategies) {
    this.strategies = strategies;
  }

  public Page<Transaction> executeQuery(
      TransactionQueryDto query, TransactionRepository repository) {
    return strategies.stream()
        .filter(strategy -> strategy.canHandle(query))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No strategy found for query"))
        .executeQuery(query, repository);
  }
}
