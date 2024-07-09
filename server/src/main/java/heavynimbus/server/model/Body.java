package heavynimbus.server.model;

import jakarta.validation.constraints.AssertTrue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.http.HttpRequest;
import java.util.Optional;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.client.RestClient;

@Data
@NoArgsConstructor
public class Body {
  private String content;
  private String file;

  public Optional<Object> getBody() {
    return Optional.<Object>of(content)
        .or(this::fileOutputStream);
  }

  Optional<OutputStream> fileOutputStream() {
    return Optional.ofNullable(file)
        .map(f -> {
          try {
            return new FileOutputStream(f);
          } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @AssertTrue
  public boolean assertBodyIsStringOrFile() {
    return content != null ^ file != null;
  }

  public HttpRequest.BodyPublisher toBodyPublisher() {
    if (content != null) {
      return HttpRequest.BodyPublishers.ofString(content);
    }
    try {
      return HttpRequest.BodyPublishers.ofFile(new File(file).toPath());
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
