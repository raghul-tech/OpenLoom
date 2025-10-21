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

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.model.ErrorBuilder;
import io.github.raghultech.openloom.model.Validate;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

/**
 * ================================================================
 * 📂 CopyDirectory - Internal Directory Copy Utility
 * ================================================================
 *
 * <p><b>Overview:</b> Internal utility class used by OpenLoom to recursively
 * copy directories and their contents from a source path to a target path.
 * Supports symbolic links, preserves file attributes, and optionally replaces existing files.</p>
 *
 * <p><b>Important:</b> This is a <b>protected/internal</b> class.
 * End-users should <b>not</b> use this class directly. To copy directories,
 * use the <code>FileManager</code> APIs from OpenLoom:</p>
 *
 * <pre>{@code
 * OpenLoom loom = new OpenLoom();
 * loom.file().copyDir(Paths.get("C:/sourceDir"), Paths.get("C:/targetDir"), true);
 * }</pre>
 *
 * <p><b>Key features:</b></p>
 * <ul>
 *   <li>✅ Recursively copies all subdirectories and files</li>
 *   <li>⚡ Preserves last modified times and file attributes</li>
 *   <li>🧠 Skips copying the target directory if inside the source</li>
 *   <li>🔄 Supports replaceExisting flag to overwrite files</li>
 * </ul>
 */

public class CopyDirectory {

	 /**
     * Recursively copy a directory from source to target.
     * <p>This method walks through the directory tree starting from the source,
     * creates corresponding directories in the target, and copies all files. It also
     * handles symbolic links and preserves basic file attributes.</p>
     *
     * <p>Important behavior:</p>
     * <ul>
     *   <li>If <code>replaceExisting</code> is true, existing files at the target
     *       are overwritten.</li>
     *   <li>Target directories are automatically created if missing.</li>
     *   <li>Files or directories inside the target path are skipped to prevent recursion.</li>
     * </ul>
     *
     * <p><b>Note:</b> This method is <b>protected</b> and intended to be used only
     * through OpenLoom's <code>FileManager</code> class.</p>
     *
     * @param source the source directory to copy
     * @param target the target directory where contents will be copied
     * @param replaceExisting whether to replace existing files in the target
     * @throws OpenLoomFileException if an I/O error occurs or directory validation fails
     */
	
	protected void copyInternal(Path source, Path target, boolean replaceExisting) {
	    Path normalizedSource = source.toAbsolutePath().normalize();
	    Path normalizedTarget = target.toAbsolutePath().normalize();

	    Validate.validateDirectory(normalizedSource);
	    Validate.validateTargetDirectory(normalizedTarget, replaceExisting);

	    // Create the target root directory if missing
	    try {
	        if (!Files.exists(normalizedTarget)) {
	            Files.createDirectories(normalizedTarget);
	        }
	    } catch (IOException e) {
	        throw new OpenLoomFileException("Failed to create target directory: " + normalizedTarget, e);
	    }

	    try {
	        Files.walkFileTree(normalizedSource, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
	            new SimpleFileVisitor<Path>() {
	                
	                @Override
	                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
	                    Path relative = normalizedSource.relativize(dir);
	                    Path targetDir = normalizedTarget.resolve(relative);

	                    // Skip copying if this folder is exactly the target folder
	                    if (dir.toAbsolutePath().normalize().equals(normalizedTarget)) {
	                        return FileVisitResult.SKIP_SUBTREE;
	                    }

	                    if (!Files.exists(targetDir)) {
	                        Files.createDirectories(targetDir);
	                        try {
	                            Files.setAttribute(targetDir, "basic:lastModifiedTime", Files.getAttribute(dir, "basic:lastModifiedTime"));
	                        } catch (Exception ignored) {}
	                    }

	                    return FileVisitResult.CONTINUE;
	                }

	                @Override
	                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	                    Path relative = normalizedSource.relativize(file);
	                    Path targetFile = normalizedTarget.resolve(relative);

	                    // Skip files that are inside the target folder
	                    if (file.toAbsolutePath().normalize().startsWith(normalizedTarget)) {
	                        return FileVisitResult.CONTINUE;
	                    }

	                    Files.createDirectories(targetFile.getParent());
	                    CopyOption[] copyOptions = replaceExisting ?
	                        new CopyOption[]{StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES} :
	                        new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES};

	                    Files.copy(file, targetFile, copyOptions);
	                    return FileVisitResult.CONTINUE;
	                }
	            });
	    } catch (IOException e) {
	        throw new OpenLoomFileException(ErrorBuilder.FileOperationError(e, source, target, replaceExisting, "COPY_DIR"), e);
	    }
	}


	
}


