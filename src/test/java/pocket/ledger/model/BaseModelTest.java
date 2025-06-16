package pocket.ledger.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import pocket.ledger.enums.TransactionType;

class BaseModelTest {

  private static class TestModel extends BaseModel {}

  @Test
  void constructor_shouldSetCreatedAndModifiedDates() {
    LocalDateTime before = LocalDateTime.now().minusSeconds(1);

    TestModel model = new TestModel();

    LocalDateTime after = LocalDateTime.now().plusSeconds(1);

    assertThat(model.getCreatedDate()).isNotNull();
    assertThat(model.getLastModifiedDate()).isNotNull();
    assertThat(model.getCreatedDate()).isBetween(before, after);
    assertThat(model.getLastModifiedDate()).isBetween(before, after);
  }

  @Test
  void setCreatedDate_shouldUpdateCreatedDate() {
    TestModel model = new TestModel();
    LocalDateTime customDate = LocalDateTime.of(2024, 1, 1, 10, 0, 0);

    model.setCreatedDate(customDate);

    assertThat(model.getCreatedDate()).isEqualTo(customDate);
  }

  @Test
  void setLastModifiedDate_shouldUpdateLastModifiedDate() {
    TestModel model = new TestModel();
    LocalDateTime customDate = LocalDateTime.of(2024, 1, 1, 10, 0, 0);

    model.setLastModifiedDate(customDate);

    assertThat(model.getLastModifiedDate()).isEqualTo(customDate);
  }

  @Test
  void inheritedByTransaction_shouldWorkCorrectly() {
    Transaction transaction =
        new Transaction(BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Test");

    assertThat(transaction.getCreatedDate()).isNotNull();
    assertThat(transaction.getLastModifiedDate()).isNotNull();
    assertThat(transaction).isInstanceOf(BaseModel.class);
  }

  @Test
  void baseModel_shouldAllowNullDates() {
    TestModel model = new TestModel();

    model.setCreatedDate(null);
    model.setLastModifiedDate(null);

    assertThat(model.getCreatedDate()).isNull();
    assertThat(model.getLastModifiedDate()).isNull();
  }

  @Test
  void transaction_shouldCallBaseModelConstructor() {
    LocalDateTime before = LocalDateTime.now().minusSeconds(1);

    Transaction transaction =
        new Transaction(BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Test");

    LocalDateTime after = LocalDateTime.now().plusSeconds(1);

    assertThat(transaction.getCreatedDate()).isBetween(before, after);
    assertThat(transaction.getLastModifiedDate()).isBetween(before, after);
  }

  @Test
  void transaction_shouldAllowSettingAllFields() {
    Transaction transaction =
        new Transaction(BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Test");

    LocalDateTime customCreated = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
    LocalDateTime customModified = LocalDateTime.of(2023, 1, 1, 11, 0, 0);
    BigDecimal newAmount = BigDecimal.valueOf(200);

    transaction.setId(123L);
    transaction.setCreatedDate(customCreated);
    transaction.setLastModifiedDate(customModified);
    transaction.setAmount(newAmount);
    transaction.setType(TransactionType.WITHDRAWAL);
    transaction.setDescription("Updated");

    assertThat(transaction.getId()).isEqualTo(123L);
    assertThat(transaction.getCreatedDate()).isEqualTo(customCreated);
    assertThat(transaction.getLastModifiedDate()).isEqualTo(customModified);
    assertThat(transaction.getAmount()).isEqualTo(newAmount);
    assertThat(transaction.getType()).isEqualTo(TransactionType.WITHDRAWAL);
    assertThat(transaction.getDescription()).isEqualTo("Updated");
  }
}
