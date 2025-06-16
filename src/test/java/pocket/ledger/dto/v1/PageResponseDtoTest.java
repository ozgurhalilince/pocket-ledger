package pocket.ledger.dto.v1;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class PageResponseDtoTest {

  @Test
  void constructor_shouldSetAllFields() {
    List<String> content = List.of("item1", "item2", "item3");
    int pageNumber = 1;
    int pageSize = 10;
    long totalElements = 25L;
    int totalPages = 3;
    boolean first = false;
    boolean last = false;

    PageResponseDto<String> dto =
        new PageResponseDto<>(
            content, pageNumber, pageSize, totalElements, totalPages, first, last);

    assertThat(dto.content()).isEqualTo(content);
    assertThat(dto.pageNumber()).isEqualTo(pageNumber);
    assertThat(dto.pageSize()).isEqualTo(pageSize);
    assertThat(dto.totalElements()).isEqualTo(totalElements);
    assertThat(dto.totalPages()).isEqualTo(totalPages);
    assertThat(dto.first()).isEqualTo(first);
    assertThat(dto.last()).isEqualTo(last);
  }

  @Test
  void constructor_shouldHandleEmptyContent() {
    List<String> emptyContent = List.of();
    PageResponseDto<String> dto = new PageResponseDto<>(emptyContent, 0, 10, 0L, 0, true, true);

    assertThat(dto.content()).isEmpty();
    assertThat(dto.pageNumber()).isEqualTo(0);
    assertThat(dto.first()).isTrue();
    assertThat(dto.last()).isTrue();
  }

  @Test
  void constructor_shouldHandleFirstPage() {
    List<Integer> content = List.of(1, 2, 3);
    PageResponseDto<Integer> dto = new PageResponseDto<>(content, 0, 3, 10L, 4, true, false);

    assertThat(dto.first()).isTrue();
    assertThat(dto.last()).isFalse();
    assertThat(dto.pageNumber()).isEqualTo(0);
  }

  @Test
  void constructor_shouldHandleLastPage() {
    List<Integer> content = List.of(10);
    PageResponseDto<Integer> dto = new PageResponseDto<>(content, 3, 3, 10L, 4, false, true);

    assertThat(dto.first()).isFalse();
    assertThat(dto.last()).isTrue();
    assertThat(dto.pageNumber()).isEqualTo(3);
  }

  @Test
  void constructor_shouldHandleSinglePage() {
    List<String> content = List.of("only item");
    PageResponseDto<String> dto = new PageResponseDto<>(content, 0, 10, 1L, 1, true, true);

    assertThat(dto.first()).isTrue();
    assertThat(dto.last()).isTrue();
    assertThat(dto.totalPages()).isEqualTo(1);
  }

  @Test
  void constructor_shouldHandleDifferentGenericTypes() {
    List<Double> doubleContent = List.of(1.5, 2.5, 3.5);
    PageResponseDto<Double> doubleDto =
        new PageResponseDto<>(doubleContent, 0, 3, 3L, 1, true, true);

    assertThat(doubleDto.content()).containsExactly(1.5, 2.5, 3.5);

    List<Boolean> booleanContent = List.of(true, false);
    PageResponseDto<Boolean> booleanDto =
        new PageResponseDto<>(booleanContent, 0, 2, 2L, 1, true, true);

    assertThat(booleanDto.content()).containsExactly(true, false);
  }

  @Test
  void constructor_shouldHandleNullContent() {
    PageResponseDto<String> dto = new PageResponseDto<>(null, 0, 10, 0L, 0, true, true);

    assertThat(dto.content()).isNull();
  }
}
