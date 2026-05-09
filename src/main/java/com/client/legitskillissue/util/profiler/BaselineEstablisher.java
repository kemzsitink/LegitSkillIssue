package com.client.legitskillissue.util.profiler;

/**
 * Captures and stores a single {@link BaselineSnapshot} of client performance metrics,
 * and generates {@link BaselineReport} instances that compare current metrics against
 * that baseline.
 *
 * <h3>Typical usage</h3>
 * <pre>{@code
 * BaselineEstablisher establisher = new BaselineEstablisher();
 *
 * // Capture the baseline once (e.g., at startup or after warm-up)
 * establisher.captureBaseline(60.0, usedBytes, maxBytes, 12.0, 16.0);
 *
 * // Later, compare current metrics against the baseline
 * if (establisher.hasBaseline()) {
 *     BaselineReport report = establisher.generateReport(
 *             currentFps, currentMemoryUsed, currentTickTimeMs, currentFrameTimeMs);
 *     System.out.println("FPS improvement: " + report.getFpsImprovementPercent() + "%");
 * }
 * }</pre>
 *
 * <h3>Thread safety</h3>
 * <p>The stored snapshot field is declared {@code volatile} so that writes performed by
 * one thread are immediately visible to all other threads without additional
 * synchronization. Individual calls to {@link #captureBaseline} and
 * {@link #generateReport} are each atomic with respect to the snapshot reference, but
 * callers that need a consistent read-then-act sequence should perform their own
 * external synchronization.
 *
 * @see BaselineSnapshot
 * @see BaselineReport
 */
public final class BaselineEstablisher {

    /**
     * The most recently captured baseline snapshot, or {@code null} if no baseline has
     * been captured yet.
     *
     * <p>Declared {@code volatile} to ensure visibility across threads without requiring
     * explicit synchronization on every read.
     */
    private volatile BaselineSnapshot snapshot;

    /**
     * Constructs a new {@code BaselineEstablisher} with no baseline captured.
     */
    public BaselineEstablisher() {
        this.snapshot = null;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Captures a new baseline snapshot from the supplied performance metrics and stores
     * it, replacing any previously captured baseline.
     *
     * <p>The capture timestamp is set to {@link System#currentTimeMillis()} at the
     * moment this method is called.
     *
     * @param fps              frames per second at capture time; should be non-negative
     * @param memoryUsedBytes  heap memory in use at capture time, in bytes; should be
     *                         non-negative
     * @param memoryMaxBytes   maximum heap memory available at capture time, in bytes;
     *                         should be &ge; {@code memoryUsedBytes}
     * @param tickTimeMs       game-tick execution time at capture time, in milliseconds;
     *                         should be non-negative
     * @param frameTimeMs      frame render time at capture time, in milliseconds; should
     *                         be non-negative
     * @return the newly created and stored {@link BaselineSnapshot}; never {@code null}
     */
    public BaselineSnapshot captureBaseline(
            double fps,
            long memoryUsedBytes,
            long memoryMaxBytes,
            double tickTimeMs,
            double frameTimeMs) {
        final BaselineSnapshot newSnapshot = new BaselineSnapshot(
                fps,
                memoryUsedBytes,
                memoryMaxBytes,
                tickTimeMs,
                frameTimeMs,
                System.currentTimeMillis());
        this.snapshot = newSnapshot;
        return newSnapshot;
    }

    /**
     * Returns the stored baseline snapshot, or {@code null} if no baseline has been
     * captured yet.
     *
     * @return the stored {@link BaselineSnapshot}, or {@code null}
     */
    public BaselineSnapshot getBaseline() {
        return snapshot;
    }

    /**
     * Returns {@code true} if a baseline has been captured and is available for
     * comparison.
     *
     * @return {@code true} if {@link #getBaseline()} would return a non-{@code null}
     *         value; {@code false} otherwise
     */
    public boolean hasBaseline() {
        return snapshot != null;
    }

    /**
     * Generates a {@link BaselineReport} comparing the supplied current metrics against
     * the stored baseline snapshot.
     *
     * @param currentFps         current frames per second; should be non-negative
     * @param currentMemoryUsed  current heap memory in use, in bytes; should be
     *                           non-negative
     * @param currentTickTimeMs  current game-tick execution time, in milliseconds; should
     *                           be non-negative
     * @param currentFrameTimeMs current frame render time, in milliseconds; should be
     *                           non-negative
     * @return a new {@link BaselineReport} comparing current metrics to the baseline;
     *         never {@code null}
     * @throws IllegalStateException if no baseline has been captured yet (i.e.,
     *                               {@link #hasBaseline()} returns {@code false})
     */
    public BaselineReport generateReport(
            double currentFps,
            long currentMemoryUsed,
            double currentTickTimeMs,
            double currentFrameTimeMs) {
        final BaselineSnapshot current = snapshot;
        if (current == null) {
            throw new IllegalStateException(
                    "No baseline has been captured yet. Call captureBaseline() first.");
        }
        return new BaselineReport(
                current,
                currentFps,
                currentMemoryUsed,
                currentTickTimeMs,
                currentFrameTimeMs);
    }
}
