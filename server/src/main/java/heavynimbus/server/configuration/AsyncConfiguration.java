package heavynimbus.server.configuration;

import java.util.Map;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

@Log4j2
@EnableAsync
@Configuration
public class AsyncConfiguration {

  @Bean("callbackExecutor")
  public TaskExecutor callbackExecutor() {
    return new MDCFollowingTaskExecutor("callback-");
  }

  static class MDCFollowingTaskExecutor implements TaskExecutor {
    private final TaskExecutor delegate;

    public MDCFollowingTaskExecutor(String prefix) {
      this.delegate = new VirtualThreadTaskExecutor(prefix);
    }

    @Override
    public void execute(@NonNull Runnable task) {
      Map<String, String> loggingContext = MDC.getCopyOfContextMap();
      delegate.execute(
          () -> {
            if (loggingContext != null) {
              MDC.setContextMap(loggingContext);
            }
            task.run();
          });
    }
  }
}
