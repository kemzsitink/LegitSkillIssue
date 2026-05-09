package com.client.legitskillissue.util.profiler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable snapshot of rendering pipeline statistics for a single frame or
 * measurement window.
 *
 * <p>Captures the total number of draw calls issued, vertices submitted,
 * texture binds performed, total render time, and a per-draw-call-type
 * breakdown of draw call counts.
 *
 * <p>The {@code drawCallsByType} map is defensively copied on construction so
 * that external modifications to the source map do not affect this snapshot.
 * The map returned by {@link #getDrawCallsByType()} is unmodifiable.
 *
 * <p>Thread-safety: this class is immutable and therefore inherently
 * thread-safe.
 *
 * @see PerformanceProfiler
 */
public final class RenderStats {

    /** Total number of OpenGL draw calls issued during the measurement window. */
    private final int drawCalls;

    /** Total number of vertices submitted to the GPU during the measurement window. */
    private final int vertices;

    /** Total number of texture bind operations performed during the measurement window. */
    private final int textureBinds;

    /** Total wall-clock time spent in rendering operations, in nanoseconds. */
    private final long renderTime;

    /**
     * Per-draw-call-type breakdown.
     *
     * <p>Keys are human-readable draw call type names (e.g., {@code "rectangle"},
     * {@code "circle"}, {@code "esp_box"}); values are the number of draw calls
     * of that type issued during the measurement window.
     */
    private final Map<String, Integer> drawCallsByType;

    /**
     * Constructs a new {@code RenderStats} snapshot.
     *
     * <p>The {@code drawCallsByType} map is defensively copied; the caller may
     * safely modify the original map after this constructor returns.
     *
     * @param drawCalls      total draw calls issued
     * @param vertices       total vertices submitted
     * @param textureBinds   total texture bind operations
     * @param renderTime     total render time in nanoseconds
     * @param drawCallsByType per-type draw call breakdown; must not be {@code null}
     * @throws NullPointerException if {@code drawCallsByType} is {@code null}
     */
    public RenderStats(
            int drawCalls,
            int vertices,
            int textureBinds,
            long renderTime,
            Map<String, Integer> drawCallsByType) {
        this.drawCalls = drawCalls;
        this.vertices = vertices;
        this.textureBinds = textureBinds;
        this.renderTime = renderTime;
        // Defensive copy — prevents external mutation of this snapshot.
        this.drawCallsByType = Collections.unmodifiableMap(
                new HashMap<String, Integer>(
                        Objects.requireNonNull(drawCallsByType, "drawCallsByType must not be null")));
    }

    /**
     * Returns the total number of OpenGL draw calls issued during the
     * measurement window.
     *
     * @return draw call count; non-negative
     */
    public int getDrawCalls() {
        return drawCalls;
    }

    /**
     * Returns the total number of vertices submitted to the GPU during the
     * measurement window.
     *
     * @return vertex count; non-negative
     */
    public int getVertices() {
        return vertices;
    }

    /**
     * Returns the total number of texture bind operations performed during the
     * measurement window.
     *
     * @return texture bind count; non-negative
     */
    public int getTextureBinds() {
        return textureBinds;
    }

    /**
     * Returns the total wall-clock time spent in rendering operations.
     *
     * @return render time in nanoseconds; non-negative
     */
    public long getRenderTime() {
        return renderTime;
    }

    /**
     * Returns an unmodifiable view of the per-draw-call-type breakdown.
     *
     * <p>Keys are human-readable draw call type names; values are the number
     * of draw calls of that type issued during the measurement window.
     *
     * @return unmodifiable map of draw call type to count; never {@code null}
     */
    public Map<String, Integer> getDrawCallsByType() {
        return drawCallsByType;
    }

    /**
     * Returns a human-readable summary of this render stats snapshot.
     *
     * @return string representation including key rendering metrics
     */
    @Override
    public String toString() {
        return "RenderStats{"
                + "drawCalls=" + drawCalls
                + ", vertices=" + vertices
                + ", textureBinds=" + textureBinds
                + ", renderTime=" + renderTime
                + ", drawCallsByType=" + drawCallsByType
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
        if (!(o instanceof RenderStats)) {
            return false;
        }
        RenderStats that = (RenderStats) o;
        return drawCalls == that.drawCalls
                && vertices == that.vertices
                && textureBinds == that.textureBinds
                && renderTime == that.renderTime
                && drawCallsByType.equals(that.drawCallsByType);
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = drawCalls;
        result = 31 * result + vertices;
        result = 31 * result + textureBinds;
        result = 31 * result + (int) (renderTime ^ (renderTime >>> 32));
        result = 31 * result + drawCallsByType.hashCode();
        return result;
    }
}
