package heavynimbus.server.model;

import heavynimbus.server.validation.ContentOrFile;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Body validation tests")
public class BodyValidationTests extends ValidationTest {

  @Nested
  @DisplayName("Content or file validation tests")
  class ContentOrFileValidationTests {

    @Test
    @DisplayName("Body cannot have null content and file")
    void bodyCannotHaveNullContentAndFile() {
      constraintTest(
          Body.builder().build(),
          List.of(new PropertyViolation<>("", ContentOrFile.DEFAULT_MESSAGE)));
    }

    @Test
    @DisplayName("Body cannot have content and file")
    void bodyCannotHaveContentAndFile() {
      constraintTest(
          Body.builder().file("pom.xml").content("content").build(),
          List.of(new ContentOrFilePropertyViolation<>()));
    }

    @Test
    @DisplayName("Body with only content")
    void bodyWithOnlyContent() {
      emptyConstraintTest(Body.builder().content("Hello World !").build());
    }

    @Test
    @DisplayName("Body with only file")
    void bodyWithOnlyFile() {
      emptyConstraintTest(Body.builder().file("pom.xml").build());
    }
  }

  @Test
  @DisplayName("Body cannot have non-existing file")
  void bodyCannotHaveNonExistingFile() {
    constraintTest(
        Body.builder().file("non-existing-file").build(),
        List.of(new FileExistsPropertyViolation<>("file")));
  }
}
