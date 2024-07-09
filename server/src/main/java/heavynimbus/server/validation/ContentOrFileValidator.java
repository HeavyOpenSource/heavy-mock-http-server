package heavynimbus.server.validation;

import heavynimbus.server.model.Body;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContentOrFileValidator implements ConstraintValidator<ContentOrFile, Body> {
  @Override
  public boolean isValid(Body value, ConstraintValidatorContext context) {
    if (value.getContent() == null) {
      return value.getFile() != null;
    }
    return value.getFile() == null;
  }
}
