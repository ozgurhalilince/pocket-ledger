package pocket.ledger.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PageRequestTest {

  @Test
  void constructor_shouldSetPageAndSize() {
    PageRequest pageRequest = new PageRequest(2, 20);

    assertThat(pageRequest.getPageNumber()).isEqualTo(2);
    assertThat(pageRequest.getPageSize()).isEqualTo(20);
  }

  @Test
  void of_shouldCreatePageRequestWithCorrectValues() {
    PageRequest pageRequest = PageRequest.of(1, 15);

    assertThat(pageRequest.getPageNumber()).isEqualTo(1);
    assertThat(pageRequest.getPageSize()).isEqualTo(15);
  }

  @Test
  void getOffset_shouldCalculateCorrectOffset() {
    PageRequest page0 = PageRequest.of(0, 10);
    PageRequest page1 = PageRequest.of(1, 10);
    PageRequest page2 = PageRequest.of(2, 10);
    PageRequest page3 = PageRequest.of(5, 20);

    assertThat(page0.getOffset()).isEqualTo(0L);
    assertThat(page1.getOffset()).isEqualTo(10L);
    assertThat(page2.getOffset()).isEqualTo(20L);
    assertThat(page3.getOffset()).isEqualTo(100L);
  }

  @Test
  void getOffset_shouldHandleZeroPageSize() {
    PageRequest pageRequest = PageRequest.of(5, 0);

    assertThat(pageRequest.getOffset()).isEqualTo(0L);
  }

  @Test
  void getOffset_shouldHandleLargeValues() {
    PageRequest pageRequest = PageRequest.of(1000, 100);

    assertThat(pageRequest.getOffset()).isEqualTo(100000L);
  }
}
