package com.client.legitskillissue.property;

import com.client.legitskillissue.util.profiler.BaselineEstablisher;
import com.client.legitskillissue.util.profiler.BaselineReport;
import com.client.legitskillissue.util.profiler.ProfilerSection;
import com.client.legitskillissue.util.profiler.ProfilerStats;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Property-based tests for the performance profiling infrastructure.
 *
 * <p>Tests Properties 1–4 from the design document, validating correctness of
 * top-N ranking, memory hotspot ranking, performance report calculations, and
 * statistical sampling completeness.
 */
public class PerformanceProfilerPropertyTest {

    // -------------------------------------------------------------------------
    // Static helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the top {@code n} entries from {@code stats} sorted by
     * {@link ProfilerStats#getTotalTime()} in descending order.
     *
     * @param stats source list; must not be {@code null}
     * @param n     number of entries to return; clamped to list size
     * @return sorted sublist of at most {@code n} entries
     */
    static List<ProfilerStats> topN(List<ProfilerStats> stats, int n) {
        List<ProfilerStats> sorted = new ArrayList<ProfilerStats>(stats);
        Collections.sort(sorted, new Comparator<ProfilerStats>() {
            @Override
            public int compare(ProfilerStats a, ProfilerStats b) {
                return Long.compare(b.getTotalTime(), a.getTotalTime());
            }
        });
        int limit = Math.min(n, sorted.size());
        return sorted.subList(0, limit);
    }

    /**
     * Returns the top {@code n} entries from {@code rates} sorted by value
     * (allocation rate) in descending order.
     *
     * @param rates source map of class name → allocation rate; must not be {@code null}
     * @param n     number of entries to return; clamped to map size
     * @return sorted list of at most {@code n} map entries
     */
    static List<Map.Entry<String, Long>> topNMemory(Map<String, Long> rates, int n) {
        List<Map.Entry<String, Long>> entries =
                new ArrayList<Map.Entry<String, Long>>(rates.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> a, Map.Entry<String, Long> b) {
                return Long.compare(b.getValue(), a.getValue());
            }
        });
        int limit = Math.min(n, entries.size());
        return entries.subList(0, limit);
    }

    // -------------------------------------------------------------------------
    // Arbitraries / Providers
    // -------------------------------------------------------------------------

    /**
     * Generates a list of 1–50 {@link ProfilerStats} objects with random
     * {@code totalTime} values drawn from {@code [1, Long.MAX_VALUE / 2]}.
     */
    @Provide
    Arbitrary<List<ProfilerStats>> profilerStatsList() {
        Arbitrary<Long> totalTimes = Arbitraries.longs().between(1L, Long.MAX_VALUE / 2);
        Arbitrary<ProfilerStats> statArbitrary = totalTimes.map(totalTime ->
                new ProfilerStats(
                        "section-" + totalTime,
                        totalTime,
                        1L,
                        totalTime,
                        totalTime,
                        (double) totalTime,
                        (double) totalTime,
                        (double) totalTime,
                        (double) totalTime));
        return statArbitrary.list().ofMinSize(1).ofMaxSize(50);
    }

    /**
     * Generates a map of 1–50 class names to random allocation rates drawn
     * from {@code [1, Long.MAX_VALUE / 2]}.
     */
    @Provide
    Arbitrary<Map<String, Long>> allocationRatesMap() {
        Arbitrary<String> classNames =
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20);
        Arbitrary<Long> rates = Arbitraries.longs().between(1L, Long.MAX_VALUE / 2);

        return Arbitraries.maps(classNames, rates).ofMinSize(1).ofMaxSize(50);
    }

    // -------------------------------------------------------------------------
    // Property 1 — Top-N Ranking Correctness
    // -------------------------------------------------------------------------

    /**
     * **Validates: Requirements 1.4**
     *
     * <p>For any list of {@link ProfilerStats} with varying {@code totalTime}
     * values, the top-10 hotspots returned by {@link #topN} must be the 10
     * entries with the highest {@code totalTime} in descending order.
     */
    @Property(tries = 100)
    void topNRankingIsCorrectlySortedDescending(
            @ForAll("profilerStatsList") List<ProfilerStats> stats) {

        List<ProfilerStats> result = topN(stats, 10);

        // Result size must be min(10, stats.size())
        int expectedSize = Math.min(10, stats.size());
        assertEquals(expectedSize, result.size(),
                "topN should return exactly min(10, size) entries");

        // Result must be sorted in descending order by totalTime
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(
                    result.get(i).getTotalTime() >= result.get(i + 1).getTotalTime(),
                    "Entry at index " + i + " must have totalTime >= entry at index " + (i + 1));
        }

        // Every entry in the result must have a totalTime >= the minimum totalTime
        // of any entry NOT in the result (i.e., the result contains the true top-N)
        if (stats.size() > 10) {
            long minInResult = result.get(result.size() - 1).getTotalTime();

            // Build a set of totalTimes in the result for comparison
            List<Long> resultTimes = new ArrayList<Long>();
            for (ProfilerStats s : result) {
                resultTimes.add(s.getTotalTime());
            }

            for (ProfilerStats s : stats) {
                if (!resultTimes.contains(s.getTotalTime())) {
                    assertTrue(
                            s.getTotalTime() <= minInResult,
                            "An entry outside the top-10 has a higher totalTime than the minimum in the result");
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Property 2 — Memory Hotspot Ranking Correctness
    // -------------------------------------------------------------------------

    /**
     * **Validates: Requirements 1.5**
     *
     * <p>For any map of class names to allocation rates, the top-10 memory
     * hotspots returned by {@link #topNMemory} must be the 10 entries with the
     * highest allocation rates in descending order.
     */
    @Property(tries = 100)
    void topNMemoryRankingIsCorrectlySortedDescending(
            @ForAll("allocationRatesMap") Map<String, Long> rates) {

        List<Map.Entry<String, Long>> result = topNMemory(rates, 10);

        // Result size must be min(10, rates.size())
        int expectedSize = Math.min(10, rates.size());
        assertEquals(expectedSize, result.size(),
                "topNMemory should return exactly min(10, size) entries");

        // Result must be sorted in descending order by allocation rate
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(
                    result.get(i).getValue() >= result.get(i + 1).getValue(),
                    "Entry at index " + i + " must have rate >= entry at index " + (i + 1));
        }

        // Every entry in the result must have a rate >= any entry NOT in the result
        if (rates.size() > 10) {
            long minInResult = result.get(result.size() - 1).getValue();

            // Collect keys present in the result
            List<String> resultKeys = new ArrayList<String>();
            for (Map.Entry<String, Long> e : result) {
                resultKeys.add(e.getKey());
            }

            for (Map.Entry<String, Long> e : rates.entrySet()) {
                if (!resultKeys.contains(e.getKey())) {
                    assertTrue(
                            e.getValue() <= minInResult,
                            "An entry outside the top-10 has a higher rate than the minimum in the result");
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Property 3 — Performance Report Calculation Accuracy
    // -------------------------------------------------------------------------

    /**
     * **Validates: Requirements 1.8**
     *
     * <p>For any pair of positive baseline FPS and current FPS values, the FPS
     * improvement percentage reported by {@link BaselineReport#getFpsImprovementPercent()}
     * must equal {@code ((current - baseline) / baseline) * 100} within a
     * floating-point tolerance of {@code 1e-9}.
     */
    @Property(tries = 100)
    void fpsImprovementPercentMatchesFormula(
            @ForAll("positiveFpsValues") double baselineFps,
            @ForAll("positiveFpsValues") double currentFps) {

        BaselineEstablisher establisher = new BaselineEstablisher();
        // Capture baseline with the generated FPS; other metrics are irrelevant here
        establisher.captureBaseline(baselineFps, 0L, 0L, 0.0, 0.0);

        BaselineReport report = establisher.generateReport(currentFps, 0L, 0.0, 0.0);

        double expected = ((currentFps - baselineFps) / baselineFps) * 100.0;
        double actual = report.getFpsImprovementPercent();

        assertEquals(expected, actual, 1e-9,
                "getFpsImprovementPercent() must match ((current - baseline) / baseline) * 100");
    }

    /**
     * Generates positive FPS values in the range {@code [1.0, 10000.0]}.
     */
    @Provide
    Arbitrary<Double> positiveFpsValues() {
        return Arbitraries.doubles().between(1.0, 10000.0).ofScale(2);
    }

    // -------------------------------------------------------------------------
    // Property 4 — Statistical Sampling Completeness
    // -------------------------------------------------------------------------

    /**
     * **Validates: Requirements 1.9**
     *
     * <p>For any {@link ProfilerSection} that has been called at least 1000
     * times, the reservoir must contain exactly {@code RESERVOIR_SIZE} (1000)
     * samples.
     */
    @Property(tries = 100)
    void reservoirIsFullAfterAtLeast1000Calls(
            @ForAll @IntRange(min = 1000, max = 2000) int callCount)
            throws Exception {

        ProfilerSection section = new ProfilerSection("testSection");

        for (int i = 0; i < callCount; i++) {
            section.startProfiling();
            section.stopProfiling();
        }

        // Access the private filledCount field via reflection
        Field filledCountField = ProfilerSection.class.getDeclaredField("filledCount");
        filledCountField.setAccessible(true);
        int filledCount = (int) filledCountField.get(section);

        // RESERVOIR_SIZE is package-private; read it via reflection
        Field reservoirSizeField = ProfilerSection.class.getDeclaredField("RESERVOIR_SIZE");
        reservoirSizeField.setAccessible(true);
        int expectedReservoirSize = (int) reservoirSizeField.get(null);

        assertEquals(expectedReservoirSize, filledCount,
                "filledCount must equal RESERVOIR_SIZE (" + expectedReservoirSize
                        + ") after " + callCount + " calls");
    }
}
