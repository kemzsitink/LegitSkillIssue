package com.client.legitskillissue.util.profiler;

/**
 * Immutable comparison report between a captured {@link BaselineSnapshot} and a set of
 * current performance metrics.
 *
 * <p>A {@code BaselineReport} is produced by
 * {@link BaselineEstablisher#generateReport(double, long, double, double)} and exposes
 * four improvement-percentage helpers:
 * <ul>
 *   <li>{@link #getFpsImprovementPercent()} — positive means FPS improved (higher is better)</li>
 *   <li>{@link #getMemoryImprovementPercent()} — positive means memory usage decreased (lower is
 *       better)</li>
 *   <li>{@link #getTickTimeImprovementPercent()} — positive means tick time decreased (lower is
 *       better)</li>
 *   <li>{@link #getFrameTimeImprovementPercent()} — positive means frame time decreased (lower is
 *       better)</li>
 * </ul>
 *
 * <p>All improvement methods guard against division by zero: if the corresponding baseline
 * value is {@code 0}, the method returns {@code 0.0}.
 *
 * <p><b>Thread safety:</b> this class is immutable and therefore inherently thread-safe.
 *
 * @see BaselineEstablisher
 * @see BaselineSnapshot
 */
public final class BaselineReport {

    /** The baseline snapshot used as the reference point for comparison. */
    private final BaselineSnapshot baseline;

    /** Current frames per second at the time this report was generated. */
    private final double currentFps;

    /** Current heap memory in use at the time this report was generated, in bytes. */
    private final long currentMemoryUsed;

    /** Current game-tick execution time at the time this report was generated, in milliseconds. */
    private final double currentTickTimeMs;

    /** Current frame render time at the time this report was generated, in milliseconds. */
    private final double currentFrameTimeMs;

    /**
     * Constructs a new {@code BaselineReport}.
     *
     * @param baseline          the reference baseline snapshot; must not be {@code null}
     * @param currentFps        current frames per second; should be non-negative
     * @param currentMemoryUsed current heap memory in use, in bytes; should be non-negative
     * @param currentTickTimeMs current game-tick execution time, in milliseconds; should be
     *                          non-negative
     * @param currentFrameTimeMs current frame render time, in milliseconds; should be non-negative
     * @throws NullPointerException if {@code baseline} is {@code null}
     */
    public BaselineReport(
            BaselineSnapshot baseline,
            double currentFps,
            long currentMemoryUsed,
            double currentTickTimeMs,
            double currentFrameTimeMs) {
        if (baseline == null) {
            throw new NullPointerException("baseline must not be null");
        }
        this.baseline = baseline;
        this.currentFps = currentFps;
        this.currentMemoryUsed = currentMemoryUsed;
        this.currentTickTimeMs = currentTickTimeMs;
        this.currentFrameTimeMs = currentFrameTimeMs;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /**
     * Returns the baseline snapshot used as the reference point for this report.
     *
     * @return baseline snapshot; never {@code null}
     */
    public BaselineSnapshot getBaseline() {
        return baseline;
    }

    /**
     * Returns the current frames per second recorded in this report.
     *
     * @return current FPS; non-negative
     */
    public double getCurrentFps() {
        return currentFps;
    }

    /**
     * Returns the current heap memory in use recorded in this report.
     *
     * @return current memory used in bytes; non-negative
     */
    public long getCurrentMemoryUsed() {
        return currentMemoryUsed;
    }

    /**
     * Returns the current game-tick execution time recorded in this report.
     *
     * @return current tick time in milliseconds; non-negative
     */
    public double getCurrentTickTimeMs() {
        return currentTickTimeMs;
    }

    /**
     * Returns the current frame render time recorded in this report.
     *
     * @return current frame time in milliseconds; non-negative
     */
    public double getCurrentFrameTimeMs() {
        return currentFrameTimeMs;
    }

    // -------------------------------------------------------------------------
    // Improvement calculations
    // -------------------------------------------------------------------------

    /**
     * Calculates the FPS improvement percentage relative to the baseline.
     *
     * <p>A positive result indicates that the current FPS is higher than the baseline
     * (an improvement). A negative result indicates a regression.
     *
     * <p>Formula: {@code ((current - baseline) / baseline) * 100}
     *
     * @return FPS improvement as a percentage; {@code 0.0} if the baseline FPS is {@code 0}
     */
    public double getFpsImprovementPercent() {
        double baselineFps = baseline.getFps();
        if (baselineFps == 0.0) {
            return 0.0;
        }
        return ((currentFps - baselineFps) / baselineFps) * 100.0;
    }

    /**
     * Calculates the memory-usage improvement percentage relative to the baseline.
     *
     * <p>A positive result indicates that the current memory usage is lower than the baseline
     * (an improvement, since lower memory usage is better). A negative result indicates a
     * regression.
     *
     * <p>Formula: {@code ((baseline - current) / baseline) * 100}
     *
     * @return memory improvement as a percentage; {@code 0.0} if the baseline memory used is
     *         {@code 0}
     */
    public double getMemoryImprovementPercent() {
        long baselineMemory = baseline.getMemoryUsedBytes();
        if (baselineMemory == 0L) {
            return 0.0;
        }
        return ((double) (baselineMemory - currentMemoryUsed) / (double) baselineMemory) * 100.0;
    }

    /**
     * Calculates the tick-time improvement percentage relative to the baseline.
     *
     * <p>A positive result indicates that the current tick time is lower than the baseline
     * (an improvement, since lower tick time is better). A negative result indicates a
     * regression.
     *
     * <p>Formula: {@code ((baseline - current) / baseline) * 100}
     *
     * @return tick-time improvement as a percentage; {@code 0.0} if the baseline tick time is
     *         {@code 0}
     */
    public double getTickTimeImprovementPercent() {
        double baselineTickTime = baseline.getTickTimeMs();
        if (baselineTickTime == 0.0) {
            return 0.0;
        }
        return ((baselineTickTime - currentTickTimeMs) / baselineTickTime) * 100.0;
    }

    /**
     * Calculates the frame-time improvement percentage relative to the baseline.
     *
     * <p>A positive result indicates that the current frame time is lower than the baseline
     * (an improvement, since lower frame time is better). A negative result indicates a
     * regression.
     *
     * <p>Formula: {@code ((baseline - current) / baseline) * 100}
     *
     * @return frame-time improvement as a percentage; {@code 0.0} if the baseline frame time is
     *         {@code 0}
     */
    public double getFrameTimeImprovementPercent() {
        double baselineFrameTime = baseline.getFrameTimeMs();
        if (baselineFrameTime == 0.0) {
            return 0.0;
        }
        return ((baselineFrameTime - currentFrameTimeMs) / baselineFrameTime) * 100.0;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    /**
     * Returns a human-readable summary of this report.
     *
     * @return string representation including baseline reference and all current metric values
     */
    @Override
    public String toString() {
        return "BaselineReport{"
                + "baseline=" + baseline
                + ", currentFps=" + currentFps
                + ", currentMemoryUsed=" + currentMemoryUsed
                + ", currentTickTimeMs=" + currentTickTimeMs
                + ", currentFrameTimeMs=" + currentFrameTimeMs
                + ", fpsImprovementPercent=" + getFpsImprovementPercent()
                + ", memoryImprovementPercent=" + getMemoryImprovementPercent()
                + ", tickTimeImprovementPercent=" + getTickTimeImprovementPercent()
                + ", frameTimeImprovementPercent=" + getFrameTimeImprovementPercent()
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
        if (!(o instanceof BaselineReport)) {
            return false;
        }
        BaselineReport that = (BaselineReport) o;
        return Double.compare(that.currentFps, currentFps) == 0
                && currentMemoryUsed == that.currentMemoryUsed
                && Double.compare(that.currentTickTimeMs, currentTickTimeMs) == 0
                && Double.compare(that.currentFrameTimeMs, currentFrameTimeMs) == 0
                && baseline.equals(that.baseline);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = baseline.hashCode();
        long bits;
        bits = Double.doubleToLongBits(currentFps);
        result = 31 * result + (int) (bits ^ (bits >>> 32));
        result = 31 * result + (int) (currentMemoryUsed ^ (currentMemoryUsed >>> 32));
        bits = Double.doubleToLongBits(currentTickTimeMs);
        result = 31 * result + (int) (bits ^ (bits >>> 32));
        bits = Double.doubleToLongBits(currentFrameTimeMs);
        result = 31 * result + (int) (bits ^ (bits >>> 32));
        return result;
    }
}
