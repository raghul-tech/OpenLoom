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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.model.Utils;
import io.github.raghultech.openloom.model.Validate;


/**
 * -------------------------------------------------------------
 * 📄 {@code InsertLine}
 * -------------------------------------------------------------
 * The {@code InsertLine} class provides high-performance and safe mechanisms
 * for inserting one or more lines into existing text files.
 * 
 * It is designed to integrate with the OpenLoom framework via:
 * <pre>{@code
 * OpenLoom loom = new OpenLoom(StandardCharsets.UTF_8);
 * var insert = loom.insert();
 * insert.insertLine(file, 2, "Inserted line at position 2");
 * }</pre>
 *
 * The class offers both standard and atomic-safe variants:
 * <ul>
 *   <li>{@link #insertLine(File, int, String, Charset)} — direct insertion into memory and overwrite.</li>
 *   <li>{@link #insertLineSafe(File, int, String, Charset)} — atomic write to a temp file, replaces original safely.</li>
 *   <li>{@link #insertLines(File, Map, Charset)} — batch insert of multiple lines.</li>
 *   <li>{@link #insertLinesSafe(File, Map, Charset)} — atomic-safe version of batch insert.</li>
 * </ul>
 *
 * <h3>Safety and Integrity</h3>
 * <ul>
 *   <li>All operations are validated before modification.</li>
 *   <li>Safe variants ensure atomic replacement (no corruption if interrupted).</li>
 *   <li>Temp files are auto-deleted on failure.</li>
 * </ul>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * OpenLoom loom = new OpenLoom(StandardCharsets.UTF_8);
 * var insert = loom.insert();
 *
 * // Insert a single line at line 2
 * insert.insertLine(file, 2, "Inserted line at position 2");
 *
 * // Insert multiple lines
 * Map<Integer, String> inserts = new HashMap<>();
 * inserts.put(1, "Header line");
 * inserts.put(4, "Footer line");
 * insert.insertLinesSafe(file, inserts, StandardCharsets.UTF_8);
 * }</pre>
 *
 * @author Raghul John (@raghul-tech)
 * @version 1.0
 * @since 2025
 */
public class InsertLine {
	
	
    /**
     * Inserts a single line at the specified position within the file.
     * <p>
     * This version reads all lines into memory, inserts the new line, and rewrites
     * the entire file. It is fast and lightweight, but not atomic — use
     * {@link #insertLineSafe(File, int, String, Charset)} for production-grade safety.
     *
     * @param file        the file to modify (must exist and be writable)
     * @param lineNumber  the line number (1-based) where the new line should be inserted
     * @param newContent  the content to insert
     * @param charset     the charset to use for reading and writing
     * @throws OpenLoomFileException if the file cannot be read, written, or line does not exist
     */
	protected void insertLine(File file, int lineNumber, String newContent, Charset charset) {
	   Validate.validate(file, lineNumber, newContent);

	    List<String> lines = new ArrayList<>();
	    boolean inserted = false;
	    
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
	        String line;
	        int current = 1;
	        while ((line = reader.readLine()) != null) {
	            if (current == lineNumber) {
	                lines.add(newContent);
	                inserted = true;
	            }
	            lines.add(line);
	            current++;
	        }
	      
	    } catch (IOException e) {
	        throw new OpenLoomFileException("Error reading file: " + file, e);
	    }
	    
	    if (!inserted) {
	        throw new OpenLoomFileException("Line " + lineNumber + " does not exist. Use append() instead.");
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
     * Safely inserts a single line using atomic replacement.
     * <p>
     * Writes to a temporary file first, then atomically replaces the original.
     * If insertion fails (e.g., line number out of bounds), the original file
     * remains untouched.
     *
     * @param file        the target file
     * @param lineNumber  the 1-based line number where the new line will be inserted
     * @param newContent  the content to insert
     * @param charset     the charset to use for reading and writing
     * @throws OpenLoomFileException if any I/O or validation error occurs
     */
	protected void insertLineSafe(File file, int lineNumber, String newContent, Charset charset) {
	    Validate.validate(file, lineNumber, newContent);

	    Path originalPath = file.toPath();
	    Path tempPath =  Utils.createTemp(file, "insertLineSafe");

	    boolean inserted = false;

	    try (
	        BufferedReader reader = Files.newBufferedReader(originalPath, charset);
	        BufferedWriter writer = Files.newBufferedWriter(tempPath, charset)
	    ) {
	        String line;
	        int current = 1;

	        while ((line = reader.readLine()) != null) {
	            if (current == lineNumber) {
	                writer.write(newContent);
	                writer.newLine();
	                inserted = true;
	            }
	            writer.write(line);
	            writer.newLine();
	            current++;
	        }
	    } catch (IOException e) {
	        try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
	        throw new OpenLoomFileException("Error processing file during safe insert: " + file, e);
	    }

	    // Strict validation: insertion must succeed
	    if (!inserted) {
	        try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
	        throw new OpenLoomFileException(
	            "Insertion aborted. Line " + lineNumber +
	            " does not exist in file. Consider using append() instead. No changes were written."
	        );
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
	            throw new OpenLoomFileException("Failed to replace original file after safe insert: " + file, ex);
	        }
	    }
	}

    /**
     * Inserts multiple lines into a file at specified line positions.
     * <p>
     * Line numbers are 1-based and processed in ascending order.
     * If a specified line number exceeds the total line count, the new
     * line will be appended to the end.
     *
     * @param file     the target file
     * @param inserts  a map of line numbers to content (1-based)
     * @param charset  the charset to use
     * @throws OpenLoomFileException if validation or I/O fails
     */
	protected void insertLines(File file, Map<Integer, String> inserts, Charset charset) {
	    Validate.validate(file, inserts);

	    List<String> original = new ArrayList<>();

	    // Read all original lines
	    try (BufferedReader reader = new BufferedReader(
	            new InputStreamReader(new FileInputStream(file), charset))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            original.add(line);
	        }
	    } catch (IOException e) {
	        throw new OpenLoomFileException("Error reading file: " + file, e);
	    }

	    List<String> result = new ArrayList<>(original);
	    int offset = 0;

	    // Ensure deterministic order
	    for (Map.Entry<Integer, String> entry : new TreeMap<>(inserts).entrySet()) {
	        int lineNumber = entry.getKey();
	        if (lineNumber <= 0) {
	            throw new OpenLoomFileException("Line number must be >= 1, got " + lineNumber);
	        }

	        int position = lineNumber + offset - 1; // 0-based index
	        String newLine = entry.getValue();
	        if (newLine == null) {
	            throw new OpenLoomFileException("Insert value cannot be null at line " + lineNumber);
	        }

	        if (position >= result.size()) {
	            result.add(newLine); // append if beyond EOF
	        } else {
	            result.add(position, newLine);
	        }
	        offset++;
	    }
	    // Write back
	    try (BufferedWriter writer = new BufferedWriter(
	            new OutputStreamWriter(new FileOutputStream(file, false), charset))) {
	        for (String l : result) {
	            writer.write(l);
	            writer.newLine();
	        }
	    } catch (IOException e) {
	        throw new OpenLoomFileException("Error writing file: " + file, e);
	    }
	}


    /**
     * Safely inserts multiple lines with atomic replacement.
     * <p>
     * All inserts are written to a temporary file first, ensuring that
     * no partial modifications occur in case of failure.
     *
     * @param file     the target file
     * @param inserts  a map of line numbers to new content
     * @param charset  the charset used for file I/O
     * @throws OpenLoomFileException if any I/O or validation error occurs
     */
	protected void insertLinesSafe(File file, Map<Integer, String> inserts, Charset charset) {
	    Validate.validate(file, inserts);

	    Path originalPath = file.toPath();
	    Path tempPath =  Utils.createTemp(file, "insertLinesSafe");
	    
	    List<String> original = new ArrayList<>();

	    // Read all original lines
	    try (BufferedReader reader = new BufferedReader(
	            new InputStreamReader(new FileInputStream(file), charset))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            original.add(line);
	        }
	    } catch (IOException e) {
	        try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
	        throw new OpenLoomFileException("Error reading file: " + file, e);
	    }

	    List<String> result = new ArrayList<>(original);
	    int offset = 0;

	    // Ensure deterministic order
	    for (Map.Entry<Integer, String> entry : new TreeMap<>(inserts).entrySet()) {
	        int lineNumber = entry.getKey();
	        if (lineNumber <= 0) {
	        	 try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
	            throw new OpenLoomFileException("Line number must be >= 1, got " + lineNumber);
	        }

	        int position = lineNumber + offset - 1; // 0-based index
	        String newLine = entry.getValue();
	        if (newLine == null) {
	        	 try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
	            throw new OpenLoomFileException("Insert value cannot be null at line " + lineNumber);
	        }

	        if (position >= result.size()) {
	            result.add(newLine); // append if beyond EOF
	        } else {
	            result.add(position, newLine);
	        }
	        offset++;
	    }


	    // 🔍 Strict validation: ensure all inserts applied
	    if (result.size() < original.size() + inserts.size()) {
	        try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
	        throw new OpenLoomFileException("Insert aborted. Some inserts were not applied.");
	    }

	    // Write to temp
	    try (BufferedWriter writer = new BufferedWriter(
	            new OutputStreamWriter(new FileOutputStream(tempPath.toFile()), charset))) {
	        for (String l : result) {
	            writer.write(l);
	            writer.newLine();
	        }
	    } catch (IOException e) {
	        try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
	        throw new OpenLoomFileException("Error writing temp file during safe insert: " + file, e);
	    }

	    // Replace atomically
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

