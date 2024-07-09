package heavynimbus.server.integration;

import java.net.URI;

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
