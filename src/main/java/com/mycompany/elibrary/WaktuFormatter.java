package com.mycompany.elibrary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

public class WaktuFormatter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss | dd-MM-yyyy");

    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(formatter) : "-";
    }

    public static String format(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime().format(formatter) : "-";
    }

    public static String format(String timestampStr) {
        try {
            Timestamp timestamp = Timestamp.valueOf(timestampStr);
            return format(timestamp);
        } catch (Exception e) {
            return timestampStr;
        }
    }

    public static String now() {
        return LocalDateTime.now().format(formatter);
    }
}
