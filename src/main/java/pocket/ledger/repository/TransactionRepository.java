package pocket.ledger.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import pocket.ledger.enums.TransactionType;
import pocket.ledger.model.Transaction;
import pocket.ledger.util.Page;
import pocket.ledger.util.PageRequest;

public interface TransactionRepository {

  Transaction save(Transaction transaction);

  Optional<Transaction> findById(Long id);

  Page<Transaction> findAll(PageRequest pageable);

  Page<Transaction> findByDateRange(
      LocalDateTime startDate, LocalDateTime endDate, PageRequest pageable);

  Page<Transaction> findByType(TransactionType type, PageRequest pageable);

  Page<Transaction> findByDateRangeAndType(
      LocalDateTime startDate, LocalDateTime endDate, TransactionType type, PageRequest pageable);

  BigDecimal calculateBalance();

  Long countTransactions();
}
