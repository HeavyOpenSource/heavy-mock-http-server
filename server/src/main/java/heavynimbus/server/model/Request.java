package heavynimbus.server.model;

import heavynimbus.server.util.QueryParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.MultiValueMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Request {
  @NotNull private List<@NotNull HttpMethod> methods;

  @NotNull private List<String> paths;

  private Map<@NotNull String, @NotNull List<String>> headers;

  private Map<@NotNull String, @NotNull List<String>> query;

  public boolean supports(HttpServletRequest request) {
    boolean anyMethodMatches =
        methods.stream()
            .map(HttpMethod::name)
            .anyMatch(method -> method.equals(request.getMethod()));
    if (!anyMethodMatches) return false;

    boolean anyPathMatches = paths.stream().anyMatch(path -> request.getRequestURI().matches(path));
    if (!anyPathMatches) return false;

    if (headers != null) {
      for (String key : headers.keySet()) {
        List<String> expectedHeaderValues = headers.get(key);
        List<String> requestHeaderValues = List.of(request.getHeader(key));
        if (requestHeaderValues.size() < expectedHeaderValues.size()) return false;
        if (!new HashSet<>(requestHeaderValues).containsAll(expectedHeaderValues)) return false;
      }
    }

    if (query == null) {
      return true;
    }

    String requestQueryString = request.getQueryString();
    MultiValueMap<String, String> requestQueryMap = QueryParser.parse(requestQueryString);
    Set<String> requestQueryKeys = requestQueryMap.keySet();
    if (!query.keySet().containsAll(requestQueryKeys)) return false;

    for (String key : query.keySet()) {
      List<String> expectedQueryValues = query.get(key);
      List<String> requestQueryValues = requestQueryMap.get(key);
      if (requestQueryValues.size() < expectedQueryValues.size()) return false;
      if (!new HashSet<>(requestQueryValues).containsAll(expectedQueryValues)) return false;
    }

    return true;
  }
}
