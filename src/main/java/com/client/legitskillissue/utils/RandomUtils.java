package com.client.legitskillissue.utils;

import java.util.Random;

/**
 * Utility class for generating realistic random values.
 * Uses Gaussian distribution to mimic human behavior patterns.
 */
public class RandomUtils {
    private static final Random random = new Random();

    /**
     * Generates a Gaussian-distributed random value.
     * 
     * @param mean The mean (center) of the distribution
     * @param stdDev The standard deviation (spread)
     * @return A random value following Gaussian distribution
     */
    public static double gaussianRandom(double mean, double stdDev) {
        return mean + stdDev * random.nextGaussian();
    }

    /**
     * Generates a Gaussian-distributed random value clamped to a range.
     * 
     * @param mean The mean (center) of the distribution
     * @param stdDev The standard deviation (spread)
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @return A clamped random value following Gaussian distribution
     */
    public static double gaussianRandomClamped(double mean, double stdDev, double min, double max) {
        double value = gaussianRandom(mean, stdDev);
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Generates a realistic human reaction time delay in milliseconds.
     * Average human reaction time: 200-250ms with ~50ms standard deviation.
     * 
     * @return Delay in milliseconds
     */
    public static long humanReactionDelay() {
        return (long) gaussianRandomClamped(225.0, 50.0, 150.0, 400.0);
    }

    /**
     * Generates a realistic click delay for AutoClicker/TriggerBot.
     * 
     * @param targetCPS Target clicks per second
     * @return Delay in milliseconds
     */
    public static long clickDelay(double targetCPS) {
        double meanDelay = 1000.0 / targetCPS;
        double stdDev = meanDelay * 0.15; // 15% variation
        return (long) gaussianRandomClamped(meanDelay, stdDev, meanDelay * 0.7, meanDelay * 1.3);
    }

    /**
     * Standard uniform random for non-critical uses.
     */
    public static Random getRandom() {
        return random;
    }
}
