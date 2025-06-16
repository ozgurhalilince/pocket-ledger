package pocket.ledger.dto.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated response wrapper")
public record PageResponseDto<T>(
    @Schema(description = "Page content") List<T> content,
    @Schema(description = "Page number (0-based)") int pageNumber,
    @Schema(description = "Page size") int pageSize,
    @Schema(description = "Total number of elements") long totalElements,
    @Schema(description = "Total number of pages") int totalPages,
    @Schema(description = "Is first page") boolean first,
    @Schema(description = "Is last page") boolean last) {}
