package heavynimbus.server.integration;

import heavynimbus.server.configuration.properties.MockConfigurationProperties;
import heavynimbus.server.model.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(
		webEnvironment = RANDOM_PORT,
		properties = {
				"heavy.mock.config=classpath:configs/single-endpoint.yml",
		})
@EnableConfigurationProperties(MockConfigurationProperties.class)
public class SingleEndpointIntegrationTests implements IntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("Simple Config Tests")
	public void testSimpleConfig() throws Exception {
		mockMvc.perform(get(getHelloUri()))

				.andExpect(status().isOk())
				.andExpect(header().string("Content-Type", "application/json"))
				.andExpect(content().json("""
						{
							"message": "Hello, Heavy!"
						}
						"""));
	}

	@Test
	@DisplayName("Simple Config Tests on not found")
	public void testSimpleConfigNotFound() throws Exception {
		mockMvc.perform(request(HttpMethod.GET.name(), new URI("/not-found")))

				.andExpect(status().isNotFound())
				.andExpect(content().string(""));
	}

}
