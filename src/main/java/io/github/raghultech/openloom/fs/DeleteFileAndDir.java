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

package io.github.raghultech.openloom.fs;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.model.ErrorBuilder;
import io.github.raghultech.openloom.model.Validate;

/**
 * ================================================================
 * 🗑 DeleteFileAndDir - Internal File/Directory Deletion Utility
 * ================================================================
 *
 * <p><b>Overview:</b> Provides protected methods to safely delete files and directories.
 * It first attempts to move items to the OS Trash/Recycle Bin and falls back to
 * permanent deletion if necessary. Supports recursive deletion for directories.</p>
 *
 * <p><b>Important:</b> This class is internal. End-users should not call it directly.
 * Use <code>FileManager</code> from OpenLoom for deletion operations:</p>
 *
 * <pre>{@code
 * OpenLoom loom = new OpenLoom();
 * loom.file().deleteFile(Paths.get("C:/example.txt"));           // deletes a file
 * loom.file().deleteDir(Paths.get("C:/exampleDir"));       // deletes a directory recursively
 * }</pre>
 *
 * <p><b>Key features:</b></p>
 * <ul>
 *   <li>✅ Deletes single files or entire directories</li>
 *   <li>⚡ Attempts to move items to Trash/Recycle Bin before permanent deletion</li>
 *   <li>🧠 Recursive deletion preserves safe order (children before parent)</li>
 *   <li>🔄 Wraps I/O errors in detailed OpenLoomFileException</li>
 * </ul>
 */

public class DeleteFileAndDir {

	 /**
     * Deletes a single file.
     * <p>Tries to move it to Trash/Recycle Bin first; falls back to permanent delete if unsuccessful.</p>
     *
     * @param path the file to delete
     * @throws OpenLoomFileException if validation fails or deletion fails
     */
    protected void deleteInternal(Path path) {
        Validate.validateFile(path);
        File file = path.toFile();

        try {
            if (tryMoveToTrash(file)) {
                return; // successfully moved to trash
            }

            // Fallback → permanent delete
            Files.delete(path);

        } catch (IOException e) {
        	 throw new OpenLoomFileException(
                     ErrorBuilder.FileOperationError(e, path, "DELETE_FILE"), e
             );
        }
    }
    
    /**
     * Permanently deletes a single file without using Trash/Recycle Bin.
     *
     * @param path the file to permanently delete
     * @throws OpenLoomFileException if validation fails or deletion fails
     */
    protected void deleteInternalPermanent(Path path) {
    	 Validate.validateFile(path);
    	 try {
			Files.delete(path);
		} catch (IOException e) {
			 throw new OpenLoomFileException(
                     ErrorBuilder.FileOperationError(e, path, "DELETE_FILE_PERMANENT"), e
             );
		}
    }
    
    /**
     * Permanently deletes a directory recursively.
     * <p>All children are deleted before the parent directory.</p>
     *
     * @param dir the directory to permanently delete
     * @throws OpenLoomFileException if validation fails or deletion fails
     */
    protected void deleteDirectoryPermanent(Path dir) {
    	 Validate.validateDirectory(dir);
    	try {
    		 // Fallback → recursive permanent delete
            Files.walk(dir)
                 .sorted((a, b) -> b.compareTo(a)) // delete children before parent
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                     } catch (IOException e) {
                    	 throw new OpenLoomFileException(
                                 ErrorBuilder.FileOperationError(e, path, "DELETE_DIR_PERMANENT"), e
                         );
                     }
                 });

        } catch (IOException e) {
        	throw new OpenLoomFileException(
                    ErrorBuilder.FileOperationError(e, dir, "DELETE_DIR_PERMANENT"), e
            );
        }
    }


    /**
     * Deletes a directory.
     * <p>Tries to move it to Trash/Recycle Bin first; falls back to recursive permanent delete if unsuccessful.</p>
     *
     * @param dir the directory to delete
     * @throws OpenLoomFileException if validation fails or deletion fails
     */
    protected void deleteDirectoryInternal(Path dir) {
        Validate.validateDirectory(dir);
        File directoryFile = dir.toFile();

        try {
            if (tryMoveToTrash(directoryFile)) {
                return; // successfully moved to trash
            }

            // Fallback → recursive permanent delete
            Files.walk(dir)
                 .sorted((a, b) -> b.compareTo(a)) // delete children before parent
                 .forEach(path -> {
                     try {
                         Files.delete(path);
                     } catch (IOException e) {
                    	 throw new OpenLoomFileException(
                                 ErrorBuilder.FileOperationError(e, path, "DELETE_DIR"), e
                         );
                     }
                 });

        } catch (IOException e) {
        	throw new OpenLoomFileException(
                    ErrorBuilder.FileOperationError(e, dir, "DELETE_DIR"), e
            );
        }
    }

    /**
     * Attempts to move a file or directory to the OS Trash/Recycle Bin.
     * <p>Supports Windows, macOS, and Linux (Gnome/KDE). Returns false if unsupported.</p>
     *
     * @param file the file or directory to move to trash
     * @return true if successfully moved to trash, false otherwise
     */
    private boolean tryMoveToTrash(File file) {
        try {
            // First try Desktop API (works on Windows & macOS in most cases)
            if (Desktop.isDesktopSupported()) {
                try {
                    boolean moved = Desktop.getDesktop().moveToTrash(file);
                    if (moved) {
                        return true;
                    }
                } catch (UnsupportedOperationException ignored) {
                    // Not supported on this platform
                }
            }

            // OS-specific fallbacks
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("mac")) {
                // macOS Finder AppleScript
                new ProcessBuilder("osascript", "-e",
                        "tell app \"Finder\" to delete POSIX file \"" + file.getAbsolutePath() + "\"")
                        .inheritIO().start().waitFor();
                return true;
            } else if (os.contains("nix") || os.contains("nux")) {
                // Linux: try gio trash, then trash-put
                try {
                    Process p = new ProcessBuilder("gio", "trash", file.getAbsolutePath())
                            .inheritIO().start();
                    if (p.waitFor() == 0) return true;
                } catch (IOException ignored) { }

                try {
                    Process p = new ProcessBuilder("trash-put", file.getAbsolutePath())
                            .inheritIO().start();
                    if (p.waitFor() == 0) return true;
                } catch (IOException ignored) { }
            }

        } catch (Exception e) {
            // If anything fails → fallback to permanent delete
        }
        return false;
    }
}
