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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.model.Utils;
import io.github.raghultech.openloom.model.Validate;


/**
 * Provides efficient and safe line-replacement operations for text files.
 * <p>
 * This class enables replacing one or multiple lines in a text file with new content,
 * ensuring data integrity and supporting both direct and safe (atomic) modes.
 * It is part of the {@code io.github.raghultech.openloom.writer} package and used internally by
 * {@link io.github.raghultech.openloom.OpenLoom} through its line modification APIs.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * OpenLoom loom = new OpenLoom(StandardCharsets.UTF_8);
 * var modify = loom.modify();
 *
 * // Replace the 3rd line in a file
 * modify.replaceLine(file, 3, "Updated line content");
 *
 * // Replace multiple lines safely
 * Map<Integer, String> map = Map.of(1, "Header", 5, "Updated footer");
 * modify.replaceLinesSafe(file, map);
 * }</pre>
 *
 * <p><b>Note:</b> Safe methods create a temporary backup and perform atomic replacement
 * to prevent corruption during write failures.</p>
 */
public class ReplaceLine {

	
    /**
     * Replaces a single line in the file with new content.
     * <p>
     * Reads the entire file, replaces the specified line number with {@code newContent},
     * and writes all lines back to the same file.
     * </p>
     *
     * @param file        the file to modify
     * @param lineNumber  the line number to replace (1-based)
     * @param newContent  the new content for the specified line
     * @param charset     the charset used for reading and writing
     * @throws OpenLoomFileException if the line does not exist or an I/O error occurs
     */
	protected void replaceLine(File file, int lineNumber, String newContent, Charset charset) {
	Validate.validate(file, lineNumber, newContent);
	    List<String> lines = new ArrayList<>();
	    boolean replaced = false;
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
	        String line;
	        int current = 1;
	        while ((line = reader.readLine()) != null) {
	            if (current == lineNumber) {
	                lines.add(newContent);
	                replaced = true;
	            } else {
	                lines.add(line);
	            }
	            current++;
	        }
	    } catch (IOException e) {
	        throw new OpenLoomFileException("Error reading file: " + file, e);
	    }
	    
	    if (!replaced) {
            throw new OpenLoomFileException(
                "Line " + lineNumber + " does not exist in file (total lines: " + lines.size() + ")"
            );
        }

	    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), charset))) {
	        for (String l : lines) {
	            writer.write(l);
	            writer.newLine();
	        }
	    } catch (IOException e) {
	        throw new OpenLoomFileException("Error writing file: " + file, e);
	    }
	}
	
	
    /**
     * Safely replaces a single line in the file with new content using an atomic write.
     * <p>
     * This method first writes changes to a temporary file, and upon success,
     * replaces the original file atomically.
     * </p>
     *
     * @param file        the file to modify
     * @param lineNumber  the line number to replace (1-based)
     * @param newContent  the new content for the specified line
     * @param charset     the charset used for reading and writing
     * @throws OpenLoomFileException if the line does not exist or an I/O error occurs
     */
	protected void replaceLineSafe(File file, int lineNumber, String newContent, Charset charset) {
	Validate.validate(file, lineNumber, newContent);

	    Path originalPath = file.toPath();
	    Path tempPath =  Utils.createTemp(file, "replaceLineSafe");

	    boolean replaced = false;
	    int total = 0;

	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
	         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempPath.toFile()), charset))) {

	        String line;
	        int current = 1;

	        while ((line = reader.readLine()) != null) {
	            total++;
	            if (current == lineNumber) {
	                writer.write(newContent);
	                replaced = true;
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
	        throw new OpenLoomFileException("Line " + lineNumber + " does not exist in file (total lines: " + total + ")");
	    }

	    try {
	        Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
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
     * Replaces multiple lines in a file simultaneously.
     * <p>
     * The {@code replacements} map specifies which lines to replace and their new content.
     * All requested lines must exist; otherwise, the operation throws an exception.
     * </p>
     *
     * @param file          the file to modify
     * @param replacements  a map where keys are line numbers and values are new content
     * @param charset       the charset used for reading and writing
     * @throws OpenLoomFileException if one or more line numbers do not exist or an I/O error occurs
     */
	protected void replaceLines(File file, Map<Integer, String> replacements, Charset charset) {
		Validate.validate(file, replacements);

	    List<String> lines = new ArrayList<>();
	    Set<Integer> replacedLines = new HashSet<>();

	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
	        String line;
	        int current = 1;

	        while ((line = reader.readLine()) != null) {
	            if (replacements.containsKey(current)) {
	                lines.add(replacements.get(current));
	                replacedLines.add(current);
	            } else {
	                lines.add(line);
	            }
	            current++;
	        }
	    } catch (IOException e) {
	        throw new OpenLoomFileException("Error reading file: " + file, e);
	    }

	    // strict check
	    Set<Integer> missing = new HashSet<>(replacements.keySet());
	    missing.removeAll(replacedLines);
	    if (!missing.isEmpty()) {
	        throw new OpenLoomFileException("The following lines do not exist in file: " + missing);
	    }

	    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), charset))) {
	        for (String l : lines) {
	            writer.write(l);
	            writer.newLine();
	        }
	    } catch (IOException e) {
	        throw new OpenLoomFileException("Error writing file: " + file, e);
	    }
	}
	
	
	 /**
     * Safely replaces multiple lines in a file using atomic replacement.
     * <p>
     * The operation writes to a temporary file first, ensuring that the original
     * file remains unmodified if an error occurs. All lines in {@code replacements}
     * must exist.
     * </p>
     *
     * @param file          the file to modify
     * @param replacements  a map where keys are line numbers and values are new content
     * @param charset       the charset used for reading and writing
     * @throws OpenLoomFileException if one or more lines do not exist or an I/O error occurs
     */
	protected void replaceLinesSafe(File file, Map<Integer, String> replacements, Charset charset) {
	Validate.validate(file, replacements);

	    Path originalPath = file.toPath();
	    Path tempPath =  Utils.createTemp(file, "replaceLinesSafe");
	    Set<Integer> replacedLines = new HashSet<>();

	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
	         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempPath.toFile()), charset))) {

	        String line;
	        int current = 1;

	        while ((line = reader.readLine()) != null) {
	            if (replacements.containsKey(current)) {
	                writer.write(replacements.get(current));
	                replacedLines.add(current);
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

	    // strict check
	    Set<Integer> missing = new HashSet<>(replacements.keySet());
	    missing.removeAll(replacedLines);
	    if (!missing.isEmpty()) {
	        try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
	        throw new OpenLoomFileException("The following lines do not exist in file: " + missing);
	    }

	    try {
	        Files.move(tempPath, originalPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
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

