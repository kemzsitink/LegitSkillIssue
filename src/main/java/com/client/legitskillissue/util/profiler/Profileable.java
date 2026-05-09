package com.client.legitskillissue.util.profiler;

/**
 * Marks a component as capable of being profiled by the {@link PerformanceProfiler}.
 *
 * <p>Implementing classes expose a human-readable name, lifecycle hooks for
 * starting and stopping a timing measurement, and a method to retrieve the
 * accumulated statistics snapshot.
 *
 * <p>Thread-safety: implementations are expected to be thread-safe. Callers
 * may invoke {@link #startProfiling()} and {@link #stopProfiling()} from any
 * thread.
 *
 * @see PerformanceProfiler
 * @see ProfilerSection
 */
public interface Profileable {

    /**
     * Returns the human-readable name that identifies this profiled component.
     *
     * @return profiler section name; never {@code null}
     */
    String getProfilerName();

    /**
     * Records the start timestamp for the current timing measurement.
     *
     * <p>Must be paired with a subsequent call to {@link #stopProfiling()}.
     * Calling {@code startProfiling()} twice without an intervening
     * {@code stopProfiling()} results in implementation-defined behavior.
     */
    void startProfiling();

    /**
     * Records the end timestamp for the current timing measurement, computes
     * the elapsed time, and updates the internal statistics.
     *
     * <p>Must be preceded by a call to {@link #startProfiling()}.
     */
    void stopProfiling();

    /**
     * Returns an immutable snapshot of the accumulated performance statistics
     * for this component.
     *
     * @return current {@link ProfilerStats} snapshot; never {@code null}
     */
    ProfilerStats getStats();
}
