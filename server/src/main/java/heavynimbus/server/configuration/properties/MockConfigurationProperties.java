package heavynimbus.server.configuration.properties;

import java.io.File;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "heavy.mock")
public class MockConfigurationProperties {
  private File config;
}
