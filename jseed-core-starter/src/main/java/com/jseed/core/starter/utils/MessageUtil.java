package com.jseed.core.starter.utils;

import org.slf4j.helpers.MessageFormatter;

/**
 * Message utilities
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class MessageUtil {

    /**
     * Fills placeholders in the message template.
     * For example: format("This was written by {} and has a {} chance of failing", "cyrus", "100%").
     *
     * @param message message template
     * @param objects placeholder values
     * @return
     */
    public static String format(String message, Object... objects) {
        return MessageFormatter.arrayFormat(message, objects).getMessage();
    }


    /**
     * Converts an exception to a string.
     *
     * @param e exception
     * @return
     */
    public static String toString(Throwable e) {
        if (null == e) {
            return "";
        }
        StackTraceElement[] traceElements = e.getStackTrace();
        StringBuilder traceBuilder = new StringBuilder();
        traceBuilder.append("\n").append(e).append("\n");
        if (traceElements == null) {
           return traceBuilder.toString();
        }
        for (StackTraceElement traceElement : traceElements) {
            traceBuilder.append(traceElement.toString());
            traceBuilder.append("\n");
        }
        return traceBuilder.toString();
    }

}
