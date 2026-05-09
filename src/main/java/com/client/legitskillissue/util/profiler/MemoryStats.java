package com.client.legitskillissue.util.profiler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable snapshot of memory usage and garbage-collection statistics.
 *
 * <p>Captures the number of bytes allocated and freed during a measurement
 * window, the current heap usage, GC invocation count, total GC pause time,
 * and a per-class breakdown of allocation sizes.
 *
 * <p>The {@code allocationsByClass} map is defensively copied on construction
 * so that external modifications to the source map do not affect this
 * snapshot. The map returned by {@link #getAllocationsByClass()} is
 * unmodifiable.
 *
 * <p>Thread-safety: this class is immutable and therefore inherently
 * thread-safe.
 *
 * @see PerformanceProfiler
 */
public final class MemoryStats {

    /** Total bytes allocated during the measurement window. */
    private final long allocatedBytes;

    /** Total bytes freed (reclaimed by GC) during the measurement window. */
    private final long freedBytes;

    /** Current heap memory in use at the time of snapshot, in bytes. */
    private final long currentUsage;

    /** Number of garbage-collection cycles that occurred during the window. */
    private final int gcCount;

    /** Total wall-clock time spent in GC pauses during the window, in milliseconds. */
    private final long gcTotalTime;

    /**
     * Per-class allocation breakdown.
     *
     * <p>Keys are fully-qualified class names; values are the number of bytes
     * allocated for instances of that class during the measurement window.
     */
    private final Map<String, Long> allocationsByClass;

    /**
     * Constructs a new {@code MemoryStats} snapshot.
     *
     * <p>The {@code allocationsByClass} map is defensively copied; the caller
     * may safely modify the original map after this constructor returns.
     *
     * @param allocatedBytes    total bytes allocated during the window
     * @param freedBytes        total bytes freed during the window
     * @param currentUsage      current heap usage in bytes
     * @param gcCount           number of GC cycles during the window
     * @param gcTotalTime       total GC pause time in milliseconds
     * @param allocationsByClass per-class allocation map; must not be {@code null}
     * @throws NullPointerException if {@code allocationsByClass} is {@code null}
     */
    public MemoryStats(
            long allocatedBytes,
            long freedBytes,
            long currentUsage,
            int gcCount,
            long gcTotalTime,
            Map<String, Long> allocationsByClass) {
        this.allocatedBytes = allocatedBytes;
        this.freedBytes = freedBytes;
        this.currentUsage = currentUsage;
        this.gcCount = gcCount;
        this.gcTotalTime = gcTotalTime;
        // Defensive copy — prevents external mutation of this snapshot.
        this.allocationsByClass = Collections.unmodifiableMap(
                new HashMap<String, Long>(
                        Objects.requireNonNull(allocationsByClass, "allocationsByClass must not be null")));
    }

    /**
     * Returns the total number of bytes allocated during the measurement window.
     *
     * @return allocated bytes; non-negative
     */
    public long getAllocatedBytes() {
        return allocatedBytes;
    }

    /**
     * Returns the total number of bytes freed (reclaimed by GC) during the
     * measurement window.
     *
     * @return freed bytes; non-negative
     */
    public long getFreedBytes() {
        return freedBytes;
    }

    /**
     * Returns the current heap memory in use at the time this snapshot was
     * captured.
     *
     * @return current heap usage in bytes; non-negative
     */
    public long getCurrentUsage() {
        return currentUsage;
    }

    /**
     * Returns the number of garbage-collection cycles that occurred during the
     * measurement window.
     *
     * @return GC cycle count; non-negative
     */
    public int getGcCount() {
        return gcCount;
    }

    /**
     * Returns the total wall-clock time spent in GC pauses during the
     * measurement window.
     *
     * @return total GC pause time in milliseconds; non-negative
     */
    public long getGcTotalTime() {
        return gcTotalTime;
    }

    /**
     * Returns an unmodifiable view of the per-class allocation breakdown.
     *
     * <p>Keys are fully-qualified class names; values are the number of bytes
     * allocated for instances of that class during the measurement window.
     *
     * @return unmodifiable map of class name to allocated bytes; never {@code null}
     */
    public Map<String, Long> getAllocationsByClass() {
        return allocationsByClass;
    }

    /**
     * Returns a human-readable summary of this memory snapshot.
     *
     * @return string representation including key memory metrics
     */
    @Override
    public String toString() {
        return "MemoryStats{"
                + "allocatedBytes=" + allocatedBytes
                + ", freedBytes=" + freedBytes
                + ", currentUsage=" + currentUsage
                + ", gcCount=" + gcCount
                + ", gcTotalTime=" + gcTotalTime
                + ", allocationsByClass=" + allocationsByClass
                + '}';
    }

    /**
     * Checks equality based on all fields.
     *
     * @param o the object to compare
     * @return {@code true} if all fields are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MemoryStats)) {
            return false;
        }
        MemoryStats that = (MemoryStats) o;
        return allocatedBytes == that.allocatedBytes
                && freedBytes == that.freedBytes
                && currentUsage == that.currentUsage
                && gcCount == that.gcCount
                && gcTotalTime == that.gcTotalTime
                && allocationsByClass.equals(that.allocationsByClass);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = (int) (allocatedBytes ^ (allocatedBytes >>> 32));
        result = 31 * result + (int) (freedBytes ^ (freedBytes >>> 32));
        result = 31 * result + (int) (currentUsage ^ (currentUsage >>> 32));
        result = 31 * result + gcCount;
        result = 31 * result + (int) (gcTotalTime ^ (gcTotalTime >>> 32));
        result = 31 * result + allocationsByClass.hashCode();
        return result;
    }
}
