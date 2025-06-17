package pocket.ledger.controller.v1;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import pocket.ledger.dto.v1.BalanceResponseDto;
import pocket.ledger.dto.v1.TransactionQueryDto;
import pocket.ledger.dto.v1.TransactionRequestDto;
import pocket.ledger.dto.v1.TransactionResponseDto;
import pocket.ledger.enums.TransactionType;
import pocket.ledger.exception.GlobalExceptionHandler;
import pocket.ledger.exception.InsufficientBalanceException;
import pocket.ledger.exception.TransactionNotFoundException;
import pocket.ledger.service.LedgerService;
import pocket.ledger.util.Page;
import pocket.ledger.util.PageRequest;

@WebMvcTest(LedgerController.class)
@ContextConfiguration(
    classes = {
      LedgerController.class,
      GlobalExceptionHandler.class,
      LedgerControllerTest.TestConfig.class
    })
@DisplayName("LedgerController Integration Tests")
class LedgerControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private LedgerService ledgerService;

  @TestConfiguration
  static class TestConfig {

    @Bean
    @Primary
    public LedgerService ledgerService() {
      return Mockito.mock(LedgerService.class);
    }
  }

  @Nested
  @DisplayName("GET /api/v1/balance")
  class GetBalanceTests {

    @Test
    @DisplayName("Should return balance successfully")
    void shouldReturnBalanceSuccessfully() throws Exception {
      BalanceResponseDto balanceResponse =
          new BalanceResponseDto(
              BigDecimal.valueOf(1500.75), 25L, LocalDateTime.of(2024, 1, 1, 10, 0));

      when(ledgerService.getBalance()).thenReturn(balanceResponse);

      mockMvc
          .perform(get("/api/v1/balance"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data.balance", is(1500.75)))
          .andExpect(jsonPath("$.data.totalTransactions", is(25)))
          .andExpect(jsonPath("$.data.asOfTimestamp", is("2024-01-01T10:00:00")));
    }

    @Test
    @DisplayName("Should return zero balance for empty account")
    void shouldReturnZeroBalanceForEmptyAccount() throws Exception {
      BalanceResponseDto balanceResponse =
          new BalanceResponseDto(BigDecimal.ZERO, 0L, LocalDateTime.of(2024, 1, 1, 10, 0));

      when(ledgerService.getBalance()).thenReturn(balanceResponse);

      mockMvc
          .perform(get("/api/v1/balance"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.balance", is(0)))
          .andExpect(jsonPath("$.data.totalTransactions", is(0)));
    }

    @Test
    @DisplayName("Should handle negative balance")
    void shouldHandleNegativeBalance() throws Exception {
      BalanceResponseDto balanceResponse =
          new BalanceResponseDto(
              BigDecimal.valueOf(-50.25), 5L, LocalDateTime.of(2024, 1, 1, 10, 0));

      when(ledgerService.getBalance()).thenReturn(balanceResponse);

      mockMvc
          .perform(get("/api/v1/balance"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.balance", is(-50.25)))
          .andExpect(jsonPath("$.data.totalTransactions", is(5)));
    }
  }

  @Nested
  @DisplayName("POST /api/v1/transactions")
  class CreateTransactionTests {

    @Test
    @DisplayName("Should create deposit transaction successfully")
    void shouldCreateDepositTransactionSuccessfully() throws Exception {
      TransactionRequestDto request =
          new TransactionRequestDto(
              BigDecimal.valueOf(1000), TransactionType.DEPOSIT, "Initial deposit");

      TransactionResponseDto response =
          new TransactionResponseDto(
              1L,
              BigDecimal.valueOf(1000),
              TransactionType.DEPOSIT,
              "Initial deposit",
              LocalDateTime.of(2024, 1, 1, 10, 0),
              LocalDateTime.of(2024, 1, 1, 10, 0));

      when(ledgerService.createTransaction(any(TransactionRequestDto.class))).thenReturn(response);

      mockMvc
          .perform(
              post("/api/v1/transactions")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data.id", is(1)))
          .andExpect(jsonPath("$.data.amount", is(1000)))
          .andExpect(jsonPath("$.data.type", is("DEPOSIT")))
          .andExpect(jsonPath("$.data.description", is("Initial deposit")))
          .andExpect(jsonPath("$.data.createdDate", is("2024-01-01T10:00:00")));
    }

    @Test
    @DisplayName("Should create withdrawal transaction successfully")
    void shouldCreateWithdrawalTransactionSuccessfully() throws Exception {
      TransactionRequestDto request =
          new TransactionRequestDto(
              BigDecimal.valueOf(250.50), TransactionType.WITHDRAWAL, "ATM withdrawal");

      TransactionResponseDto response =
          new TransactionResponseDto(
              2L,
              BigDecimal.valueOf(250.50),
              TransactionType.WITHDRAWAL,
              "ATM withdrawal",
              LocalDateTime.of(2024, 1, 1, 11, 0),
              LocalDateTime.of(2024, 1, 1, 11, 0));

      when(ledgerService.createTransaction(any(TransactionRequestDto.class))).thenReturn(response);

      mockMvc
          .perform(
              post("/api/v1/transactions")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.data.id", is(2)))
          .andExpect(jsonPath("$.data.amount", is(250.5)))
          .andExpect(jsonPath("$.data.type", is("WITHDRAWAL")));
    }

    @Test
    @DisplayName("Should return 400 for invalid transaction request")
    void shouldReturn400ForInvalidTransactionRequest() throws Exception {
      String invalidJson = "{\"type\":\"DEPOSIT\",\"description\":\"Invalid transaction\"}";

      mockMvc
          .perform(
              post("/api/v1/transactions")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(invalidJson))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message", is("Validation failed")))
          .andExpect(jsonPath("$.status", is(400)))
          .andExpect(jsonPath("$.validationErrors.amount").exists());
    }

    @Test
    @DisplayName("Should return 400 for negative amount")
    void shouldReturn400ForNegativeAmount() throws Exception {
      TransactionRequestDto request =
          new TransactionRequestDto(
              BigDecimal.valueOf(-100), TransactionType.DEPOSIT, "Invalid negative amount");

      mockMvc
          .perform(
              post("/api/v1/transactions")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.validationErrors.amount").exists());
    }

    @Test
    @DisplayName("Should return 400 for missing transaction type")
    void shouldReturn400ForMissingTransactionType() throws Exception {
      String jsonWithoutType = "{\"amount\":100,\"description\":\"No type specified\"}";

      mockMvc
          .perform(
              post("/api/v1/transactions")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(jsonWithoutType))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.validationErrors.type").exists());
    }

    @Test
    @DisplayName("Should return 422 for insufficient balance")
    void shouldReturn422ForInsufficientBalance() throws Exception {
      TransactionRequestDto request =
          new TransactionRequestDto(
              BigDecimal.valueOf(1000), TransactionType.WITHDRAWAL, "Large withdrawal");

      when(ledgerService.createTransaction(any(TransactionRequestDto.class)))
          .thenThrow(
              new InsufficientBalanceException(BigDecimal.valueOf(500), BigDecimal.valueOf(1000)));

      mockMvc
          .perform(
              post("/api/v1/transactions")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isUnprocessableEntity())
          .andExpect(
              jsonPath("$.message")
                  .value(org.hamcrest.Matchers.containsString("Insufficient balance")))
          .andExpect(jsonPath("$.status", is(422)));
    }

    @Test
    @DisplayName("Should handle transaction with empty description")
    void shouldHandleTransactionWithEmptyDescription() throws Exception {
      TransactionRequestDto request =
          new TransactionRequestDto(BigDecimal.valueOf(100), TransactionType.DEPOSIT, "");

      TransactionResponseDto response =
          new TransactionResponseDto(
              1L,
              BigDecimal.valueOf(100),
              TransactionType.DEPOSIT,
              "",
              LocalDateTime.of(2024, 1, 1, 10, 0),
              LocalDateTime.of(2024, 1, 1, 10, 0));

      when(ledgerService.createTransaction(any(TransactionRequestDto.class))).thenReturn(response);

      mockMvc
          .perform(
              post("/api/v1/transactions")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.data.description", is("")));
    }

    @Test
    @DisplayName("Should handle transaction with very long description")
    void shouldHandleTransactionWithVeryLongDescription() throws Exception {
      String longDescription = "A".repeat(255);
      TransactionRequestDto request =
          new TransactionRequestDto(
              BigDecimal.valueOf(100), TransactionType.DEPOSIT, longDescription);

      TransactionResponseDto response =
          new TransactionResponseDto(
              1L,
              BigDecimal.valueOf(100),
              TransactionType.DEPOSIT,
              longDescription,
              LocalDateTime.of(2024, 1, 1, 10, 0),
              LocalDateTime.of(2024, 1, 1, 10, 0));

      when(ledgerService.createTransaction(any(TransactionRequestDto.class))).thenReturn(response);

      mockMvc
          .perform(
              post("/api/v1/transactions")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.data.description", is(longDescription)));
    }

    @Test
    @DisplayName("Should return 400 for description exceeding max length")
    void shouldReturn400ForDescriptionExceedingMaxLength() throws Exception {
      String tooLongDescription = "A".repeat(256);
      TransactionRequestDto request =
          new TransactionRequestDto(
              BigDecimal.valueOf(100), TransactionType.DEPOSIT, tooLongDescription);

      mockMvc
          .perform(
              post("/api/v1/transactions")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.validationErrors.description").exists());
    }
  }

  @Nested
  @DisplayName("GET /api/v1/transactions/{id}")
  class GetTransactionByIdTests {

    @Test
    @DisplayName("Should return transaction by ID successfully")
    void shouldReturnTransactionByIdSuccessfully() throws Exception {
      Long transactionId = 1L;
      TransactionResponseDto response =
          new TransactionResponseDto(
              transactionId,
              BigDecimal.valueOf(500),
              TransactionType.DEPOSIT,
              "Test transaction",
              LocalDateTime.of(2024, 1, 1, 10, 0),
              LocalDateTime.of(2024, 1, 1, 10, 0));

      when(ledgerService.getTransactionById(transactionId)).thenReturn(response);

      mockMvc
          .perform(get("/api/v1/transactions/{id}", transactionId))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data.id", is(1)))
          .andExpect(jsonPath("$.data.amount", is(500)))
          .andExpect(jsonPath("$.data.type", is("DEPOSIT")))
          .andExpect(jsonPath("$.data.description", is("Test transaction")));
    }

    @Test
    @DisplayName("Should return 404 for non-existent transaction ID")
    void shouldReturn404ForNonExistentTransactionId() throws Exception {
      Long nonExistentId = 999L;
      when(ledgerService.getTransactionById(nonExistentId))
          .thenThrow(new TransactionNotFoundException(nonExistentId));

      mockMvc
          .perform(get("/api/v1/transactions/{id}", nonExistentId))
          .andExpect(status().isNotFound())
          .andExpect(
              jsonPath("$.message")
                  .value(org.hamcrest.Matchers.containsString("Transaction not found")))
          .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("Should return 400 for invalid ID format")
    void shouldReturn400ForInvalidIdFormat() throws Exception {
      mockMvc
          .perform(get("/api/v1/transactions/{id}", "invalid-id"))
          .andExpect(status().isBadRequest())
          .andExpect(
              jsonPath("$.message")
                  .value(org.hamcrest.Matchers.containsString("should be of type Long")))
          .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @DisplayName("Should handle zero ID")
    void shouldHandleZeroId() throws Exception {
      Long zeroId = 0L;
      when(ledgerService.getTransactionById(zeroId))
          .thenThrow(new TransactionNotFoundException(zeroId));

      mockMvc.perform(get("/api/v1/transactions/{id}", zeroId)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle negative ID")
    void shouldHandleNegativeId() throws Exception {
      Long negativeId = -1L;
      when(ledgerService.getTransactionById(negativeId))
          .thenThrow(new TransactionNotFoundException(negativeId));

      mockMvc
          .perform(get("/api/v1/transactions/{id}", negativeId))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("GET /api/v1/transactions")
  class GetTransactionsTests {

    @Test
    @DisplayName("Should return paginated transactions with default parameters")
    void shouldReturnPaginatedTransactionsWithDefaultParameters() throws Exception {
      TransactionResponseDto transaction1 =
          new TransactionResponseDto(
              1L,
              BigDecimal.valueOf(100),
              TransactionType.DEPOSIT,
              "Transaction 1",
              LocalDateTime.of(2024, 1, 1, 10, 0),
              LocalDateTime.of(2024, 1, 1, 10, 0));

      TransactionResponseDto transaction2 =
          new TransactionResponseDto(
              2L,
              BigDecimal.valueOf(50),
              TransactionType.WITHDRAWAL,
              "Transaction 2",
              LocalDateTime.of(2024, 1, 1, 11, 0),
              LocalDateTime.of(2024, 1, 1, 11, 0));

      List<TransactionResponseDto> transactions = List.of(transaction1, transaction2);
      Page<TransactionResponseDto> page = new Page<>(transactions, PageRequest.of(0, 10), 2);

      when(ledgerService.getTransactions(any(TransactionQueryDto.class))).thenReturn(page);

      mockMvc
          .perform(get("/api/v1/transactions"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.data", hasSize(2)))
          .andExpect(jsonPath("$.data[0].id", is(1)))
          .andExpect(jsonPath("$.data[0].amount", is(100)))
          .andExpect(jsonPath("$.data[1].id", is(2)))
          .andExpect(jsonPath("$.data[1].amount", is(50)))
          .andExpect(jsonPath("$.pageNumber", is(0)))
          .andExpect(jsonPath("$.pageSize", is(10)))
          .andExpect(jsonPath("$.totalElements", is(2)))
          .andExpect(jsonPath("$.totalPages", is(1)))
          .andExpect(jsonPath("$.first", is(true)))
          .andExpect(jsonPath("$.last", is(true)));
    }

    @Test
    @DisplayName("Should return transactions with custom pagination")
    void shouldReturnTransactionsWithCustomPagination() throws Exception {
      TransactionResponseDto transaction =
          new TransactionResponseDto(
              3L,
              BigDecimal.valueOf(200),
              TransactionType.DEPOSIT,
              "Transaction 3",
              LocalDateTime.of(2024, 1, 1, 12, 0),
              LocalDateTime.of(2024, 1, 1, 12, 0));

      List<TransactionResponseDto> transactions = List.of(transaction);
      Page<TransactionResponseDto> page = new Page<>(transactions, PageRequest.of(1, 5), 10);

      when(ledgerService.getTransactions(any(TransactionQueryDto.class))).thenReturn(page);

      mockMvc
          .perform(get("/api/v1/transactions").param("page", "1").param("size", "5"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data", hasSize(1)))
          .andExpect(jsonPath("$.pageNumber", is(1)))
          .andExpect(jsonPath("$.pageSize", is(5)))
          .andExpect(jsonPath("$.totalElements", is(10)))
          .andExpect(jsonPath("$.first", is(false)))
          .andExpect(jsonPath("$.last", is(true)));
    }

    @Test
    @DisplayName("Should return transactions filtered by type")
    void shouldReturnTransactionsFilteredByType() throws Exception {
      TransactionResponseDto deposit =
          new TransactionResponseDto(
              1L,
              BigDecimal.valueOf(100),
              TransactionType.DEPOSIT,
              "Deposit",
              LocalDateTime.of(2024, 1, 1, 10, 0),
              LocalDateTime.of(2024, 1, 1, 10, 0));

      List<TransactionResponseDto> transactions = List.of(deposit);
      Page<TransactionResponseDto> page = new Page<>(transactions, PageRequest.of(0, 10), 1);

      when(ledgerService.getTransactions(any(TransactionQueryDto.class))).thenReturn(page);

      mockMvc
          .perform(get("/api/v1/transactions").param("type", "DEPOSIT"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data", hasSize(1)))
          .andExpect(jsonPath("$.data[0].type", is("DEPOSIT")));
    }

    @Test
    @DisplayName("Should return transactions filtered by date range")
    void shouldReturnTransactionsFilteredByDateRange() throws Exception {
      TransactionResponseDto transaction =
          new TransactionResponseDto(
              1L,
              BigDecimal.valueOf(100),
              TransactionType.DEPOSIT,
              "Date filtered",
              LocalDateTime.of(2024, 1, 15, 10, 0),
              LocalDateTime.of(2024, 1, 15, 10, 0));

      List<TransactionResponseDto> transactions = List.of(transaction);
      Page<TransactionResponseDto> page = new Page<>(transactions, PageRequest.of(0, 10), 1);

      when(ledgerService.getTransactions(any(TransactionQueryDto.class))).thenReturn(page);

      mockMvc
          .perform(
              get("/api/v1/transactions")
                  .param("startDate", "2024-01-01T00:00:00")
                  .param("endDate", "2024-01-31T23:59:59"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    @DisplayName("Should return empty page when no transactions match criteria")
    void shouldReturnEmptyPageWhenNoTransactionsMatchCriteria() throws Exception {
      Page<TransactionResponseDto> emptyPage = new Page<>(List.of(), PageRequest.of(0, 10), 0);
      when(ledgerService.getTransactions(any(TransactionQueryDto.class))).thenReturn(emptyPage);

      mockMvc
          .perform(get("/api/v1/transactions").param("type", "WITHDRAWAL"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data", hasSize(0)))
          .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    @DisplayName("Should enforce maximum page size")
    void shouldEnforceMaximumPageSize() throws Exception {
      List<TransactionResponseDto> transactions = List.of();
      Page<TransactionResponseDto> page = new Page<>(transactions, PageRequest.of(0, 50), 0);

      when(ledgerService.getTransactions(any(TransactionQueryDto.class))).thenReturn(page);

      mockMvc
          .perform(get("/api/v1/transactions").param("size", "1000"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.pageSize", is(50)));
    }

    @Test
    @DisplayName("Should return 400 for invalid date format")
    void shouldReturn400ForInvalidDateFormat() throws Exception {
      mockMvc
          .perform(get("/api/v1/transactions").param("startDate", "invalid-date"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 for invalid transaction type")
    void shouldReturn400ForInvalidTransactionType() throws Exception {
      mockMvc
          .perform(get("/api/v1/transactions").param("type", "INVALID_TYPE"))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle negative page number")
    void shouldHandleNegativePageNumber() throws Exception {
      Page<TransactionResponseDto> emptyPage = new Page<>(List.of(), PageRequest.of(0, 10), 0);
      when(ledgerService.getTransactions(any(TransactionQueryDto.class))).thenReturn(emptyPage);

      mockMvc
          .perform(get("/api/v1/transactions").param("page", "-1"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    @DisplayName("Should handle zero page size")
    void shouldHandleZeroPageSize() throws Exception {
      Page<TransactionResponseDto> emptyPage = new Page<>(List.of(), PageRequest.of(0, 1), 0);
      when(ledgerService.getTransactions(any(TransactionQueryDto.class))).thenReturn(emptyPage);

      mockMvc.perform(get("/api/v1/transactions").param("size", "0")).andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("Content Type and Accept Headers")
  class ContentTypeTests {

    @Test
    @DisplayName("Should require JSON content type for POST requests")
    void shouldRequireJsonContentTypeForPostRequests() throws Exception {
      TransactionRequestDto request =
          new TransactionRequestDto(BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Test");

      mockMvc
          .perform(
              post("/api/v1/transactions")
                  .contentType(MediaType.TEXT_PLAIN)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Should handle missing content type gracefully")
    void shouldHandleMissingContentTypeGracefully() throws Exception {
      TransactionRequestDto request =
          new TransactionRequestDto(BigDecimal.valueOf(100), TransactionType.DEPOSIT, "Test");

      mockMvc
          .perform(post("/api/v1/transactions").content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Should return JSON content type for all successful responses")
    void shouldReturnJsonContentTypeForAllSuccessfulResponses() throws Exception {
      BalanceResponseDto balanceResponse =
          new BalanceResponseDto(BigDecimal.valueOf(100), 1L, LocalDateTime.now());
      when(ledgerService.getBalance()).thenReturn(balanceResponse);

      mockMvc
          .perform(get("/api/v1/balance"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
  }
}
