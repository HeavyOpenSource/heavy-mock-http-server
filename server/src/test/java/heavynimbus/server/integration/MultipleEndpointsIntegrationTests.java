package heavynimbus.server.integration;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import heavynimbus.server.configuration.properties.MockConfigurationProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {
      "heavy.mock.config=classpath:configs/multiple-endpoints.yml",
    })
@EnableConfigurationProperties(MockConfigurationProperties.class)
public class MultipleEndpointsIntegrationTests implements IntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("GET /hello/heavy returns 200 \"Hello, Heavy!\"")
  public void testHelloHeavy() throws Exception {
    mockMvc
        .perform(get(getHelloUri("heavy")))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(content().json("{\"message\":\"Hello, Heavy!\"}"));
  }

  @Test
  @DisplayName("GET /hello/nimbus returns 200 \"Hello, World!\"")
  public void testHelloNimbus() throws Exception {
    mockMvc
        .perform(get(getHelloUri("nimbus")))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"))
        .andExpect(content().json("{\"message\":\"Hello, World!\"}"));
  }

  @Test
  @DisplayName("GET /not-found returns 404 \"Not Found\"")
  public void testNotFound() throws Exception {
    mockMvc
        .perform(get(getNotFoundUri()))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType("application/json"))
        .andExpect(content().json("{\"message\":\"Not Found\"}"));
  }
}
