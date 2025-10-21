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
import io.github.raghultech.openloom.writer.WriteManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * ================================================================
 * 📝 ExampleWrite — Demonstration of WriteManager Capabilities
 * ================================================================
 *
 * <p>This class demonstrates all major methods of {@link WriteManager}
 * in OpenLoom for writing, appending, and monitoring file output.</p>
 *
 * <ul>
 *   <li>✅ Simple writing</li>
 *   <li>✅ Buffered & large writes</li>
 *   <li>✅ Append operations</li>
 *   <li>✅ Reader-based writes</li>
 *   <li>✅ Progress monitoring</li>
 *   <li>✅ Charset management</li>
 *   <li>✅ Async usage (CompletableFuture)</li>
 * </ul>
 *
 * <p><b>Usage:</b> Run this example from your IDE or terminal to test writing.</p>
 *
 * @author
 *     Raghul John (@raghul-tech)
 * @version
 *     1.0
 * @since
 *     2025
 */
public class ExampleWrite {

    public static void main(String[] args) {

        // Initialize OpenLoom
        OpenLoom loom = new OpenLoom();

        // Get WriteManager
        WriteManager writer = loom.write();

        File outputFile = new File("example_output.txt");
        Path outputPath = Path.of("example_path.txt");

        // ------------------------------------------------------------------
        // 1️⃣ Simple Write
        // ------------------------------------------------------------------
        String text = "Hello, OpenLoom!\nThis is a simple write test.";
        writer.write(outputFile, text);
        System.out.println("✅ Simple write completed");

        // ------------------------------------------------------------------
        // 2️⃣ Write with Custom Buffer Size
        // ------------------------------------------------------------------
        String bufferedText = "This is a buffered write example.";
        writer.write(outputFile, bufferedText, 16_384);
        System.out.println("✅ Buffered write (16KB) completed");

        // ------------------------------------------------------------------
        // 3️⃣ Write Using Progress Consumer
        // ------------------------------------------------------------------
        Consumer<Double> progress = p -> System.out.printf("Progress: %.2f%%%n", p * 100);
        String largeText = "This is a large text content ".repeat(50000);
        writer.write(outputPath, largeText, progress, 32_768);
        System.out.println("✅ Large write with progress completed");

        // ------------------------------------------------------------------
        // 4️⃣ Append to Existing File
        // ------------------------------------------------------------------
        writer.append(outputFile, "\nAppended content line 1.");
        writer.append(outputFile, "\nAppended content line 2.");
        System.out.println("✅ Append completed");

        // ------------------------------------------------------------------
        // 5️⃣ Append with Progress
        // ------------------------------------------------------------------
        String appendLarge = "Append large data ".repeat(40000);
        writer.append(outputPath, appendLarge, progress, 32_768);
        System.out.println("✅ Append with progress completed");

        // ------------------------------------------------------------------
        // 6️⃣ Reader-Based Write (from StringReader)
        // ------------------------------------------------------------------
        try (Reader reader = new StringReader("Reader-based write example.\nLine 2 from Reader.")) {
            writer.write(new File("reader_output.txt"), reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("✅ Reader-based write completed");

        // ------------------------------------------------------------------
        // 7️⃣ Append from Reader
        // ------------------------------------------------------------------
        try (Reader reader = new StringReader("\nAppended using Reader!")) {
            writer.append(Path.of("reader_output.txt"), reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("✅ Reader append completed");

        // ------------------------------------------------------------------
        // 8️⃣ Charset Customization
        // ------------------------------------------------------------------
        writer.setCharset(StandardCharsets.UTF_8);
        System.out.println("✅ Current Charset: " + writer.getCharsetName());

        // ------------------------------------------------------------------
        // 9️⃣ Direct Small/Large Control
        // ------------------------------------------------------------------
        String smallData = "Direct small write test.";
        writer.writeSmall(outputFile, smallData, 8_192);
        System.out.println("✅ Direct small write completed");

        String largeData = "Direct large write ".repeat(10000);
        writer.writeLarge(outputFile, largeData, 32_768);
        System.out.println("✅ Direct large write completed");

        // ------------------------------------------------------------------
        // 🔟 Asynchronous Example (CompletableFuture)
        // ------------------------------------------------------------------
        CompletableFuture.runAsync(() -> {
            writer.write(Path.of("async_write.txt"), "Async write using CompletableFuture!");
        }).thenRun(() -> System.out.println("✅ Async write complete"));

        // ------------------------------------------------------------------
        // ✅ Done
        // ------------------------------------------------------------------
        System.out.println("\n🎉 All write operations demonstrated successfully!");
    }
}
