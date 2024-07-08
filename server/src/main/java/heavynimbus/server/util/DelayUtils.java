package heavynimbus.server.util;

public class DelayUtils {

  public static void delayExactly(long start, long delay) {
    long now = System.currentTimeMillis();
    long elapsed = now - start;
    if (elapsed < delay) {
      delay(delay - elapsed);
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
