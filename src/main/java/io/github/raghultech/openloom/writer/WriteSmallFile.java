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


package io.github.raghultech.openloom.writer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.function.Consumer;

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.model.ErrorBuilder;
import io.github.raghultech.openloom.model.Validate;

/**
 * Utility class for writing small to medium-sized files efficiently.
 * <p>
 * This class provides methods to write content directly to a file with optional
 * progress reporting. It is optimized for files that fit comfortably in memory
 * but still uses buffered writing for performance and I/O safety.
 * </p>
 * <p>
 * Typical usage via the OpenLoom entry point:
 * <pre>{@code
 * OpenLoom loom = new OpenLoom();
 * loom.write().writeSmall(file, content, false, StandardCharsets.UTF_8, 8192);
 *
 * // With progress monitoring
 * loom.write().writeSmall(file, content, true, StandardCharsets.UTF_8, 8192,
 *                         progress -> System.out.println("Progress: " + (progress * 100) + "%"));
 * }</pre>
 * </p>
 *
 * @since 1.0
 */

public class WriteSmallFile {
	
    /**
     * Writes content to the specified file using buffered writing.
     * <p>
     * This method overwrites or appends to the file depending on the {@code append} flag.
     * It uses the provided {@link Charset} for encoding and a configurable buffer size
     * for performance optimization.
     * </p>
     *
     * @param file the target file to write to; must be valid and writable
     * @param content the string content to write
     * @param append if true, content is appended; if false, file is overwritten
     * @param charset the character set used for encoding the content
     * @param bufferSize the buffer size used for writing
     * @throws OpenLoomFileException if an I/O error occurs during writing
     *
     * @see java.io.BufferedWriter
     * @see java.io.OutputStreamWriter
     */
	 protected void writeSmall(File file, String content, boolean append,  Charset charset,int bufferSize) {
     
    	 Validate.validateFile(file);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, append), charset),
                bufferSize)) {
            writer.write(content);
        } catch (IOException e) {
        	 throw new OpenLoomFileException(
                     ErrorBuilder.ReadWriteError(e, file.toPath(), "WRITE"), e
             );
        }
    }
    
	    /**
	     * Writes content to the specified file using buffered writing with progress reporting.
	     * <p>
	     * This method functions similarly to {@link #writeSmall(File, String, boolean, Charset, int)}
	     * but provides a {@link Consumer} callback that receives progress updates
	     * as a double between 0.0 and 1.0.
	     * </p>
	     *
	     * @param file the target file to write to; must be valid and writable
	     * @param content the string content to write
	     * @param append if true, content is appended; if false, file is overwritten
	     * @param charset the character set used for encoding the content
	     * @param bufferSize the buffer size used for writing
	     * @param progressConsumer a callback receiving progress updates; may be null
	     * @throws OpenLoomFileException if an I/O error occurs during writing
	     *
	     * @see java.io.BufferedWriter
	     * @see java.io.OutputStreamWriter
	     * @see java.util.function.Consumer
	     */
	 protected void writeSmall(File file, String content, boolean append, Charset charset, int bufferSize, Consumer<Double> progressConsumer) {
    	 Validate.validateFile(file);
    	try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, append), charset),
                bufferSize)) {
            
            int total = content.length();
            int offset = 0;

            while (offset < total) {
                int length = Math.min(bufferSize, total - offset);
                writer.write(content, offset, length);
                offset += length;

                if (progressConsumer != null) {
                    double progress = (double) offset / total;
                    progressConsumer.accept(progress);
                }
            }

            writer.flush();
        } catch (IOException e) {
        	 throw new OpenLoomFileException(
                     ErrorBuilder.ReadWriteError(e, file.toPath(), "WRITE"), e
             );
        }
    }
    

  
}

