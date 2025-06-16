package pocket.ledger.util;

public class PageRequest {
  private final int page;
  private final int size;

  public PageRequest(int page, int size) {
    this.page = page;
    this.size = size;
  }

  public static PageRequest of(int page, int size) {
    return new PageRequest(page, size);
  }

  public int getPageNumber() {
    return page;
  }

  public int getPageSize() {
    return size;
  }

  public long getOffset() {
    return (long) page * size;
  }
}
