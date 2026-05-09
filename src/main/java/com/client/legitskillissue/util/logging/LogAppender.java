package com.client.legitskillissue.util.logging;

/**
 * Strategy interface for writing {@link LogEntry} objects to a destination.
 *
 * <p>Implementations must be thread-safe: the {@link AsyncLogQueue} drains
 * entries from a background thread and calls {@code append} concurrently with
 * any thread that may also call {@code append} directly.
 *
 * <p>Implementations should not throw unchecked exceptions from {@code append};
 * any I/O or formatting errors should be handled internally (e.g., printed to
 * {@code System.err}) so that a single failing appender does not disrupt the
 * rest of the logging pipeline.
 */
public interface LogAppender {

    /**
     * Writes the given log entry to this appender's destination.
     *
     * @param entry the log entry to write; never {@code null}
     */
    void append(LogEntry entry);
}
