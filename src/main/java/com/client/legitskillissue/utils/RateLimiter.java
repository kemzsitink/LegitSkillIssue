package com.client.legitskillissue.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple rate limiter to prevent packet spam detection.
 * Uses token bucket algorithm for smooth rate limiting.
 */
public class RateLimiter {
    private final double permitsPerSecond;
    private final AtomicLong nextFreeTicketNanos;
    private final long intervalNanos;

    private RateLimiter(double permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
        this.intervalNanos = (long) (1_000_000_000.0 / permitsPerSecond);
        this.nextFreeTicketNanos = new AtomicLong(System.nanoTime());
    }

    /**
     * Creates a rate limiter with the specified permits per second.
     * 
     * @param permitsPerSecond Number of permits per second (e.g., 20.0 for server TPS)
     * @return A new RateLimiter instance
     */
    public static RateLimiter create(double permitsPerSecond) {
        return new RateLimiter(permitsPerSecond);
    }

    /**
     * Acquires a permit, blocking if necessary until one is available.
     * 
     * @return true if permit was acquired
     */
    public boolean tryAcquire() {
        long now = System.nanoTime();
        long nextFree = nextFreeTicketNanos.get();
        
        if (now >= nextFree) {
            // Try to acquire
            if (nextFreeTicketNanos.compareAndSet(nextFree, now + intervalNanos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Acquires a permit, waiting if necessary.
     * 
     * @return Time waited in milliseconds
     */
    public long acquire() {
        long now = System.nanoTime();
        long nextFree = nextFreeTicketNanos.getAndAdd(intervalNanos);
        
        long waitTime = Math.max(0, nextFree - now);
        if (waitTime > 0) {
            try {
                Thread.sleep(waitTime / 1_000_000, (int) (waitTime % 1_000_000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return waitTime / 1_000_000; // Convert to milliseconds
    }

    /**
     * Checks if a permit can be acquired without blocking.
     * 
     * @return true if permit is available
     */
    public boolean canAcquire() {
        long now = System.nanoTime();
        return now >= nextFreeTicketNanos.get();
    }

    /**
     * Gets the configured rate.
     * 
     * @return Permits per second
     */
    public double getRate() {
        return permitsPerSecond;
    }

    /**
     * Resets the rate limiter state.
     */
    public void reset() {
        nextFreeTicketNanos.set(System.nanoTime());
    }
}
