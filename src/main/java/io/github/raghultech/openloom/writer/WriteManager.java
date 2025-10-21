package io.github.raghultech.openloom.writer;

import java.io.File;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.function.Consumer;

import io.github.raghultech.openloom.config.FileIOConfig;

/**
 * WriteManager
 * ---------------------------------------------------------
 * Central controller for all file-writing operations in OpenLoom.
 *
 * Supports:
 *  1. Synchronous writing (default)
 *  2. Buffered writing (small/medium files)
 *  3. Large file writing
 *  4. Reader-based writing
 *  5. Progress monitoring via Consumer<Double>
 *  6. Append operations
 *
 * ---------------------------------------------------------
 * Threading and Asynchronous Use
 * ---------------------------------------------------------
 * OpenLoom writing methods are synchronous by design for predictability.
 * Developers can wrap any write call using **any asynchronous execution mechanism**:
 *
 *  - CompletableFuture
 *  - ExecutorService
 *  - SwingWorker / SwingUtilities
 *  - JavaFX Platform.runLater
 *  - Virtual threads (Java 21+)
 *
 * Example (CompletableFuture):
 * <pre>
 * CompletableFuture.runAsync(() -> writeManager.write(file, content))
 *                  .thenRun(() -> System.out.println("Write complete"));
 * </pre>
 *
 * This ensures OpenLoom remains lightweight and framework-agnostic.
 */
public class WriteManager {

    // Thresholds in bytes
    private static final long SMALL_FILE_THRESHOLD = 1 * 1024 * 1024;   // 1 MB
    private static final long MEDIUM_FILE_THRESHOLD = 10 * 1024 * 1024; // 10 MB

    private final FileIOConfig config;
    private final WriteSmallFile smallWriter = new WriteSmallFile();
    private final WriteLargeFile largeWriter = new WriteLargeFile();
    private final WriteReaderFile readerWriter = new WriteReaderFile();

    // ------------------------ Constructors ------------------------
    public WriteManager() {
        this.config = new FileIOConfig();
    }

    public WriteManager(FileIOConfig config) {
        this.config = (config != null) ? config : new FileIOConfig();
    }

    public WriteManager(Charset charset) {
        this.config = new FileIOConfig();
        this.config.setCharset(charset);
    }

    public WriteManager(String charset) {
        this.config = new FileIOConfig();
        this.config.setCharset(charset);
    }

    // ------------------------ Charset Management ------------------------
    public Charset getCharset() { return config.getCharset(); }
    public void setCharset(Charset charset) { config.setCharset(charset); }

    public String getCharsetName() { return config.getCharsetName(); }
    public void setCharset(String charset) { config.setCharset(charset); }

    // ------------------------ Internal Write Logic ------------------------
    private void writeInternal(File file, String content, int bufferSize) {
        long size = content.length();
        Charset charset = config.getCharset();

        if (size <= MEDIUM_FILE_THRESHOLD) {
            smallWriter.writeSmall(file, content, false, charset, bufferSize);
        } else {
            largeWriter.writeLarge(file, content, false, charset, bufferSize);
        }
    }

    private void writeInternal(File file, String content, Consumer<Double> progressConsumer, int bufferSize) {
        long size = content.length();
        Charset charset = config.getCharset();

        if (size <= MEDIUM_FILE_THRESHOLD) {
            smallWriter.writeSmall(file, content, false, charset, bufferSize, progressConsumer);
        } else {
            largeWriter.writeLarge(file, content, false, charset, bufferSize, progressConsumer);
        }
    }

    private void writeInternal(File file, String content) {
        writeInternal(file, content, selectBufferSize(content.length()));
    }

    private void writeInternal(File file, String content, Consumer<Double> progressConsumer) {
        writeInternal(file, content, progressConsumer, selectBufferSize(content.length()));
    }

    // ------------------------ Internal Append Logic ------------------------
    private void appendInternal(File file, String content, int bufferSize) {
        long size = content.length();
        Charset charset = config.getCharset();

        if (size <= MEDIUM_FILE_THRESHOLD) {
            smallWriter.writeSmall(file, content, true, charset, bufferSize);
        } else {
            largeWriter.writeLarge(file, content, true, charset, bufferSize);
        }
    }

    private void appendInternal(File file, String content, Consumer<Double> progressConsumer, int bufferSize) {
        long size = content.length();
        Charset charset = config.getCharset();

        if (size <= MEDIUM_FILE_THRESHOLD) {
            smallWriter.writeSmall(file, content, true, charset, bufferSize, progressConsumer);
        } else {
            largeWriter.writeLarge(file, content, true, charset, bufferSize, progressConsumer);
        }
    }

    private void appendInternal(File file, String content) {
        appendInternal(file, content, selectBufferSize(content.length()));
    }

    private void appendInternal(File file, String content, Consumer<Double> progressConsumer) {
        appendInternal(file, content, progressConsumer, selectBufferSize(content.length()));
    }

    // ------------------------ Public Write Methods ------------------------
    public void write(File file, String content) { writeInternal(file, content); }
    public void write(Path path, String content) { writeInternal(path.toFile(), content); }

    public void write(File file, String content, int bufferSize) { writeInternal(file, content, bufferSize); }
    public void write(Path path, String content, int bufferSize) { writeInternal(path.toFile(), content, bufferSize); }

    public void write(File file, String content, Consumer<Double> progressConsumer) { writeInternal(file, content, progressConsumer); }
    public void write(Path path, String content, Consumer<Double> progressConsumer) { writeInternal(path.toFile(), content, progressConsumer); }

    public void write(File file, String content, Consumer<Double> progressConsumer, int bufferSize) {
        writeInternal(file, content, progressConsumer, bufferSize);
    }
    public void write(Path path, String content, Consumer<Double> progressConsumer, int bufferSize) {
        writeInternal(path.toFile(), content, progressConsumer, bufferSize);
    }

    // ------------------------ Reader-based Writing ------------------------
    public void write(File file, Reader reader) {
        readerWriter.writeReader(file, reader, false, config.getCharset(), 32_768);
    }

    public void write(Path path, Reader reader) {
        readerWriter.writeReader(path.toFile(), reader, false, config.getCharset(), 32_768);
    }

    public void write(File file, Reader reader, int bufferSize) {
        readerWriter.writeReader(file, reader, false, config.getCharset(), bufferSize);
    }

    public void write(Path path, Reader reader, int bufferSize) {
        readerWriter.writeReader(path.toFile(), reader, false, config.getCharset(), bufferSize);
    }

    // ------------------------ Append Methods ------------------------
    public void append(File file, String content) { appendInternal(file, content); }
    public void append(Path path, String content) { appendInternal(path.toFile(), content); }

    public void append(File file, String content, int bufferSize) { appendInternal(file, content, bufferSize); }
    public void append(Path path, String content, int bufferSize) { appendInternal(path.toFile(), content, bufferSize); }

    public void append(File file, String content, Consumer<Double> progressConsumer) { appendInternal(file, content, progressConsumer); }
    public void append(Path path, String content, Consumer<Double> progressConsumer) { appendInternal(path.toFile(), content, progressConsumer); }

    public void append(File file, String content, Consumer<Double> progressConsumer, int bufferSize) {
        appendInternal(file, content, progressConsumer, bufferSize);
    }
    public void append(Path path, String content, Consumer<Double> progressConsumer, int bufferSize) {
        appendInternal(path.toFile(), content, progressConsumer, bufferSize);
    }

    public void append(File file, Reader reader) {
        readerWriter.writeReader(file, reader, true, config.getCharset(), 32_768);
    }

    public void append(Path path, Reader reader) {
        readerWriter.writeReader(path.toFile(), reader, true, config.getCharset(), 32_768);
    }

    public void append(File file, Reader reader, int bufferSize) {
        readerWriter.writeReader(file, reader, true, config.getCharset(), bufferSize);
    }

    public void append(Path path, Reader reader, int bufferSize) {
        readerWriter.writeReader(path.toFile(), reader, true, config.getCharset(), bufferSize);
    }

    // ------------------------ Direct Small/Large Writes ------------------------
    public void writeSmall(File file, String content, int bufferSize) {
        smallWriter.writeSmall(file, content, false, config.getCharset(), bufferSize);
    }
    public void writeSmall(Path path, String content, int bufferSize) {
        smallWriter.writeSmall(path.toFile(), content, false, config.getCharset(), bufferSize);
    }
    public void writeSmall(File file, String content, int bufferSize,Consumer<Double> progressConsumer) {
        smallWriter.writeSmall(file, content, false, config.getCharset(), bufferSize, progressConsumer);
    }
    public void writeSmall(Path path, String content, int bufferSize,Consumer<Double> progressConsumer) {
        smallWriter.writeSmall(path.toFile(), content, false, config.getCharset(), bufferSize, progressConsumer);
    }

    public void writeLarge(File file, String content, int bufferSize) {
        largeWriter.writeLarge(file, content, false, config.getCharset(), bufferSize);
    }
    public void writeLarge(Path path, String content, int bufferSize) {
        largeWriter.writeLarge(path.toFile(), content, false, config.getCharset(), bufferSize);
    }
    public void writeLarge(File file, String content, int bufferSize,Consumer<Double> progressConsumer) {
        largeWriter.writeLarge(file, content, false, config.getCharset(), bufferSize, progressConsumer);
    }
    public void writeLarge(Path path, String content, int bufferSize,Consumer<Double> progressConsumer) {
        largeWriter.writeLarge(path.toFile(), content, false, config.getCharset(), bufferSize, progressConsumer);
    }
    
    public void appendSmall(File file, String content, int bufferSize) {
        smallWriter.writeSmall(file, content, true, config.getCharset(), bufferSize);
    }
    public void appendSmall(Path path, String content, int bufferSize) {
        smallWriter.writeSmall(path.toFile(), content, true, config.getCharset(), bufferSize);
    }
    public void appendSmall(File file, String content, int bufferSize,Consumer<Double> progressConsumer) {
        smallWriter.writeSmall(file, content, true, config.getCharset(), bufferSize, progressConsumer);
    }
    public void appendSmall(Path path, String content, int bufferSize,Consumer<Double> progressConsumer) {
        smallWriter.writeSmall(path.toFile(), content, true, config.getCharset(), bufferSize, progressConsumer);
    }

    public void appendLarge(File file, String content, int bufferSize) {
        largeWriter.writeLarge(file, content, true, config.getCharset(), bufferSize);
    }
    public void appendLarge(Path path, String content, int bufferSize) {
        largeWriter.writeLarge(path.toFile(), content, true, config.getCharset(), bufferSize);
    }
    public void appendLarge(File file, String content, int bufferSize,Consumer<Double> progressConsumer) {
        largeWriter.writeLarge(file, content, true, config.getCharset(), bufferSize, progressConsumer);
    }
    public void appendLarge(Path path, String content, int bufferSize,Consumer<Double> progressConsumer) {
        largeWriter.writeLarge(path.toFile(), content, true, config.getCharset(), bufferSize, progressConsumer);
    }

    // ------------------------ Buffer Size Selector ------------------------
    private int selectBufferSize(long contentLength) {
        if (contentLength <= SMALL_FILE_THRESHOLD) return 8192;      // 8 KB
        if (contentLength <= MEDIUM_FILE_THRESHOLD) return 16_384;  // 16 KB
        return 32_768;                                              // 32 KB
    }
}
