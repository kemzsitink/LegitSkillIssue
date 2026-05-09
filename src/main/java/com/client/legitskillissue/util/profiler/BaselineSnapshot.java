package com.client.legitskillissue.util.profiler;

/**
 * Immutable snapshot of baseline performance metrics captured at a specific point in time.
 *
 * <p>A {@code BaselineSnapshot} records the four key performance indicators used to
 * evaluate client performance:
 * <ul>
 *   <li><b>FPS</b> — frames rendered per second</li>
 *   <li><b>Memory</b> — heap memory used and maximum heap available, in bytes</li>
 *   <li><b>Tick time</b> — wall-clock time to execute one game tick, in milliseconds</li>
 *   <li><b>Frame time</b> — wall-clock time to render one frame, in milliseconds</li>
 * </ul>
 *
 * <p>Instances are created by {@link BaselineEstablisher#captureBaseline} and are
 * immutable once constructed. All fields are set through the constructor and cannot
 * be modified afterwards.
 *
 * <p><b>Thread safety:</b> this class is immutable and therefore inherently thread-safe.
 *
 * @see BaselineEstablisher
 * @see BaselineReport
 */
public final class BaselineSnapshot {

    /** Frames rendered per second at the time of capture. */
    private final double fps;

    /** Heap memory in use at the time of capture, in bytes. */
    private final long memoryUsedBytes;

    /** Maximum heap memory available to the JVM at the time of capture, in bytes. */
    private final long memoryMaxBytes;

    /** Wall-clock time to execute one game tick at the time of capture, in milliseconds. */
    private final double tickTimeMs;

    /** Wall-clock time to render one frame at the time of capture, in milliseconds. */
    private final double frameTimeMs;

    /** Epoch milliseconds (from {@link System#currentTimeMillis()}) when this snapshot was taken. */
    private final long capturedAt;

    /**
     * Constructs a new {@code BaselineSnapshot} with the supplied metric values.
     *
     * @param fps              frames per second at capture time; should be non-negative
     * @param memoryUsedBytes  heap memory in use at capture time, in bytes; should be non-negative
     * @param memoryMaxBytes   maximum heap memory available at capture time, in bytes; should be
     *                         &ge; {@code memoryUsedBytes}
     * @param tickTimeMs       game-tick execution time at capture time, in milliseconds; should be
     *                         non-negative
     * @param frameTimeMs      frame render time at capture time, in milliseconds; should be
     *                         non-negative
     * @param capturedAt       epoch milliseconds when this snapshot was taken
     */
    public BaselineSnapshot(
            double fps,
            long memoryUsedBytes,
            long memoryMaxBytes,
            double tickTimeMs,
            double frameTimeMs,
            long capturedAt) {
        this.fps = fps;
        this.memoryUsedBytes = memoryUsedBytes;
        this.memoryMaxBytes = memoryMaxBytes;
        this.tickTimeMs = tickTimeMs;
        this.frameTimeMs = frameTimeMs;
        this.capturedAt = capturedAt;
    }

    /**
     * Returns the frames-per-second value recorded in this snapshot.
     *
     * @return FPS at capture time; non-negative
     */
    public double getFps() {
        return fps;
    }

    /**
     * Returns the heap memory in use at the time this snapshot was captured.
     *
     * @return used heap memory in bytes; non-negative
     */
    public long getMemoryUsedBytes() {
        return memoryUsedBytes;
    }

    /**
     * Returns the maximum heap memory available to the JVM at the time this snapshot was captured.
     *
     * @return maximum heap memory in bytes; non-negative
     */
    public long getMemoryMaxBytes() {
        return memoryMaxBytes;
    }

    /**
     * Returns the game-tick execution time recorded in this snapshot.
     *
     * @return tick time in milliseconds; non-negative
     */
    public double getTickTimeMs() {
        return tickTimeMs;
    }

    /**
     * Returns the frame render time recorded in this snapshot.
     *
     * @return frame time in milliseconds; non-negative
     */
    public double getFrameTimeMs() {
        return frameTimeMs;
    }

    /**
     * Returns the epoch milliseconds at which this snapshot was captured.
     *
     * <p>The value is obtained from {@link System#currentTimeMillis()} at capture time.
     *
     * @return capture timestamp in epoch milliseconds
     */
    public long getCapturedAt() {
        return capturedAt;
    }

    /**
     * Returns a human-readable summary of this baseline snapshot.
     *
     * @return string representation including all metric values and capture timestamp
     */
    @Override
    public String toString() {
        return "BaselineSnapshot{"
                + "fps=" + fps
                + ", memoryUsedBytes=" + memoryUsedBytes
                + ", memoryMaxBytes=" + memoryMaxBytes
                + ", tickTimeMs=" + tickTimeMs
                + ", frameTimeMs=" + frameTimeMs
                + ", capturedAt=" + capturedAt
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
        if (!(o instanceof BaselineSnapshot)) {
            return false;
        }
        BaselineSnapshot that = (BaselineSnapshot) o;
        return Double.compare(that.fps, fps) == 0
                && memoryUsedBytes == that.memoryUsedBytes
                && memoryMaxBytes == that.memoryMaxBytes
                && Double.compare(that.tickTimeMs, tickTimeMs) == 0
                && Double.compare(that.frameTimeMs, frameTimeMs) == 0
                && capturedAt == that.capturedAt;
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result;
        long bits;
        bits = Double.doubleToLongBits(fps);
        result = (int) (bits ^ (bits >>> 32));
        result = 31 * result + (int) (memoryUsedBytes ^ (memoryUsedBytes >>> 32));
        result = 31 * result + (int) (memoryMaxBytes ^ (memoryMaxBytes >>> 32));
        bits = Double.doubleToLongBits(tickTimeMs);
        result = 31 * result + (int) (bits ^ (bits >>> 32));
        bits = Double.doubleToLongBits(frameTimeMs);
        result = 31 * result + (int) (bits ^ (bits >>> 32));
        result = 31 * result + (int) (capturedAt ^ (capturedAt >>> 32));
        return result;
    }
}
