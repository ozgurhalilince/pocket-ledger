package pocket.ledger.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import pocket.ledger.enums.TransactionType;
import pocket.ledger.model.Transaction;
import pocket.ledger.util.Page;
import pocket.ledger.util.PageRequest;

@Slf4j
@Repository
public class TransactionRepositoryImpl implements TransactionRepository {

  private final ConcurrentHashMap<Long, Transaction> transactions = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);
  private final AtomicReference<BigDecimal> cachedBalance = new AtomicReference<>(BigDecimal.ZERO);
  private final NavigableMap<LocalDateTime, Long> timeIndex = new ConcurrentSkipListMap<>();

  @Override
  public Transaction save(Transaction transaction) {
    if (transaction.getId() == null) {
      transaction.setId(idGenerator.getAndIncrement());
    }
    transaction.setLastModifiedDate(LocalDateTime.now());
    transactions.put(transaction.getId(), transaction);

    BigDecimal delta =
        transaction.getAmount().multiply(BigDecimal.valueOf(transaction.getType().getMultiplier()));
    cachedBalance.updateAndGet(currentBalance -> currentBalance.add(delta));

    timeIndex.put(transaction.getCreatedDate(), transaction.getId());

    return transaction;
  }

  @Override
  public Optional<Transaction> findById(Long id) {
    return Optional.ofNullable(transactions.get(id));
  }

  @Override
  public Page<Transaction> findAll(PageRequest pageable) {
    List<Transaction> sortedTransactions = getSortedTransactions();
    return createPage(sortedTransactions, pageable);
  }

  @Override
  public Page<Transaction> findByDateRange(
      LocalDateTime startDate, LocalDateTime endDate, PageRequest pageable) {

    List<Transaction> filteredTransactions =
        timeIndex.subMap(startDate, true, endDate, true).values().parallelStream()
            .map(transactions::get)
            .filter(t -> t != null)
            .sorted((t1, t2) -> t2.getCreatedDate().compareTo(t1.getCreatedDate()))
            .toList();

    return createPage(filteredTransactions, pageable);
  }

  @Override
  public Page<Transaction> findByType(TransactionType type, PageRequest pageable) {
    List<Transaction> filteredTransactions =
        getSortedTransactions().parallelStream().filter(t -> t.getType() == type).toList();

    return createPage(filteredTransactions, pageable);
  }

  @Override
  public Page<Transaction> findByDateRangeAndType(
      LocalDateTime startDate, LocalDateTime endDate, TransactionType type, PageRequest pageable) {
    List<Transaction> filteredTransactions =
        timeIndex.subMap(startDate, true, endDate, true).values().parallelStream()
            .map(transactions::get)
            .filter(t -> t != null)
            .filter(t -> t.getType() == type)
            .sorted((t1, t2) -> t2.getCreatedDate().compareTo(t1.getCreatedDate()))
            .toList();

    return createPage(filteredTransactions, pageable);
  }

  @Override
  public BigDecimal calculateBalance() {
    return cachedBalance.get();
  }

  @Override
  public Long countTransactions() {
    return (long) transactions.size();
  }

  private List<Transaction> getSortedTransactions() {
    return transactions.values().parallelStream()
        .sorted((t1, t2) -> t2.getCreatedDate().compareTo(t1.getCreatedDate()))
        .toList();
  }

  private Page<Transaction> createPage(List<Transaction> transactions, PageRequest pageable) {
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), transactions.size());

    if (start > transactions.size()) {
      return new Page<>(List.of(), pageable, transactions.size());
    }

    List<Transaction> pageContent = transactions.subList(start, end);
    return new Page<>(pageContent, pageable, transactions.size());
  }
}
