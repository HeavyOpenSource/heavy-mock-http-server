package heavynimbus.server.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DelayUtils {

  public static long getStart(HttpServletRequest request) {
    return (Long) request.getAttribute("start");
  }

  public static long initStart(HttpServletRequest request) {
    long start = System.currentTimeMillis();
    request.setAttribute("start", start);
    return start;
  }

  public static void delayExactly(long start, long delay) {
    long now = System.currentTimeMillis();
    long elapsed = now - start;
    if (elapsed < delay) {
      delay(delay - elapsed);
      log.debug("Elapsed: {} ms, Delay: {} ms", elapsed, delay - elapsed);
    }
  }

  public static void delay(long delay) {
    if (delay <= 0) {
      return;
    }
    try {
      Thread.sleep(delay);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }
}
