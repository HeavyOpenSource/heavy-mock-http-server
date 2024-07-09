package heavynimbus.server.model;

import static heavynimbus.server.validation.ValidationConstants.*;

import heavynimbus.server.util.QueryParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.util.MultiValueMap;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {
  @Builder.Default
  @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
  @Size(min = 1, message = NOT_EMPTY_VALIDATION_MESSAGE)
  @UniqueElements(message = UNIQUE_ELEMENTS_VALIDATION_MESSAGE)
  private List<@NotNull(message = NOT_NULL_VALIDATION_MESSAGE) HttpMethod> methods =
      List.of(HttpMethod.values());

  @Builder.Default
  @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
  @Size(min = 1, message = NOT_EMPTY_VALIDATION_MESSAGE)
  @UniqueElements(message = UNIQUE_ELEMENTS_VALIDATION_MESSAGE)
  private List<@NotBlank(message = NOT_BLANK_VALIDATION_MESSAGE) String> paths = List.of("^/?.*$");

  @Builder.Default
  @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
  private Map<
          @NotBlank(message = NOT_BLANK_VALIDATION_MESSAGE) String,
          @NotNull(message = NOT_NULL_VALIDATION_MESSAGE) List<
              @NotBlank(message = NOT_BLANK_VALIDATION_MESSAGE) String>>
      headers = Map.of();

  @Builder.Default
  @NotNull(message = NOT_NULL_VALIDATION_MESSAGE)
  private Map<
          @NotBlank(message = NOT_BLANK_VALIDATION_MESSAGE) String,
          @NotNull(message = NOT_NULL_VALIDATION_MESSAGE) List<
              @NotBlank(message = NOT_BLANK_VALIDATION_MESSAGE) String>>
      query = Map.of();

  public boolean supports(HttpServletRequest request) {
    boolean anyMethodMatches =
        methods.stream()
            .map(HttpMethod::name)
            .anyMatch(method -> method.equals(request.getMethod()));
    if (!anyMethodMatches) return false;

    boolean anyPathMatches = paths.stream().anyMatch(path -> request.getRequestURI().matches(path));
    if (!anyPathMatches) return false;

    if (!headers.isEmpty()) {
      for (String key : headers.keySet()) {
        List<String> expectedHeaderValues = headers.get(key);
        List<String> requestHeaderValues = List.of(request.getHeader(key));
        if (requestHeaderValues.size() < expectedHeaderValues.size()) return false;
        if (!new HashSet<>(requestHeaderValues).containsAll(expectedHeaderValues)) return false;
      }
    }

    if (query.isEmpty()) {
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
