package pocket.ledger.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pocket.ledger.dto.v1.BalanceResponseDto;
import pocket.ledger.dto.v1.ErrorResponse;
import pocket.ledger.dto.v1.PageResponse;
import pocket.ledger.dto.v1.SuccessResponse;
import pocket.ledger.dto.v1.TransactionQueryDto;
import pocket.ledger.dto.v1.TransactionRequestDto;
import pocket.ledger.dto.v1.TransactionResponseDto;
import pocket.ledger.enums.TransactionType;
import pocket.ledger.service.LedgerService;
import pocket.ledger.util.LedgerConstants;
import pocket.ledger.util.Page;
import pocket.ledger.util.PageRequest;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class LedgerController {

  private final LedgerService ledgerService;

  @GetMapping("/balance")
  @Operation(
      summary = "Get current balance",
      description = "Get the current account balance with transaction count",
      tags = {"Balance"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Balance retrieved successfully"),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public SuccessResponse<BalanceResponseDto> getBalance() {
    return SuccessResponse.ok(ledgerService.getBalance());
  }

  @GetMapping("/transactions")
  @Operation(
      summary = "Get transaction history",
      description = "Get paginated transaction history with optional filtering",
      tags = {"Transactions"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid query parameters",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public PageResponse<TransactionResponseDto> getTransactions(
      @Parameter(description = "Page number (0-based)")
          @RequestParam(name = "page", defaultValue = "0")
          int page,
      @Parameter(description = "Page size (max " + LedgerConstants.MAX_PAGE_SIZE + ")")
          @RequestParam(name = "size", defaultValue = "" + LedgerConstants.DEFAULT_PAGE_SIZE)
          int size,
      @Parameter(description = "Start date (ISO format)")
          @RequestParam(name = "startDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime startDate,
      @Parameter(description = "End date (ISO format)")
          @RequestParam(name = "endDate", required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          LocalDateTime endDate,
      @Parameter(description = "Transaction type") @RequestParam(name = "type", required = false)
          TransactionType type) {

    size = Math.min(size, LedgerConstants.MAX_PAGE_SIZE);
    PageRequest pageable = PageRequest.of(page, size);

    Page<TransactionResponseDto> pageResult =
        ledgerService.getTransactions(new TransactionQueryDto(startDate, endDate, type, pageable));

    return new PageResponse<>(
        pageResult.getContent(),
        pageResult.getPageable().getPageNumber(),
        pageResult.getPageable().getPageSize(),
        pageResult.getTotalElements(),
        pageResult.getTotalPages(),
        pageResult.isFirst(),
        pageResult.isLast());
  }

  @PostMapping("/transactions")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Create a new transaction",
      description = "Create a new deposit or withdrawal transaction",
      tags = {"Transactions"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "Validation failed",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "422",
            description = "Business rule violation",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public SuccessResponse<TransactionResponseDto> createTransaction(
      @Valid @RequestBody TransactionRequestDto request) {
    TransactionResponseDto transaction = ledgerService.createTransaction(request);
    return SuccessResponse.ok(transaction, "Transaction created successfully");
  }

  @GetMapping("/transactions/{id}")
  @Operation(
      summary = "Get transaction by ID",
      description = "Retrieve a specific transaction by its ID. ID must be a valid long number.",
      tags = {"Transactions"})
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Transaction found"),
        @ApiResponse(
            responseCode = "404",
            description = "Transaction not found",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid ID format",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)))
      })
  public SuccessResponse<TransactionResponseDto> getTransactionById(
      @Parameter(description = "ID of the transaction", required = true) @PathVariable(name = "id")
          Long id) {

    TransactionResponseDto transaction = ledgerService.getTransactionById(id);
    return SuccessResponse.ok(transaction);
  }
}
