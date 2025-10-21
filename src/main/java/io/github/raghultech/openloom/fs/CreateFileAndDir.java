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

import io.github.raghultech.openloom.exception.OpenLoomFileException;
import io.github.raghultech.openloom.model.ErrorBuilder;

/**
 * ================================================================
 * 🛠 CreateFileAndDir - Internal File/Directory Creation Utility
 * ================================================================
 *
 * <p><b>Overview:</b> This class provides protected methods for creating
 * single files or directories. It handles validation, ensures parent directories
 * exist for files, and wraps I/O errors in <code>OpenLoomFileException</code>.</p>
 *
 * <p><b>Important:</b> This class is internal. End-users should not call it directly.
 * Use <code>FileManager</code> from OpenLoom for file and directory creation:</p>
 *
 * <pre>{@code
 * OpenLoom loom = new OpenLoom();
 * loom.file().createFile(Paths.get("C:/example.txt"));
 * loom.file().createDirectories(Paths.get("C:/exampleDir/subDir"));
 * }</pre>
 *
 * <p><b>Key features:</b></p>
 * <ul>
 *   <li>✅ Creates single files safely</li>
 *   <li>⚡ Creates directories recursively</li>
 *   <li>🧠 Throws detailed exceptions for validation or I/O errors</li>
 *   <li>🔄 Internal utility; meant for use via FileManager APIs</li>
 * </ul>
 */

public class CreateFileAndDir {

	 /**
     * Creates a single file at the specified target path.
     * <p>Validates that the parent directory exists before creation.</p>
     *
     * @param target the file path to create
     * @throws OpenLoomFileException if target is null, parent does not exist, or creation fails
     */
	
    protected void createFileInternal(Path target) {
    	 if (target == null) {
             throw new OpenLoomFileException("[CREATE_FILE ERROR] Target path cannot be null.");
         }

         Path parent = target.getParent();
         if (parent != null && !Files.exists(parent)) {
             throw new OpenLoomFileException("[CREATE_FILE ERROR] Parent directory does not exist: " + parent);
         }
    	
        try {
            Files.createFile(target);
        } catch (IOException e) {
            String msg = ErrorBuilder.FileOperationError(e, target, "CREATE_FILE");
            throw new OpenLoomFileException(msg, e);
        }
      
    }
    
    /**
     * Creates directories recursively for the specified path.
     * <p>If the directory already exists, this method does nothing.</p>
     *
     * @param directory the directory path to create
     * @throws OpenLoomFileException if target is null or creation fails
     */
    
    protected void createDirectoriesInternal(Path directory) {
        if (directory == null) {
            throw new OpenLoomFileException("[CREATE_DIR ERROR] Target path cannot be null.");
        }
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            String msg = ErrorBuilder.FileOperationError(e, directory, "CREATE_DIR");
            throw new OpenLoomFileException(msg, e);
        }
    }
	
	
}

