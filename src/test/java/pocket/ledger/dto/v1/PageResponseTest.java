package pocket.ledger.dto.v1;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("PageResponse Tests")
class PageResponseTest {

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Should create PageResponse with valid parameters")
    void shouldCreatePageResponseWithValidParameters() {
      List<String> data = Arrays.asList("item1", "item2", "item3");
      int pageNumber = 0;
      int pageSize = 10;
      long totalElements = 25;
      int totalPages = 3;
      boolean first = true;
      boolean last = false;

      PageResponse<String> pageResponse =
          new PageResponse<>(data, pageNumber, pageSize, totalElements, totalPages, first, last);

      assertThat(pageResponse.data()).isEqualTo(data);
      assertThat(pageResponse.pageNumber()).isEqualTo(pageNumber);
      assertThat(pageResponse.pageSize()).isEqualTo(pageSize);
      assertThat(pageResponse.totalElements()).isEqualTo(totalElements);
      assertThat(pageResponse.totalPages()).isEqualTo(totalPages);
      assertThat(pageResponse.first()).isEqualTo(first);
      assertThat(pageResponse.last()).isEqualTo(last);
    }

    @Test
    @DisplayName("Should create PageResponse with empty data list")
    void shouldCreatePageResponseWithEmptyDataList() {
      List<String> emptyData = Collections.emptyList();

      PageResponse<String> pageResponse = new PageResponse<>(emptyData, 0, 10, 0, 0, true, true);

      assertThat(pageResponse.data()).isEmpty();
      assertThat(pageResponse.totalElements()).isZero();
      assertThat(pageResponse.totalPages()).isZero();
      assertThat(pageResponse.first()).isTrue();
      assertThat(pageResponse.last()).isTrue();
    }

    @Test
    @DisplayName("Should create PageResponse with null data")
    void shouldCreatePageResponseWithNullData() {
      PageResponse<String> pageResponse = new PageResponse<>(null, 0, 10, 0, 0, true, true);

      assertThat(pageResponse.data()).isNull();
    }
  }

  @Nested
  @DisplayName("Pagination Logic Tests")
  class PaginationLogicTests {

    @ParameterizedTest
    @DisplayName("Should correctly identify first page")
    @CsvSource({
      "0, 10, 100, 10, true, false",
      "0, 5, 50, 10, true, false",
      "0, 20, 5, 1, true, true"
    })
    void shouldCorrectlyIdentifyFirstPage(
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean expectedFirst,
        boolean expectedLast) {
      List<String> data = Arrays.asList("item1", "item2");

      PageResponse<String> pageResponse =
          new PageResponse<>(
              data, pageNumber, pageSize, totalElements, totalPages, expectedFirst, expectedLast);

      assertThat(pageResponse.first()).isEqualTo(expectedFirst);
      assertThat(pageResponse.last()).isEqualTo(expectedLast);
    }

    @ParameterizedTest
    @DisplayName("Should correctly identify last page")
    @CsvSource({
      "9, 10, 100, 10, false, true",
      "4, 5, 25, 5, false, true",
      "0, 20, 5, 1, true, true"
    })
    void shouldCorrectlyIdentifyLastPage(
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean expectedFirst,
        boolean expectedLast) {
      List<String> data = Arrays.asList("item1", "item2");

      PageResponse<String> pageResponse =
          new PageResponse<>(
              data, pageNumber, pageSize, totalElements, totalPages, expectedFirst, expectedLast);

      assertThat(pageResponse.first()).isEqualTo(expectedFirst);
      assertThat(pageResponse.last()).isEqualTo(expectedLast);
    }

    @Test
    @DisplayName("Should handle single page scenario")
    void shouldHandleSinglePageScenario() {
      List<String> data = Arrays.asList("item1", "item2");

      PageResponse<String> pageResponse = new PageResponse<>(data, 0, 10, 2, 1, true, true);

      assertThat(pageResponse.first()).isTrue();
      assertThat(pageResponse.last()).isTrue();
      assertThat(pageResponse.totalPages()).isEqualTo(1);
      assertThat(pageResponse.totalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should handle middle page scenario")
    void shouldHandleMiddlePageScenario() {
      List<String> data = Arrays.asList("item1", "item2", "item3");

      PageResponse<String> pageResponse = new PageResponse<>(data, 2, 3, 10, 4, false, false);

      assertThat(pageResponse.first()).isFalse();
      assertThat(pageResponse.last()).isFalse();
      assertThat(pageResponse.pageNumber()).isEqualTo(2);
      assertThat(pageResponse.totalPages()).isEqualTo(4);
    }
  }
}
