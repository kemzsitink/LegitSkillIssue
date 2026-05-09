package com.client.legitskillissue.util.profiler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Aggregates metrics from multiple {@link MetricSource} instances and provides
 * a single point of collection.
 *
 * <p>Sources are registered by name via {@link #register(String, MetricSource)}.
 * Calling {@link #collectAll()} polls every registered source and returns a
 * two-level map: {@code sourceName → (metricName → value)}.
 *
 * <h3>Thread safety</h3>
 * The internal source registry is backed by a {@link ConcurrentHashMap}, so
 * {@link #register} and {@link #collectAll} may be called concurrently from
 * any thread without external synchronization.
 *
 * @see MetricSource
 * @see PerformanceProfiler
 */
public final class MetricsCollector {

    /**
     * Registry of named metric sources.
     *
     * <p>Uses {@link ConcurrentHashMap} for thread-safe reads and writes
     * without explicit locking.
     */
    private final ConcurrentHashMap<String, MetricSource> sources;

    /**
     * Constructs a new, empty {@code MetricsCollector}.
     */
    public MetricsCollector() {
        this.sources = new ConcurrentHashMap<String, MetricSource>();
    }

    /**
     * Registers a {@link MetricSource} under the given name.
     *
     * <p>If a source with the same name is already registered, it is replaced
     * by the new source.
     *
     * @param name   unique name for the source; must not be {@code null}
     * @param source the metric source to register; must not be {@code null}
     * @throws NullPointerException if {@code name} or {@code source} is {@code null}
     */
    public void register(String name, MetricSource source) {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(source, "source must not be null");
        sources.put(name, source);
    }

    /**
     * Collects metrics from all registered sources and returns them as a
     * two-level map.
     *
     * <p>The outer map key is the source name supplied to
     * {@link #register(String, MetricSource)}. The inner map is the result of
     * calling {@link MetricSource#collectMetrics()} on that source. Both maps
     * are unmodifiable views.
     *
     * <p>If a source's {@link MetricSource#collectMetrics()} returns
     * {@code null}, that source is skipped and an empty inner map is used
     * instead.
     *
     * @return unmodifiable two-level map of source name to metric values;
     *         never {@code null}
     */
    public Map<String, Map<String, Double>> collectAll() {
        final Map<String, Map<String, Double>> result =
                new HashMap<String, Map<String, Double>>(sources.size() * 2);

        for (Map.Entry<String, MetricSource> entry : sources.entrySet()) {
            final String sourceName = entry.getKey();
            final MetricSource source = entry.getValue();

            Map<String, Double> metrics;
            try {
                metrics = source.collectMetrics();
            } catch (Exception e) {
                metrics = null;
            }

            if (metrics == null) {
                metrics = Collections.emptyMap();
            }

            result.put(sourceName, Collections.unmodifiableMap(
                    new HashMap<String, Double>(metrics)));
        }

        return Collections.unmodifiableMap(result);
    }

    /**
     * Returns the number of sources currently registered with this collector.
     *
     * @return registered source count; non-negative
     */
    public int sourceCount() {
        return sources.size();
    }
}
