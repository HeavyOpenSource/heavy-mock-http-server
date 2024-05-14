package heavynimbus.server.service;

import heavynimbus.server.model.Callback;
import heavynimbus.server.util.DelayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;

@Log4j2
@Service
@RequiredArgsConstructor
public class CallbackService {

	private final ExecutorService executorService;

	public void registerCallback(Callback callback) {
		executorService.submit(() -> {
			DelayUtils.delay(callback.getDelay());
			sendCallback(callback);
		});
	}

	private void sendCallback(Callback callback) {
		HttpResponse<String> response;
		try (HttpClient client = callback.toHttpClient()) {
			response = client.send(callback.toHttpRequest(), HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException("Something wrong happened during callback", e);
		}
		if (response.statusCode() >= 400) {
			throw new RuntimeException("Callback failed with" + response.statusCode() + " status: " + response.body());
		}
		log.info("Callback sent successfully(" + response.statusCode() + "): " + response.body());
	}
}
