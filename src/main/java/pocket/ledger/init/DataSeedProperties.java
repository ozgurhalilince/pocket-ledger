package pocket.ledger.init;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.data-seed")
public class DataSeedProperties {
  private boolean enabled = false;
  private int count = 25;
}
