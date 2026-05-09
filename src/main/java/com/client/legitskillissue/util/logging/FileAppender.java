package com.client.legitskillissue.util.logging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

/**
 * {@link LogAppender} that writes log entries to a rotating set of files.
 *
 * <p>Rotation policy:
 * <ul>
 *   <li>Maximum file size: {@value #MAX_FILE_SIZE_BYTES} bytes (10 MB).</li>
 *   <li>Maximum number of files kept: {@value #MAX_FILES}.</li>
 *   <li>When the active file reaches the size limit it is closed, existing
 *       backup files are shifted (e.g. {@code app.log.4} is deleted,
 *       {@code app.log.3} becomes {@code app.log.4}, …, {@code app.log}
 *       becomes {@code app.log.1}), and a new {@code app.log} is opened.</li>
 * </ul>
 *
 * <p>All files are written in UTF-8 encoding.
 *
 * <p>Thread safety: all public methods are {@code synchronized} on {@code this}.
 */
public final class FileAppender implements LogAppender {

    /** Maximum size of a single log file before rotation (10 MB). */
    static final long MAX_FILE_SIZE_BYTES = 10L * 1024L * 1024L;

    /** Number of backup files to retain (plus the active file = 5 total). */
    static final int MAX_FILES = 5;

    private final File baseFile;
    private BufferedWriter writer;
    private long currentFileSize;

    /**
     * Constructs a {@code FileAppender} that writes to the given base file path.
     *
     * @param baseFilePath path of the active log file (e.g. {@code "logs/app.log"})
     * @throws IOException if the file cannot be opened for writing
     */
    public FileAppender(String baseFilePath) throws IOException {
        this.baseFile = new File(baseFilePath);
        ensureParentDirs();
        openWriter(true /* append */);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Formats the entry identically to {@link ConsoleAppender} and writes it
     * to the current log file.  Triggers rotation if the file has reached the
     * size limit.
     */
    @Override
    public synchronized void append(LogEntry entry) {
        String line = format(entry);
        try {
            if (currentFileSize >= MAX_FILE_SIZE_BYTES) {
                rotate();
            }
            writer.write(line);
            writer.newLine();
            writer.flush();
            // +1 for the platform line separator (approximate; good enough for
            // rotation purposes — exact byte counting would require encoding the
            // string, which is more expensive than necessary here).
            currentFileSize += line.length() + 1;
        } catch (IOException e) {
            System.err.println("[FileAppender] Failed to write log entry: " + e.getMessage());
        }
    }

    /**
     * Closes the underlying writer, flushing any buffered data.
     * After this call the appender must not be used.
     */
    public synchronized void close() {
        if (writer != null) {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                System.err.println("[FileAppender] Failed to close writer: " + e.getMessage());
            } finally {
                writer = null;
            }
        }
    }

    // ── internal helpers ─────────────────────────────────────────────────────

    /**
     * Rotates log files:
     * <ol>
     *   <li>Close the current writer.</li>
     *   <li>Delete the oldest backup (index {@value #MAX_FILES} - 1).</li>
     *   <li>Shift backups: {@code .N-1} → {@code .N}, …, {@code .1} → {@code .2}.</li>
     *   <li>Rename the active file to {@code .1}.</li>
     *   <li>Open a fresh active file.</li>
     * </ol>
     */
    private void rotate() throws IOException {
        closeWriter();

        // Delete the oldest backup if it exists
        File oldest = backupFile(MAX_FILES - 1);
        if (oldest.exists()) {
            oldest.delete();
        }

        // Shift existing backups: .3 → .4, .2 → .3, .1 → .2
        for (int i = MAX_FILES - 2; i >= 1; i--) {
            File src = backupFile(i);
            if (src.exists()) {
                src.renameTo(backupFile(i + 1));
            }
        }

        // Rename active file to .1
        if (baseFile.exists()) {
            baseFile.renameTo(backupFile(1));
        }

        openWriter(false /* do not append — fresh file */);
    }

    private File backupFile(int index) {
        return new File(baseFile.getParentFile(), baseFile.getName() + "." + index);
    }

    private void openWriter(boolean append) throws IOException {
        FileOutputStream fos = new FileOutputStream(baseFile, append);
        writer = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
        currentFileSize = append ? baseFile.length() : 0L;
    }

    private void closeWriter() throws IOException {
        if (writer != null) {
            writer.flush();
            writer.close();
            writer = null;
        }
    }

    private void ensureParentDirs() {
        File parent = baseFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    private static String format(LogEntry entry) {
        String iso8601 = Instant.ofEpochMilli(entry.getTimestamp()).toString();
        StringBuilder sb = new StringBuilder();
        sb.append('[').append(iso8601).append(']')
          .append(' ')
          .append('[').append(entry.getThreadName()).append(']')
          .append(' ')
          .append('[').append(entry.getLoggerName()).append(']')
          .append(' ')
          .append(entry.getLevel().name())
          .append(": ")
          .append(entry.getMessage());

        if (entry.getThrowable() != null) {
            sb.append(System.lineSeparator());
            sb.append(stackTraceToString(entry.getThrowable()));
        }
        return sb.toString();
    }

    private static String stackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }
}
