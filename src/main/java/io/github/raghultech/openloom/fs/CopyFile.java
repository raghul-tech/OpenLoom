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
 * 📄 CopyFile - Internal File Copy Utility
 * ================================================================
 *
 * <p><b>Overview:</b> Internal utility class used by OpenLoom to copy a single
 * file from a source path to a target path. Supports preserving file attributes
 * and optionally replacing existing files.</p>
 *
 * <p><b>Important:</b> This is a <b>protected/internal</b> class.
 * End-users should <b>not</b> use this class directly. To copy files,
 * use the <code>FileManager</code> APIs from OpenLoom:</p>
 *
 * <pre>{@code
 * OpenLoom loom = new OpenLoom();
 * loom.file().copyFile(Paths.get("C:/source.txt"), Paths.get("C:/target.txt"), true);
 * }</pre>
 *
 * <p><b>Key features:</b></p>
 * <ul>
 *   <li>✅ Copies files safely and efficiently</li>
 *   <li>⚡ Preserves basic file attributes such as last modified time</li>
 *   <li>🔄 Supports replaceExisting flag to overwrite files</li>
 *   <li>🧠 Automatically resolves target path conflicts</li>
 * </ul>
 */

public class CopyFile {

	
	/**
     * Copy a file from source to target.
     * <p>This method handles file validation, resolves the final target path,
     * preserves file attributes, and optionally replaces existing files.</p>
     *
     * <p><b>Important behavior:</b></p>
     * <ul>
     *   <li>If <code>replaceExisting</code> is true, the target file is overwritten.</li>
     *   <li>Throws <code>OpenLoomFileException</code> for validation or I/O errors.</li>
     * </ul>
     *
     * <p><b>Note:</b> This method is <b>protected</b> and intended to be used only
     * through OpenLoom's <code>FileManager</code> class.</p>
     *
     * @param source the source file to copy
     * @param target the target file path
     * @param replaceExisting whether to replace the target file if it exists
     * @throws OpenLoomFileException if an I/O error occurs or file validation fails
     */

	 protected void copyInternal(Path source, Path target, boolean replaceExisting) {
	        Validate.validateFile(source);
	        
	        Path finalTarget = Utils.resolveFinalTarget(source, target);
	        Validate.validateTargetFile(finalTarget, replaceExisting);

	        try {
	            if (replaceExisting) {
	                Files.copy(source, finalTarget, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
	            } else {
	                Files.copy(source, finalTarget, StandardCopyOption.COPY_ATTRIBUTES);
	            }
	        } catch (IOException e) {
	            String msg = ErrorBuilder.FileOperationError(e, source, finalTarget, replaceExisting, "COPY");
	            throw new OpenLoomFileException(msg, e);
	        }
	    }

	
}

