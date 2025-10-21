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
/**
 * Demonstrates file system operations using OpenLoom's FileManager.
 * 
 * <p>This example shows how to:
 * <ul>
 *   <li>Create files and directories</li>
 *   <li>Copy and move files/directories</li>
 *   <li>Delete files and directories</li>
 *   <li>Use permanent deletion for secure removal</li>
 * </ul>
 * 
 * <p><b>Key Features:</b>
 * <ul>
 *   <li>Automatic parent directory creation</li>
 *   <li>Recursion protection in directory operations</li>
 *   <li>Cross-platform path handling</li>
 *   <li>Safe operations with validation</li>
 * </ul>
 * 
 * @see FileManager
 * @see OpenLoom
 */
public class ExampleFile {
    
    /**
     * Executes a complete file system operations workflow.
     * 
     * <p>Creates sample files and directories, then performs copy, move,
     * and delete operations to demonstrate FileManager capabilities.</p>
     * 
     * <p><b>Operation Flow:</b>
     * <ol>
     *   <li>Create files and directories</li>
     *   <li>Copy files and directories</li>
     *   <li>Move files and directories</li>
     *   <li>Delete files and directories</li>
     * </ol>
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Initialize OpenLoom and FileManager
        OpenLoom loom = new OpenLoom();
        FileManager fileManager = loom.file();
        File dir1 = new File("DIR1");
        File dir = new File("DIR");

        // Create a new file
        File newFile = new File(dir + "\\exampleFile.txt");
        fileManager.createFile(newFile);
        System.out.println("File created: " + newFile.getAbsolutePath());

        // Create directories (including parent dirs)
        File newDir = new File(dir1 + "//exampleDir");
        fileManager.createDirectories(newDir);
        System.out.println("Directories created: " + newDir.getAbsolutePath());

        // Copy file to new location (replace if exists)
        File copyFile = new File(dir + "//exampleFile_copy.txt");
        fileManager.copyFile(newFile, copyFile, true);
        System.out.println("File copied to: " + copyFile.getAbsolutePath());

        // Copy entire directory
        File copyDir = new File(dir1 + "//exampleDir_copy");
        fileManager.copyDir(dir1, copyDir, true);
        System.out.println("Directory copied to: " + copyDir.getAbsolutePath());

        // Move file to new location
        File movedFile = new File(dir1 + "//moved_exampleFile.txt");
        fileManager.moveFile(newFile, movedFile, true);
        System.out.println("File moved to: " + movedFile.getAbsolutePath());

        // Move directory to new location
        File movedDir = new File(dir + "//moved_exampleDir");
        fileManager.moveDir(copyDir, movedDir, true);
        System.out.println("Directory moved to: " + movedDir.getAbsolutePath());

        // Delete operations
        fileManager.deleteFile(copyFile);
        System.out.println("File deleted: " + copyFile.getAbsolutePath());

        fileManager.deleteDir(movedDir);
        System.out.println("Directory deleted: " + copyDir.getAbsolutePath());

        // Permanent deletion
        fileManager.deleteFilePermanent(copyFile);
        fileManager.deleteDirPermanent(movedDir);
    }
}
