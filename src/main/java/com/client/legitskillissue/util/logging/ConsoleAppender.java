package com.client.legitskillissue.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

/**
 * {@link LogAppender} that writes formatted log entries to {@code System.out}.
 *
 * <p>Output format:
 * <pre>
 * [ISO8601_TIMESTAMP] [THREAD] [LOGGER] LEVEL: message
 * </pre>
 * When a {@link Throwable} is present the stack trace is appended on subsequent
 * lines.
 *
 * <p>Thread safety: {@code System.out} is internally synchronized; this class
 * adds no additional state and is therefore thread-safe.
 */
public final class ConsoleAppender implements LogAppender {

    /**
     * Constructs a new {@code ConsoleAppender}.
     * No configuration is required; output always goes to {@code System.out}.
     */
    public ConsoleAppender() {
        // no-op
    }

    /**
     * {@inheritDoc}
     *
     * <p>Formats the entry as
     * {@code [ISO8601] [thread] [logger] LEVEL: message}
     * and prints it to {@code System.out}.  If the entry carries a
     * {@link Throwable}, the stack trace is appended immediately after.
     */
    @Override
    public void append(LogEntry entry) {
        String iso8601 = Instant.ofEpochMilli(entry.getTimestamp()).toString();
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(iso8601).append(']')
          .append(' ')
          .append('[').append(entry.getThreadName()).append(']')
          .append(' ')
          .append('[').append(entry.getLoggerName()).append(']')
          .append(' ')
          .append(entry.getLevel().name())
          .append(": ")
          .append(entry.getMessage());

        if (entry.getThrowable() != null) {
            sb.append(System.lineSeparator());
            sb.append(stackTraceToString(entry.getThrowable()));
        }

        System.out.println(sb.toString());
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static String stackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }
}
