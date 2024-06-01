package heavynimbus.server.service;

import heavynimbus.server.model.Callback;
import heavynimbus.server.util.DelayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Log4j2
@Service
@RequiredArgsConstructor
public class CallbackService {

//	private final RestClient restClient;
/*
	public void handleCallbacks(List<Callback> callbacks, long start) {
		List<CompletableFuture<?>> syncCallbacks = new ArrayList<>();
		for (Callback callback : callbacks) {
			if (callback.isAsync()) {
				syncCallbacks.add(sendCallbackAsync(callback));
				continue;
			}
			sendCallback(callback);
		}
	}

	public void registerCallback(Callback callback) {
		log.info("Registering callback: " + callback);
		if (callback.isAsync()) {
			sendCallbackAsync(callback);
			return;
		}
		sendCallback(callback);
	}

	@Async
	protected CompletableFuture<Void> sendCallbackAsync(Callback callback) {
		log.info("Sending callback asynchronously: " + callback);
		sendCallback(callback);
		return CompletableFuture.completedFuture(null);
	}
 */

	/*private void sendCallback(Callback callback) {
		DelayUtils.delay(callback.getDelay());
		callback.getHeaders();

		restClient.method(HttpMethod.valueOf(callback.getMethod().name()))
				.uri(callback.getUrl())
				.headers(headers -> callback.getHeaders().forEach(headers::set))
				.body(callback.getBody())
				.retrieve()
				.onStatus(HttpStatusCode::isError, (req, res) -> {
					log.error("Callback {} {} failed with status {}", req.getMethod(), req.getURI(), res.getStatusCode());
				});
	}*/

}
