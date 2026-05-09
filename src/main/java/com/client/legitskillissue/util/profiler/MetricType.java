package com.client.legitskillissue.util.profiler;

/**
 * Classifies the kind of resource or subsystem that a {@link MetricSource}
 * measures.
 *
 * <p>Each constant represents a distinct performance domain:
 * <ul>
 *   <li>{@link #CPU} — processor time consumed by game logic or module code</li>
 *   <li>{@link #MEMORY} — heap allocation and garbage-collection activity</li>
 *   <li>{@link #RENDER} — OpenGL draw calls, vertex throughput, and frame time</li>
 *   <li>{@link #NETWORK} — packet send/receive rates and processing latency</li>
 * </ul>
 *
 * <p>Thread-safety: enum constants are inherently thread-safe.
 *
 * @see MetricSource
 * @see MetricsCollector
 */
public enum MetricType {

    /**
     * Processor time consumed by game logic, module tick handlers, or other
     * CPU-bound operations.
     */
    CPU,

    /**
     * Heap memory allocation rates, garbage-collection frequency, and pause
     * durations.
     */
    MEMORY,

    /**
     * Rendering pipeline metrics including draw call counts, vertex throughput,
     * texture bind operations, and frame time.
     */
    RENDER,

    /**
     * Network metrics including packet send/receive rates, packet sizes, and
     * processing latency.
     */
    NETWORK
}
