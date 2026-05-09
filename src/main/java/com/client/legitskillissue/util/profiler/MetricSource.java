package com.client.legitskillissue.util.profiler;

import java.util.Map;

/**
 * Represents a source of named performance metrics belonging to a specific
 * {@link MetricType} category.
 *
 * <p>Implementations are registered with a {@link MetricsCollector} and polled
 * on demand via {@link #collectMetrics()}. Each returned entry maps a
 * human-readable metric name (e.g., {@code "heapUsedBytes"}) to its current
 * {@code double} value.
 *
 * <p>Thread-safety: implementations must be thread-safe, as
 * {@link MetricsCollector#collectAll()} may be called from any thread.
 *
 * @see MetricsCollector
 * @see MetricType
 */
public interface MetricSource {

    /**
     * Collects and returns the current metric values for this source.
     *
     * <p>Keys are human-readable metric names; values are the corresponding
     * measurements. The returned map must not be {@code null}, but may be
     * empty if no metrics are currently available.
     *
     * @return map of metric name to current value; never {@code null}
     */
    Map<String, Double> collectMetrics();

    /**
     * Returns the {@link MetricType} category that this source belongs to.
     *
     * @return metric type; never {@code null}
     */
    MetricType getType();
}
