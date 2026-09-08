package com.lost2found.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility functions for Date and Time formatting.
 */
public final class DateTimeUtils {

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);

    private DateTimeUtils() {
    }

    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DEFAULT_FORMATTER);
    }

    public static LocalDateTime parse(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        String str = dateTimeStr.trim();
        try {
            if (str.length() == 10 && str.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(str).atStartOfDay();
            }
            if (str.contains("T")) {
                String isoStr = str;
                if (isoStr.endsWith("Z")) {
                    isoStr = isoStr.substring(0, isoStr.length() - 1);
                }
                if (isoStr.contains(".")) {
                    isoStr = isoStr.substring(0, isoStr.indexOf("."));
                }
                return LocalDateTime.parse(isoStr);
            }
            return LocalDateTime.parse(str, DEFAULT_FORMATTER);
        } catch (Exception e) {
            try {
                if (str.length() >= 10) {
                    return LocalDate.parse(str.substring(0, 10)).atStartOfDay();
                }
            } catch (Exception ignored) {
            }
            return LocalDateTime.now();
        }
    }
}

