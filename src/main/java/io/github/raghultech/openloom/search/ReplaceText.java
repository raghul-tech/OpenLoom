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


package io.github.raghultech.openloom.search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.model.Utils;
import io.github.raghultech.openloom.model.Validate;


/**
 * Provides advanced text replacement capabilities for files, including
 * both normal and safe (atomic) operations.
 * <p>
 * This class is part of the {@code io.github.raghultech.openloom.search} package and is
 * internally accessed through the {@link io.github.raghultech.openloom.OpenLoom} entry point
 * via the {@code search()} manager.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * OpenLoom loom = new OpenLoom(StandardCharsets.UTF_8);
 * var search = loom.search();
 *
 * // Replace a word in a specific line
 * search.replaceText(file, 3, "oldWord", "newWord");
 *
 * // Replace all occurrences of a text throughout the file
 * search.replaceTextAll(file, "foo", "bar");
 * }</pre>
 *
 * <p><b>Safe methods</b> (ending with “Safe”) perform atomic operations using
 * a temporary file to ensure that the original file remains unmodified
 * if an exception or failure occurs during writing.</p>
 *
 * @see io.github.raghultech.openloom.OpenLoom
 * @see io.github.raghultech.openloom.exception.OpenLoomFileException
 * @see io.github.raghultech.openloom.model.Validate
 */
public class ReplaceText {

    /**
     * Replaces all occurrences of the specified text in a given line of the file.
     * <p>
     * This method reads the entire file, modifies only the given line, and
     * overwrites the file with the updated content.
     * </p>
     *
     * <h4>Example:</h4>
     * <pre>{@code
     * OpenLoom loom = new OpenLoom(StandardCharsets.UTF_8);
     * var search = loom.search();
     * search.replaceText(file, 5, "oldText", "newText");
     * }</pre>
     *
     * @param file         the file to modify
     * @param lineNumber   the line number (1-based) where replacement should occur
     * @param target       the text to be replaced
     * @param replacement  the replacement text
     * @param charset      the charset used for reading and writing
     * @throws OpenLoomFileException if no match is found, the line doesn’t exist, or an I/O error occurs
     */
    protected void replaceText(File file, int lineNumber, String target, String replacement, Charset charset) {
        Validate.validate(file, target, replacement, lineNumber);

        StringBuilder result = new StringBuilder();
        boolean replaced = false;
        int current = 1;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), charset))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (current == lineNumber) {
                    String updated = line.replace(target, replacement);
                    if (!updated.equals(line)) replaced = true;
                    result.append(updated);
                } else {
                    result.append(line);
                }
                result.append(System.lineSeparator());
                current++;
            }

        } catch (IOException e) {
            throw new OpenLoomFileException("Error reading file: " + file, e);
        }

        if (!replaced) {
            throw new OpenLoomFileException(
                    "No text found to replace at line " + lineNumber + " in file: " + file);
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, false), charset))) {
            writer.write(result.toString());
        } catch (IOException e) {
            throw new OpenLoomFileException("Error writing file: " + file, e);
        }
    }

   
    /**
     * Safely replaces all occurrences of the specified text in a given line using atomic file replacement.
     * <p>
     * Changes are written to a temporary file, which then replaces the original file
     * only upon successful completion of the operation.
     * </p>
     *
     * <h4>Example:</h4>
     * <pre>{@code
     * OpenLoom loom = new OpenLoom(StandardCharsets.UTF_8);
     * var search = loom.search();
     * search.replaceTextSafe(file, 3, "debug", "release");
     * }</pre>
     *
     * @param file         the file to modify
     * @param lineNumber   the line number (1-based) where replacement should occur
     * @param target       the text to be replaced
     * @param replacement  the replacement text
     * @param charset      the charset used for reading and writing
     * @throws OpenLoomFileException if no match is found, the line doesn’t exist, or an I/O error occurs
     */
    
    protected void replaceTextSafe(File file, int lineNumber, String target, String replacement, Charset charset) {
        Validate.validate(file, target, replacement, lineNumber);

        Path originalPath = file.toPath();
        Path tempPath =  Utils.createTemp(file, "replaceTextSafe");

        boolean replaced = false;
        int current = 1;

        try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), charset));
             BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(tempPath.toFile()), charset))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (current == lineNumber) {
                    String updated = line.replace(target, replacement);
                    if (!updated.equals(line)) replaced = true;
                    writer.write(updated);
                } else {
                    writer.write(line);
                }
                writer.newLine();
                current++;
            }

        } catch (IOException e) {
            try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
            throw new OpenLoomFileException("Error processing file: " + file, e);
        }

        if (!replaced) {
            try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
            throw new OpenLoomFileException(
                    "No text found to replace at line " + lineNumber + " in file: " + file);
        }

        try {
            Files.move(tempPath, originalPath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            try {
                Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
                throw new OpenLoomFileException("Failed to replace original file: " + file, ex);
            }
        }
    }

    /**
     * Replaces all occurrences of a specific text throughout the entire file.
     * <p>
     * This method performs a global replacement for every line, modifying
     * the entire file in one pass.
     * </p>
     *
     * <h4>Example:</h4>
     * <pre>{@code
     * OpenLoom loom = new OpenLoom(StandardCharsets.UTF_8);
     * var search = loom.search();
     * search.replaceTextAll(file, "TODO", "Done");
     * }</pre>
     *
     * @param file         the file to modify
     * @param target       the text to be replaced in all lines
     * @param replacement  the replacement text
     * @param charset      the charset used for reading and writing
     * @throws OpenLoomFileException if no matches are found or an I/O error occurs
     */
    protected void replaceTextAll(File file, String target, String replacement, Charset charset) {
        Validate.validate(file, target, replacement);

        StringBuilder result = new StringBuilder();
        boolean replaced = false;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), charset))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String updated = line.replace(target, replacement);
                if (!updated.equals(line)) replaced = true;
                result.append(updated).append(System.lineSeparator());
            }

        } catch (IOException e) {
            throw new OpenLoomFileException("Error reading file: " + file, e);
        }

        if (!replaced) {
            throw new OpenLoomFileException("No text found to replace in file: " + file);
        }

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file, false), charset))) {
            writer.write(result.toString());
        } catch (IOException e) {
            throw new OpenLoomFileException("Error writing file: " + file, e);
        }
    }


    /**
     * Safely replaces all occurrences of a specific text throughout the entire file using atomic replacement.
     * <p>
     * Writes to a temporary file and replaces the original only when the operation
     * completes successfully, ensuring data integrity even in the event of a failure.
     * </p>
     *
     * <h4>Example:</h4>
     * <pre>{@code
     * OpenLoom loom = new OpenLoom(StandardCharsets.UTF_8);
     * var search = loom.search();
     * search.replaceTextAllSafe(file, "v1", "v2");
     * }</pre>
     *
     * @param file         the file to modify
     * @param target       the text to be replaced in all lines
     * @param replacement  the replacement text
     * @param charset      the charset used for reading and writing
     * @throws OpenLoomFileException if no matches are found or an I/O error occurs
     */
    protected void replaceTextAllSafe(File file, String target, String replacement, Charset charset) {
        Validate.validate(file, target, replacement);

        Path originalPath = file.toPath();
        Path tempPath =  Utils.createTemp(file, "replaceTextAllSafe");

        boolean replaced = false;

        try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), charset));
             BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(tempPath.toFile()), charset))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String updated = line.replace(target, replacement);
                if (!updated.equals(line)) replaced = true;
                writer.write(updated);
                writer.newLine();
            }

        } catch (IOException e) {
            try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
            throw new OpenLoomFileException("Error processing file: " + file, e);
        }

        if (!replaced) {
            try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
            throw new OpenLoomFileException("No text found to replace in file: " + file);
        }

        try {
            Files.move(tempPath, originalPath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            try {
                Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
                throw new OpenLoomFileException("Failed to replace original file: " + file, ex);
            }
        }
    }
}

