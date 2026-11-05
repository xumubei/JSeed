package com.jseed.oss.starter.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Date and time utilities
 *
 * @author cyrus
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtil extends DateUtils {

    public static final String YYYY = "yyyy";

    public static final String YYYY_MM = "yyyy-MM";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final String[] PARSE_PATTERNS = {
        "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
        "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
        "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * Gets the current date and time as a Date.
     *
     * @return current date and time
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * Formats today's date as yyyy-MM-dd by default.
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Formats the date as a path, for example 2018/08/08.
     */
    public static String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * Formats the date as yyyyMMdd, for example 20180808.
     */
    public static String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * Parses a date string using the supplied format.
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), PARSE_PATTERNS);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Gets the server start time.
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * Calculates the difference in days.
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        return Math.abs((int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24)));
    }

    /**
     * Calculates the difference between two times.
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // Get the difference in milliseconds.
        long diff = endDate.getTime() - nowDate.getTime();
        // Calculate the day component.
        long day = diff / nd;
        // Calculate the hour component.
        long hour = diff % nd / nh;
        // Calculate the minute component.
        long min = diff % nd % nh / nm;
        // Calculate the second component and format the result.
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * Converts LocalDateTime to Date.
     */
    public static Date toDate(LocalDateTime temporalAccessor) {
        ZonedDateTime zdt = temporalAccessor.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }
    /**
     * Checks whether the difference exceeds 15 days.
     */
    public static boolean checkIfOver15Days(LocalDateTime updatetime) {
        LocalDateTime now = LocalDateTime.now();
        long daysBetween = ChronoUnit.DAYS.between(updatetime, now);
        return daysBetween > 15;
    }
    /**
     * Converts LocalDate to Date.
     */
    public static Date toDate(LocalDate temporalAccessor) {
        LocalDateTime localDateTime = LocalDateTime.of(temporalAccessor, LocalTime.of(0, 0, 0));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * Returns the difference between two dates in minutes.
     * @param endDate end date
     * @param nowDate start date
     * @return elapsed minutes, including any fraction
     */
    public static double getDateDiffInMinutes(Date endDate, Date nowDate) {
        long diffInMilliseconds = endDate.getTime() - nowDate.getTime();
        return diffInMilliseconds / (1000.0 * 60);
    }

    /**
     * Returns the difference between two dates in minutes.
     * @param endDate
     * @param nowDate
     * @return
     */
    public static double getDateDiffInMinutes(LocalDateTime endDate, LocalDateTime nowDate) {
        long totalSeconds = ChronoUnit.SECONDS.between(nowDate, endDate);
        return (double) totalSeconds / 60;
    }
}
