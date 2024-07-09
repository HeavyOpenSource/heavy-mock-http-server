package heavynimbus.server.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.MDC;

public class TrackIdHighlighting extends ForegroundCompositeConverterBase<ILoggingEvent> {

  private static final String[] colors =
      new String[] {
        ANSIConstants.RED_FG,
        ANSIConstants.GREEN_FG,
        ANSIConstants.YELLOW_FG,
        ANSIConstants.BLUE_FG,
        ANSIConstants.MAGENTA_FG,
        ANSIConstants.CYAN_FG,
        ANSIConstants.DEFAULT_FG // we use default color instead of white / black
      };

  private static int lastColorIndex = 0;

  public static final Map<String, String> trackIdsColors = new HashMap<>();

  private static String anyColor() {
    int index = (int) (Math.random() * colors.length);
    if (index == lastColorIndex) {
      index = (index + 1) % colors.length;
    }
    lastColorIndex = index;
    return colors[index];
  }

  public static void generateTrackId() {
    UUID trackId = UUID.randomUUID();
    trackIdsColors.put(trackId.toString(), anyColor());
    MDC.put("trackId", trackId.toString());
  }

  public static void clearTrackId() {
    String trackId = MDC.get("trackId");
    if (trackId == null) return;

    trackIdsColors.remove(trackId);
    MDC.remove("trackId");
  }

  @Override
  protected String getForegroundColorCode(ILoggingEvent event) {
    String trackId = event.getMDCPropertyMap().get("trackId");
    if (trackId != null) {
      return trackIdsColors.computeIfAbsent(trackId, k -> anyColor());
    } else {
      return ANSIConstants.DEFAULT_FG;
    }
  }
}
