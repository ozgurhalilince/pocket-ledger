package pocket.ledger.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class LedgerConstantsTest {

  @Test
  void verifyPaginationConstants() {
    assertThat(LedgerConstants.MAX_PAGE_SIZE).isEqualTo(100);
    assertThat(LedgerConstants.DEFAULT_PAGE_SIZE).isEqualTo(10);
  }

  @Test
  void verifyTransactionConstants() {
    assertThat(LedgerConstants.MAX_TRANSACTION_AMOUNT).isEqualTo(new BigDecimal("9999999999.99"));
    assertThat(LedgerConstants.MIN_TRANSACTION_AMOUNT).isEqualTo(new BigDecimal("0.01"));
    assertThat(LedgerConstants.MAX_DESCRIPTION_LENGTH).isEqualTo(255);
  }

  @Test
  void verifyRequestTrackingConstants() {
    assertThat(LedgerConstants.REQUEST_ID_HEADER).isEqualTo("X-Request-ID");
    assertThat(LedgerConstants.REQUEST_ID_MDC_KEY).isEqualTo("requestId");
  }

  @Test
  void verifyUtilityClassCannotBeInstantiated() throws Exception {
    Constructor<LedgerConstants> constructor = LedgerConstants.class.getDeclaredConstructor();
    assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();

    constructor.setAccessible(true);
    constructor.newInstance();
  }
}
