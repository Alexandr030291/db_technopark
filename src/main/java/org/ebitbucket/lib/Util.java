package org.ebitbucket.lib;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Util {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Util() {
    }

    public static String dateToStr(LocalDateTime dateTime) {
        return DATE_FORMAT.format(dateTime);
    }

    public static LocalDateTime strToDate(String dateTime) {
        return LocalDateTime.parse(dateTime, DATE_FORMAT);
    }

    public static String validSince(String since) {
        if (since == null) {
            since = "1000-01-01 00:00:00";
        }else {
            DATE_FORMAT.parse(since);
        }
        return since;
    }

}