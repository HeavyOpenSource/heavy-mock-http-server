package heavynimbus.server.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@Data
@ConfigurationProperties(prefix = "heavy.mock")
public class MockConfigurationProperties {
	private File config;
}
