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
import java.io.Reader;
import java.nio.charset.Charset;

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.model.ErrorBuilder;
import io.github.raghultech.openloom.model.Validate;

/**
 * Utility class for writing content from a {@link Reader} to a file efficiently.
 * <p>
 * This class is optimized for large content by writing in buffered chunks,
 * supporting memory-efficient streaming from any Reader source.
 * All file paths are validated and I/O errors are wrapped in {@link OpenLoomFileException}.
 * </p>
 * <p>
 * Typical usage via the entry point might look like:
 * <pre>{@code
 * OpenLoom loom = new OpenLoom();
 * Reader reader = new FileReader(sourceFile);
 * loom.write().writeReader(destFile, reader, false, StandardCharsets.UTF_8, 8192);
 * }</pre>
 * </p>
 *
 * @since 1.0
 */

public class WriteReaderFile {

	/**
     * Writes content from a {@link Reader} to the specified file in buffered chunks.
     * <p>
     * The method reads from the provided {@code Reader} and writes to the file using
     * the specified {@link Charset} and buffer size. The {@code append} parameter
     * controls whether existing content is overwritten or appended.
     * This approach efficiently handles large content while minimizing memory usage.
     * </p>
     *
     * @param file the target file to write content to; must be valid and writable
     * @param reader the source {@link Reader} providing the content
     * @param append if true, content is appended to the file; if false, the file is overwritten
     * @param charset the character set used for encoding the content
     * @param bufferSize the size of the internal buffer for chunked writing
     * @throws OpenLoomFileException if an I/O error occurs during writing
     *
     * @see java.io.BufferedWriter
     * @see java.io.OutputStreamWriter
     * @see java.io.Reader
     */
	 protected void writeReader(File file, Reader reader, boolean append,Charset charset,int bufferSize) {
    	 Validate.validateFile(file);
        //int bufferSize = 32_768; // Default large buffer

        try (BufferedWriter writer = new BufferedWriter(
                 new OutputStreamWriter(new FileOutputStream(file, append), charset),
                 bufferSize)) {
            char[] buffer = new char[bufferSize];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, charsRead);
            }
            writer.flush();
        } catch (IOException e) {
            throw new OpenLoomFileException(
                    ErrorBuilder.ReadWriteError(e, file.toPath(), "WRITE"), e
            );
        }
    }
        
}

