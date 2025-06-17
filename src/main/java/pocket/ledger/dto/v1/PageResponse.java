package pocket.ledger.dto.v1;

import java.util.List;

public record PageResponse<T>(
    List<T> data,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last) {}
