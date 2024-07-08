package heavynimbus.server.integration;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import heavynimbus.server.configuration.properties.MockConfigurationProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {
      "heavy.mock.config=classpath:configs/delayed-endpoints.yml",
    })
@EnableConfigurationProperties(MockConfigurationProperties.class)
public class DelayedEndpointsIntegrationTests implements IntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("GET /hello/no-delay returns 200 \"Hello, Heavy!\"")
  public void testHelloHeavy() throws Exception {
    // WARMUP
    mockMvc.perform(get(getHelloUri("no-delay")));

    long start = System.currentTimeMillis();
    var res = mockMvc.perform(get(getHelloUri("no-delay")));
    long end = System.currentTimeMillis();
    long delay = end - start;
    Assertions.assertThat(delay).as("Delay should be less than 20ms").isLessThan(20L);

    res.andExpect(status().isOk())
        .andExpect(header().string("Content-Type", "application/json"))
        .andExpect(
            content()
                .json(
                    """
						{
							"message": "Hello, Heavy!"
						}
						"""));
  }

  @TestFactory
  @DisplayName("Delayed endpoint Tests")
  public List<DynamicTest> testSimpleConfig() throws Exception {
    // WARMUP
    mockMvc.perform(get(getHelloUri("no-delay")));

    final int ITERATIONS = 10;
    List<Integer> testCases = List.of(200, 500, 1000);
    List<DynamicTest> tests = new ArrayList<>();

    for (Integer testCase : testCases) {
      tests.add(
          DynamicTest.dynamicTest(
              "Average time for "
                  + testCase
                  + "ms delay with "
                  + ITERATIONS
                  + " iterations and a margin of error of 5 milliseconds",
              () -> {
                long average = 0;
                for (int i = 0; i < ITERATIONS; i++) {
                  long start = System.currentTimeMillis();
                  var res = mockMvc.perform(get(getHelloUri(testCase.toString())));
                  long end = System.currentTimeMillis();
                  average += end - start;
                  res.andExpect(status().isOk())
                      .andExpect(header().string("Content-Type", "application/json"))
                      .andExpect(
                          content()
                              .json(
                                  """
									{
										"message": "Hello, Heavy!"
									}
									"""));
                }

                average /= ITERATIONS;
                Assertions.assertThat(average)
                    .as("Average time should be greater than or equal to " + testCase + "ms")
                    .isGreaterThanOrEqualTo(testCase);
                Assertions.assertThat(average)
                    .as("Average time should be less than or equal to " + (testCase + 5) + "ms")
                    .isLessThanOrEqualTo(testCase + 5);
              }));
    }

    for (Integer testCase : testCases) {
      tests.add(
          DynamicTest.dynamicTest(
              "Average time for "
                  + testCase
                  + "ms delay with "
                  + ITERATIONS
                  + " iterations at the same time  and a margin of error of 5 milliseconds",
              () -> {
                AtomicLong average = new AtomicLong(0);
                try (ExecutorService exec = Executors.newVirtualThreadPerTaskExecutor()) {
                  for (int i = 0; i < ITERATIONS; i++) {
                    exec.submit(
                        () -> {
                          long start = System.currentTimeMillis();
                          try {
                            var res = mockMvc.perform(get(getHelloUri(testCase.toString())));
                            long end = System.currentTimeMillis();
                            average.addAndGet(end - start);
                            res.andExpect(status().isOk())
                                .andExpect(header().string("Content-Type", "application/json"))
                                .andExpect(
                                    content()
                                        .json(
                                            """
														{
															"message": "Hello, Heavy!"
														}
														"""));
                          } catch (Exception e) {
                            throw new RuntimeException(e);
                          }
                        });
                  }
                  exec.shutdown();
                  boolean termination = exec.awaitTermination(1, TimeUnit.MINUTES);
                  assert termination : "Executor did not terminate in time";

                  average.set(average.get() / ITERATIONS);
                  Assertions.assertThat(average.get())
                      .as("Average time should be greater than or equal to " + testCase + "ms")
                      .isGreaterThanOrEqualTo(testCase);
                  Assertions.assertThat(average.get())
                      .as("Average time should be less than or equal to " + (testCase + 5) + "ms")
                      .isLessThanOrEqualTo(testCase + 5);
                } catch (InterruptedException e) {
                  throw new RuntimeException(e);
                }
              }));
    }
    return tests;
  }
}
