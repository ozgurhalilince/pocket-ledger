package pocket.ledger.service.query;

import pocket.ledger.dto.v1.TransactionQueryDto;
import pocket.ledger.model.Transaction;
import pocket.ledger.repository.TransactionRepository;
import pocket.ledger.util.Page;

public interface TransactionQueryStrategy {
  Page<Transaction> executeQuery(TransactionQueryDto query, TransactionRepository repository);

  boolean canHandle(TransactionQueryDto query);
}
