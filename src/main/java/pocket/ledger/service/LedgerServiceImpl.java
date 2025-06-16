package pocket.ledger.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Synchronized;
import org.springframework.stereotype.Service;
import pocket.ledger.dto.v1.BalanceResponseDto;
import pocket.ledger.dto.v1.TransactionQueryDto;
import pocket.ledger.dto.v1.TransactionRequestDto;
import pocket.ledger.dto.v1.TransactionResponseDto;
import pocket.ledger.enums.TransactionType;
import pocket.ledger.exception.InsufficientBalanceException;
import pocket.ledger.exception.TransactionNotFoundException;
import pocket.ledger.model.Transaction;
import pocket.ledger.repository.TransactionRepository;
import pocket.ledger.service.query.TransactionQueryHandler;
import pocket.ledger.util.Page;

@Service
@AllArgsConstructor
public class LedgerServiceImpl implements LedgerService {

  private final TransactionRepository transactionRepository;
  private final TransactionQueryHandler queryHandler;
  private final Object balanceLock = new Object();

  @Override
  @Synchronized("balanceLock")
  public TransactionResponseDto createTransaction(TransactionRequestDto request) {
    if (request.type() == TransactionType.WITHDRAWAL) {
      BigDecimal currentBalance = transactionRepository.calculateBalance();
      if (currentBalance.compareTo(request.amount()) < 0) {
        throw new InsufficientBalanceException(currentBalance, request.amount());
      }
    }

    Transaction transaction = request.toEntity();
    Transaction savedTransaction = transactionRepository.save(transaction);
    return TransactionResponseDto.fromEntity(savedTransaction);
  }

  @Override
  public TransactionResponseDto getTransactionById(Long id) {
    Transaction transaction =
        transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
    return TransactionResponseDto.fromEntity(transaction);
  }

  @Override
  public BalanceResponseDto getBalance() {
    BigDecimal balance = transactionRepository.calculateBalance();
    Long totalTransactions = transactionRepository.countTransactions();
    return new BalanceResponseDto(balance, totalTransactions, LocalDateTime.now());
  }

  @Override
  public Page<TransactionResponseDto> getTransactions(TransactionQueryDto query) {
    Page<Transaction> transactions = queryHandler.executeQuery(query, transactionRepository);
    return transactions.map(TransactionResponseDto::fromEntity);
  }
}
