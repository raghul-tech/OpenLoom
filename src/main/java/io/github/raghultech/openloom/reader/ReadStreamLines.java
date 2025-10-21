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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.function.Consumer;
import java.util.function.Predicate;

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.exception.OpenLoomMemoryException;
import io.github.raghultech.openloom.model.ErrorBuilder;
import io.github.raghultech.openloom.model.Validate;

/**
 * Provides efficient, stream-based file reading utilities for large text files.
 * <p>
 * This class powers OpenLoom’s <b>read()</b> functionality and supports:
 * <ul>
 *     <li>Sequential line-by-line reading</li>
 *     <li>Filtered reading using {@link Predicate}</li>
 *     <li>Selective reading of line ranges for partial data processing</li>
 * </ul>
 *
 * <p><b>Use Case:</b> Ideal for log analysis, previewing large files, or
 * processing specific sections of data without loading entire files into memory.</p>
 *
 * <h3>Example Usage (via OpenLoom entry point):</h3>
 * <pre>{@code
 * OpenLoom loom = new OpenLoom();
 *
 * // 1️⃣ Line-by-line streaming
 * loom.read().readLines(file, line -> System.out.println("Line: " + line));
 *
 * // 2️⃣ Filtered line reading
 * loom.read().readLinesFilter(file, line -> line.contains("@"), System.out::println);
 *
 * // 3️⃣ Read specific line range (e.g., 10–20)
 * loom.read().readLinesRange(file, 10, 20, line -> System.out.println("Range: " + line));
 * }</pre>
 *
 * <p>All methods handle large files efficiently using buffered streaming,
 * and automatically validate input paths and encodings.</p>
 *
 * @author
 *     Raghul John (@raghul-tech)
 * @version 1.0
 * @since 2025
 */

public class ReadStreamLines {

	 
	
	 /**
     * Streams each line from the file and applies the given {@link Predicate} filter.
     * Only lines passing the filter are consumed.
     *
     * <p>Does not load the full file into memory, making it suitable for very large files.</p>
     *
     * <h4>Example:</h4>
     * <pre>{@code
     * File file = new File("log.txt");
     * Predicate<String> isError = line -> line.contains("ERROR");
     * loom.read().readLinesFilter(file, isError, System.out::println);
     * }</pre>
     *
     * @param file     the file to read from (must exist and be readable)
     * @param filter   predicate used to test each line (true = accepted)
     * @param consumer consumer to handle accepted lines
     * @param charset  file character encoding
     * @throws OpenLoomFileException if an I/O error occurs
     */
	protected void streamFilteredLines(File file, Predicate<String> filter, Consumer<String> consumer, Charset charset) {
	    Validate.validateFile(file);

	    try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            if (filter.test(line)) {
	                consumer.accept(line);
	            }
	        }
	    } catch (IOException e) {
	        throw new OpenLoomFileException("Error streaming filtered lines from file: " + file.getName(), e);
	    }
	}
	
	
	 /**
     * Streams all lines from a file sequentially and passes them to the provided consumer.
     * <p>
     * Ideal for log parsing, search operations, or progressive UI updates
     * without consuming excessive memory.
     * </p>
     *
     * <h4>Example:</h4>
     * <pre>{@code
     * loom.read().readLines(new File("data.txt"), System.out::println);
     * }</pre>
     *
     * @param file         the file to read
     * @param lineConsumer consumer to process each line
     * @param charset      character encoding of the file
     * @throws OpenLoomFileException if an I/O error occurs
     * @throws OpenLoomMemoryException if memory overflow is detected
     */
	
	 protected void streamLines(File file, Consumer<String> lineConsumer,Charset charset) {
		 Validate.validateFile(file);
	    try (BufferedReader reader = new BufferedReader(
	            new InputStreamReader(new FileInputStream(file), charset))) {

	        String line;
	        while ((line = reader.readLine()) != null) {
	            lineConsumer.accept(line);
	        }

	    } catch (OutOfMemoryError e) {
            throw new OpenLoomMemoryException(
                    "File too large to stream lines: " + file.getName(), e
            );
        } catch (IOException e) {
            throw new OpenLoomFileException(
                    ErrorBuilder.ReadWriteError(e, file.toPath(), "READ"), e
            );
        }
	}
	 
	 
	 /**
	     * Reads a specific range of lines from a file (inclusive) and streams them to a consumer.
	     * <p>
	     * This is useful for pagination, previews, or reading specific sections of large files.
	     * </p>
	     *
	     * <h4>Example:</h4>
	     * <pre>{@code
	     * loom.read().readLinesRange(new File("notes.txt"), 10, 20, System.out::println);
	     * }</pre>
	     *
	     * @param file         the file to read from
	     * @param charset      file encoding
	     * @param startLine    starting line number (1-based)
	     * @param endLine      ending line number (inclusive)
	     * @param lineConsumer consumer to handle each selected line
	     * @throws IllegalArgumentException if line range is invalid
	     * @throws OpenLoomFileException if an I/O error occurs
	     * @throws OpenLoomMemoryException if file is too large to handle safely
	     */
	 protected void streamLinesRange(File file, Charset charset, int startLine, int endLine, Consumer<String> lineConsumer) {
		 Validate.validateFile(file);
		 
		 if (startLine < 1 || endLine < startLine) {
	            throw new IllegalArgumentException("Invalid start/end line numbers: start=" + startLine + ", end=" + endLine);
	        }

	    try (BufferedReader reader = new BufferedReader(
	            new InputStreamReader(new FileInputStream(file), charset))) {

	        String line;
	        int lineNumber = 0;
	        while ((line = reader.readLine()) != null) {
	            lineNumber++;
	            if (lineNumber > endLine) {
	                break;
	            }
	            if (lineNumber >= startLine) {
	                lineConsumer.accept(line);
	            }
	        }
	    } catch (OutOfMemoryError e) {
            throw new OpenLoomMemoryException(
                    "File too large to stream lines: " + file.getName(), e
            );
        } catch (IOException e) {
            throw new OpenLoomFileException(
                    ErrorBuilder.ReadWriteError(e, file.toPath(), "READ"), e
            );
        }
	}
	 

	
}

