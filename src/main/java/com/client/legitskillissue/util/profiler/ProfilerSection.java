package com.client.legitskillissue.util.profiler;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A thread-safe, named timing section that implements {@link Profileable}.
 *
 * <p>Each section maintains a fixed-size circular reservoir of up to
 * {@value #RESERVOIR_SIZE} elapsed-time samples (in nanoseconds). When the
 * reservoir is full, new samples overwrite the oldest entry (circular
 * replacement). This approach provides bounded memory usage while preserving
 * a statistically representative sample set for percentile calculations.
 *
 * <h3>Usage pattern</h3>
 * <pre>{@code
 * ProfilerSection section = new ProfilerSection("mySection");
 * section.startProfiling();
 * // ... code to measure ...
 * section.stopProfiling();
 * ProfilerStats stats = section.getStats();
 * }</pre>
 *
 * <h3>Thread safety</h3>
 * <ul>
 *   <li>The reservoir array and its write index are guarded by
 *       {@code synchronized} blocks on the reservoir array itself.</li>
 *   <li>Aggregate counters ({@code totalTime}, {@code callCount},
 *       {@code minTime}, {@code maxTime}) use {@link AtomicLong} for
 *       lock-free updates.</li>
 *   <li>The start-time field is {@code volatile} to ensure visibility across
 *       threads when start/stop are called from different threads.</li>
 * </ul>
 *
 * @see PerformanceProfiler
 * @see ProfilerStats
 */
public final class ProfilerSection implements Profileable {

    /** Number of samples kept in the circular reservoir. */
    static final int RESERVOIR_SIZE = 1000;

    /** Human-readable name of this section. */
    private final String name;

    /**
     * Circular reservoir of elapsed-time samples in nanoseconds.
     * Guarded by {@code synchronized(reservoir)}.
     */
    private final long[] reservoir;

    /**
     * Next write position in the circular reservoir.
     * Guarded by {@code synchronized(reservoir)}.
     */
    private int writeIndex;

    /**
     * Number of samples actually written (capped at {@value #RESERVOIR_SIZE}).
     * Guarded by {@code synchronized(reservoir)}.
     */
    private int filledCount;

    /** Total accumulated elapsed time across all calls, in nanoseconds. */
    private final AtomicLong totalTime = new AtomicLong(0L);

    /** Number of completed start/stop pairs recorded. */
    private final AtomicLong callCount = new AtomicLong(0L);

    /** Minimum single-call elapsed time observed, in nanoseconds. */
    private final AtomicLong minTime = new AtomicLong(Long.MAX_VALUE);

    /** Maximum single-call elapsed time observed, in nanoseconds. */
    private final AtomicLong maxTime = new AtomicLong(0L);

    /**
     * Timestamp recorded by the most recent {@link #startProfiling()} call,
     * in nanoseconds. {@code volatile} for cross-thread visibility.
     */
    private volatile long startNanos;

    /**
     * Constructs a new {@code ProfilerSection} with the given name.
     *
     * @param name human-readable section name; must not be {@code null}
     * @throws NullPointerException if {@code name} is {@code null}
     */
    public ProfilerSection(String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.reservoir = new long[RESERVOIR_SIZE];
        this.writeIndex = 0;
        this.filledCount = 0;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Records {@link System#nanoTime()} as the start timestamp for the
     * current measurement window.
     */
    @Override
    public String getProfilerName() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Records {@link System#nanoTime()} as the start timestamp.
     */
    @Override
    public void startProfiling() {
        startNanos = System.nanoTime();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Computes the elapsed time since the last {@link #startProfiling()}
     * call, updates the aggregate statistics atomically, and adds the sample
     * to the circular reservoir.
     */
    @Override
    public void stopProfiling() {
        final long elapsed = System.nanoTime() - startNanos;

        // Update aggregate counters (lock-free).
        totalTime.addAndGet(elapsed);
        callCount.incrementAndGet();
        updateMin(elapsed);
        updateMax(elapsed);

        // Add sample to circular reservoir (synchronized on the array).
        synchronized (reservoir) {
            reservoir[writeIndex] = elapsed;
            writeIndex = (writeIndex + 1) % RESERVOIR_SIZE;
            if (filledCount < RESERVOIR_SIZE) {
                filledCount++;
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Computes percentiles (p50, p95, p99) by sorting a copy of the filled
     * portion of the reservoir. The sort is performed on a snapshot taken
     * inside a {@code synchronized} block so that the reservoir is not
     * modified during the copy.
     *
     * @return immutable {@link ProfilerStats} snapshot; never {@code null}
     */
    @Override
    public ProfilerStats getStats() {
        final long snap_totalTime = totalTime.get();
        final long snap_callCount = callCount.get();
        final long snap_minTime = minTime.get();
        final long snap_maxTime = maxTime.get();

        final double avgTime = snap_callCount > 0
                ? (double) snap_totalTime / snap_callCount
                : 0.0;

        // Take a snapshot of the filled portion of the reservoir.
        final long[] snapshot;
        final int snapshotSize;
        synchronized (reservoir) {
            snapshotSize = filledCount;
            snapshot = Arrays.copyOf(reservoir, snapshotSize);
        }

        double p50 = 0.0;
        double p95 = 0.0;
        double p99 = 0.0;

        if (snapshotSize > 0) {
            Arrays.sort(snapshot);
            p50 = snapshot[percentileIndex(snapshotSize, 50)];
            p95 = snapshot[percentileIndex(snapshotSize, 95)];
            p99 = snapshot[percentileIndex(snapshotSize, 99)];
        }

        return new ProfilerStats(
                name,
                snap_totalTime,
                snap_callCount,
                snap_minTime == Long.MAX_VALUE ? 0L : snap_minTime,
                snap_maxTime,
                avgTime,
                p50,
                p95,
                p99);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Updates the running minimum using a CAS loop.
     *
     * @param value new elapsed time to compare against the current minimum
     */
    private void updateMin(long value) {
        long current = minTime.get();
        while (value < current) {
            if (minTime.compareAndSet(current, value)) {
                break;
            }
            current = minTime.get();
        }
    }

    /**
     * Updates the running maximum using a CAS loop.
     *
     * @param value new elapsed time to compare against the current maximum
     */
    private void updateMax(long value) {
        long current = maxTime.get();
        while (value > current) {
            if (maxTime.compareAndSet(current, value)) {
                break;
            }
            current = maxTime.get();
        }
    }

    /**
     * Computes the zero-based array index for the given percentile in a sorted
     * array of {@code size} elements.
     *
     * <p>Uses the nearest-rank method: {@code index = ceil(p/100 * size) - 1},
     * clamped to {@code [0, size - 1]}.
     *
     * @param size       number of elements in the sorted array; must be &gt; 0
     * @param percentile percentile value in the range [1, 99]
     * @return zero-based index into the sorted array
     */
    private static int percentileIndex(int size, int percentile) {
        int index = (int) Math.ceil(percentile / 100.0 * size) - 1;
        if (index < 0) {
            index = 0;
        } else if (index >= size) {
            index = size - 1;
        }
        return index;
    }
}
