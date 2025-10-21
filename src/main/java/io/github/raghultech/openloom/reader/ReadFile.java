/*
 * Copyright 2025 Raghul-tech
 * https://github.com/raghul-tech
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.github.raghultech.openloom.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.exception.OpenLoomMemoryException;
import io.github.raghultech.openloom.model.ErrorBuilder;
import io.github.raghultech.openloom.model.Validate;


/**
 * ================================================================
 * 📚 ReadFile — Core File Reading Engine for OpenLoom
 * ================================================================
 *
 * <p>The {@code ReadFile} class implements multiple optimized reading
 * strategies for different file sizes and workloads. It serves as the
 * internal backbone of {@link io.github.raghultech.openloom.reader.ReadManager},
 * offering low-level access to buffered, NIO-channel, chunked, and
 * memory-mapped file reading.</p>
 *
 * <p>This class is not intended for direct public use.
 * Use {@link io.github.raghultech.openloom.OpenLoom#read()} instead.</p>
 *
 * <hr>
 * <p><b>Supported Reading Strategies:</b></p>
 * <ul>
 *   <li><b>Small Files</b> — Uses {@link BufferedReader} for lightweight I/O</li>
 *   <li><b>Medium Files</b> — Uses {@link FileChannel} and {@link ByteBuffer}</li>
 *   <li><b>Large Files</b> — Reads in buffered character chunks (configurable size)</li>
 *   <li><b>Huge Files</b> — Uses {@link MappedByteBuffer} for memory-mapped reading</li>
 * </ul>
 *
 * <p>Each mode has two overloads:
 * <ul>
 *   <li>Default — Uses internal buffer sizes tuned for typical workloads</li>
 *   <li>Custom — Accepts a user-specified buffer or map size</li>
 * </ul>
 * </p>
 *
 * <hr>
 * <p><b>Example Usage (Indirect):</b></p>
 * <pre>{@code
 * import io.github.raghultech.openloom.OpenLoom;
 * import java.io.File;
 * import java.nio.charset.StandardCharsets;
 *
 * public class Example {
 *     public static void main(String[] args) {
 *         OpenLoom loom = new OpenLoom();
 *
 *         // Automatically selects the optimal strategy
 *         String text = loom.read().read(new File("notes.txt"));
 *
 *         // Force large-file read with custom buffer
 *         String content = loom.read().readBuffered(new File("big.log"), 64 * 1024);
 *     }
 * }
 * }</pre>
 *
 * <hr>
 * <p><b>Error Handling:</b></p>
 * <ul>
 *   <li>{@link io.github.raghultech.openloom.exception.OpenLoomFileException} — I/O failures</li>
 *   <li>{@link io.github.raghultech.openloom.exception.OpenLoomMemoryException} — Out of memory during large file reads</li>
 * </ul>
 *
 * @see io.github.raghultech.openloom.reader.ReadManager
 * @see io.github.raghultech.openloom.exception.OpenLoomFileException
 * @see io.github.raghultech.openloom.exception.OpenLoomMemoryException
 * @see java.nio.channels.FileChannel
 * @see java.nio.MappedByteBuffer
 *
 * @since 2025
 * @author
 *     Raghul John (@raghul-tech)
 */

public class ReadFile {

    /* -----------------------------------------------------------
     * Small File Reader (BufferedReader)
     * ----------------------------------------------------------- */
    protected String loadSmallFile(File file, Charset charset)
            throws OpenLoomFileException, OpenLoomMemoryException {
        return loadSmallFile(file, charset, 8 * 1024); // default 8 KB
    }

    protected String loadSmallFile(File file, Charset charset, int bufferSize)
            throws OpenLoomFileException, OpenLoomMemoryException {

        Validate.validateFile(file);
       // Objects.requireNonNull(charset, "Charset cannot be null");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), charset), bufferSize)) {

            StringBuilder content = new StringBuilder((int) file.length());
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();

        } catch (OutOfMemoryError e) {
            throw new OpenLoomMemoryException("File too large to load: " + file.getName(), e);
        } catch (IOException e) {
            throw new OpenLoomFileException(
                    ErrorBuilder.ReadWriteError(e, file.toPath(), "READ"), e);
        }
    }

    /* -----------------------------------------------------------
     * Medium File Reader (NIO FileChannel)
     * ----------------------------------------------------------- */
    protected String loadMediumFile(File file, Charset charset)
            throws OpenLoomMemoryException, OpenLoomFileException {
        return loadMediumFile(file, charset, 64 * 1024); // default 64KB
    }

    protected String loadMediumFile(File file, Charset charset, int bufferSize)
            throws OpenLoomMemoryException, OpenLoomFileException {

        Validate.validateFile(file);
       // Objects.requireNonNull(charset, "Charset cannot be null");

        CharsetDecoder decoder = charset.newDecoder();
        StringBuilder content = new StringBuilder((int) Math.min(file.length(), Integer.MAX_VALUE));

        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             FileChannel fileChannel = raf.getChannel()) {

            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            while (fileChannel.read(buffer) > 0) {
                buffer.flip();
                CharBuffer charBuffer = decoder.decode(buffer);
                content.append(charBuffer);
                buffer.clear();
            }
            return content.toString();

        } catch (OutOfMemoryError e) {
            throw new OpenLoomMemoryException("File too large to load: " + file.getName(), e);
        } catch (IOException e) {
            throw new OpenLoomFileException(
                    ErrorBuilder.ReadWriteError(e, file.toPath(), "READ"), e);
        }
    }

    /* -----------------------------------------------------------
     * Large File Reader (Buffered Stream, Chunked)
     * ----------------------------------------------------------- */
    protected String loadLargeFile(File file, Charset charset)
            throws OpenLoomMemoryException, OpenLoomFileException {
        return loadLargeFile(file, charset, 32 * 1024); // default 32KB
    }

    protected String loadLargeFile(File file, Charset charset, int bufferSize)
            throws OpenLoomMemoryException, OpenLoomFileException {

        Validate.validateFile(file);
       // Objects.requireNonNull(charset, "Charset cannot be null");

        StringBuilder content = new StringBuilder((int) Math.min(file.length(), Integer.MAX_VALUE));

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, charset);
             BufferedReader reader = new BufferedReader(isr, bufferSize)) {

            char[] buffer = new char[bufferSize];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                content.append(buffer, 0, charsRead);
            }
            return content.toString();

        } catch (OutOfMemoryError e) {
            throw new OpenLoomMemoryException("File too large to load: " + file, e);
        } catch (IOException e) {
            throw new OpenLoomFileException(
                    ErrorBuilder.ReadWriteError(e, file.toPath(), "READ"), e);
        }
    }

    /* -----------------------------------------------------------
     * Very Large File Reader (Memory-Mapped)
     * ----------------------------------------------------------- */
    protected String loadBigFile(File file, Charset charset)
            throws OpenLoomMemoryException, OpenLoomFileException {
        // Memory-mapped files don’t use traditional buffer sizes
        // But we’ll keep a similar overload for consistency.
        return loadBigFile(file, charset, 128 * 1024 * 1024); // default max map 128MB
    }

    protected String loadBigFile(File file, Charset charset, long maxMapSize)
            throws OpenLoomMemoryException, OpenLoomFileException {

        Validate.validateFile(file);
      //  Objects.requireNonNull(charset, "Charset cannot be null");

        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             FileChannel fileChannel = raf.getChannel()) {

            long fileSize = fileChannel.size();
            long mapSize = Math.min(fileSize, maxMapSize); // respect user limit

            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, mapSize);
            byte[] byteArray = new byte[(int) mapSize];
            buffer.get(byteArray);

            return new String(byteArray, charset);

        } catch (IOException e) {
            throw new OpenLoomFileException(
                    ErrorBuilder.ReadWriteError(e, file.toPath(), "READ"), e);
        } catch (OutOfMemoryError e) {
            throw new OpenLoomMemoryException("Memory mapping failed for: " + file, e);
        }
    }
}

