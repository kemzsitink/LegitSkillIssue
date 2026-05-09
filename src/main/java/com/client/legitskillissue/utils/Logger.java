package com.client.legitskillissue.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple logging utility for LegitSkillIssue.
 * Provides structured logging with levels and timestamps.
 * 
 * TODO: Replace with SLF4J in future for better integration.
 */
public class Logger {
    private final String name;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static LogLevel minLevel = LogLevel.INFO;

    public enum LogLevel {
        DEBUG(0, "DEBUG"),
        INFO(1, "INFO"),
        WARN(2, "WARN"),
        ERROR(3, "ERROR");

        private final int priority;
        private final String label;

        LogLevel(int priority, String label) {
            this.priority = priority;
            this.label = label;
        }

        public int getPriority() {
            return priority;
        }

        public String getLabel() {
            return label;
        }
    }

    private Logger(String name) {
        this.name = name;
    }

    /**
     * Gets a logger for the specified class.
     * 
     * @param clazz The class to log for
     * @return A logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return new Logger(clazz.getSimpleName());
    }

    /**
     * Gets a logger with the specified name.
     * 
     * @param name The logger name
     * @return A logger instance
     */
    public static Logger getLogger(String name) {
        return new Logger(name);
    }

    /**
     * Sets the minimum log level.
     * 
     * @param level Minimum level to log
     */
    public static void setMinLevel(LogLevel level) {
        minLevel = level;
    }

    /**
     * Logs a debug message.
     * 
     * @param message The message to log
     */
    public void debug(String message) {
        log(LogLevel.DEBUG, message);
    }

    /**
     * Logs an info message.
     * 
     * @param message The message to log
     */
    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    /**
     * Logs a warning message.
     * 
     * @param message The message to log
     */
    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    /**
     * Logs a warning message with exception.
     * 
     * @param message The message to log
     * @param throwable The exception
     */
    public void warn(String message, Throwable throwable) {
        log(LogLevel.WARN, message + "\n" + getStackTrace(throwable));
    }

    /**
     * Logs an error message.
     * 
     * @param message The message to log
     */
    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    /**
     * Logs an error message with exception.
     * 
     * @param message The message to log
     * @param throwable The exception
     */
    public void error(String message, Throwable throwable) {
        log(LogLevel.ERROR, message + "\n" + getStackTrace(throwable));
    }

    /**
     * Logs a message at the specified level.
     * 
     * @param level The log level
     * @param message The message to log
     */
    private void log(LogLevel level, String message) {
        if (level.getPriority() < minLevel.getPriority()) {
            return;
        }

        String timestamp = dateFormat.format(new Date());
        String formattedMessage = String.format("[%s] [%s/%s] %s",
                timestamp, name, level.getLabel(), message);

        if (level.getPriority() >= LogLevel.ERROR.getPriority()) {
            System.err.println(formattedMessage);
        } else {
            System.out.println(formattedMessage);
        }
    }

    /**
     * Gets the stack trace from a throwable as a string.
     * 
     * @param throwable The throwable
     * @return Stack trace as string
     */
    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Checks if debug logging is enabled.
     * 
     * @return true if debug is enabled
     */
    public boolean isDebugEnabled() {
        return minLevel.getPriority() <= LogLevel.DEBUG.getPriority();
    }

    /**
     * Checks if info logging is enabled.
     * 
     * @return true if info is enabled
     */
    public boolean isInfoEnabled() {
        return minLevel.getPriority() <= LogLevel.INFO.getPriority();
    }
}
