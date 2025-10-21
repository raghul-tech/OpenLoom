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

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Centralized utility for building detailed, readable error messages
 * for all file-related operations in OpenLoom.
 */
public final class ErrorBuilder {

    private ErrorBuilder() {
        // Utility class — prevent instantiation
    }

    /**
     * Builds a detailed message for file/directory operations such as move, copy, delete, etc.
     *
     * @param e the IOException that occurred
     * @param source the source path
     * @param target the target path
     * @param replaceExisting flag for replacement
     * @param operation operation name (MOVE, COPY, DELETE, etc.)
     * @return formatted, human-readable error message
     */
    public static String FileOperationError(IOException e, Path source, Path target, boolean replaceExisting, String operation) {
        String srcName = (source != null) ? source.getFileName().toString() : "UNKNOWN_SOURCE";
        String tgtName = (target != null) ? target.getFileName().toString() : "UNKNOWN_TARGET";

        if (e instanceof FileAlreadyExistsException) {
            return String.format("[%s ERROR] Cannot %s '%s' to '%s' — target already exists (replaceExisting=%s).",
                    operation, operation.toLowerCase(), srcName, tgtName, replaceExisting);
        } else if (e instanceof NoSuchFileException) {
            return String.format("[%s ERROR] Source '%s' not found. Operation aborted.", operation, srcName);
        } else if (e instanceof AccessDeniedException) {
            return String.format("[%s ERROR] Access denied when trying to %s '%s' to '%s'. Check permissions.",
                    operation, operation.toLowerCase(), srcName, tgtName);
        } else if (e instanceof DirectoryNotEmptyException) {
            return String.format("[%s ERROR] Cannot %s '%s' — directory not empty.", operation, srcName);
        } else if (e instanceof AtomicMoveNotSupportedException) {
            return String.format("[%s WARNING] Atomic move not supported for '%s'. Falling back to copy-delete.", operation, srcName);
        } else if (e instanceof FileSystemException) {
            return String.format("[%s ERROR] File system issue while performing %s: %s → %s. System message: %s",
                    operation, operation.toLowerCase(), srcName, tgtName, e.getMessage());
        } else {
            return String.format("[%s ERROR] Failed to %s '%s' → '%s'. Reason: %s",
                    operation, operation.toLowerCase(), srcName, tgtName, e.getMessage());
        }
    }
    
    
    
    public static String FileOperationError(IOException e, Path path, String action) {
        if (e instanceof FileAlreadyExistsException) {
            return String.format("[%s ERROR] '%s' already exists.", action, path);
        } else if (e instanceof NoSuchFileException) {
            return String.format("[%s ERROR] File or directory '%s' not found.", action, path);
        } else if (e instanceof AccessDeniedException) {
            return String.format("[%s ERROR] Access denied when trying to %s '%s'. Check file permissions.",
                    action, action.toLowerCase(), path);
        } else {
            return String.format("[%s ERROR] Failed to %s '%s'. Error: %s",
                    action, action.toLowerCase(), path, e.getMessage());
        }
    }



    /**
     * Builds an error message for read/write failures.
     *
     * @param e the IOException that occurred
     * @param file the file path
     * @param action the action (READ, WRITE, APPEND, etc.)
     * @return formatted error message
     */
    public static String ReadWriteError(IOException e, Path file, String action) {
        String fileName = (file != null && file.getFileName() != null)
                ? file.getFileName().toString()
                : "UNKNOWN_FILE";

        if (e instanceof NoSuchFileException) {
            return String.format("[%s ERROR] File '%s' not found. Cannot proceed with %s.",
                    action, fileName, action.toLowerCase());
        } else if (e instanceof AccessDeniedException) {
            return String.format("[%s ERROR] Access denied when trying to %s '%s'. Check file permissions.",
                    action, action.toLowerCase(), fileName);
        } else {
            return String.format("[%s ERROR] Failed to %s file '%s'. Reason: %s",
                    action, action.toLowerCase(), fileName, e.getMessage());
        }
    }
}


