package heavynimbus.server.configuration;

import heavynimbus.server.configuration.properties.MockConfigurationProperties;
import heavynimbus.server.model.Model;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Set;

@Log4j2
@Configuration
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
}
