package heavynimbus.server.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class QueryParser {
  public static MultiValueMap<String, String> parse(String query) {
    MultiValueMap<String, String> queryMap = new LinkedMultiValueMap<>();
    if (query == null) return queryMap;

    String[] pairs = query.split("&");
    for (String pair : pairs) {
      String[] keyValue = pair.split("=");
      queryMap.add(keyValue[0], keyValue[1]);
    }
    return queryMap;
  }
}
