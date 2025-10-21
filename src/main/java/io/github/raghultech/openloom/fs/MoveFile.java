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
import io.github.raghultech.openloom.model.Utils;
import io.github.raghultech.openloom.model.Validate;

/**
 * ================================================================
 * 📄 MoveFile - File Move Utility
 * ================================================================
 *
 * <p><b>Overview:</b> Provides a safe and efficient method to move a single file
 * from a source path to a target path. Supports optional replacement of existing
 * files and uses atomic moves where possible.</p>
 *
 * <p><b>Important:</b> This class is protected and intended for internal usage.
 * Users should access file move operations through <code>FileManager</code>:</p>
 *
 * <pre>{@code
 * OpenLoom loom = new OpenLoom();
 * loom.file().moveFile(Paths.get("C:/source.txt"), Paths.get("C:/target.txt"), true);
 * }</pre>
 *
 * <p><b>Key features:</b></p>
 * <ul>
 *   <li>✅ Moves files safely using atomic operations</li>
 *   <li>⚡ Supports optional overwriting of existing files</li>
 *   <li>🧠 Resolves final target paths to avoid conflicts</li>
 *   <li>🔄 Wraps I/O errors in detailed OpenLoomFileException</li>
 * </ul>
 */

public class MoveFile {
    
	 /**
     * Moves a file from source to target.
     * <p>If <code>replaceExisting</code> is true, an existing target file
     * will be replaced. Uses atomic moves where supported by the file system.</p>
     *
     * @param source the source file to move
     * @param target the target location
     * @param replaceExisting whether to overwrite the target file if it exists
     * @throws OpenLoomFileException if validation fails or an I/O error occurs
     */
	
    protected void moveInternal(Path source, Path target, boolean replaceExisting) {
        Validate.validateFile(source);
        
        Path finalTarget = Utils.resolveFinalTarget(source, target);
        Validate.validateTargetFile(finalTarget, replaceExisting);

        try {
  
            if (replaceExisting) {
                Files.move(source, finalTarget, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } else {
                Files.move(source, finalTarget, StandardCopyOption.ATOMIC_MOVE);
            }
        } catch (IOException firstEx) {
  
        	 String msg = ErrorBuilder.FileOperationError(firstEx, source, finalTarget, replaceExisting, "MOVE");
             throw new OpenLoomFileException(msg, firstEx);
        }
    }

  
}