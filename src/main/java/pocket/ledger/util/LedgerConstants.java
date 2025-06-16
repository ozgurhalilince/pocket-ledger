package pocket.ledger.util;

import java.math.BigDecimal;

public final class LedgerConstants {

  // Pagination constants
  public static final int MAX_PAGE_SIZE = 100;
  public static final int DEFAULT_PAGE_SIZE = 10;

  // Transaction constants
  public static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("9999999999.99");
  public static final BigDecimal MIN_TRANSACTION_AMOUNT = new BigDecimal("0.01");
  public static final int MAX_DESCRIPTION_LENGTH = 255;

  // Request tracking constants
  public static final String REQUEST_ID_HEADER = "X-Request-ID";
  public static final String REQUEST_ID_MDC_KEY = "requestId";

  private LedgerConstants() {}
}
