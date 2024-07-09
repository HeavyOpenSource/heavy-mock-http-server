package heavynimbus.server.model;

import jakarta.validation.constraints.*;

import java.net.URI;
import java.util.Map;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Destination {
  @NotBlank(message = "must not be null or blank")
  private String name;

  @Builder.Default
  @NotNull(message = "must not be null")
  private Protocol protocol = Protocol.HTTP;

  @NotBlank(message = "must not be null or blank")
  private String host;

  @Builder.Default
  @Positive(message = "must be greater than 0")
  @Max(value = 65535, message = "must be less than or equal to 65535")
  private int port = 80;

  @Builder.Default
  @NotNull(message = "must not be null")
  private String basePath = "";

  @Builder.Default
  @PositiveOrZero(message = "must be greater than or equal to 0")
  private long delay = 0L;

  @Builder.Default private Map<@NotNull String, String> headers = Map.of();

  public enum Protocol {
    HTTP,
    HTTPS
  }

  public URI getUri() {
    return URI.create(protocol + "://" + host + ":" + port + basePath);
  }
}
