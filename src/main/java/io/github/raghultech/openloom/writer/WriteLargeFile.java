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
 * Utility class for writing large files efficiently in chunks.
 * <p>
 * This class is designed to handle very large files without overloading memory.
 * It writes content in buffered segments and supports optional progress reporting.
 * All file paths are validated and any I/O errors are wrapped in {@link OpenLoomFileException}.
 * </p>
 * <p>
 * Typical usage via the entry point might look like:
 * <pre>{@code
 * OpenLoom loom = new OpenLoom();
 * loom.write().writeLarge(file, content, false, StandardCharsets.UTF_8, 8192);
 *
 * // With progress monitoring
 * loom.write().writeLarge(file, content, true, StandardCharsets.UTF_8, 8192,
 *                         progress -> System.out.println("Progress: " + (progress * 100) + "%"));
 * }</pre>
 * </p>
 *
 * @since 1.0
 */

public class WriteLargeFile {

	 /**
     * Writes the specified content to a file in buffered chunks.
     * <p>
     * The content is written using the given charset and buffer size.
     * The file can be overwritten or appended based on the {@code append} parameter.
     * Memory usage is optimized for large content by writing in segments.
     * </p>
     *
     * @param file the target file to write content to; must be valid and writable
     * @param content the string content to write to the file
     * @param append if true, content is appended to the file; if false, file is overwritten
     * @param charset the character set used for encoding the content
     * @param bufferSize the size of the internal buffer for chunked writing
     * @throws OpenLoomFileException if an I/O error occurs during writing
     *
     * @see java.io.BufferedWriter
     * @see java.io.OutputStreamWriter
     */
	 protected void writeLarge(File file, String content, boolean append, Charset charset, int bufferSize) {
		  Validate.validateFile(file);  
		 try (
	            BufferedWriter writer = new BufferedWriter(
	                new OutputStreamWriter(new FileOutputStream(file, append), charset),
	                bufferSize
	            )
	        ) {
	            int offset = 0;
	            while (offset < content.length()) {
	                int end = Math.min(offset + bufferSize, content.length());
	                writer.write(content, offset, end - offset);
	                offset = end;
	            }
	            writer.flush();
	        } catch (IOException e) {
	        	 throw new OpenLoomFileException(
	                     ErrorBuilder.ReadWriteError(e, file.toPath(), "WRITE"), e
	             );
	        }
	    }
	
	 /**
	     * Writes the specified content to a file in buffered chunks with optional progress reporting.
	     * <p>
	     * This method functions identically to {@link #writeLarge(File, String, boolean, Charset, int)}
	     * but additionally provides progress updates via a {@link Consumer} callback.
	     * This is useful for UI or console feedback when writing very large files.
	     * </p>
	     *
	     * @param file the target file to write content to; must be valid and writable
	     * @param content the string content to write to the file
	     * @param append if true, content is appended to the file; if false, file is overwritten
	     * @param charset the character set used for encoding the content
	     * @param bufferSize the size of the internal buffer for chunked writing
	     * @param progressConsumer a callback that receives progress as a double between 0.0 and 1.0; may be null
	     * @throws OpenLoomFileException if an I/O error occurs during writing
	     *
	     * @see java.io.BufferedWriter
	     * @see java.io.OutputStreamWriter
	     * @see java.util.function.Consumer
	     */
	 
	 protected void writeLarge(File file, String content, boolean append, Charset charset, int bufferSize, Consumer<Double> progressConsumer) {
	    	  Validate.validateFile(file);
	    	try (
	            BufferedWriter writer = new BufferedWriter(
	                new OutputStreamWriter(new FileOutputStream(file, append), charset),
	                bufferSize
	            )
	        ) {
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

