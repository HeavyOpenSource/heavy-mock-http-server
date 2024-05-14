package heavynimbus.server.configuration;

import heavynimbus.server.configuration.properties.MockConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableConfigurationProperties({MockConfigurationProperties.class})
public class WebConfiguration {

	@Bean
	public ExecutorService executorService() {
		return Executors.newVirtualThreadPerTaskExecutor();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
