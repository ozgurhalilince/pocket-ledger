package pocket.ledger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LedgerApplicationTest {

  @Test
  void contextLoads() {
    assertThat(true).isTrue();
  }

  @Test
  void main_shouldCallSpringApplicationRun() {
    try (MockedStatic<SpringApplication> mockedSpringApplication =
        mockStatic(SpringApplication.class)) {
      String[] args = {"--spring.profiles.active=test"};

      LedgerApplication.main(args);

      mockedSpringApplication.verify(() -> SpringApplication.run(LedgerApplication.class, args));
    }
  }

  @Test
  void main_shouldAcceptEmptyArgs() {
    try (MockedStatic<SpringApplication> mockedSpringApplication =
        mockStatic(SpringApplication.class)) {
      String[] emptyArgs = {};

      LedgerApplication.main(emptyArgs);

      mockedSpringApplication.verify(
          () -> SpringApplication.run(LedgerApplication.class, emptyArgs));
    }
  }

  @Test
  void main_shouldAcceptNullArgs() {
    try (MockedStatic<SpringApplication> mockedSpringApplication =
        mockStatic(SpringApplication.class)) {
      String[] nullArgs = null;

      LedgerApplication.main(nullArgs);

      mockedSpringApplication.verify(
          () -> SpringApplication.run(LedgerApplication.class, nullArgs));
    }
  }
}
