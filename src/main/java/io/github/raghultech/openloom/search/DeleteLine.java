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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.exception.OpenLoomMemoryException;
import io.github.raghultech.openloom.model.Utils;
import io.github.raghultech.openloom.model.Validate;

/**
 * Provides line deletion operations for files with both in-memory and safe (temp-file-based) strategies.
 * <p>
 * The {@code DeleteLine} class supports removing one or more lines from text files using a specified
 * {@link java.nio.charset.Charset}. It includes both standard and fail-safe delete operations.
 * <br><br>
 * Safe methods create a temporary file and atomically replace the original file, ensuring data integrity
 * in case of crashes or I/O errors during processing.
 * </p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *   <li>Delete a single line by line number</li>
 *   <li>Delete multiple lines by collection of line numbers</li>
 *   <li>Safe versions that use temporary files and atomic moves</li>
 *   <li>Comprehensive error handling with custom exceptions</li>
 * </ul>
 *
 * <p>
 * Throws {@link io.github.raghultech.openloom.exception.OpenLoomFileException}
 * for file-related errors and {@link io.github.raghultech.openloom.exception.OpenLoomMemoryException}
 * for large-file memory constraints.
 * </p>
 */
public class DeleteLine {

    /**
     * Deletes a single line from the specified file.
     * <p>
     * Reads all lines into memory, removes the target line, and rewrites the file.
     * This method is fast but may consume more memory for large files.
     * </p>
     *
     * @param file        the file from which a line will be deleted
     * @param lineNumber  the line number to delete (1-based)
     * @param charset     the character encoding of the file
     * @throws io.github.raghultech.openloom.exception.OpenLoomFileException if the line does not exist or an I/O error occurs
     * @throws io.github.raghultech.openloom.exception.OpenLoomMemoryException if the file is too large for memory-based processing
     */
	protected void deleteLine(File file, int lineNumber, Charset charset) {
	   Validate.validate(file, lineNumber);
	    List<String> lines = new ArrayList<>();
	    boolean deleted = false;

	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
	        String line;
	        int current = 1;
	        while ((line = reader.readLine()) != null) {
	            if (current == lineNumber) {
	                deleted = true;
	            } else {
	                lines.add(line);
	            }
	            current++;
	        }
	    } catch (OutOfMemoryError e) {
	        throw new OpenLoomMemoryException(
	            "The file is too large to delete lines in memory. Please use deleteLineSafe() instead.", e
	        );
	    } catch (IOException e) {
	        throw new OpenLoomFileException("Error reading file: " + file, e);
	    }

	    if (!deleted) {
	        throw new OpenLoomFileException("Line " + lineNumber + " does not exist. Nothing deleted.");
	    }

	    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), charset))) {
	        for (String l : lines) {
	            writer.write(l);
	            writer.newLine();
	        }
	    } catch (OutOfMemoryError e) {
	        throw new OpenLoomMemoryException(
	            "The file is too large to write changes in memory. Please use deleteLineSafe() instead.", e
	        );
	    } catch (IOException e) {
	        throw new OpenLoomFileException("Error writing file: " + file, e);
	    }
	}
	
	  /**
     * Safely deletes a single line using a temporary file.
     * <p>
     * This method is safer than {@link #deleteLine(File, int, Charset)} as it does not load
     * the entire file into memory. Instead, it streams line by line and writes to a temp file,
     * replacing the original file atomically.
     * </p>
     *
     * @param file        the file from which a line will be deleted
     * @param lineNumber  the line number to delete (1-based)
     * @param charset     the character encoding of the file
     * @throws io.github.raghultech.openloom.exception.OpenLoomFileException if the line does not exist or an I/O error occurs
     */
	   protected  void deleteLineSafe(File file, int lineNumber, Charset charset) {
	     Validate.validate(file, lineNumber);

	        Path originalPath = file.toPath();
	        Path tempPath = Utils.createTemp(file, "deleteLineSafe");
	        
	        boolean deleted = false;

	        try (
	                BufferedReader reader = Files.newBufferedReader(originalPath, charset);
	                BufferedWriter writer = Files.newBufferedWriter(tempPath, charset)
	        ) {
	            String line;
	            int current = 1;
	            while ((line = reader.readLine()) != null) {
	                if (current == lineNumber) {
	                    deleted = true;
	                } else {
	                    writer.write(line);
	                    writer.newLine();
	                }
	                current++;
	            }
	        } catch (IOException e) {
	            try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
	            throw new OpenLoomFileException("Error processing file during safe delete: " + file, e);
	        }

	        if (!deleted) {
	            try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
	            throw new OpenLoomFileException("Line " + lineNumber + " does not exist. No changes were written.");
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
	                throw new OpenLoomFileException("Failed to replace original file after safe delete: " + file, ex);
	            }
	        }
	    }
	   
	   
	   /**
	     * Deletes multiple lines from the file in memory.
	     * <p>
	     * Reads the entire file, removes all specified lines, and writes back the result.
	     * Throws an exception if any of the requested line numbers do not exist.
	     * </p>
	     *
	     * @param file         the file to modify
	     * @param lineNumbers  the collection of line numbers to delete (1-based)
	     * @param charset      the character encoding of the file
	     * @throws io.github.raghultech.openloom.exception.OpenLoomFileException if a line number is invalid or I/O fails
	     */
	   protected void deleteLines(File file, Collection<Integer> lineNumbers, Charset charset) {
		    Validate.validateFile(file);
		    if (lineNumbers == null || lineNumbers.isEmpty()) {
		        throw new OpenLoomFileException("No line numbers provided for deletion");
		    }

		    Set<Integer> toDelete = new TreeSet<>(lineNumbers); // sorted for deterministic behavior
		    if (toDelete.contains(0)) {
		        throw new OpenLoomFileException("Line numbers must be >= 1");
		    }

		    List<String> original = new ArrayList<>();
		    try (BufferedReader reader = new BufferedReader(
		            new InputStreamReader(new FileInputStream(file), charset))) {
		        String line;
		        while ((line = reader.readLine()) != null) {
		            original.add(line);
		        }
		    } catch (IOException e) {
		        throw new OpenLoomFileException("Error reading file: " + file, e);
		    }

		    List<String> result = new ArrayList<>();
		    Set<Integer> foundDeleted = new HashSet<>();
		    int current = 1;

		    for (String line : original) {
		        if (toDelete.contains(current)) {
		            foundDeleted.add(current); // mark as deleted
		        } else {
		            result.add(line);
		        }
		        current++;
		    }

		    // check if all requested deletes were actually possible
		    Set<Integer> notFound = new TreeSet<>(toDelete);
		    notFound.removeAll(foundDeleted);
		    if (!notFound.isEmpty()) {
		        throw new OpenLoomFileException("The following requested lines do not exist: " + notFound);
		    }

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
	     * Safely deletes multiple lines using a temporary file.
	     * <p>
	     * Processes the file line by line, omitting lines specified for deletion,
	     * and writes the remaining lines to a temporary file. Once complete, the temp file
	     * replaces the original atomically.
	     * </p>
	     *
	     * @param file         the file to modify
	     * @param lineNumbers  the collection of line numbers to delete (1-based)
	     * @param charset      the character encoding of the file
	     * @throws io.github.raghultech.openloom.exception.OpenLoomFileException if a line number does not exist or an I/O error occurs
	     */
	   protected void deleteLinesSafe(File file, Collection<Integer> lineNumbers, Charset charset) {
		    Validate.validateFile(file);
		    if (lineNumbers == null || lineNumbers.isEmpty()) {
		        throw new OpenLoomFileException("No line numbers provided for deletion");
		    }

		    Set<Integer> toDelete = new TreeSet<>(lineNumbers); // deterministic order
		    if (toDelete.contains(0)) {
		        throw new OpenLoomFileException("Line numbers must be >= 1");
		    }

		    Path originalPath = file.toPath();
		    Path tempPath =  Utils.createTemp(file, "deleteLinesSafe");

		    Set<Integer> foundDeleted = new HashSet<>();
		    int current = 1;

		    try (
		            BufferedReader reader = Files.newBufferedReader(originalPath, charset);
		            BufferedWriter writer = Files.newBufferedWriter(tempPath, charset)
		    ) {
		        String line;
		        while ((line = reader.readLine()) != null) {
		            if (toDelete.contains(current)) {
		                foundDeleted.add(current); // deleted
		            } else {
		                writer.write(line);
		                writer.newLine();
		            }
		            current++;
		        }
		    } catch (IOException e) {
		        try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
		        throw new OpenLoomFileException("Error processing file during safe delete: " + file, e);
		    }

		    // strict check
		    Set<Integer> notFound = new TreeSet<>(toDelete);
		    notFound.removeAll(foundDeleted);
		    if (!notFound.isEmpty()) {
		        try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
		        throw new OpenLoomFileException("The following requested lines do not exist: " + notFound);
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
		            throw new OpenLoomFileException("Failed to replace original file after safe delete: " + file, ex);
		        }
		    }
		}



	
}

