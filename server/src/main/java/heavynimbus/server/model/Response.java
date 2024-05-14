package heavynimbus.server.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Log4j2
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Response {
	@Min(0)
	private Long delay;

	private HttpStatus status = HttpStatus.OK;

	private Map<@NotNull String, @NotNull List<String>> headers;

	@Valid
	private Body body;
}
