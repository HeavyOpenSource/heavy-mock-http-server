package heavynimbus.server.model;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.mock.web.MockHttpServletRequest;

@DisplayName("Request validation tests")
public class RequestValidationTests extends ValidationTest {

  @Nested
  @DisplayName("Request methods validation tests")
  class RequestMethodsValidationTests {
    static final String METHODS_PROPERTY = "methods";
    static final String METHODS_FIRST_ELEMENT = "methods[0].<list element>";

    @Test
    @DisplayName("Request methods cannot be null")
    void requestMethodCannotBeNull() {
      constraintTest(
          Request.builder().methods(null).build(),
          List.of(new NotNullPropertyViolation<>(METHODS_PROPERTY)));
    }

    @Test
    @DisplayName("Request methods cannot be empty")
    void requestMethodCannotBeEmpty() {
      constraintTest(
          Request.builder().methods(List.of()).build(),
          List.of(new NotEmptyPropertyViolation<>(METHODS_PROPERTY)));
    }

    @Test
    @DisplayName("Request methods cannot have null elements")
    void requestMethodCannotHaveNullElements() {
      var methods = new ArrayList<HttpMethod>();
      methods.add(null);
      constraintTest(
          Request.builder().methods(methods).build(),
          List.of(new NotNullPropertyViolation<>(METHODS_FIRST_ELEMENT)));
    }

    @Test
    @DisplayName("Request methods cannot have duplicate elements")
    void requestMethodCannotHaveDuplicateElements() {
      var methods = List.of(HttpMethod.GET, HttpMethod.GET);
      constraintTest(
          Request.builder().methods(methods).build(),
          List.of(new UniqueElementsPropertyViolation<>(METHODS_PROPERTY)));
    }

    @Test
    @DisplayName("Request methods can have unique elements")
    void requestMethodCanHaveUniqueElements() {
      var methods = List.of(HttpMethod.GET, HttpMethod.POST);
      emptyConstraintTest(Request.builder().methods(methods).build());
    }

    @Test
    @DisplayName("Request methods default value is all methods")
    void requestMethodDefaultValueIsAllMethods() {
      var req = Request.builder().build();
      emptyConstraintTest(req);
      Assertions.assertEquals(Set.of(HttpMethod.values()), Set.of(req.getMethods().toArray()));
    }
  }

  @Nested
  @DisplayName("Request paths validation tests")
  class RequestPathsValidationTests {
    static final String PATHS_PROPERTY = "paths";
    static final String PATHS_FIRST_ELEMENT = "paths[0].<list element>";

    @Test
    @DisplayName("Request paths cannot be null")
    void requestPathCannotBeNull() {
      constraintTest(
          Request.builder().paths(null).build(),
          List.of(new NotNullPropertyViolation<>(PATHS_PROPERTY)));
    }

    @Test
    @DisplayName("Request paths cannot be empty")
    void requestPathCannotBeEmpty() {
      constraintTest(
          Request.builder().paths(List.of()).build(),
          List.of(new NotEmptyPropertyViolation<>(PATHS_PROPERTY)));
    }

    @Test
    @DisplayName("Request paths cannot have null elements")
    void requestPathCannotHaveNullElements() {
      var paths = new ArrayList<String>();
      paths.add(null);
      constraintTest(
          Request.builder().paths(paths).build(),
          List.of(new NotBlankPropertyViolation<>(PATHS_FIRST_ELEMENT)));
    }

    @Test
    @DisplayName("Request paths cannot have duplicate elements")
    void requestPathCannotHaveDuplicateElements() {
      var paths = List.of("/path", "/path");
      constraintTest(
          Request.builder().paths(paths).build(),
          List.of(new UniqueElementsPropertyViolation<>(PATHS_PROPERTY)));
    }

    @Test
    @DisplayName("Request paths can have unique elements")
    void requestPathCanHaveUniqueElements() {
      var paths = List.of("/path1", "/path2");
      emptyConstraintTest(Request.builder().paths(paths).build());
    }

    @Test
    @DisplayName("Request paths default value is all paths")
    void requestPathDefaultValueIsAllPaths() {
      var req = Request.builder().build();
      emptyConstraintTest(req);
      Assertions.assertEquals(List.of("^/?.*$"), req.getPaths());
    }
  }

  @Nested
  @DisplayName("Request headers validation tests")
  class RequestHeadersValidationTests {
    static final String HEADERS_PROPERTY = "headers";

    @Test
    @DisplayName("Request headers cannot be null")
    void requestHeadersCannotBeNull() {
      constraintTest(
          Request.builder().headers(null).build(),
          List.of(new NotNullPropertyViolation<>(HEADERS_PROPERTY)));
    }

    @Test
    @DisplayName("Request headers can be empty")
    void requestHeadersCanBeEmpty() {
      emptyConstraintTest(Request.builder().headers(Map.of()).build());
    }

    @Test
    @DisplayName("Request headers key cannot be null or blank")
    void requestHeadersKeyCannotBeNullOrBlank() {
      var headers = Map.of("   ", List.of("value"));
      constraintTest(
          Request.builder().headers(headers).build(),
          List.of(new NotBlankPropertyViolation<>("headers<K>[   ].<map key>")));
    }

    @Test
    @DisplayName("Request headers value cannot be null or blank")
    void requestHeadersValueCannotBeNullOrBlank() {
      var headers = Map.of("key", List.of("   "));
      constraintTest(
          Request.builder().headers(headers).build(),
          List.of(new NotBlankPropertyViolation<>("headers[key].<map value>[0].<list element>")));
    }

    @Test
    @DisplayName("Request headers value can be empty")
    void requestHeadersValueCanBeEmpty() {
      var headers = Map.<String, List<String>>of("key", List.of());
      emptyConstraintTest(Request.builder().headers(headers).build());
    }

    @Test
    @DisplayName("Request headers value can have unique elements")
    void requestHeadersValueCanHaveUniqueElements() {
      var headers = Map.of("key", List.of("value1", "value2"));
      emptyConstraintTest(Request.builder().headers(headers).build());
    }

    @Test
    @DisplayName("Request headers can have unique keys")
    void requestHeadersCanHaveUniqueKeys() {
      var headers = Map.of("key1", List.of("value1"), "key2", List.of("value2"));
      emptyConstraintTest(Request.builder().headers(headers).build());
    }

    @Test
    @DisplayName("Request headers default value is empty")
    void requestHeadersDefaultValueIsEmpty() {
      var req = Request.builder().build();
      emptyConstraintTest(req);
      Assertions.assertEquals(Map.of(), req.getHeaders());
    }
  }

  @Nested
  @DisplayName("Request query validation tests")
  class RequestQueryValidationTests {
    static final String QUERY_PROPERTY = "query";

    @Test
    @DisplayName("Request query cannot be null")
    void requestQueryCannotBeNull() {
      constraintTest(
          Request.builder().query(null).build(),
          List.of(new NotNullPropertyViolation<>(QUERY_PROPERTY)));
    }

    @Test
    @DisplayName("Request query can be empty")
    void requestQueryCanBeEmpty() {
      emptyConstraintTest(Request.builder().query(Map.of()).build());
    }

    @Test
    @DisplayName("Request query key cannot be null or blank")
    void requestQueryKeyCannotBeNullOrBlank() {
      var query = Map.of("   ", List.of("value"));
      constraintTest(
          Request.builder().query(query).build(),
          List.of(new NotBlankPropertyViolation<>("query<K>[   ].<map key>")));
    }

    @Test
    @DisplayName("Request query value cannot be null or blank")
    void requestQueryValueCannotBeNullOrBlank() {
      var query = Map.of("key", List.of("   "));
      constraintTest(
          Request.builder().query(query).build(),
          List.of(new NotBlankPropertyViolation<>("query[key].<map value>[0].<list element>")));
    }

    @Test
    @DisplayName("Request query value can be empty")
    void requestQueryValueCanBeEmpty() {
      var query = Map.<String, List<String>>of("key", List.of());
      emptyConstraintTest(Request.builder().query(query).build());
    }

    @Test
    @DisplayName("Request query value can have unique elements")
    void requestQueryValueCanHaveUniqueElements() {
      var query = Map.of("key", List.of("value1", "value2"));
      emptyConstraintTest(Request.builder().query(query).build());
    }

    @Test
    @DisplayName("Request query can have unique keys")
    void requestQueryCanHaveUniqueKeys() {
      var query = Map.of("key1", List.of("value1"), "key2", List.of("value2"));
      emptyConstraintTest(Request.builder().query(query).build());
    }

    @Test
    @DisplayName("Request query default value is empty")
    void requestQueryDefaultValueIsNull() {
      var req = Request.builder().build();
      emptyConstraintTest(req);
      Assertions.assertEquals(Map.of(), req.getQuery());
    }
  }

  @Nested
  @DisplayName("Request supports validation tests")
  class RequestSupportsValidationTests {

    public static HttpServletRequest mockedRequest(
        String method, String path, Map<String, String> headers, Map<String, String> query) {
      var request = new MockHttpServletRequest(method, path);
      headers.forEach(request::addHeader);
      query.forEach(request::addParameter);
      return request;
    }

    public static HttpServletRequest mockedRequest(
        String method, String path, Map<String, String> headers) {
      return mockedRequest(method, path, headers, Map.of());
    }

    public static HttpServletRequest mockedRequest(String method, String path) {
      return mockedRequest(method, path, Map.of(), Map.of());
    }

    public static Stream<Arguments> paramsThatSupportRequest() {
      return Stream.of(
          Arguments.of(mockedRequest("GET", "/path")),
          Arguments.of(mockedRequest("POST", "/path")),
          Arguments.of(mockedRequest("GET", "/path/1")),
          Arguments.of(mockedRequest("POST", "/path/1")),
          Arguments.of(mockedRequest("PUT", "/path/1")),
          Arguments.of(mockedRequest("DELETE", "/path/1")),
          Arguments.of(mockedRequest("GET", "/path/1", Map.of("key", "value"))),
          Arguments.of(
              mockedRequest("GET", "/path/1", Map.of("key", "value"), Map.of("key", "value"))),
          Arguments.of(
              mockedRequest(
                  "GET",
                  "/path/1",
                  Map.of("key", "value"),
                  Map.of("key", "value", "key2", "value2"))));
    }

    @ParameterizedTest
    @MethodSource("paramsThatSupportRequest")
    @DisplayName("Request supports all requests by default")
    void requestSupportsAllRequestsByDefault(MockHttpServletRequest request) {
      Assertions.assertTrue(Request.builder().build().supports(request));
    }

    @Test
    @DisplayName("Request supports specific methods")
    void requestSupportsSpecificMethods() {
      var req = Request.builder().methods(List.of(HttpMethod.GET)).build();
      Assertions.assertTrue(req.supports(mockedRequest("GET", "/path")));
    }

    @Test
    @DisplayName("Request not matching method is not supported")
    void requestNotMatchingMethod() {
      var req = Request.builder().methods(List.of(HttpMethod.GET)).build();
      Assertions.assertFalse(req.supports(mockedRequest("POST", "/path")));
    }

    public static Stream<Arguments> paramsThatSupportRequestWithPathRegex() {
      return Stream.of(
          Arguments.of("^/?$", mockedRequest("GET", "/")),
          Arguments.of("^/?$", mockedRequest("GET", "")),
          Arguments.of(".*", mockedRequest("GET", "/path")),
          Arguments.of(".*", mockedRequest("POST", "/any/path")),
          Arguments.of("/path/.*", mockedRequest("PUT", "/path/1")),
          Arguments.of("/path/.*", mockedRequest("POST", "/path/1/2")));
    }

    @MethodSource("paramsThatSupportRequestWithPathRegex")
    @ParameterizedTest(name = "Request supports specific path with regex {0}")
    void requestSupportsSpecificPathsWithRegex(String pathRegex, HttpServletRequest request) {
      var req = Request.builder().paths(List.of(pathRegex)).build();
      Assertions.assertTrue(req.supports(request));
    }

    public static Stream<Arguments> paramsThatNotSupportRequestWithPathRegex() {
      return Stream.of(
          Arguments.of("^/a.*", mockedRequest("GET", "/path/1")),
          Arguments.of("/path/.*", mockedRequest("GET", "/path")));
    }

    @MethodSource("paramsThatNotSupportRequestWithPathRegex")
    @ParameterizedTest(name = "Request does not support specific path with regex {0}")
    void requestDoesNotSupportSpecificPathsWithRegex(String pathRegex, HttpServletRequest request) {
      var req = Request.builder().paths(List.of(pathRegex)).build();
      Assertions.assertFalse(req.supports(request));
    }
  }
}
