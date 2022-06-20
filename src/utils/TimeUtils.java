package utils;

import java.time.Duration;

public class TimeUtils {
    public static String getNewTime(String oldTime, long seconds) {
        Duration duration = Duration.ofSeconds(getSeconds(oldTime));
        duration = duration.plusSeconds(seconds);
        return String.format("%02d:%02d:%02d",duration.toHours(),duration.toMinutesPart(),duration.toSecondsPart());
    }

    public static long getSeconds(String oldTime) {
        String[] timeSplit = oldTime.split(":");
        return  ((long) Integer.parseInt(timeSplit[0]) * 60 * 60 +  Integer.parseInt(timeSplit[1]) * 60L + Integer.parseInt(timeSplit[2]));
    }

}
