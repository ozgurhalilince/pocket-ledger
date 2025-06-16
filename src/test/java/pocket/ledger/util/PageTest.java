package pocket.ledger.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class PageTest {

  @Test
  void constructor_shouldSetAllFields() {
    List<String> content = List.of("item1", "item2", "item3");
    PageRequest pageable = PageRequest.of(0, 10);
    long totalElements = 25L;

    Page<String> page = new Page<>(content, pageable, totalElements);

    assertThat(page.getContent()).isEqualTo(content);
    assertThat(page.getPageable()).isEqualTo(pageable);
    assertThat(page.getTotalElements()).isEqualTo(totalElements);
  }

  @Test
  void getTotalPages_shouldCalculateCorrectly() {
    PageRequest pageable = PageRequest.of(0, 10);

    Page<String> page1 = new Page<>(List.of(), pageable, 25L);
    assertThat(page1.getTotalPages()).isEqualTo(3);

    Page<String> page2 = new Page<>(List.of(), pageable, 20L);
    assertThat(page2.getTotalPages()).isEqualTo(2);

    Page<String> page3 = new Page<>(List.of(), pageable, 0L);
    assertThat(page3.getTotalPages()).isEqualTo(0);
  }

  @Test
  void isFirst_shouldReturnTrueForFirstPage() {
    PageRequest firstPage = PageRequest.of(0, 10);
    PageRequest secondPage = PageRequest.of(1, 10);

    Page<String> page1 = new Page<>(List.of(), firstPage, 25L);
    Page<String> page2 = new Page<>(List.of(), secondPage, 25L);

    assertThat(page1.isFirst()).isTrue();
    assertThat(page2.isFirst()).isFalse();
  }

  @Test
  void isLast_shouldReturnTrueForLastPage() {
    PageRequest firstPage = PageRequest.of(0, 10);
    PageRequest lastPage = PageRequest.of(2, 10);

    Page<String> page1 = new Page<>(List.of(), firstPage, 25L);
    Page<String> page2 = new Page<>(List.of(), lastPage, 25L);

    assertThat(page1.isLast()).isFalse();
    assertThat(page2.isLast()).isTrue();
  }

  @Test
  void isLast_shouldReturnTrueForSinglePage() {
    PageRequest singlePage = PageRequest.of(0, 10);
    Page<String> page = new Page<>(List.of(), singlePage, 5L);

    assertThat(page.isLast()).isTrue();
  }

  @Test
  void map_shouldTransformContentCorrectly() {
    List<Integer> numbers = List.of(1, 2, 3);
    PageRequest pageable = PageRequest.of(0, 10);
    Page<Integer> originalPage = new Page<>(numbers, pageable, 3L);

    Page<String> mappedPage = originalPage.map(String::valueOf);

    assertThat(mappedPage.getContent()).containsExactly("1", "2", "3");
    assertThat(mappedPage.getPageable()).isEqualTo(pageable);
    assertThat(mappedPage.getTotalElements()).isEqualTo(3L);
  }

  @Test
  void map_shouldHandleEmptyContent() {
    List<Integer> emptyList = List.of();
    PageRequest pageable = PageRequest.of(0, 10);
    Page<Integer> originalPage = new Page<>(emptyList, pageable, 0L);

    Page<String> mappedPage = originalPage.map(String::valueOf);

    assertThat(mappedPage.getContent()).isEmpty();
    assertThat(mappedPage.getPageable()).isEqualTo(pageable);
    assertThat(mappedPage.getTotalElements()).isEqualTo(0L);
  }
}
