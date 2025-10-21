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



import io.github.raghultech.openloom.OpenLoom;
import io.github.raghultech.openloom.model.Match;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * ExampleSearch demonstrates how to use OpenLoom's SearchManager
 * to perform various file search, replace, and modify operations.
 *
 * This example covers:
 * 1. Basic line search
 * 2. Regex search
 * 3. Search within a line range
 * 4. Replacing, inserting, modifying, and deleting lines
 * 5. Optional asynchronous usage
 *
 * Notes:
 * - This is a synchronous-first example. Async usage is optional.
 * - All file paths used here must exist beforehand.
 */
public class ExampleSearch {

    public static void main(String[] args) {
        // 1️⃣ Initialize OpenLoom with UTF-8 charset
        OpenLoom loom = new OpenLoom(StandardCharsets.UTF_8);

        // 2️⃣ Obtain the SearchManager from OpenLoom
        //    This is the central manager for all search-related operations.
        var search = loom.search();

        // Example file to operate on
        File file = new File("sample.txt");
        Path path = file.toPath();

        // ================================
        // 🔹 Basic Line Search
        // ================================
        List<Match> matches = search.findLine(file, "async");
        System.out.println("Found " + matches.size() + " matches for 'keyword' in example.txt");
        matches.forEach(m -> System.out.println("Line " + m.getLineNumber() + ": " + m.getLineContent()));

        // Case-sensitive search
        List<Match> caseSensitiveMatches = search.findLine(path, "error", true);
        System.out.println("Matches between line 10 and 50: " + caseSensitiveMatches.toString());
        // ================================
        // 🔹 Search within a Line Range
        // ================================
        List<Match> rangeMatches = search.findLineInRange(file, "error", 10, 50);
        System.out.println("Matches between line 10 and 50: " + rangeMatches.size());

        // ================================
        // 🔹 Regex Search
        // ================================
        List<Match> regexMatches = search.findLineRegex(file, "\\d{4}-\\d{2}-\\d{2}", true); // e.g., YYYY-MM-DD
        System.out.println("Found " + regexMatches.size() + " date patterns");

        // Regex search within line range
        List<Match> regexRange = search.findLineRegexInRange(file, "\\w+@\\w+\\.com", 5, 30, false);
        System.out.println("Emails between lines 5 and 30: " + regexRange.size());

        // ================================
        // 🔹 Replace / Insert / Modify / Delete
        // ================================

        // Replace content of line 3
        search.replaceLine(file, 3, "This is the new content for line 3");

        // Insert a new line at line 2
        search.insertLine(file, 2, "Inserted line at position 2");

        // Modify line content using a lambda function
        search.modifyLine(file, 5, line -> line.toUpperCase()); // Converts line 5 to uppercase

        // Delete line 7
        search.deleteLine(file, 7);

        // Replace text in entire file
        search.replaceText(file,4, "oldText", "newText");

        // ================================
        // 🔹 Optional Asynchronous Usage
        // ================================
        // You can use CompletableFuture or any executor to perform searches async
        CompletableFuture.runAsync(() -> {
            List<Match> asyncMatches = search.findLine(file, "asyncKeyword");
            System.out.println("Async search found " + asyncMatches.size() + " matches");
        });

        // ================================
        // 🔹 Notes
        // ================================
        // - All operations above are thread-safe when using different files.
        // - Use the Safe versions (e.g., replaceLineSafe) to avoid file corruption in critical operations.
        // - You can mix File and Path inputs interchangeably.
    }
}
