package pocket.ledger.service.query;

import org.springframework.stereotype.Component;
import pocket.ledger.dto.v1.TransactionQueryDto;
import pocket.ledger.model.Transaction;
import pocket.ledger.repository.TransactionRepository;
import pocket.ledger.util.Page;

@Component
public class TypeStrategy implements TransactionQueryStrategy {

  @Override
  public Page<Transaction> executeQuery(
      TransactionQueryDto query, TransactionRepository repository) {
    return repository.findByType(query.type(), query.pageRequest());
  }

  @Override
  public boolean canHandle(TransactionQueryDto query) {
    return query.hasType() && !query.hasDateRange();
  }
}
