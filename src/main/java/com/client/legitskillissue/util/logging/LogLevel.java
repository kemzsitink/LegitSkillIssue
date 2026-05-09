package com.client.legitskillissue.util.logging;

/**
 * Log severity levels in ascending order of severity.
 *
 * <p>Ordinal order: TRACE(0) &lt; DEBUG(1) &lt; INFO(2) &lt; WARN(3) &lt; ERROR(4).
 * A {@link Logger} configured with a minimum level will only emit entries whose
 * level ordinal is &ge; the configured minimum.
 *
 * <p>Thread safety: enum constants are inherently thread-safe.
 */
public enum LogLevel {
    /** Finest-grained diagnostic information. */
    TRACE,
    /** Diagnostic information useful during development. */
    DEBUG,
    /** Informational messages about normal operation. */
    INFO,
    /** Potentially harmful situations that deserve attention. */
    WARN,
    /** Error events that may still allow the application to continue. */
    ERROR
}
