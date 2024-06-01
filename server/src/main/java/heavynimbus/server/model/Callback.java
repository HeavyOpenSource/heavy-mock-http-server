package heavynimbus.server.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Map;

@Data
@NoArgsConstructor
public class Callback {
	@Min(0)
	private long delay = 0;

	@NotNull
	private boolean async;

	@NotBlank
	private String url;

	@NotNull
	private HttpMethod method = HttpMethod.GET;

	@Valid
	private Body body;

	private Map<@NotNull String, String> headers;

	@NotNull
	private Duration connectTimeout = Duration.ofSeconds(10);
}
