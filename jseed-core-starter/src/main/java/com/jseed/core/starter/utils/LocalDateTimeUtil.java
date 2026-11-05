package com.jseed.core.starter.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Sets;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * System date and time utilities
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class LocalDateTimeUtil {

    /**
     * Gets the start of the current year.
     *
     * @return
     */
    public static LocalDateTime getStartTimeOfCurrentYear() {
        return getStartTimeOfSpecifiedYear(LocalDateTime.now().getYear());
    }

    /**
     * Gets the end of the current year.
     *
     * @return
     */
    public static LocalDateTime getEndTimeOfCurrentYear() {
        return getEndTimeOfSpecifiedYear(LocalDateTime.now().getYear());
    }

    /**
     * Gets the start of the given year.
     *
     * @param year target year
     * @return
     */
    public static LocalDateTime getStartTimeOfSpecifiedYear(int year) {
        return getStartTimeOfSpecifiedYearMonthDay(year, 1, 1);
    }

    /**
     * Gets the end of the given year.
     *
     * @param year target year
     * @return
     */
    public static LocalDateTime getEndTimeOfSpecifiedYear(int year) {
        return getEndTimeOfSpecifiedYearMonthDay(year, 12, 31);
    }

    /**
     * Gets the start of the given month.
     *
     * @param year  year
     * @param month month
     * @return
     */
    public static LocalDateTime getStartTimeOfSpecifiedYearMonth(int year, int month) {
        return getStartTimeOfSpecifiedYearMonthDay(year, month, 1);
    }


    /**
     * Gets the end of the given month.
     *
     * @param year  year
     * @param month month
     * @return
     */
    public static LocalDateTime getEndTimeOfSpecifiedYearMonth(int year, int month) {
        return getEndTimeOfSpecifiedYearMonthDay(year, month, getDayOfMonth(year, month));
    }

    /**
     * Gets the start of the day at the given offset from today.
     *
     * @param plusDay negative for days before today, positive for days after today
     * @return
     */
    public static LocalDateTime getStartTimeOfTodayPlusDay(int plusDay) {
        LocalDateTime yesterday = LocalDateTime.now().plusDays(plusDay);
        return getStartTimeOfSpecifiedYearMonthDay(yesterday.getYear(), yesterday.getMonthValue(), yesterday.getDayOfMonth());
    }

    /**
     * Gets the end of the day at the given offset from today.
     *
     * @param plusDay negative for days before today, positive for days after today
     * @return
     */
    public static LocalDateTime getEndTimeOfTodayPlusDay(int plusDay) {
        LocalDateTime yesterday = LocalDateTime.now().plusDays(plusDay);
        return getEndTimeOfSpecifiedYearMonthDay(yesterday.getYear(), yesterday.getMonthValue(), yesterday.getDayOfMonth());
    }

    /**
     * Gets the start of today.
     *
     * @return
     */
    public static LocalDateTime getStartTimeOfToday() {
        LocalDateTime now = LocalDateTime.now();
        return getStartTimeOfSpecifiedYearMonthDay(now.getYear(), now.getMonthValue(), now.getDayOfMonth());
    }

    /**
     * Gets the end of today.
     *
     * @return
     */
    public static LocalDateTime getEndTimeOfToday() {
        LocalDateTime now = LocalDateTime.now();
        return getEndTimeOfSpecifiedYearMonthDay(now.getYear(), now.getMonthValue(), now.getDayOfMonth());
    }

    /**
     * Gets the start of yesterday.
     *
     * @return
     */
    public static LocalDateTime getStartTimeOfYesterday() {
        LocalDateTime yesterday = LocalDateTime.now().plusDays(-1);
        return getStartTimeOfSpecifiedYearMonthDay(yesterday.getYear(), yesterday.getMonthValue(), yesterday.getDayOfMonth());
    }

    /**
     * Gets the end of yesterday.
     *
     * @return
     */
    public static LocalDateTime getEndTimeOfYesterday() {
        LocalDateTime yesterday = LocalDateTime.now().plusDays(-1);
        return getEndTimeOfSpecifiedYearMonthDay(yesterday.getYear(), yesterday.getMonthValue(), yesterday.getDayOfMonth());
    }

    /**
     * Gets the start of the given date.
     *
     * @param year       year
     * @param month      month
     * @param dayOfMouth day of the month
     * @return
     */
    private static LocalDateTime getStartTimeOfSpecifiedYearMonthDay(int year, int month, int dayOfMouth) {
        return LocalDateTime.of(year, month, dayOfMouth, 0, 0, 0);
    }


    /**
     * Gets the end of the given date.
     *
     * @param year       year
     * @param month      month
     * @param dayOfMouth day of the month
     * @return
     */
    private static LocalDateTime getEndTimeOfSpecifiedYearMonthDay(int year, int month, int dayOfMouth) {
        return LocalDateTime.of(year, month, dayOfMouth, 23, 59, 59);
    }

    /**
     * Converts to a timestamp using China Standard Time.
     *
     * @param localDateTime
     * @return
     */
    public static Long toTimeMillis(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneOffset.ofHours(8)).toInstant()).getTime();
    }

    /**
     * Converts to Date using China Standard Time.
     *
     * @param localDateTime
     * @return
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneOffset.ofHours(8)).toInstant());
    }

    /**
     * Formats the value as yyyy-MM-dd HH:mm:ss.
     *
     * @param localDateTime date and time to format
     * @return
     */
    public static String toString(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            return null;
        }
        return DatePattern.NORM_DATETIME_FORMATTER.format(localDateTime);
    }

    /**
     * Formats the value using the supplied pattern.
     *
     * @param localDateTime date and time to format
     * @param format        format pattern, see DatePattern
     * @return
     */
    public static String format(LocalDateTime localDateTime, String format) {
        return DateUtil.format(localDateTime, format);
    }

    /**
     * Formats the value using the supplied pattern.
     *
     * @param localDate date to format
     * @param format    format pattern, see DatePattern
     * @return
     */
    public static String format(LocalDate localDate, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return dateTimeFormatter.format(localDate);
    }

    /**
     * Formats the value using the supplied pattern.
     *
     * @param localTime time to format
     * @param format    format pattern, see DatePattern
     * @return
     */
    public static String format(LocalTime localTime, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return dateTimeFormatter.format(localTime);
    }

    /**
     * Gets the start of the given LocalDate.
     *
     * @return
     */
    public static LocalDateTime getStartTimeFor(LocalDate localDate) {
        return localDate.atTime(0, 0, 0);
    }

    /**
     * Gets the end of the given LocalDate.
     *
     * @return
     */
    public static LocalDateTime getEndTimeFor(LocalDate localDate) {
        return localDate.atTime(23, 59, 59);
    }


    /**
     * Gets the number of days in the given month.
     *
     * @param year  year
     * @param month month
     * @return
     */
    public static int getDayOfMonth(int year, int month) {
        Set<Integer> bigDay = Sets.newHashSet(1, 3, 5, 7, 8, 10, 12);
        if (bigDay.contains(month)) {
            return 31;
        }
        Set<Integer> smartDay = Sets.newHashSet(4, 6, 9, 11);
        if (smartDay.contains(month)) {
            return 30;
        }
        if (2 == month) {
            // Checks whether the year is a leap year.
            boolean isLeapYear = year % 4 == 0 && year % 100 != 0;
            boolean isLeapYear2 = year % 400 == 0;
            return isLeapYear || isLeapYear2 ? 29 : 28;
        }
        throw new RuntimeException(String.format("year %d, month %d is Unreasonable", year, month));
    }
}



