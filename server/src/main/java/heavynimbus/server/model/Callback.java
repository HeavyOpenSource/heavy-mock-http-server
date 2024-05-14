package heavynimbus.server.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
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
	private HttpClient.Version httpVersion = HttpClient.Version.HTTP_1_1;

	@NotNull
	private HttpClient.Redirect redirect = HttpClient.Redirect.NORMAL;

	@NotNull
	private Duration connectTimeout = Duration.ofSeconds(10);

	@Valid
	private Proxy proxy;

	public HttpRequest toHttpRequest() {
		HttpRequest.Builder builder = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.method(method.name(), body == null ? HttpRequest.BodyPublishers.noBody() : body.toBodyPublisher())
				.version(httpVersion)
				.timeout(connectTimeout);
		if (headers != null) {
			headers.forEach(builder::header);
		}
		return builder.build();
	}

	public HttpClient toHttpClient() {
		HttpClient.Builder builder = HttpClient.newBuilder()
				.version(httpVersion)
				.followRedirects(redirect)
				.connectTimeout(connectTimeout);
		if (proxy != null) {
			builder.proxy(ProxySelector.of(proxy.toInetSocketAddress()));
		}
		return builder.build();
	}

	public record Proxy(
			@NotBlank String host,
			@Min(1) @Max(65535) int port) {
		public InetSocketAddress toInetSocketAddress() {
			return new InetSocketAddress(host, port);
		}
	}
}
