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
import io.github.raghultech.openloom.reader.ReadManager;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * ================================================================
 * 📘 ExampleRead — Demonstration of ReadManager Capabilities
 * ================================================================
 *
 * <p>This example shows how to use all major reading APIs provided
 * by the {@link ReadManager} class in the OpenLoom library. It covers:</p>
 *
 * <ul>
 *   <li>✅ Smart reading (auto-selects best I/O mode)</li>
 *   <li>✅ Manual control (buffered, channel, chunk, and memory-mapped)</li>
 *   <li>✅ Line streaming with filters and ranges</li>
 *   <li>✅ Structured data reading (columns/CSV)</li>
 *   <li>✅ File metadata retrieval</li>
 *   <li>✅ Charset customization</li>
 * </ul>
 *
 * <p><b>Usage:</b> Run this example from your IDE or terminal to observe
 * different reading modes in action.</p>
 *
 * <hr>
 *
 * <p><b>Note:</b> Replace {@code sample.txt} and {@code data.csv} with valid
 * paths on your system before running.</p>
 *
 * @author
 *     Raghul John (@raghul-tech)
 * @version
 *     1.0
 * @since
 *     2025
 */
public class ExampleRead {

    public static void main(String[] args) {

        // Initialize OpenLoom main entry point
        OpenLoom loom = new OpenLoom();

        // Get the read manager
        ReadManager reader = loom.read();

        File textFile = new File("sample.txt");
        Path csvPath = Path.of("data.csv");

        // ------------------------------------------------------------------
        // 1️⃣ Smart Reading (auto-select mode based on file size)
        // ------------------------------------------------------------------
        String contentAuto = reader.read(textFile);
        System.out.println("🔹 Smart Read Output:\n" + contentAuto);

        // With manual buffer size (in bytes)
        String contentBufferedAuto = reader.read(textFile, 64 * 1024);
        System.out.println("\n🔹 Smart Read (Buffered 64KB):\n" + contentBufferedAuto);

        // ------------------------------------------------------------------
        // 2️⃣ Manual Control – Buffered Reading
        // ------------------------------------------------------------------
        String smallBuffered = reader.readBuffered(textFile);
        System.out.println("\n🔹 Buffered Read:\n" + smallBuffered);

        // With custom buffer size
        String smallBufferedCustom = reader.readBuffered(textFile, 128 * 1024);
        System.out.println("\n🔹 Buffered Read (128KB buffer):\n" + smallBufferedCustom);

        // ------------------------------------------------------------------
        // 3️⃣ Manual Control – Channel-Based Reading
        // ------------------------------------------------------------------
        String channelRead = reader.readUsingChannel(textFile);
        System.out.println("\n🔹 Channel-Based Read:\n" + channelRead);

        // With buffer size
        String channelReadCustom = reader.readUsingChannel(textFile, 32 * 1024);
        System.out.println("\n🔹 Channel-Based Read (32KB buffer):\n" + channelReadCustom);

        // ------------------------------------------------------------------
        // 4️⃣ Manual Control – Chunk Reading
        // ------------------------------------------------------------------
        String chunkRead = reader.readInChunk(textFile);
        System.out.println("\n🔹 Chunk Read:\n" + chunkRead);

        String chunkReadCustom = reader.readInChunk(textFile, 256 * 1024);
        System.out.println("\n🔹 Chunk Read (256KB chunks):\n" + chunkReadCustom);

        // ------------------------------------------------------------------
        // 5️⃣ Manual Control – Memory-Mapped Reading
        // ------------------------------------------------------------------
        String memoryMapped = reader.readMemoryMapped(textFile);
        System.out.println("\n🔹 Memory-Mapped Read:\n" + memoryMapped);

        String memoryMappedCustom = reader.readMemoryMapped(textFile, 512 * 1024);
        System.out.println("\n🔹 Memory-Mapped Read (512KB):\n" + memoryMappedCustom);

        // ------------------------------------------------------------------
        // 6️⃣ Line Streaming
        // ------------------------------------------------------------------
        System.out.println("\n🔹 Streaming lines:");
        Consumer<String> printer = System.out::println;
        reader.readLines(textFile, printer);

        // Filtered line streaming
        Predicate<String> containsError = line -> line.contains("ERROR");
        System.out.println("\n🔹 Filtered lines (containing 'ERROR'):");
        reader.readLinesFilter(textFile, containsError, System.out::println);

        // Range-based line streaming
        System.out.println("\n🔹 Lines 5–10:");
        reader.readLinesRange(textFile, 5, 10, System.out::println);

        // ------------------------------------------------------------------
        // 7️⃣ Structured Data Reading (CSV)
        // ------------------------------------------------------------------
        List<String[]> allColumns = reader.readColumns(csvPath, ",");
        System.out.println("\n🔹 CSV Columns (all):");
        for (String[] row : allColumns) {
            System.out.println(String.join(" | ", row));
        }

        List<String[]> selectedColumns = reader.readColumns(csvPath, ",", new int[]{0, 2});
        System.out.println("\n🔹 CSV Columns (only 0 and 2):");
        for (String[] row : selectedColumns) {
            System.out.println(String.join(" | ", row));
        }

        // ------------------------------------------------------------------
        // 8️⃣ File Metadata
        // ------------------------------------------------------------------
        Map<String, Object> metadata = reader.readMetadata(textFile);
        System.out.println("\n🔹 File Metadata:");
        metadata.forEach((k, v) -> System.out.println(k + " : " + v));

        // ------------------------------------------------------------------
        // 9️⃣ Charset Configuration
        // ------------------------------------------------------------------
        reader.setCharset(StandardCharsets.UTF_8);
        System.out.println("\n🔹 Current Charset: " + reader.getCharsetName());

        // ------------------------------------------------------------------
        // ✅ Done
        // ------------------------------------------------------------------
        System.out.println("\n✅ Example completed successfully!");
    }
}

