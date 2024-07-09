package heavynimbus.server.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Callback {
  @NotBlank private String destination;

  @NotBlank private String path;

  @NotNull private HttpMethod method = HttpMethod.GET;

  @Valid private Body body;

  private Map<@NotNull String, String> headers;

  @Min(0)
  private long delay = 0;
}
