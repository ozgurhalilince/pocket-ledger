package pocket.ledger.init;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pocket.ledger.dto.v1.TransactionRequestDto;
import pocket.ledger.enums.TransactionType;
import pocket.ledger.service.LedgerService;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeedComponent implements CommandLineRunner {

  private final LedgerService ledgerService;
  private final DataSeedProperties dataSeedProperties;
  private final Faker faker = new Faker();

  @Override
  public void run(String... args) {
    if (!dataSeedProperties.isEnabled()) {
      log.info("Data seeding disabled.");
      return;
    }

    int count = dataSeedProperties.getCount();
    log.info("Seeding {} transactions...", count);

    for (int i = 1; i < count; i++) {
      TransactionType type = getRandomOrManuelTransactionType();
      BigDecimal amount =
          (type == TransactionType.DEPOSIT) ? randomAmount(200, 3000) : randomAmount(5, 500);

      String description = faker.commerce().productName();
      createTransaction(amount, type, description);
    }

    log.info("Seeding completed.");
  }

  private void createTransaction(BigDecimal amount, TransactionType type, String description) {
    try {
      ledgerService.createTransaction(new TransactionRequestDto(amount, type, description));
    } catch (Exception e) {
      log.warn("Failed to create transaction: {} - {}", type, amount, e);
    }
  }

  private TransactionType getRandomOrManuelTransactionType() {
    if (ledgerService.getBalance().balance().intValue() < 1000) {
      return TransactionType.DEPOSIT;
    }

    return this.randomTransactionType();
  }

  private TransactionType randomTransactionType() {
    return faker.random().nextDouble() < 0.3 ? TransactionType.DEPOSIT : TransactionType.WITHDRAWAL;
  }

  private BigDecimal randomAmount(double min, double max) {
    double value = ThreadLocalRandom.current().nextDouble(min, max);
    return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
  }
}
