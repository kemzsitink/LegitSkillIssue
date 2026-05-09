package com.client.legitskillissue.util.profiler;

import java.util.Objects;

/**
 * Immutable snapshot of performance statistics for a single profiled section.
 *
 * <p>All time values are stored in nanoseconds. Use {@link #getAvgTimeMs()} to
 * obtain the average execution time converted to milliseconds for display
 * purposes.
 *
 * <p>Instances are created once and never mutated; all fields are set through
 * the constructor.
 *
 * <p>Thread-safety: this class is immutable and therefore inherently
 * thread-safe.
 *
 * @see PerformanceProfiler
 */
public final class ProfilerStats {

    /** Human-readable name of the profiled section. */
    private final String name;

    /** Total accumulated execution time across all recorded calls, in nanoseconds. */
    private final long totalTime;

    /** Number of times the profiled section was invoked. */
    private final long callCount;

    /** Minimum single-call execution time observed, in nanoseconds. */
    private final long minTime;

    /** Maximum single-call execution time observed, in nanoseconds. */
    private final long maxTime;

    /** Arithmetic mean of all recorded execution times, in nanoseconds. */
    private final double avgTime;

    /** 50th-percentile (median) execution time, in nanoseconds. */
    private final double p50;

    /** 95th-percentile execution time, in nanoseconds. */
    private final double p95;

    /** 99th-percentile execution time, in nanoseconds. */
    private final double p99;

    /**
     * Constructs a new {@code ProfilerStats} snapshot.
     *
     * @param name      human-readable name of the profiled section; must not be {@code null}
     * @param totalTime total accumulated execution time in nanoseconds
     * @param callCount number of invocations recorded
     * @param minTime   minimum single-call execution time in nanoseconds
     * @param maxTime   maximum single-call execution time in nanoseconds
     * @param avgTime   arithmetic mean execution time in nanoseconds
     * @param p50       50th-percentile execution time in nanoseconds
     * @param p95       95th-percentile execution time in nanoseconds
     * @param p99       99th-percentile execution time in nanoseconds
     * @throws NullPointerException if {@code name} is {@code null}
     */
    public ProfilerStats(
            String name,
            long totalTime,
            long callCount,
            long minTime,
            long maxTime,
            double avgTime,
            double p50,
            double p95,
            double p99) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.totalTime = totalTime;
        this.callCount = callCount;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.avgTime = avgTime;
        this.p50 = p50;
        this.p95 = p95;
        this.p99 = p99;
    }

    /**
     * Returns the human-readable name of the profiled section.
     *
     * @return section name; never {@code null}
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the total accumulated execution time across all recorded calls.
     *
     * @return total time in nanoseconds
     */
    public long getTotalTime() {
        return totalTime;
    }

    /**
     * Returns the number of times the profiled section was invoked.
     *
     * @return call count; zero if the section was never entered
     */
    public long getCallCount() {
        return callCount;
    }

    /**
     * Returns the minimum single-call execution time observed.
     *
     * @return minimum time in nanoseconds; {@link Long#MAX_VALUE} if no calls were recorded
     */
    public long getMinTime() {
        return minTime;
    }

    /**
     * Returns the maximum single-call execution time observed.
     *
     * @return maximum time in nanoseconds; zero if no calls were recorded
     */
    public long getMaxTime() {
        return maxTime;
    }

    /**
     * Returns the arithmetic mean of all recorded execution times.
     *
     * @return average time in nanoseconds; zero if no calls were recorded
     */
    public double getAvgTime() {
        return avgTime;
    }

    /**
     * Returns the 50th-percentile (median) execution time.
     *
     * @return p50 latency in nanoseconds
     */
    public double getP50() {
        return p50;
    }

    /**
     * Returns the 95th-percentile execution time.
     *
     * @return p95 latency in nanoseconds
     */
    public double getP95() {
        return p95;
    }

    /**
     * Returns the 99th-percentile execution time.
     *
     * @return p99 latency in nanoseconds
     */
    public double getP99() {
        return p99;
    }

    /**
     * Converts the average execution time from nanoseconds to milliseconds.
     *
     * <p>Equivalent to {@code getAvgTime() / 1_000_000.0}.
     *
     * @return average execution time in milliseconds
     */
    public double getAvgTimeMs() {
        return avgTime / 1_000_000.0;
    }

    /**
     * Returns a human-readable summary of this stats snapshot.
     *
     * @return string representation including name, call count, and average time
     */
    @Override
    public String toString() {
        return "ProfilerStats{"
                + "name='" + name + '\''
                + ", callCount=" + callCount
                + ", avgTimeMs=" + getAvgTimeMs()
                + ", minTime=" + minTime
                + ", maxTime=" + maxTime
                + ", p50=" + p50
                + ", p95=" + p95
                + ", p99=" + p99
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
        if (!(o instanceof ProfilerStats)) {
            return false;
        }
        ProfilerStats that = (ProfilerStats) o;
        return totalTime == that.totalTime
                && callCount == that.callCount
                && minTime == that.minTime
                && maxTime == that.maxTime
                && Double.compare(that.avgTime, avgTime) == 0
                && Double.compare(that.p50, p50) == 0
                && Double.compare(that.p95, p95) == 0
                && Double.compare(that.p99, p99) == 0
                && name.equals(that.name);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (int) (totalTime ^ (totalTime >>> 32));
        result = 31 * result + (int) (callCount ^ (callCount >>> 32));
        result = 31 * result + (int) (minTime ^ (minTime >>> 32));
        result = 31 * result + (int) (maxTime ^ (maxTime >>> 32));
        long bits;
        bits = Double.doubleToLongBits(avgTime);
        result = 31 * result + (int) (bits ^ (bits >>> 32));
        bits = Double.doubleToLongBits(p50);
        result = 31 * result + (int) (bits ^ (bits >>> 32));
        bits = Double.doubleToLongBits(p95);
        result = 31 * result + (int) (bits ^ (bits >>> 32));
        bits = Double.doubleToLongBits(p99);
        result = 31 * result + (int) (bits ^ (bits >>> 32));
        return result;
    }
}
