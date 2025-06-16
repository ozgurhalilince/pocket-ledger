package pocket.ledger.service;

import pocket.ledger.dto.v1.BalanceResponseDto;
import pocket.ledger.dto.v1.TransactionQueryDto;
import pocket.ledger.dto.v1.TransactionRequestDto;
import pocket.ledger.dto.v1.TransactionResponseDto;
import pocket.ledger.util.Page;

public interface LedgerService {
  TransactionResponseDto createTransaction(TransactionRequestDto request);

  TransactionResponseDto getTransactionById(Long id);

  BalanceResponseDto getBalance();

  Page<TransactionResponseDto> getTransactions(TransactionQueryDto query);
}
