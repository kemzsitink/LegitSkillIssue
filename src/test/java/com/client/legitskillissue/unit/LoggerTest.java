package com.client.legitskillissue.unit;

import com.client.legitskillissue.util.logging.AsyncLogQueue;
import com.client.legitskillissue.util.logging.FileAppender;
import com.client.legitskillissue.util.logging.LogAppender;
import com.client.legitskillissue.util.logging.LogEntry;
import com.client.legitskillissue.util.logging.LogLevel;
import com.client.legitskillissue.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Logger system.
 *
 * <p>Validates Requirements 12.6: level-gated formatting, async ordering,
 * and file rotation.
 *
 * <p>The Logger constructor and FileAppender constants are package-private;
 * reflection is used to access them from this test package.
 */
class LoggerTest {

    /** Known value of FileAppender.MAX_FILE_SIZE_BYTES (10 MB). */
    private static final long MAX_FILE_SIZE_BYTES = readMaxFileSizeBytes();

    /** Temporary directory used by file-rotation tests. */
    private File tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("logger-test-").toFile();
    }

    @AfterEach
    void tearDown() {
        if (tempDir != null && tempDir.exists()) {
            File[] files = tempDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
            tempDir.delete();
        }
    }

    // ── Test 1: disabled levels skip String.format ────────────────────────────

    /**
     * When a level is below the configured minLevel the appender must never
     * be called, proving that String.format is not invoked on the hot path.
     *
     * <p>Validates: Requirements 12.6
     */
    @Test
    void disabledLevelSkipsAppender() throws Exception {
        CountingAppender appender = new CountingAppender();
        List<LogAppender> appenders = Collections.<LogAppender>singletonList(appender);
        AsyncLogQueue queue = new AsyncLogQueue(appenders);
        queue.start();

        // minLevel = WARN, so DEBUG is disabled
        Logger logger = newLogger("test-disabled", LogLevel.WARN, appenders, queue);

        logger.debug("expensive format %s %s %s", "a", "b", "c");

        // Give the queue a moment to drain (it should drain nothing)
        Thread.sleep(100);
        queue.shutdown();

        assertEquals(0, appender.getCount(),
                "Appender must not be called when the log level is disabled");
    }

    // ── Test 2: async queue drains entries in order ───────────────────────────

    /**
     * Entries enqueued in a specific order must be delivered to the appender
     * in exactly that order after the queue is shut down.
     *
     * <p>Validates: Requirements 12.6
     */
    @Test
    void asyncQueueDrainsEntriesInOrder() throws Exception {
        OrderCapturingAppender appender = new OrderCapturingAppender();
        List<LogAppender> appenders = Collections.<LogAppender>singletonList(appender);
        AsyncLogQueue queue = new AsyncLogQueue(appenders);
        queue.start();

        Logger logger = newLogger("test-order", LogLevel.DEBUG, appenders, queue);

        List<String> expectedMessages = Arrays.asList(
                "message-1", "message-2", "message-3", "message-4", "message-5"
        );

        for (String msg : expectedMessages) {
            logger.info(msg);
        }

        // Shutdown waits up to 2 s for the drain thread to finish
        queue.shutdown();

        List<String> received = appender.getMessages();
        assertEquals(expectedMessages.size(), received.size(),
                "All enqueued entries must be delivered");
        for (int i = 0; i < expectedMessages.size(); i++) {
            assertEquals(expectedMessages.get(i), received.get(i),
                    "Entry at index " + i + " must match the enqueue order");
        }
    }

    // ── Test 3: log rotation triggers at 10 MB ────────────────────────────────

    /**
     * Writing more than MAX_FILE_SIZE_BYTES to a FileAppender must trigger
     * rotation: a backup file (.1) is created and the active file is smaller
     * than the limit.
     *
     * <p>Validates: Requirements 12.6
     */
    @Test
    void logRotationTriggersAtTenMegabytes() throws IOException {
        String logPath = new File(tempDir, "app.log").getAbsolutePath();
        FileAppender fileAppender = new FileAppender(logPath);

        // Build a 1 KB message so we can count writes precisely.
        int lineLength = 1024;
        StringBuilder sb = new StringBuilder(lineLength);
        for (int i = 0; i < lineLength; i++) {
            sb.append('A');
        }
        String paddedMessage = sb.toString();

        // Write enough entries to exceed 10 MB.
        int writes = (int) (MAX_FILE_SIZE_BYTES / lineLength) + 500;

        for (int i = 0; i < writes; i++) {
            LogEntry entry = new LogEntry(
                    System.currentTimeMillis(),
                    LogLevel.INFO,
                    "rotation-test",
                    Thread.currentThread().getName(),
                    paddedMessage,
                    null
            );
            fileAppender.append(entry);
        }

        fileAppender.close();

        File activeFile = new File(logPath);
        File backupFile = new File(logPath + ".1");

        assertTrue(backupFile.exists(),
                "Backup file (.1) must exist after rotation");
        assertTrue(activeFile.length() < MAX_FILE_SIZE_BYTES,
                "Active log file must be smaller than MAX_FILE_SIZE_BYTES after rotation; "
                        + "actual size: " + activeFile.length());
    }

    // ── Reflection helpers ────────────────────────────────────────────────────

    /**
     * Creates a Logger using its package-private testing constructor via
     * reflection, since the test lives in a different package.
     */
    private static Logger newLogger(String name, LogLevel minLevel,
                                    List<LogAppender> appenders,
                                    AsyncLogQueue asyncQueue) throws Exception {
        Constructor<Logger> ctor = Logger.class.getDeclaredConstructor(
                String.class, LogLevel.class, List.class, AsyncLogQueue.class);
        ctor.setAccessible(true);
        return ctor.newInstance(name, minLevel, appenders, asyncQueue);
    }

    /**
     * Reads the package-private {@code MAX_FILE_SIZE_BYTES} constant from
     * {@link FileAppender} via reflection.
     */
    private static long readMaxFileSizeBytes() {
        try {
            Field f = FileAppender.class.getDeclaredField("MAX_FILE_SIZE_BYTES");
            f.setAccessible(true);
            return f.getLong(null);
        } catch (Exception e) {
            // Fall back to the known value if reflection fails
            return 10L * 1024L * 1024L;
        }
    }

    // ── Helper appenders ──────────────────────────────────────────────────────

    /** Counts how many times {@code append} is called. */
    private static final class CountingAppender implements LogAppender {
        private int count = 0;

        @Override
        public synchronized void append(LogEntry entry) {
            count++;
        }

        synchronized int getCount() {
            return count;
        }
    }

    /** Records the message of every entry in arrival order. */
    private static final class OrderCapturingAppender implements LogAppender {
        private final List<String> messages = new CopyOnWriteArrayList<String>();

        @Override
        public void append(LogEntry entry) {
            messages.add(entry.getMessage());
        }

        List<String> getMessages() {
            return new ArrayList<String>(messages);
        }
    }
}
