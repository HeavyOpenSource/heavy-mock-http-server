package heavynimbus.server.configuration;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Log4j2
@EnableAsync
@Configuration
public class AsyncConfiguration {

	@Bean
	public Executor asyncExecutor() {
		log.info("Creating virtual thread executor");
		return Executors.newVirtualThreadPerTaskExecutor();
	}
}
