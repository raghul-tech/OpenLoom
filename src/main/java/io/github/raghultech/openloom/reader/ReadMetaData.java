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


package io.github.raghultech.openloom.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.LinkedHashMap;
import java.util.Map;

import io.github.raghultech.openloom.exception.OpenLoomFileException;

/**
 * ================================================================
 * 🧾 ReadMetaData — File Information Inspector for OpenLoom
 * ================================================================
 *
 * <p>The {@code ReadMetaData} class provides a unified, cross-platform way
 * to extract essential and extended metadata from a given file or directory.
 * It acts as a backend component used by higher-level APIs in OpenLoom such
 * as {@code FileInfoManager} or {@code OpenLoom.meta()}.</p>
 *
 * <hr>
 * <p><b>Extracted Attributes:</b></p>
 * <ul>
 *   <li><b>name</b> — File name</li>
 *   <li><b>size</b> — File size in bytes</li>
 *   <li><b>isDirectory</b> — Whether the path represents a directory</li>
 *   <li><b>lastModified</b> — Last modified timestamp</li>
 *   <li><b>created</b> — File creation timestamp</li>
 *   <li><b>readable</b> / <b>writable</b> / <b>hidden</b> — File visibility and access flags</li>
 *   <li><b>owner</b> / <b>group</b> / <b>permissions</b> — POSIX-level attributes (if supported)</li>
 * </ul>
 *
 * <hr>
 * <p><b>Platform Compatibility:</b></p>
 * <ul>
 *   <li>On <b>Windows</b> — Skips POSIX details gracefully.</li>
 *   <li>On <b>Linux/macOS</b> — Includes full owner, group, and permission info.</li>
 * </ul>
 *
 * <hr>
 * <p><b>Example Usage:</b></p>
 * <pre>{@code
 * import io.github.raghultech.openloom.OpenLoom;
 * import java.io.File;
 * import java.util.Map;
 *
 * public class Example {
 *     public static void main(String[] args) {
 *         OpenLoom loom = new OpenLoom();
 *         Map<String, Object> info = loom.meta().inspect(new File("report.pdf"));
 *         info.forEach((k, v) -> System.out.println(k + ": " + v));
 *     }
 * }
 * }</pre>
 *
 * @see java.nio.file.attribute.BasicFileAttributes
 * @see java.nio.file.attribute.PosixFileAttributes
 * @see io.github.raghultech.openloom.exception.OpenLoomFileException
 *
 * @since 2025
 * @author
 *     Raghul John (@raghul-tech)
 */
public class ReadMetaData {

    /**
     * Retrieves detailed metadata of the given file or directory.
     *
     * <p>This method reads both basic and (if available) POSIX attributes,
     * returning a map of descriptive keys and their corresponding values.
     * If POSIX attributes are not supported (e.g., on Windows), the method
     * silently skips that part without throwing an error.</p>
     *
     * @param file the target file or directory (must exist)
     * @return a {@link LinkedHashMap} containing ordered metadata attributes
     * @throws OpenLoomFileException if the file does not exist or metadata cannot be read
     */
	protected Map<String, Object> readMetadata(File file) {
		if (file == null)
            throw new OpenLoomFileException("File cannot be null");
        if (!file.exists())
            throw new OpenLoomFileException("File not found: " + file.getAbsolutePath());

	    Map<String, Object> meta = new LinkedHashMap<>();
	    Path path = file.toPath();

	    try {
	        BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
	        meta.put("name", file.getName());
	        meta.put("size", attrs.size());
	        meta.put("isDirectory", attrs.isDirectory());
	        meta.put("lastModified", attrs.lastModifiedTime());
	        meta.put("created", attrs.creationTime());
	        meta.put("readable", Files.isReadable(path));
	        meta.put("writable", Files.isWritable(path));
	        meta.put("hidden", Files.isHidden(path));
	        
	        // Optional: Add POSIX details if available
            try {
                PosixFileAttributes posixAttrs = Files.readAttributes(path, PosixFileAttributes.class);
                meta.put("owner", posixAttrs.owner().getName());
                meta.put("group", posixAttrs.group().getName());
                meta.put("permissions", PosixFilePermissions.toString(posixAttrs.permissions()));
            } catch (UnsupportedOperationException ignored) {
                // Non-POSIX systems (like Windows)
            }
	        
	    } catch (IOException e) {
	        throw new OpenLoomFileException("Error reading metadata for: " + file.getName(), e);
	    }

	    return meta;
	}

	
}

