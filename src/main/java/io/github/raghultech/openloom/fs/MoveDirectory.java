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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.model.ErrorBuilder;
import io.github.raghultech.openloom.model.Validate;

/**
 * ================================================================
 * 📂 MoveDirectory - Directory Move Utility
 * ================================================================
 *
 * <p><b>Overview:</b> Provides a robust method to move directories from a source path
 * to a target path. Supports optional replacement of existing directories
 * and prevents moving a directory into itself or its subdirectories.</p>
 *
 * <p><b>Important:</b> This class is protected and intended for internal usage.
 * End-users should access directory move operations via <code>FileManager</code>:</p>
 *
 * <pre>{@code
 * OpenLoom loom = new OpenLoom();
 * loom.file().moveDir(Paths.get("C:/sourceDir"), Paths.get("C:/targetDir"), true);
 * }</pre>
 *
 * <p><b>Key features:</b></p>
 * <ul>
 *   <li>✅ Moves directories safely and efficiently</li>
 *   <li>⚡ Supports optional overwriting of existing directories</li>
 *   <li>🧠 Prevents recursive moves into self or subdirectories</li>
 *   <li>🔄 Wraps I/O errors in detailed OpenLoomFileException</li>
 * </ul>
 */

public class MoveDirectory {

	 /**
     * Moves a directory from source to target.
     * <p>If <code>replaceExisting</code> is true, an existing target directory
     * will be replaced. Throws an exception if the target is inside the source
     * directory to prevent recursion.</p>
     *
     * @param source the source directory to move
     * @param target the target directory where the source will be moved
     * @param replaceExisting whether to replace existing target directory
     * @throws OpenLoomFileException if validation fails or an I/O error occurs
     */
    protected void moveInternal(Path source, Path target, boolean replaceExisting) {
        Path normalizedSource = source.toAbsolutePath().normalize();
        Path normalizedTarget = target.toAbsolutePath().normalize();

        Validate.validateDirectory(normalizedSource);
        Validate.validateTargetDirectory(normalizedTarget, replaceExisting);

        // CRITICAL: Add recursion prevention
        if (normalizedTarget.startsWith(normalizedSource)) {
            throw new OpenLoomFileException(
                "Cannot move directory into itself or its subdirectory:\n" +
                "Source: " + normalizedSource + "\n" +
                "Target: " + normalizedTarget
            );
        }

        try {
            if (replaceExisting) {
                Files.move(normalizedSource, normalizedTarget, StandardCopyOption.REPLACE_EXISTING);
            } else {
                Files.move(normalizedSource, normalizedTarget);
            }
        } catch (IOException e) {
            String msg = ErrorBuilder.FileOperationError(e, source, target, replaceExisting, "MOVE_DIR");
            throw new OpenLoomFileException(msg, e);
        }
    }
}
