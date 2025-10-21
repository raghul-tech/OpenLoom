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
import java.util.function.Function;

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.model.Utils;
import io.github.raghultech.openloom.model.Validate;


/**
 * Provides functionality to modify specific lines or multiple lines in a file.
 * <p>
 * This class is used internally by the {@code SearchManager} within the
 * {@link io.github.raghultech.openloom.OpenLoom} framework and supports both
 * standard and atomic-safe modification operations.
 * </p>
 *
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * OpenLoom loom = new OpenLoom(StandardCharsets.UTF_8);
 * var search = loom.search();
 *
 * // Modify a specific line using a lambda-based modifier
 * search.modifyLine(file, 3, old -> "Modified: " + old);
 *
 * // Modify multiple lines in one operation
 * Map<Integer, Function<String, String>> modifiers = Map.of(
 *     2, line -> "Updated line 2",
 *     5, line -> line.toUpperCase()
 * );
 * search.modifyLines(file, modifiers);
 * }</pre>
 *
 * <p>
 * All modification operations enforce strict validation and ensure that:
 * <ul>
 *   <li>Target line numbers exist in the file.</li>
 *   <li>Modifier functions never return {@code null}.</li>
 *   <li>Atomic-safe methods ensure integrity even on I/O failure.</li>
 * </ul>
 * </p>
 *
 * @see io.github.raghultech.openloom.model.Validate
 * @see io.github.raghultech.openloom.exception.OpenLoomFileException
 */
public class ModifyLine {
	
    /**
     * Modifies a single line in the specified file.
     * <p>
     * The provided modifier function is applied to the line content at
     * the specified line number. The modified result replaces the
     * original line.
     * </p>
     *
     * @param file the target file to modify
     * @param lineNumber the line number to modify (1-based)
     * @param modifier a function that receives the original line and returns the modified line
     * @param charset the character encoding to use for reading/writing
     * @throws OpenLoomFileException if the file cannot be read, written,
     *                               or if the modifier returns {@code null}
     */
	protected void modifyLine(File file, int lineNumber, Function<String, String> modifier, Charset charset) {
	  Validate.validate(file,lineNumber);
	    List<String> lines = new ArrayList<>();
	    boolean modified = false;
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
	        String line;
	        int current = 1;
	        while ((line = reader.readLine()) != null) {
	            if (current == lineNumber) {
	                try {
	                    String result = modifier.apply(line);
	                    if (result == null) {
	                        throw new OpenLoomFileException("Modifier returned null at line " + current);
	                    }
	                    lines.add(result);
	                    modified = true;
	                } catch (Exception e) {
	                    throw new OpenLoomFileException("Modifier function failed at line " + current, e);
	                }
	            } else {
	                lines.add(line);
	            }
	            current++;
	        }
	    } catch (IOException e) {
	        throw new OpenLoomFileException("Error reading file: " + file, e);
	    }
	    
	    if (!modified) {
	        throw new OpenLoomFileException("Line " + lineNumber + " does not exist in file: " + file);
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
     * Safely modifies a single line in a file using an atomic write operation.
     * <p>
     * This method writes to a temporary file and replaces the original only
     * after a successful modification, ensuring that the file is never left
     * in a corrupted or partial state.
     * </p>
     *
     * @param file the target file to modify
     * @param lineNumber the line number to modify (1-based)
     * @param modifier the function that transforms the line content
     * @param charset the character encoding to use
     * @throws OpenLoomFileException if the target line does not exist,
     *                               the modifier fails, or atomic replacement fails
     */

    protected void modifyLineSafe(File file, int lineNumber,
                           Function<String, String> modifier,
                           Charset charset) {
    	  Validate.validate(file,lineNumber);
        Path originalPath = file.toPath();
        Path tempPath =  Utils.createTemp(file, "modifyLineSafe");

        boolean modified = false;

        try (BufferedReader reader = new BufferedReader(
                 new InputStreamReader(new FileInputStream(file), charset));
             BufferedWriter writer = new BufferedWriter(
                 new OutputStreamWriter(new FileOutputStream(tempPath.toFile()), charset))) {

            String line;
            int current = 1;

            while ((line = reader.readLine()) != null) {
                if (current == lineNumber) {
                    try {
                        String newLine = modifier.apply(line);
                        if (newLine == null) {
                            try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
                            throw new OpenLoomFileException("Modifier returned null at line " + lineNumber);
                        }
                        writer.write(newLine);
                        modified = true;
                    } catch (Exception e) {
                    	  try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
                        throw new OpenLoomFileException("Modifier function failed at line " + lineNumber, e);
                    }
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

        if (!modified) {
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ignored) {}
            throw new OpenLoomFileException("Line " + lineNumber + " does not exist in file: " + file);
        }

        // Safely replace original file
        try {
            Files.move(tempPath, originalPath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
        	  try {
                  Files.move(tempPath, originalPath,
                          StandardCopyOption.REPLACE_EXISTING);
              } catch (IOException ex) {
            	  try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
                  throw new OpenLoomFileException("Failed to replace original file: " + file, ex);
              }
        }
    }
    
    
    /**
     * Modifies multiple lines within the same file in a single operation.
     * <p>
     * Each entry in the provided map associates a line number with a
     * modifier function that transforms that specific line’s content.
     * </p>
     *
     * <p>
     * The modification will fail if any specified line does not exist
     * in the file or if a modifier returns {@code null}.
     * </p>
     *
     * @param file the file to modify
     * @param modifiers a map where keys are line numbers and values are modifier functions
     * @param charset the charset to use for reading and writing
     * @throws OpenLoomFileException if any modifier fails, a line is missing,
     *                               or an I/O error occurs
     */
    protected void modifyLines(File file, Map<Integer, Function<String, String>> modifiers,
            Charset charset) {
				Validate.validateModify(file, modifiers);
				List<String> lines = new ArrayList<>();
				Set<Integer> modifiedLines = new HashSet<>();
				
				try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), charset))) {
				
				String line;
				int current = 1;
				
				while ((line = reader.readLine()) != null) {
				if (modifiers.containsKey(current)) {
				    try {
				        String result = modifiers.get(current).apply(line);
				        if (result == null) {
				            throw new OpenLoomFileException("Modifier returned null at line " + current);
				        }
				        lines.add(result);
				        modifiedLines.add(current);
				    } catch (Exception e) {
				        throw new OpenLoomFileException("Modifier failed at line " + current, e);
				    }
				} else {
				    lines.add(line);
				}
				current++;
				}
				} catch (IOException e) {
				throw new OpenLoomFileException("Error reading file: " + file, e);
				}
				
				// Strict validation: all requested lines must exist and be modified
				Set<Integer> missing = new HashSet<>(modifiers.keySet());
				missing.removeAll(modifiedLines);
				
				if (!missing.isEmpty()) {
		    	throw new OpenLoomFileException("The following lines do not exist in file: " + missing);
				}
				
				if (modifiedLines.size() != modifiers.size()) {
				    throw new OpenLoomFileException(
				        "Modification aborted. Requested lines: " + modifiers.keySet() +
				        ", but only resolvable lines were: " + modifiedLines +
				        ". No changes were written to the file."
				    );
				}

				
				try (BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file, false), charset))) {
				for (String l : lines) {
				writer.write(l);
				writer.newLine();
				}
				} catch (IOException e) {
				throw new OpenLoomFileException("Error writing file: " + file, e);
				}
}

    
    /**
     * Safely modifies multiple lines using atomic file replacement.
     * <p>
     * Like {@link #modifyLines(File, Map, Charset)}, but ensures that the
     * original file is replaced only after all modifications are successfully
     * applied to a temporary file.
     * </p>
     *
     * <p>
     * This provides full transactional safety — if any modification fails,
     * no changes are written to the original file.
     * </p>
     *
     * @param file the file to modify
     * @param modifiers a map where keys are line numbers and values are modifier functions
     * @param charset the character encoding to use
     * @throws OpenLoomFileException if any modifier fails, lines are missing,
     *                               or the atomic replacement fails
     */
    protected void modifyLinesSafe(
            File file,
            Map<Integer, Function<String, String>> modifiers,
            Charset charset
    ) {
        Validate.validateModify(file, modifiers);

        Path originalPath = file.toPath();
        Path tempPath =  Utils.createTemp(file, "modifyLinesSafe");
        Set<Integer> modifiedLines = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(
                 new InputStreamReader(new FileInputStream(file), charset));
             BufferedWriter writer = new BufferedWriter(
                 new OutputStreamWriter(new FileOutputStream(tempPath.toFile()), charset))) {

            String line;
            int current = 1;

            while ((line = reader.readLine()) != null) {
                if (modifiers.containsKey(current)) {
                    try {
                        String newLine = modifiers.get(current).apply(line);
                        if (newLine == null) {
                        	 try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
                            throw new OpenLoomFileException("Modifier returned null at line " + current);
                        }
                        writer.write(newLine);
                        modifiedLines.add(current);
                    } catch (Exception e) {
                    	 try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
                        throw new OpenLoomFileException("Modifier failed at line " + current, e);
                    }
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

        // Strict validation: all requested lines must exist
        Set<Integer> missing = new HashSet<>(modifiers.keySet());
        missing.removeAll(modifiedLines);
        if (!missing.isEmpty()) {
        	 try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
            throw new OpenLoomFileException(
                "Modification aborted. The following requested lines do not exist in file: "
                + missing + ". No changes were written."
            );
        }

        // Strict validation: all requested lines must be applied
        if (modifiedLines.size() != modifiers.size()) {
        	 try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
            throw new OpenLoomFileException(
                "Modification aborted. Requested: " + modifiers.keySet() +
                ", Applied: " + modifiedLines +
                ". No changes were written."
            );
        }

        try {
            Files.move(tempPath, originalPath,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            try {
                Files.move(tempPath, originalPath,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
            	 try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
                throw new OpenLoomFileException("Failed to replace original file: " + file, ex);
            }
        }
    }

    
   

}
