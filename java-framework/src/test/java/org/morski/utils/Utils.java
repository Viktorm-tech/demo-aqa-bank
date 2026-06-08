package org.morski.utils;

import java.time.LocalDateTime;

public class Utils {

    public static LocalDateTime toLocalDateTime(Object value) {
        return switch (value) {
            case null -> null;
            case LocalDateTime ldt -> ldt;
            case java.sql.Timestamp ts -> ts.toLocalDateTime();
            default -> throw new IllegalArgumentException("Unexpected type: " + value.getClass());
        };
    }

    public static LocalDateTime truncateToSeconds(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.withNano(0);
    }
}
