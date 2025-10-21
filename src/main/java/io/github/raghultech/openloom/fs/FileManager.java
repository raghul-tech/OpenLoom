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

import java.io.File;
import java.nio.file.Path;


/**
 * ================================================================
 * 📁 FileManager - Central File Operations Manager
 * ================================================================
 *
 * <p><b>Overview:</b> Provides a unified, safe, and cross-platform interface
 * for file and directory operations. Handles copying, moving, creation, and deletion
 * of files and directories. Supports both permanent and trash-based deletion.</p>
 *
 * <p><b>Key Features:</b></p>
 * <ul>
 *   <li>✅ Copy files and directories with optional overwrite</li>
 *   <li>⚡ Move files and directories safely with atomic operations</li>
 *   <li>🧩 Create files and directories, including parent directories</li>
 *   <li>🗑 Delete files and directories (supports OS trash/recycle bin fallback)</li>
 *   <li>🛡 Validates source and target paths automatically</li>
 * </ul>
 *
 * <p><b>Usage via OpenLoom:</b></p>
 * <pre>{@code
 * import io.github.raghultech.openloom.OpenLoom;
 * import java.io.File;
 *
 * public class Example {
 *     public static void main(String[] args) {
 *         OpenLoom loom = new OpenLoom();
 *         
 *         // Copy a file
 *         loom.file().copyFile(new File("source.txt"), new File("target.txt"), true);
 *
 *         // Move a directory
 *         loom.file().moveDir(new File("sourceDir"), new File("targetDir"), false);
 *
 *         // Create a new file
 *         loom.file().createFile(new File("newFile.txt"));
 *
 *         // Delete a file permanently
 *         loom.file().deleteFilePermanent(new File("oldFile.txt"));
 *     }
 * }
 * }</pre>
 *
 * <p><b>Integration Tip:</b> Use FileManager indirectly via OpenLoom instance
 * for full compatibility with OpenLoom’s configuration and charset settings.</p>
 *
 * @author
 *     Raghul John (@raghul-tech)
 * @version 1.0
 * @since 2025
 */
public class FileManager {
	
	 private final MoveFile move = new MoveFile();
	 private final MoveDirectory moveDir = new MoveDirectory();
	 private final CopyFile copy = new CopyFile();
	 private final CopyDirectory copyDir = new CopyDirectory();
	 private final CreateFileAndDir create = new CreateFileAndDir();
	 private final DeleteFileAndDir delete = new DeleteFileAndDir();

    /**
     * Copy a file to target location safely.
     */
    public void copyFile(File source, File target, boolean replaceExisting) {
        copy.copyInternal(source.toPath(), target.toPath(), replaceExisting);
    }   

    public void copyFile(Path source, Path target, boolean replaceExisting) {
        copy.copyInternal(source, target, replaceExisting);
    }
    
    /**
     * Copy dir 
     */
    public void copyDir(File source, File target, boolean replaceExisting) {
        copyDir.copyInternal(source.toPath(), target.toPath(), replaceExisting);
    }   

    public void copyDir(Path source, Path target, boolean replaceExisting) {
        copyDir.copyInternal(source, target, replaceExisting);
    }

    /**
     * Move a file to target location safely.
     */
    
    public void moveFile(File source, File target, boolean replaceExisting) {
        move.moveInternal(source.toPath(), target.toPath(), replaceExisting);
    }

    public void moveFile(Path source, Path target, boolean replaceExisting) {
        move.moveInternal(source, target, replaceExisting);
    }
    
    /**
     * Move Dir to the target location 
     */
    public void moveDir(File source, File target, boolean replaceExisting) {
        moveDir.moveInternal(source.toPath(), target.toPath(), replaceExisting);
    }

    public void moveDir(Path source, Path target, boolean replaceExisting) {
        moveDir.moveInternal(source, target, replaceExisting);
    }

    /**
     * Create an empty file safely.
     */
    public void createFile(File target) {
        create.createFileInternal(target.toPath());
    }

    public void createFile(Path target) {
        create.createFileInternal(target);
    }

    /**
     * Create directories (including parents).
     */
    public void createDirectories(File directory) {
        create.createDirectoriesInternal(directory.toPath());
    }

    public void createDirectories(Path directory) {
        create.createDirectoriesInternal(directory);
    }
    
    public void deleteFile(File file) {
        delete.deleteInternal(file.toPath());
    }

    public void deleteFile(Path path) {
        delete.deleteInternal(path);
    }
    
    public void deleteFilePermanent(File file) {
        delete.deleteInternalPermanent(file.toPath());
    }

    public void deleteFilePermanent(Path path) {
        delete.deleteInternalPermanent(path);
    }

    public void deleteDir(File dir) {
        delete.deleteDirectoryInternal(dir.toPath());
    }

    public void deleteDir(Path dir) {
        delete.deleteDirectoryInternal(dir);
    }
    
    public void deleteDirPermanent(File dir) {
        delete.deleteDirectoryPermanent(dir.toPath());
    }

    public void deleteDirPermanent(Path dir) {
        delete.deleteDirectoryPermanent(dir);
    }   

}
