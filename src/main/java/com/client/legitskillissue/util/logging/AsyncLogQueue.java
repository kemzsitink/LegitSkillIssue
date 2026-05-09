package com.client.legitskillissue.util.logging;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Non-blocking log queue that decouples log-producing threads from
 * {@link LogAppender} I/O.
 *
 * <p>Internally backed by a {@link LinkedBlockingQueue}.  A single daemon
 * thread drains the queue and dispatches each {@link LogEntry} to every
 * registered appender.  Because the drain thread is a daemon thread it will
 * not prevent JVM shutdown.
 *
 * <p>Usage:
 * <pre>{@code
 * AsyncLogQueue queue = new AsyncLogQueue(appenders);
 * queue.start();
 * queue.enqueue(entry);
 * // …
 * queue.shutdown(); // optional — daemon thread exits automatically on JVM exit
 * }</pre>
 *
 * <p>Thread safety: {@link #enqueue} is safe to call from any thread.
 * {@link #start} and {@link #shutdown} should be called from a single
 * controlling thread.
 */
public final class AsyncLogQueue {

    private static final int DEFAULT_CAPACITY = 8192;

    private final LinkedBlockingQueue<LogEntry> queue;
    private final List<LogAppender> appenders;
    private volatile boolean running;
    private Thread drainThread;

    /**
     * Constructs an {@code AsyncLogQueue} with the default internal capacity.
     *
     * @param appenders the appenders to dispatch entries to; must not be
     *                  {@code null} and should not be modified after this call
     */
    public AsyncLogQueue(List<LogAppender> appenders) {
        this(appenders, DEFAULT_CAPACITY);
    }

    /**
     * Constructs an {@code AsyncLogQueue} with a custom internal capacity.
     *
     * @param appenders the appenders to dispatch entries to
     * @param capacity  maximum number of entries that can be queued before
     *                  {@link #enqueue} blocks
     */
    public AsyncLogQueue(List<LogAppender> appenders, int capacity) {
        if (appenders == null) {
            throw new IllegalArgumentException("appenders must not be null");
        }
        this.appenders = appenders;
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    /**
     * Starts the background drain thread.
     *
     * <p>The thread is marked as a daemon so it does not prevent JVM shutdown.
     * Calling {@code start()} more than once has no effect if the thread is
     * already running.
     */
    public synchronized void start() {
        if (running) {
            return;
        }
        running = true;
        drainThread = new Thread(this::drainLoop, "AsyncLogQueue-drain");
        drainThread.setDaemon(true);
        drainThread.start();
    }

    /**
     * Signals the drain thread to stop after processing all currently queued
     * entries, then waits for it to finish (up to 2 seconds).
     *
     * <p>After this method returns no further entries will be dispatched.
     */
    public synchronized void shutdown() {
        if (!running) {
            return;
        }
        running = false;
        if (drainThread != null) {
            drainThread.interrupt();
            try {
                drainThread.join(2000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            drainThread = null;
        }
    }

    /**
     * Adds a log entry to the queue.
     *
     * <p>If the queue is full this method blocks until space becomes available.
     * In practice the drain thread should keep the queue nearly empty, so
     * blocking is rare.
     *
     * @param entry the entry to enqueue; must not be {@code null}
     */
    public void enqueue(LogEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("entry must not be null");
        }
        try {
            queue.put(entry);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Returns the number of entries currently waiting in the queue.
     * Intended for monitoring / testing.
     */
    public int size() {
        return queue.size();
    }

    // ── drain loop ────────────────────────────────────────────────────────────

    private void drainLoop() {
        while (running || !queue.isEmpty()) {
            try {
                LogEntry entry = queue.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS);
                if (entry != null) {
                    dispatch(entry);
                }
            } catch (InterruptedException e) {
                // Re-check running flag; drain remaining entries before exiting
                Thread.currentThread().interrupt();
                break;
            }
        }
        // Drain any remaining entries after shutdown signal
        LogEntry remaining;
        while ((remaining = queue.poll()) != null) {
            dispatch(remaining);
        }
    }

    private void dispatch(LogEntry entry) {
        for (LogAppender appender : appenders) {
            try {
                appender.append(entry);
            } catch (Exception e) {
                System.err.println("[AsyncLogQueue] Appender threw exception: " + e.getMessage());
            }
        }
    }
}
