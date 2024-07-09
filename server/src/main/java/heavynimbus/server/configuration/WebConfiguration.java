package heavynimbus.server.configuration;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Log4j2
@Getter
@EnableWebMvc
@Configuration
public class WebConfiguration implements WebMvcConfigurer {}
