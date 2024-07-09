package heavynimbus.server.controller;

import heavynimbus.server.mapper.ResponseMapper;
import heavynimbus.server.model.Endpoint;
import heavynimbus.server.model.Model;
import heavynimbus.server.model.Response;
import heavynimbus.server.service.CallbackService;
import heavynimbus.server.util.DelayUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class MockController {
  private final Model model;
  private final Validator validator;
  private final ResponseMapper responseMapper;
  private final CallbackService callbackService;

  @RequestMapping("/**")
  public ResponseEntity<Object> handle(HttpServletRequest request) {
    long start = DelayUtils.getStart(request);
    Optional<Endpoint> endpointOpt =
        model.getEndpoints().stream().filter(endpoint -> endpoint.supports(request)).findFirst();

    if (endpointOpt.isEmpty()) {
      log.error(
          "No endpoint found for request: {} {} {}",
          request.getMethod(),
          request.getRequestURI(),
          request.getQueryString());
      return ResponseEntity.notFound().build();
    }

    Endpoint endpoint = endpointOpt.get();
    log.info("Found endpoint: {}", endpoint.getName());
    Response response = endpoint.getResponse();
    ResponseEntity<Object> responseEntity = responseMapper.toEntity(response);

    endpoint.getCallbacks().forEach(c -> callbackService.handleCallback(c, start));

    if (response.getDelay() == null) {
      return responseEntity;
    }

    DelayUtils.delayExactly(start, response.getDelay());
    return responseEntity;
  }
}
