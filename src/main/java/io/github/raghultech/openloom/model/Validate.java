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

package io.github.raghultech.openloom.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import io.github.raghultech.openloom.exception.OpenLoomFileException;

public class Validate {

    private Validate() {} // Prevent instantiation

    /* ===========================
       FILE VALIDATION (File API)
       =========================== */
    public static void validateFile(File file) {
        if (file == null)
            throw new OpenLoomFileException("File cannot be null");
        if (!file.exists())
            throw new OpenLoomFileException("File not found: " + file.getAbsolutePath());
        if (!file.isFile())
            throw new OpenLoomFileException("Path is not a file: " + file.getAbsolutePath());
        if (!file.canRead())
            throw new OpenLoomFileException("Cannot read file: " + file.getAbsolutePath());
    }

    /* ===========================
       FILE VALIDATION (Path API)
       =========================== */
    public static void validateFile(Path file) {
        if (file == null)
            throw new OpenLoomFileException("File cannot be null");

        if (!Files.exists(file))
            throw new OpenLoomFileException("File not found: " + file.toAbsolutePath());

        if (!Files.isRegularFile(file))
            throw new OpenLoomFileException("Path is not a regular file: " + file.toAbsolutePath());

        if (!Files.isReadable(file))
            throw new OpenLoomFileException("Cannot read file: " + file.toAbsolutePath());
    }

    /* =====================================
       TARGET FILE VALIDATION (MOVE / WRITE)
       ===================================== */
    public static void validateTargetFile(Path file, boolean replaceExisting) {
        if (file == null)
            throw new OpenLoomFileException("Target file cannot be null");

        if (Files.exists(file)) {
            if (!Files.isRegularFile(file))
                throw new OpenLoomFileException("Target is not a file: " + file);
            if (!replaceExisting)
                throw new OpenLoomFileException("Target file already exists and replaceExisting=false: " + file);
        } else {
            Path parent = file.getParent();
            if (parent == null || !Files.exists(parent))
                throw new OpenLoomFileException("Parent directory does not exist for target file: " + file);
        }
    }

    /* ===================================
       DIRECTORY VALIDATION (READ/EXISTS)
       =================================== */
    public static void validateDirectory(Path dir) {
        if (dir == null)
            throw new OpenLoomFileException("Directory cannot be null");
        if (!Files.exists(dir))
            throw new OpenLoomFileException("Directory not found: " + dir.toAbsolutePath());
        if (!Files.isDirectory(dir))
            throw new OpenLoomFileException("Path is not a directory: " + dir.toAbsolutePath());
        if (!Files.isReadable(dir))
            throw new OpenLoomFileException("Cannot read directory: " + dir.toAbsolutePath());
    }

    /* =======================================
       TARGET DIRECTORY VALIDATION (MOVE/COPY)
       ======================================= */
    public static void validateTargetDirectory(Path dir, boolean replaceExisting) {
        if (dir == null)
            throw new OpenLoomFileException("Target directory cannot be null");

        if (Files.exists(dir)) {
            if (!Files.isDirectory(dir))
                throw new OpenLoomFileException("Target exists and is not a directory: " + dir);
            if (!replaceExisting)
                throw new OpenLoomFileException("Target directory already exists and replaceExisting=false: " + dir);
        } else {
            Path parent = dir.getParent();
            if (parent == null || !Files.exists(parent))
                throw new OpenLoomFileException("Parent directory does not exist for target: " + dir);
        }
    }

    /* ======================================
       VALIDATIONS FOR LINE / CONTENT METHODS
       ====================================== */
    public static void validate(File file, int lineNumber) {
        validateFile(file);
        if (lineNumber < 1)
            throw new OpenLoomFileException("Line number must be greater than 0. Got: " + lineNumber);
    }

    public static void validate(File file, int lineNumber, String content) {
        validateFile(file);
        if (lineNumber < 1)
            throw new OpenLoomFileException("Line number must be greater than 0. Got: " + lineNumber);
        if (content == null)
            throw new OpenLoomFileException("Content string cannot be null");
    }

    public static void validate(File file, String keyword) {
        validateFile(file);
        if (keyword == null || keyword.isEmpty())
            throw new OpenLoomFileException("Keyword must not be null or empty");
    }
    
    public static void validate(File file, String keyword,String replacement ) {
        validateFile(file);
        if (keyword == null )
            throw new OpenLoomFileException("Keyword must not be null ");
        
        if (replacement == null )
            throw new OpenLoomFileException("replacement must not be null");    
    }
    public static void validate(File file, String keyword,String replacement,int lineNumber ) {
        validate(file,lineNumber);
        if (keyword == null )
            throw new OpenLoomFileException("Keyword must not be null ");
        
        if (replacement == null )
            throw new OpenLoomFileException("replacement must not be null");
        
        
    }

    /* ====================================
       VALIDATION FOR MODIFY OPERATIONS
       ==================================== */
    public static void validateModify(File file, Map<Integer, Function<String, String>> modifiers) {
        validateFile(file);
        if (modifiers == null || modifiers.isEmpty())
            throw new OpenLoomFileException("No modifications specified");

        for (Integer key : modifiers.keySet()) {
            if (key == null || key <= 0)
                throw new OpenLoomFileException("Invalid line number: " + key);
        }
    }

    public static void validate(File file, Map<Integer, String> replacements) {
        validateFile(file);
        if (replacements == null || replacements.isEmpty())
            throw new OpenLoomFileException("Replacements cannot be null or empty");
        if (replacements.keySet().stream().anyMatch(i -> i <= 0))
            throw new OpenLoomFileException("Line numbers must be positive: " + replacements.keySet());
        if (replacements.values().stream().anyMatch(Objects::isNull))
            throw new OpenLoomFileException("Replacement content cannot be null");
    }
}
