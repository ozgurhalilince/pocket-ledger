package pocket.ledger.dto.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SuccessResponse<T>(String message, boolean status, LocalDateTime timestamp, T data) {

  public static <T> SuccessResponse<T> ok(T data) {
    return new SuccessResponse<T>("OK", true, LocalDateTime.now(), data);
  }

  public static <T> SuccessResponse<T> ok(T data, String message) {
    return new SuccessResponse<T>(message, true, LocalDateTime.now(), data);
  }
}
