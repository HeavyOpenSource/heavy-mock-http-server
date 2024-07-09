package heavynimbus.server.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Model {

  @Builder.Default
  @NotNull(message = "must not be null")
  private List<@Valid @NotNull(message = "must not be null") Destination> destinations = List.of();

  @Builder.Default
  @NotNull(message = "must not be null")
  private List<@Valid @NotNull(message = "must not be null") Endpoint> endpoints = List.of();
}
