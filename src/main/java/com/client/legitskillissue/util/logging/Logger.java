package com.client.legitskillissue.util.logging;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Structured, level-gated, asynchronous logger.
 *
 * <h2>Design highlights</h2>
 * <ul>
 *   <li><b>Level gating</b>: {@link String#format} is never called when the
 *       requested level is below {@link #minLevel}, eliminating varargs array
 *       allocation and string concatenation overhead on hot paths.</li>
 *   <li><b>Asynchronous dispatch</b>: log entries are handed off to an
 *       {@link AsyncLogQueue} and written by a dedicated daemon thread, so
 *       callers are never blocked by I/O.</li>
 *   <li><b>Shared instances</b>: {@link #getLogger(String)} returns the same
 *       {@code Logger} instance for a given name, avoiding redundant object
 *       creation.</li>
 *   <li><b>ISO 8601 timestamps</b>: every entry carries a timestamp produced
 *       by {@link Instant#now()}, which is Java-8-compatible.</li>
 * </ul>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * Logger log = Logger.getLogger("MyModule");
 * log.info("Player joined: {}", playerName);
 * log.error("Unexpected state", exception);
 * }</pre>
 *
 * <p>Thread safety: all public methods are thread-safe.
 */
public final class Logger {

    // ── shared instance registry ──────────────────────────────────────────────

    private static final ConcurrentHashMap<String, Logger> REGISTRY =
            new ConcurrentHashMap<>();

    /**
     * Returns the shared {@code Logger} instance for the given name, creating
     * one if it does not yet exist.
     *
     * <p>The returned instance uses the global {@link AsyncLogQueue} and the
     * global minimum level ({@link LogLevel#INFO} by default).
     *
     * @param name logger name; typically a class or module name
     * @return shared logger instance for {@code name}
     */
    public static Logger getLogger(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        return REGISTRY.computeIfAbsent(name, Logger::new);
    }

    // ── global async queue ────────────────────────────────────────────────────

    /**
     * Global appender list shared by all loggers created via
     * {@link #getLogger(String)}.  Callers may add appenders before the first
     * log call; the list is not thread-safe for structural modifications after
     * the queue has started.
     */
    private static final List<LogAppender> GLOBAL_APPENDERS =
            Collections.synchronizedList(new ArrayList<LogAppender>());

    private static final AsyncLogQueue GLOBAL_QUEUE;

    static {
        // Default: write to console
        GLOBAL_APPENDERS.add(new ConsoleAppender());
        GLOBAL_QUEUE = new AsyncLogQueue(GLOBAL_APPENDERS);
        GLOBAL_QUEUE.start();
    }

    /**
     * Adds an appender to the global appender list.
     * Must be called before the first log entry is produced for best results.
     *
     * @param appender the appender to add; must not be {@code null}
     */
    public static void addGlobalAppender(LogAppender appender) {
        if (appender == null) {
            throw new IllegalArgumentException("appender must not be null");
        }
        GLOBAL_APPENDERS.add(appender);
    }

    // ── global minimum level ──────────────────────────────────────────────────

    /**
     * Global minimum level applied to all loggers that have not overridden
     * their own level.  Defaults to {@link LogLevel#INFO}.
     */
    private static volatile LogLevel globalMinLevel = LogLevel.INFO;

    /**
     * Sets the global minimum log level.
     *
     * @param level new minimum level; must not be {@code null}
     */
    public static void setGlobalMinLevel(LogLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("level must not be null");
        }
        globalMinLevel = level;
    }

    // ── instance state ────────────────────────────────────────────────────────

    private final String name;

    /**
     * Per-instance minimum level.  When {@code null} the global level is used.
     */
    private volatile LogLevel minLevel;

    /** Per-instance appenders.  When empty the global queue is used. */
    private final List<LogAppender> appenders;

    /** Queue used by this instance.  Defaults to the global queue. */
    private final AsyncLogQueue asyncQueue;

    // ── constructors ──────────────────────────────────────────────────────────

    /**
     * Private constructor used by {@link #getLogger(String)}.
     * Uses the global queue and no per-instance appenders.
     */
    private Logger(String name) {
        this.name = name;
        this.minLevel = null; // use global
        this.appenders = new ArrayList<>();
        this.asyncQueue = GLOBAL_QUEUE;
    }

    /**
     * Package-private constructor for testing: allows injecting a custom queue
     * and appender list.
     *
     * @param name       logger name
     * @param minLevel   minimum level for this instance
     * @param appenders  appenders for this instance
     * @param asyncQueue queue to use for async dispatch
     */
    Logger(String name, LogLevel minLevel, List<LogAppender> appenders,
           AsyncLogQueue asyncQueue) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        if (minLevel == null) {
            throw new IllegalArgumentException("minLevel must not be null");
        }
        if (appenders == null) {
            throw new IllegalArgumentException("appenders must not be null");
        }
        if (asyncQueue == null) {
            throw new IllegalArgumentException("asyncQueue must not be null");
        }
        this.name = name;
        this.minLevel = minLevel;
        this.appenders = new ArrayList<>(appenders);
        this.asyncQueue = asyncQueue;
    }

    // ── level convenience methods ─────────────────────────────────────────────

    /** Returns {@code true} if TRACE entries will be processed. */
    public boolean isTraceEnabled() {
        return isLevelEnabled(LogLevel.TRACE);
    }

    /** Returns {@code true} if DEBUG entries will be processed. */
    public boolean isDebugEnabled() {
        return isLevelEnabled(LogLevel.DEBUG);
    }

    /** Returns {@code true} if INFO entries will be processed. */
    public boolean isInfoEnabled() {
        return isLevelEnabled(LogLevel.INFO);
    }

    /** Returns {@code true} if WARN entries will be processed. */
    public boolean isWarnEnabled() {
        return isLevelEnabled(LogLevel.WARN);
    }

    /** Returns {@code true} if ERROR entries will be processed. */
    public boolean isErrorEnabled() {
        return isLevelEnabled(LogLevel.ERROR);
    }

    // ── logging methods ───────────────────────────────────────────────────────

    /**
     * Logs a TRACE message.
     *
     * <p>{@link String#format} is only called when TRACE is enabled.
     *
     * @param format  format string (see {@link String#format})
     * @param args    format arguments
     */
    public void trace(String format, Object... args) {
        log(LogLevel.TRACE, format, null, args);
    }

    /**
     * Logs a DEBUG message.
     *
     * @param format  format string
     * @param args    format arguments
     */
    public void debug(String format, Object... args) {
        log(LogLevel.DEBUG, format, null, args);
    }

    /**
     * Logs an INFO message.
     *
     * @param format  format string
     * @param args    format arguments
     */
    public void info(String format, Object... args) {
        log(LogLevel.INFO, format, null, args);
    }

    /**
     * Logs a WARN message.
     *
     * @param format  format string
     * @param args    format arguments
     */
    public void warn(String format, Object... args) {
        log(LogLevel.WARN, format, null, args);
    }

    /**
     * Logs an ERROR message with an associated {@link Throwable}.
     *
     * @param message   human-readable description of the error
     * @param throwable the exception or error; may be {@code null}
     */
    public void error(String message, Throwable throwable) {
        if (!isLevelEnabled(LogLevel.ERROR)) {
            return;
        }
        enqueue(LogLevel.ERROR, message, throwable);
    }

    /**
     * Logs an ERROR message using a format string.
     *
     * @param format  format string
     * @param args    format arguments
     */
    public void error(String format, Object... args) {
        log(LogLevel.ERROR, format, null, args);
    }

    // ── per-instance configuration ────────────────────────────────────────────

    /**
     * Overrides the minimum level for this logger instance.
     *
     * @param level new minimum level; must not be {@code null}
     */
    public void setMinLevel(LogLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("level must not be null");
        }
        this.minLevel = level;
    }

    /** Returns the name of this logger. */
    public String getName() {
        return name;
    }

    // ── internal helpers ──────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the given level is at or above the effective
     * minimum level for this logger.
     *
     * <p>The effective minimum is the per-instance level when set, otherwise
     * the global level.
     */
    boolean isLevelEnabled(LogLevel level) {
        LogLevel effective = (this.minLevel != null) ? this.minLevel : globalMinLevel;
        return level.ordinal() >= effective.ordinal();
    }

    /**
     * Core logging method.  Checks the level gate, formats the message, and
     * enqueues the entry.
     *
     * @param level     severity level
     * @param format    format string or plain message
     * @param throwable optional throwable (may be {@code null})
     * @param args      format arguments (may be empty)
     */
    private void log(LogLevel level, String format, Throwable throwable, Object... args) {
        // Level gate: skip String.format entirely when level is disabled
        if (!isLevelEnabled(level)) {
            return;
        }
        String message;
        if (args != null && args.length > 0) {
            try {
                message = String.format(format, args);
            } catch (Exception e) {
                // Formatting failure must not crash the caller
                message = format + " [formatting error: " + e.getMessage() + "]";
            }
        } else {
            message = (format != null) ? format : "";
        }
        enqueue(level, message, throwable);
    }

    /**
     * Creates a {@link LogEntry} and hands it to the async queue.
     */
    private void enqueue(LogLevel level, String message, Throwable throwable) {
        long timestamp = Instant.now().toEpochMilli();
        String threadName = Thread.currentThread().getName();
        LogEntry entry = new LogEntry(timestamp, level, name, threadName, message, throwable);
        asyncQueue.enqueue(entry);
    }
}
