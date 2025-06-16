package pocket.ledger.util;

import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Page<T> {
  private final List<T> content;
  private final PageRequest pageable;
  private final long totalElements;

  public int getTotalPages() {
    return (int) Math.ceil((double) totalElements / pageable.getPageSize());
  }

  public boolean isFirst() {
    return pageable.getPageNumber() == 0;
  }

  public boolean isLast() {
    return pageable.getPageNumber() >= getTotalPages() - 1;
  }

  public <U> Page<U> map(Function<T, U> mapper) {
    List<U> mappedContent = content.stream().map(mapper).toList();

    return new Page<>(mappedContent, pageable, totalElements);
  }
}
