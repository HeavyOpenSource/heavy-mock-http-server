package heavynimbus.server.model;

import heavynimbus.server.validation.ContentOrFile;
import heavynimbus.server.validation.FileExists;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@ContentOrFile
@NoArgsConstructor
@AllArgsConstructor
public class Body {
  private String content;

  @FileExists private String file;

  public Optional<Object> getBody() {
    return Optional.<Object>of(content).or(this::fileOutputStream);
  }

  Optional<OutputStream> fileOutputStream() {
    return Optional.ofNullable(file)
        .map(
            f -> {
              try {
                return new FileOutputStream(f);
              } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
              }
            });
  }
}
