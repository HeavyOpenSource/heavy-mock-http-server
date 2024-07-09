package heavynimbus.server.configuration;

import heavynimbus.server.configuration.properties.MockConfigurationProperties;
import heavynimbus.server.model.Callback;
import heavynimbus.server.model.Destination;
import heavynimbus.server.model.Endpoint;
import heavynimbus.server.model.Model;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

@Log4j2
@Configuration
@EnableConfigurationProperties({MockConfigurationProperties.class})
public class MockConfiguration {

  @Bean
  public Yaml yaml() {
    log.info("Creating YAML parser");
    LoaderOptions loaderOptions = new LoaderOptions();
    loaderOptions.setAllowDuplicateKeys(false);
    loaderOptions.setEnumCaseSensitive(false);

    Constructor constructor = new Constructor(Model.class, loaderOptions);
    constructor.getPropertyUtils().setSkipMissingProperties(false);

    Representer representer = new Representer(new DumperOptions());
    representer.getPropertyUtils().setSkipMissingProperties(true);

    return new Yaml(constructor, representer);
  }

  @Bean
  public Model model(Yaml yaml, MockConfigurationProperties properties, Validator validator)
      throws FileNotFoundException {
    log.info("Loading configuration from: {}", properties.getConfig());
    Model model = yaml.loadAs(new FileInputStream(properties.getConfig()), Model.class);
    Set<ConstraintViolation<Model>> constraintViolations = validator.validate(model);

    if (!constraintViolations.isEmpty()) {
      log.error("Invalid configuration: {}", constraintViolations);
      throw new ConstraintViolationException(constraintViolations);
    }

    log.info("Configuration loaded successfully");
    return model;
  }

  @Bean
  public Map<Callback, RestClient> restClients(Model model) {

    var nameToWebClient =
        model.getDestinations().stream()
            .collect(Collectors.toMap(Destination::getName, MockConfiguration::createRestClient));

    List<Callback> callbacks =
        model.getEndpoints().stream()
            .map(Endpoint::getCallbacks)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .distinct()
            .toList();

    Map<Callback, RestClient> webClients = new HashMap<>();
    for (Callback callback : callbacks) {
      var client =
          Optional.ofNullable(nameToWebClient.get(callback.getDestination()))
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(
                          "Invalid destination: " + callback.getDestination()));
      webClients.put(callback, client);
    }
    return webClients;
  }

  static RestClient createRestClient(Destination destination) {
    log.info(
        "Creating RestClient for destination: {}://{}:{}{}",
        destination,
        destination.getHost(),
        destination.getPort(),
        destination.getBasePath());

    RestClient.Builder builder = RestClient.builder().baseUrl(destination.getUri().toString());

    Optional.ofNullable(destination.getHeaders())
        .orElseGet(HashMap::new)
        .forEach((key, value) -> builder.defaultHeader(key, value.toArray(new String[0])));

    return builder.build();
  }
}
