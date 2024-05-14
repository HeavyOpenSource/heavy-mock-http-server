package heavynimbus.server.model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endpoint {

	private String name;

	@Valid
	private Request request;

	@Valid
	private Response response;

	private List<@Valid @NotNull Callback> callbacks;

	public boolean supports(HttpServletRequest httpServletRequest) {
		if (request == null) return true;
		return request.supports(httpServletRequest);
	}
}
