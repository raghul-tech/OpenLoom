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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.model.Validate;

/**
 * Utility class for reading specific or all columns from delimited text files
 * such as CSV (comma-separated) or TSV (tab-separated) files.
 * <p>
 * This class supports reading selected columns by index or retrieving
 * all columns from each line. It ensures safe file validation, handles
 * character encoding via {@link Charset}, and provides meaningful exceptions
 * when read errors occur.
 * </p>
 *
 * <p><b>Example Usage:</b></p>
 * <pre>{@code
 * ReadsColumns reader = new ReadsColumns();
 * List<String[]> selected = reader.readColumns(
 *     new File("data.csv"), ",", new int[]{0, 2}, StandardCharsets.UTF_8
 * );
 * List<String[]> all = reader.readColumns(
 *     new File("data.csv"), ",", StandardCharsets.UTF_8
 * );
 * }</pre>
 *
 * @author
 *     Raghul John (@raghul-tech)
 * @version 1.0
 * @since 2025
 */

public class ReadsColumns {

	 /**
     * Reads specific columns from a delimited file (e.g., CSV, TSV) using the specified
     * delimiter and character set.
     * <p>
     * Each line of the file is split based on the given delimiter, and only the columns
     * corresponding to the provided zero-based indices are extracted.
     * Missing columns in a line will be represented as empty strings.
     * </p>
     *
     * @param file       the input file to read
     * @param delimiter  the character or regular expression used to split columns (e.g., "," or "\\t")
     * @param columns    an array of zero-based column indices to extract
     * @param charset    the file's character encoding (e.g., {@link java.nio.charset.StandardCharsets#UTF_8})
     * @return a list of string arrays, where each array contains the selected columns for one line
     * @throws IllegalArgumentException  if {@code delimiter} or {@code columns} are null or empty
     * @throws OpenLoomFileException     if the file cannot be read or an I/O error occurs
     */
    protected List<String[]> readColumns(File file, String delimiter, int[] columns, Charset charset) {
        Validate.validateFile(file);
        if (delimiter == null || delimiter.isEmpty()) {
            throw new IllegalArgumentException("Delimiter cannot be null or empty.");
        }
        if (columns == null || columns.length == 0) {
            throw new IllegalArgumentException("Columns array cannot be null or empty.");
        }

        List<String[]> results = new ArrayList<>();
        int lineNum = 0;

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                String[] parts = line.split(delimiter, -1);
                String[] selected = new String[columns.length];
                for (int i = 0; i < columns.length; i++) {
                    int col = columns[i];
                    selected[i] = (col >= 0 && col < parts.length) ? parts[col] : "";
                }
                results.add(selected);
            }
        } catch (IOException e) {
            throw new OpenLoomFileException(
                "Error reading columns from file: " + file.getName() + " at line " + lineNum, e);
        }

        return results;
    }
    
    /**
     * Reads all columns from a delimited text file using the specified delimiter and character set.
     * <p>
     * Each line is split using the given delimiter, and all resulting columns are included.
     * Trailing empty columns are preserved using the {@code -1} split limit.
     * </p>
     *
     * @param file       the input file to read
     * @param delimiter  the character or regular expression used to split columns (e.g., "," or "\\t")
     * @param charset    the file's character encoding (e.g., {@link java.nio.charset.StandardCharsets#UTF_8})
     * @return a list of string arrays, each representing one line split into columns
     * @throws IllegalArgumentException  if {@code delimiter} is null or empty
     * @throws OpenLoomFileException     if the file cannot be read or an I/O error occurs
     */
    protected List<String[]> readColumns(File file, String delimiter, Charset charset) {
        Validate.validateFile(file);

        if (delimiter == null || delimiter.isEmpty()) {
            throw new IllegalArgumentException("Delimiter cannot be null or empty.");
        }

        List<String[]> results = new ArrayList<>();
        int lineNum = 0;

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), charset)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                String[] parts = line.split(delimiter, -1); // -1 keeps trailing empty columns
                results.add(parts);
            }
        } catch (IOException e) {
            throw new OpenLoomFileException(
                "Error reading file: " + file.getName() + " at line " + lineNum, e);
        }

        return results;
    }

}

