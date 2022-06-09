package eu.csgroup.coprs.ps2.core.common.utils;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String PATTERN_SHORT = "yyyyMMdd'T'HHmmss";
    private static final String PATTERN_LONG = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";


    public static String toDate(Instant instant) {
        return formatter(PATTERN).format(instant);
    }

    public static String toShortDate(Instant instant) {
        return formatter(PATTERN_SHORT).format(instant);
    }

    public static String toLongDate(Instant instant) {
        return formatter(PATTERN_LONG).format(instant);
    }

    public static Instant toInstant(String date) {
        return Instant.parse(date);
    }

    public static String elapsed(Instant start) {
        return toHuman(Duration.between(start, Instant.now(Clock.systemUTC())).getSeconds());
    }

    private static DateTimeFormatter formatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withZone(ZoneOffset.UTC);
    }

    private static String toHuman(long seconds) {
        return String.format("%02d:%02d:%02d", seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
    }

    private DateUtils() {
    }

}
