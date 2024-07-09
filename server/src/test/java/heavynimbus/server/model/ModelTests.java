package heavynimbus.server.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Log4j2
@DisplayName("Model validation tests")
public class ModelTests extends ValidationTest {

  @Test
  @DisplayName("Model can be empty")
  public void modelCanBeEmpty() {
    constraintTest(Model.builder().build(), List.of());
  }

  @Test
  @DisplayName("Model can't have null destination list")
  public void assertModelCannotHaveNullDestinationList() {
    constraintTest(
        Model.builder().destinations(null).build(),
        List.of(new PropertyViolation("destinations", "must not be null")));
  }

  @Test
  @DisplayName("Model can't have null endpoint list")
  public void assertModelCannotHaveNullEndpointList() {
    constraintTest(
        Model.builder().endpoints(null).build(),
        List.of(new PropertyViolation("endpoints", "must not be null")));
  }

  @Test
  @DisplayName("Model can't have null destination")
  public void assertModelCannotHaveNullDestination() {
    var destinations = new ArrayList<Destination>();
    destinations.add(null);
    constraintTest(
        Model.builder().destinations(destinations).build(),
        List.of(new PropertyViolation("destinations[0].<list element>", "must not be null")));
  }

  @Test
  @DisplayName("Model can't have null endpoint")
  public void assertModelCannotHaveNullEndpoint() {
    var endpoints = new ArrayList<Endpoint>();
    endpoints.add(null);
    constraintTest(
        Model.builder().endpoints(endpoints).build(),
        List.of(new PropertyViolation("endpoints[0].<list element>", "must not be null")));
  }

  @Nested
  @DisplayName("Destination validation tests")
  class DestinationTests {

    static final Destination DEFAULT_DESTINATION =
        Destination.builder().name("destination").host("localhost").build();

    @Test
    @DisplayName("Destination must have a name")
    public void assertDestinationMustHaveName() {
      constraintTest(
          Destination.builder().name(null).host("localhost").build(),
          List.of(new PropertyViolation<>("name", "must not be null or blank")));
    }

    @Test
    @DisplayName("Destination can't have a blank name")
    public void assertDestinationCannotHaveBlankName() {
      constraintTest(
          Destination.builder().name("  ").host("localhost").build(),
          List.of(new PropertyViolation<>("name", "must not be null or blank")));
    }

    @Test
    @DisplayName("Destination can't have null host")
    public void assertDestinationCannotHaveNullHost() {
      constraintTest(
          Destination.builder().name("destination").host(null).build(),
          List.of(new PropertyViolation<>("host", "must not be null or blank")));
    }

    @Test
    @DisplayName("Destination can't have a blank host")
    public void assertDestinationCannotHaveBlankHost() {
      constraintTest(
          Destination.builder().name("destination").host("  ").build(),
          List.of(new PropertyViolation<>("host", "must not be null or blank")));
    }

    @Test
    @DisplayName("Destination can't have a null protocol")
    public void assertDestinationCannotHaveNullProtocol() {
      constraintTest(
          Destination.builder().name("destination").host("localhost").protocol(null).build(),
          List.of(new PropertyViolation<>("protocol", "must not be null")));
    }

    @Test
    @DisplayName("Destination protocol defaults to HTTP")
    public void assertDestinationProtocolDefaultsToHTTP() {
      assertEquals(Destination.Protocol.HTTP, DEFAULT_DESTINATION.getProtocol());
    }

    @ValueSource(ints = {-80, -443, 0})
    @DisplayName("Destination can't have negative port")
    @ParameterizedTest(name = "Destination can't have negative port {port}")
    public void assertDestinationCannotHaveNegativePort(int port) {
      constraintTest(
          Destination.builder().name("destination").host("localhost").port(port).build(),
          List.of(new PropertyViolation<>("port", "must be greater than 0")));
    }

    @ValueSource(ints = {65536, 70000})
    @DisplayName("Destination can't have port greater than 65535")
    @ParameterizedTest(name = "Destination can't have port greater than 65535: {0}")
    public void assertDestinationCannotHavePortGreaterThan65535(int port) {
      constraintTest(
          Destination.builder().name("destination").host("localhost").port(port).build(),
          List.of(new PropertyViolation<>("port", "must be less than or equal to 65535")));
    }

    @Test
    @DisplayName("Destination port defaults to 80")
    public void assertDestinationPortDefaultsTo80() {
      assertEquals(80, DEFAULT_DESTINATION.getPort());
    }

    @Test
    @DisplayName("Destination can't have null basePath")
    public void assertDestinationCannotHaveNullBasePath() {
      constraintTest(
          Destination.builder().name("destination").host("localhost").basePath(null).build(),
          List.of(new PropertyViolation<>("basePath", "must not be null")));
    }

    @Test
    @DisplayName("Destination basePath defaults to empty string")
    public void assertDestinationBasePathDefaultsToEmptyString() {
      assertEquals("", DEFAULT_DESTINATION.getBasePath());
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, -100})
    @DisplayName("Destination can't have negative delay")
    public void assertDestinationCannotHaveNegativeDelay(long delay) {
      constraintTest(
          Destination.builder().name("destination").host("localhost").delay(delay).build(),
          List.of(new PropertyViolation<>("delay", "must be greater than or equal to 0")));
    }

    @Test
    @DisplayName("Destination delay defaults to 0")
    public void assertDestinationDelayDefaultsToZero() {
      assertEquals(0L, DEFAULT_DESTINATION.getDelay());
    }
  }
}
