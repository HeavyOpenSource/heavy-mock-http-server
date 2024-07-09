package heavynimbus.server.model;

import static heavynimbus.server.validation.ValidationConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import heavynimbus.server.validation.ContentOrFile;
import heavynimbus.server.validation.FileExists;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;

@Log4j2
public abstract class ValidationTest {
  protected final Validator validator;

  public ValidationTest() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  static <T> void display(Set<ConstraintViolation<T>> violations) {
    if (violations.isEmpty()) {
      log.info("No violations found");
      return;
    }
    violations.stream()
        .map(v -> String.format("%s: %s", v.getPropertyPath(), v.getMessage()))
        .forEach(log::info);
  }

  protected sealed interface ExpectedViolation<T> permits PropertyViolation, TypeViolation {
    boolean matches(ConstraintViolation<T> violation);

    boolean violationMatches(ConstraintViolation<T> violation);
  }

  @ToString
  @AllArgsConstructor
  protected static sealed class TypeViolation<T> implements ExpectedViolation<T> {
    private String message;

    @Override
    public boolean violationMatches(ConstraintViolation<T> violation) {
      return message.equals(violation.getMessage());
    }

    @Override
    public boolean matches(ConstraintViolation<T> violation) {
      return true;
    }
  }

  @ToString
  @AllArgsConstructor
  protected static sealed class PropertyViolation<T> implements ExpectedViolation<T> {
    private String property;
    private String message;

    @Override
    public boolean violationMatches(ConstraintViolation<T> violation) {
      return property.equals(violation.getPropertyPath().toString())
          && message.equals(violation.getMessage());
    }

    @Override
    public boolean matches(ConstraintViolation<T> violation) {
      return property.equals(violation.getPropertyPath().toString());
    }
  }

  protected static final class UniqueElementsPropertyViolation<T> extends PropertyViolation<T> {
    public UniqueElementsPropertyViolation(String property) {
      super(property, UNIQUE_ELEMENTS_VALIDATION_MESSAGE);
    }
  }

  protected static final class NotNullPropertyViolation<T> extends PropertyViolation<T> {
    public NotNullPropertyViolation(String property) {
      super(property, NOT_NULL_VALIDATION_MESSAGE);
    }
  }

  protected static final class NotEmptyPropertyViolation<T> extends PropertyViolation<T> {
    public NotEmptyPropertyViolation(String property) {
      super(property, NOT_EMPTY_VALIDATION_MESSAGE);
    }
  }

  protected static final class NotBlankPropertyViolation<T> extends PropertyViolation<T> {
    public NotBlankPropertyViolation(String property) {
      super(property, NOT_BLANK_VALIDATION_MESSAGE);
    }
  }

  protected static final class FileExistsPropertyViolation<T> extends PropertyViolation<T> {
    public FileExistsPropertyViolation(String property) {
      super(property, FileExists.DEFAULT_MESSAGE);
    }
  }

  protected static final class ContentOrFilePropertyViolation<T> extends TypeViolation<T> {
    public ContentOrFilePropertyViolation() {
      super(ContentOrFile.DEFAULT_MESSAGE);
    }
  }

  <T> void emptyConstraintTest(T object) {
    Set<ConstraintViolation<T>> violations = validator.validate(object);
    display(violations);
    assertEquals(0, violations.size(), "Expected no violations");
  }

  <T> void constraintTest(T object, List<ExpectedViolation<T>> expectedViolations) {
    log.info("Validating: {}", object);
    Set<ConstraintViolation<T>> violations = validator.validate(object);
    display(violations);
    assertEquals(
        expectedViolations.size(),
        violations.size(),
        "Number of violations does not match expected number");

    for (ExpectedViolation<T> expectedViolation : expectedViolations) {
      Optional<ConstraintViolation<T>> violation =
          violations.stream()
              .filter(expectedViolation::matches)
              .filter(expectedViolation::violationMatches)
              .findFirst();
      if (violation.isEmpty()) {
        Assertions.fail("Expected violation not found " + expectedViolation);
      }
      violations.remove(violation.get());
    }
  }
}
