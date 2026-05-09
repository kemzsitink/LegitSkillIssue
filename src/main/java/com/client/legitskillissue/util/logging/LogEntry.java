package com.client.legitskillissue.util.logging;

/**
 * Immutable data class representing a single log record.
 *
 * <p>Every field is set at construction time and never mutated afterwards,
 * making instances safe to pass across threads without synchronization.
 *
 * <p>Thread safety: immutable after construction.
 */
public final class LogEntry {

    /** Epoch-millisecond timestamp captured at the moment the entry was created. */
    private final long timestamp;

    /** Severity level of this entry. */
    private final LogLevel level;

    /** Name of the {@link Logger} that produced this entry. */
    private final String loggerName;

    /** Name of the thread that produced this entry. */
    private final String threadName;

    /** Formatted (or raw) log message. */
    private final String message;

    /**
     * Optional throwable associated with this entry; may be {@code null}.
     * Stored as a reference — stack-trace rendering is deferred to the appender.
     */
    private final Throwable throwable;

    /**
     * Constructs a new {@code LogEntry}.
     *
     * @param timestamp   epoch-millisecond timestamp
     * @param level       severity level (must not be {@code null})
     * @param loggerName  name of the originating logger (must not be {@code null})
     * @param threadName  name of the originating thread (must not be {@code null})
     * @param message     log message (must not be {@code null})
     * @param throwable   associated throwable, or {@code null} if none
     */
    public LogEntry(long timestamp,
                    LogLevel level,
                    String loggerName,
                    String threadName,
                    String message,
                    Throwable throwable) {
        if (level == null) {
            throw new IllegalArgumentException("level must not be null");
        }
        if (loggerName == null) {
            throw new IllegalArgumentException("loggerName must not be null");
        }
        if (threadName == null) {
            throw new IllegalArgumentException("threadName must not be null");
        }
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
        this.timestamp = timestamp;
        this.level = level;
        this.loggerName = loggerName;
        this.threadName = threadName;
        this.message = message;
        this.throwable = throwable;
    }

    /** Returns the epoch-millisecond timestamp. */
    public long getTimestamp() {
        return timestamp;
    }

    /** Returns the severity level. */
    public LogLevel getLevel() {
        return level;
    }

    /** Returns the name of the originating logger. */
    public String getLoggerName() {
        return loggerName;
    }

    /** Returns the name of the originating thread. */
    public String getThreadName() {
        return threadName;
    }

    /** Returns the log message. */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the associated throwable, or {@code null} if none was provided.
     */
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        return "LogEntry{"
                + "timestamp=" + timestamp
                + ", level=" + level
                + ", loggerName='" + loggerName + '\''
                + ", threadName='" + threadName + '\''
                + ", message='" + message + '\''
                + ", throwable=" + throwable
                + '}';
    }
}
