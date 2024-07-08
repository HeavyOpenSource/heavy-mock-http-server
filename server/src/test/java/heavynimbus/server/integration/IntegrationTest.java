package heavynimbus.server.integration;

import heavynimbus.server.configuration.properties.MockConfigurationProperties;
import java.net.URI;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@AutoConfigureMockMvc
@EnableConfigurationProperties(MockConfigurationProperties.class)
public interface IntegrationTest {
  default URI getHelloUri() {
    return getHelloUri(null);
  }

  default URI getHelloUri(String name) {
    try {
      return new URI("/hello/" + (name == null ? "" : name));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  default URI getNotFoundUri() {
    try {
      return new URI("/not-found");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
