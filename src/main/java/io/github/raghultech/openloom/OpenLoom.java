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

package io.github.raghultech.openloom;

import java.nio.charset.Charset;

import io.github.raghultech.openloom.config.FileIOConfig;
import io.github.raghultech.openloom.fs.FileManager;
import io.github.raghultech.openloom.reader.ReadManager;
import io.github.raghultech.openloom.search.SearchManager;
import io.github.raghultech.openloom.writer.WriteManager;

/**
 * ================================================================
 * 🌐 OpenLoom - Universal File I/O Framework
 * ================================================================
 *
 * <p><b>Overview:</b> OpenLoom is a unified, high-performance Java library
 * for reading, writing, searching, and managing files of any type.
 * It provides both synchronous and asynchronous APIs for maximum flexibility.</p>
 *
 * <p>Instead of dealing with multiple file APIs, OpenLoom simplifies everything
 * into one entry point — <code>OpenLoom</code>.</p>
 *
 * <hr>
 * <h2>✨ Quick Example</h2>
 * <pre>{@code
 * import io.github.raghultech.openloom.OpenLoom;
 * import java.io.File;
 *
 * public class Example {
 *     public static void main(String[] args) {
 *         OpenLoom loom = new OpenLoom();
 *
 *         // Read a file
 *         String data = loom.read().readBuffered(new File("data.txt"));
 *
 *         // Write data to a file
 *         loom.write().write(new File("output.txt"), "Hello, OpenLoom!");
 *
 *         // Search in a file
 *         loom.search().find(new File("data.txt"), "pattern");
 *     }
 * }
 * }</pre>
 *
 * <hr>
 * <h2>🚀 Features</h2>
 * <ul>
 *   <li>✅ Safe and efficient file operations</li>
 *   <li>⚡ Synchronous and asynchronous APIs</li>
 *   <li>🧠 Automatic charset handling</li>
 *   <li>🔍 Built-in file searching and filtering</li>
 *   <li>📦 Modular managers for Read, Write, Search, and File operations</li>
 * </ul>
 *
 * <hr>
 * <h2>🧩 Integration Tip</h2>
 * <p>You can integrate OpenLoom into any Java project:</p>
 * <ul>
 *   <li>Desktop (Swing / JavaFX)</li>
 *   <li>Server / Backend applications</li>
 *   <li>Command-line utilities</li>
 * </ul>
 *
 * <hr>
 * @author
 *     Raghul John (@raghul-tech)
 * @version 1.0
 * @since 2025
 */
public class OpenLoom {

    /** Global configuration shared across managers */
    private final FileIOConfig config = new FileIOConfig();

    /** Handles all read-related operations */
    private final ReadManager readManager;

    /** Handles all write-related operations */
    private final WriteManager writeManager;

    /** Handles all search-related operations */
    private final SearchManager searchManager;

    /** Handles basic file and directory operations */
    private final FileManager fileManager;

    // ============================================================
    // 🔹 Constructors
    // ============================================================

    /**
     * Default constructor using UTF-8 charset.
     * Ideal for most modern applications.
     */
    public OpenLoom() {
        this.readManager = new ReadManager(config);
        this.writeManager = new WriteManager(config);
        this.searchManager = new SearchManager(config);
        this.fileManager = new FileManager();
    }

    /**
     * Create an OpenLoom instance with a specific charset.
     * @param charset the character set to use (e.g., StandardCharsets.UTF_8)
     */
    public OpenLoom(Charset charset) {
        this.config.setCharset(charset);
        this.readManager = new ReadManager(config);
        this.writeManager = new WriteManager(config);
        this.searchManager = new SearchManager(config);
        this.fileManager = new FileManager();
    }

    /**
     * Create an OpenLoom instance with a charset name.
     * @param charset the name of the charset (e.g., "UTF-8", "ISO-8859-1")
     */
    public OpenLoom(String charset) {
        this.config.setCharset(charset);
        this.readManager = new ReadManager(config);
        this.writeManager = new WriteManager(config);
        this.searchManager = new SearchManager(config);
        this.fileManager = new FileManager();
    }

    // ============================================================
    // 🔹 Charset Configuration
    // ============================================================

    /**
     * Get the current charset used by OpenLoom.
     * @return the active charset
     */
    public Charset getCharset() {
        return config.getCharset();
    }

    /**
     * Set a new charset for file operations.
     * @param charset the charset to set
     */
    public void setCharset(Charset charset) {
        config.setCharset(charset);
    }

    /**
     * Get the charset name used by OpenLoom.
     * @return charset name (e.g., "UTF-8")
     */
    public String getCharsetName() {
        return config.getCharsetName();
    }

    /**
     * Set a new charset by name.
     * @param charset the charset name
     */
    public void setCharset(String charset) {
        config.setCharset(charset);
    }

    // ============================================================
    // 🔹 Accessors for Managers
    // ============================================================

    public ReadManager read() {
        return readManager;
    }

    public WriteManager write() {
        return writeManager;
    }

    public SearchManager search() {
        return searchManager;
    }

    public FileManager file() {
        return fileManager;
    }

    // ============================================================
    // 🔹 Summary
    // ============================================================
    /**
     * <p><b>Summary:</b> OpenLoom acts as a single entry point for all major file operations.</p>
     * <p>Usage example:</p>
     * <pre>{@code
     * OpenLoom loom = new OpenLoom(StandardCharsets.UTF_8);
     * String data = loom.read().readBuffered(new File("input.txt"));
     * loom.write().write(new File("output.txt"), data);
     * loom.search().find(new File("input.txt"), "pattern");
     * }</pre>
     * <p>This class is ideal for developers building editors, parsers, analyzers,
     * or automation tools that require robust file I/O.</p>
     */

}
