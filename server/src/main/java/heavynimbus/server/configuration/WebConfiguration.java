package heavynimbus.server.configuration;

import heavynimbus.server.configuration.properties.MockConfigurationProperties;
import heavynimbus.server.model.Callback;
import heavynimbus.server.model.Endpoint;
import heavynimbus.server.model.Model;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log4j2
@Getter
@Configuration
@EnableConfigurationProperties({MockConfigurationProperties.class})
public class WebConfiguration {

	private final Map<Callback, RestClient> restClients;

	public WebConfiguration(Model model) {
		restClients = model.getEndpoints()
				.stream()
				.map(Endpoint::getCallbacks)
				.filter(Objects::nonNull)
				.filter(Predicate.not(Collection::isEmpty))
				.flatMap(Collection::stream)
				.distinct()
				.collect(Collectors.toMap(Function.identity(), this::createRestClient));
	}

	private RestClient createRestClient(Callback callback) {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectTimeout(callback.getConnectTimeout());
		return RestClient.builder()
				.defaultStatusHandler(HttpStatusCode::isError, WebConfiguration::handleErrorStatus)
				.defaultStatusHandler(Predicate.not(HttpStatusCode::isError), WebConfiguration::handleSuccessStatus)
				.defaultHeaders(headers -> callback.getHeaders().forEach(headers::set))
				.requestFactory(factory)
				.build();
	}

	private static void handleErrorStatus(HttpRequest req, ClientHttpResponse res) throws IOException {
		log.error("Callback {} {} failed with status {}", req.getMethod(), req.getURI(), res.getStatusCode());
	}

	private static void handleSuccessStatus(HttpRequest req, ClientHttpResponse res) throws IOException {
		log.info("Callback {} {} succeeded with status {}", req.getMethod(), req.getURI(), res.getStatusCode());
	}
}
