package heavynimbus.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplicationBuilder applicationBuilder = new SpringApplicationBuilder(ServerApplication.class);
		applicationBuilder.headless(false);
		applicationBuilder.run(args);
	}

}
