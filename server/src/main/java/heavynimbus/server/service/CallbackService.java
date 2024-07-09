package heavynimbus.server.service;

import heavynimbus.server.model.Callback;
import heavynimbus.server.util.DelayUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Log4j2
@Service
@RequiredArgsConstructor
public class CallbackService {

  private final Map<Callback, RestClient> restClients;

  @Async("callbackExecutor")
  public void handleCallback(Callback callback, long start) {
    log.info(
        "Handling callback to {}: {} {}",
        callback.getDestination(),
        callback.getMethod(),
        callback.getPath());
    RestClient client = restClients.get(callback);
    var headers = headers(callback);

    var bodySpec =
        client
            .method(HttpMethod.valueOf(callback.getMethod().name()))
            .uri(callback.getPath())
            .headers(httpHeaders -> httpHeaders.putAll(headers));

    if (callback.getBody() != null) {
      bodySpec = bodySpec.body(callback.getBody().getBody());
    }

    DelayUtils.delayExactly(start, callback.getDelay());
    String response = bodySpec.retrieve().body(String.class);
    log.info("Callback response: {}", response);
  }

  static Map<String, List<String>> headers(Callback callback) {
    return Optional.ofNullable(callback.getHeaders()).map(Map::entrySet).stream()
        .flatMap(Set::stream)
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> List.of(entry.getValue())));
  }
}
