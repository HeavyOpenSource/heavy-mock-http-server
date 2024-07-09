package heavynimbus.server.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.File;

public class FileExistsValidator implements ConstraintValidator<FileExists, String> {
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) return true;
    File file = new File(value);
    return file.exists() && file.isFile();
  }
}
