package com.client.legitskillissue.util.profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton coordinator for the performance profiling subsystem.
 *
 * <p>The profiler maintains a registry of named {@link ProfilerSection}
 * instances and a shared {@link MetricsCollector}. Callers obtain (or lazily
 * create) a section via {@link #getSection(String)}, then use the section's
 * {@link Profileable} API to bracket the code they want to measure.
 *
 * <h3>Singleton access</h3>
 * <pre>{@code
 * PerformanceProfiler profiler = PerformanceProfiler.getInstance();
 * ProfilerSection section = profiler.getSection("myModule.tick");
 * section.startProfiling();
 * // ... measured code ...
 * section.stopProfiling();
 * }</pre>
 *
 * <h3>Thread safety</h3>
 * <ul>
 *   <li>The singleton is initialised via the
 *       <em>initialization-on-demand holder</em> idiom, which is inherently
 *       thread-safe without synchronization.</li>
 *   <li>The sections map is a {@link ConcurrentHashMap}, so
 *       {@link #getSection(String)} and {@link #reset()} are safe to call
 *       concurrently from any thread.</li>
 *   <li>Individual {@link ProfilerSection} instances are themselves
 *       thread-safe.</li>
 * </ul>
 *
 * @see ProfilerSection
 * @see MetricsCollector
 */
public final class PerformanceProfiler {

    // -------------------------------------------------------------------------
    // Singleton
    // -------------------------------------------------------------------------

    /**
     * Initialization-on-demand holder for the singleton instance.
     *
     * <p>The JVM guarantees that the class is loaded and the field is
     * initialised exactly once, lazily, and without synchronization overhead
     * on subsequent accesses.
     */
    private static final class Holder {
        /** The single {@code PerformanceProfiler} instance. */
        static final PerformanceProfiler INSTANCE = new PerformanceProfiler();
    }

    /**
     * Returns the singleton {@code PerformanceProfiler} instance.
     *
     * @return singleton instance; never {@code null}
     */
    public static PerformanceProfiler getInstance() {
        return Holder.INSTANCE;
    }

    // -------------------------------------------------------------------------
    // Instance state
    // -------------------------------------------------------------------------

    /**
     * Registry of named profiler sections.
     *
     * <p>Uses {@link ConcurrentHashMap} for thread-safe, lock-free reads and
     * atomic put-if-absent semantics.
     */
    private final ConcurrentHashMap<String, ProfilerSection> sections;

    /** Shared metrics collector for all registered metric sources. */
    private final MetricsCollector metricsCollector;

    /**
     * Private constructor — use {@link #getInstance()} to obtain the singleton.
     */
    private PerformanceProfiler() {
        this.sections = new ConcurrentHashMap<String, ProfilerSection>();
        this.metricsCollector = new MetricsCollector();
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Returns the {@link ProfilerSection} registered under {@code name},
     * creating and registering a new one if none exists yet.
     *
     * <p>The creation is performed atomically using
     * {@link ConcurrentHashMap#putIfAbsent} to avoid duplicate sections under
     * concurrent access.
     *
     * @param name section name; must not be {@code null}
     * @return existing or newly created {@link ProfilerSection}; never
     *         {@code null}
     * @throws NullPointerException if {@code name} is {@code null}
     */
    public ProfilerSection getSection(String name) {
        if (name == null) {
            throw new NullPointerException("name must not be null");
        }
        ProfilerSection existing = sections.get(name);
        if (existing != null) {
            return existing;
        }
        final ProfilerSection newSection = new ProfilerSection(name);
        final ProfilerSection previous = sections.putIfAbsent(name, newSection);
        return previous != null ? previous : newSection;
    }

    /**
     * Returns an immutable snapshot list of {@link ProfilerStats} for every
     * registered section.
     *
     * <p>The list is a point-in-time snapshot; sections added after this call
     * returns will not appear in the returned list.
     *
     * @return list of stats snapshots; never {@code null}, may be empty
     */
    public List<ProfilerStats> getAllStats() {
        final List<ProfilerStats> result = new ArrayList<ProfilerStats>(sections.size());
        for (ProfilerSection section : sections.values()) {
            result.add(section.getStats());
        }
        return result;
    }

    /**
     * Removes all registered sections, effectively resetting the profiler to
     * its initial empty state.
     *
     * <p>Any {@link ProfilerSection} references held by callers remain valid
     * but will no longer be tracked by this profiler after the reset.
     */
    public void reset() {
        sections.clear();
    }

    /**
     * Returns the shared {@link MetricsCollector} used to aggregate metrics
     * from all registered {@link MetricSource} instances.
     *
     * @return shared metrics collector; never {@code null}
     */
    public MetricsCollector getMetricsCollector() {
        return metricsCollector;
    }
}
