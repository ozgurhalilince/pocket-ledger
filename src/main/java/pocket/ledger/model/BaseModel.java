package pocket.ledger.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class BaseModel {

  private LocalDateTime createdDate;
  private LocalDateTime lastModifiedDate;

  public BaseModel() {
    this.createdDate = LocalDateTime.now();
    this.lastModifiedDate = LocalDateTime.now();
  }
}
