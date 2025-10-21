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
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import io.github.raghultech.openloom.config.FileIOConfig;

/**
 * ================================================================
 * 📖 ReadManager — Unified File Reading Controller for OpenLoom
 * ================================================================
 *
 * <p><b>Overview:</b> The {@code ReadManager} class provides a central access
 * point for all file reading operations within the OpenLoom framework.
 * It intelligently selects the most efficient reading strategy based on
 * file size and system memory conditions. Developers can also directly
 * invoke specialized readers such as buffered, chunked, channel-based,
 * or memory-mapped methods for manual performance tuning.</p>
 *
 * <p><b>Highlights:</b></p>
 * <ul>
 *   <li>🧠 <b>Smart Reading:</b> Auto-selects the optimal I/O mode (small, medium, large, or huge files)</li>
 *   <li>⚙️ <b>Manual Control:</b> Explicit methods for buffered, chunked, channel, and memory-mapped reads</li>
 *   <li>📄 <b>Line Streaming:</b> Stream file lines with filters, ranges, and consumer-based callbacks</li>
 *   <li>📊 <b>Structured Data:</b> Read tabular/CSV-like files as columns</li>
 *   <li>🗂 <b>Metadata Access:</b> Retrieve basic file metadata in a key–value map</li>
 *   <li>🌍 <b>Charset Management:</b> Fully customizable encoding support per file or per instance</li>
 * </ul>
 *
 * <hr>
 * <p><b>Usage via OpenLoom:</b></p>
 * <pre>{@code
 * import io.github.raghultech.openloom.OpenLoom;
 * import java.io.File;
 * import java.util.Map;
 *
 * public class Example {
 *     public static void main(String[] args) {
 *         OpenLoom loom = new OpenLoom();
 *
 *         // Basic usage — automatically selects best reading mode
 *         String text = loom.read().read(new File("notes.txt"));
 *
 *         // Buffered reading with custom buffer size (in bytes)
 *         String fastRead = loom.read().read(new File("log.txt"), 64 * 1024);
 *
 *         // Stream lines with a filter
 *         loom.read().readLinesFilter(
 *             new File("data.csv"),
 *             line -> line.contains("SUCCESS"),
 *             System.out::println
 *         );
 *
 *         // Read columns (e.g., CSV parsing)
 *         List<String[]> rows = loom.read().readColumns(new File("users.csv"), ",", new int[]{0, 2});
 *
 *         // Retrieve metadata
 *         Map<String, Object> meta = loom.read().readMetadata(new File("report.txt"));
 *     }
 * }
 * }</pre>
 *
 * <hr>
 * <p><b>Integration Tip:</b> Always access this manager through
 * {@link io.github.raghultech.openloom.OpenLoom#read()} to inherit
 * global charset, config, and logging settings. Direct usage is possible
 * but bypasses centralized configuration.</p>
 *
 * @see io.github.raghultech.openloom.OpenLoom
 * @see io.github.raghultech.openloom.reader.ReadFile
 * @see io.github.raghultech.openloom.reader.ReadStreamLines
 * @see io.github.raghultech.openloom.reader.ReadsColumns
 * @see io.github.raghultech.openloom.reader.ReadMetaData
 *
 * @author
 *     Raghul John (@raghul-tech)
 * @version
 *     1.0
 * @since
 *     2025
 */

public class ReadManager {

    // Underlying reader classes (modular architecture)
    private final ReadFile fileReader = new ReadFile();
    private final ReadStreamLines lineReader = new ReadStreamLines();
    private final ReadsColumns columnReader = new ReadsColumns();
    private final ReadMetaData metaReader = new ReadMetaData();

    // File size thresholds (in MB)
    private static final double SMALL_FILE_MB = 20.0;
    private static final double MEDIUM_FILE_MB = 60.0;
    private static final double LARGE_FILE_MB = 90.0;

    private final FileIOConfig config;

    // Constructors -----------------------------------------------------------
    public ReadManager() {
        this.config = new FileIOConfig();
    }

    public ReadManager(FileIOConfig config) {
        this.config = (config != null) ? config : new FileIOConfig();
    }

    public ReadManager(Charset charset) {
        this.config = new FileIOConfig();
        this.config.setCharset(charset);
    }

    public ReadManager(String charset) {
        this.config = new FileIOConfig();
        this.config.setCharset(charset);
    }

    // Charset management -----------------------------------------------------
    public Charset getCharset() {
        return config.getCharset();
    }

    public void setCharset(Charset charset) {
        config.setCharset(charset);
    }

    public String getCharsetName() {
        return config.getCharsetName();
    }

    public void setCharset(String charset) {
        config.setCharset(charset);
    }
    
    private String readInternal(File file) {
    	  double sizeMB = file.length() / (1024.0 * 1024.0);
          Charset charset = config.getCharset();

          if (sizeMB < SMALL_FILE_MB)
              return fileReader.loadSmallFile(file, charset);
          else if (sizeMB < MEDIUM_FILE_MB)
              return fileReader.loadMediumFile(file, charset);
          else if (sizeMB < LARGE_FILE_MB)
              return fileReader.loadLargeFile(file, charset);
          else
              return fileReader.loadBigFile(file, charset);
    }
 private String readInternal(File file,int bufferSize) {
	  double sizeMB = file.length() / (1024.0 * 1024.0);
      Charset charset = config.getCharset();

      if (sizeMB < SMALL_FILE_MB)
          return fileReader.loadSmallFile(file, charset,bufferSize);
      else if (sizeMB < MEDIUM_FILE_MB)
          return fileReader.loadMediumFile(file, charset,bufferSize);
      else if (sizeMB < LARGE_FILE_MB)
          return fileReader.loadLargeFile(file, charset,bufferSize);
      else
          return fileReader.loadBigFile(file, charset,bufferSize);
    }
    

    // ------------------------------------------------------------------------
    // 🔹 Smart Reading (auto-select based on file size)
    // ------------------------------------------------------------------------
    public String read(File file)  {
      return readInternal(file);
    }

    public String read(Path path) {
        return readInternal(path.toFile());
    }
    // ------------------------------------------------------------------------
    // 🔹 Smart Reading (Manual bufferSize)
    // ------------------------------------------------------------------------
    public String read(File file, int bufferSize)  {
      return readInternal(file,bufferSize);
    }

    public String read(Path path,int bufferSize) {
        return readInternal(path.toFile(),bufferSize);
    }

    // ------------------------------------------------------------------------
    // 🔹 Manual Control – Buffered Reading
    // ------------------------------------------------------------------------
    public String readBuffered(File file) {
        return fileReader.loadSmallFile(file, config.getCharset());
    }
    public String readBuffered(Path path) {
    	return fileReader.loadLargeFile(path.toFile(), config.getCharset());
    }
    public String readBuffered(File file, int bufferSize) {
        return fileReader.loadLargeFile(file, config.getCharset(), bufferSize);
    }
    public String readBuffered(Path path, int bufferSize) {
        return fileReader.loadLargeFile(path.toFile(), config.getCharset(), bufferSize);
    }

    // ------------------------------------------------------------------------
    // 🔹 Manual Control – Channel-Based Reading
    // ------------------------------------------------------------------------
    public String readUsingChannel(File file) {
        return fileReader.loadMediumFile(file, config.getCharset());
    }
    public String readUsingChannel(Path path) {
        return fileReader.loadMediumFile(path.toFile(), config.getCharset());
    }
    public String readUsingChannel(File file,int bufferSize) {
        return fileReader.loadMediumFile(file, config.getCharset(),bufferSize);
    }
    public String readUsingChannel(Path path,int bufferSize) {
    	 return fileReader.loadMediumFile(path.toFile(), config.getCharset(),bufferSize);
    }

    // ------------------------------------------------------------------------
    // 🔹 Manual Control – Chunk Reading
    // ------------------------------------------------------------------------
    public String readInChunk(File file) {
        return fileReader.loadLargeFile(file, config.getCharset());
    }
    public String readInChunk(Path path) {
    	 return fileReader.loadLargeFile(path.toFile(), config.getCharset());
    }
    public String readInChunk(File file, int chunkKB) {
        return fileReader.loadLargeFile(file, config.getCharset(), chunkKB);
    }
    public String readInChunk(Path path, int chunkKB) {
    	 return fileReader.loadLargeFile(path.toFile(), config.getCharset(), chunkKB);
    }

    // ------------------------------------------------------------------------
    // 🔹 Manual Control – Memory-Mapped Reading
    // ------------------------------------------------------------------------
    public String readMemoryMapped(File file) {
        return fileReader.loadBigFile(file, config.getCharset());
    }
    public String readMemoryMapped(Path path) {
    	 return fileReader.loadBigFile(path.toFile(), config.getCharset());
    }
    public String readMemoryMapped(File file,int bufferSize) {
        return fileReader.loadBigFile(file, config.getCharset(),bufferSize);
    }
    public String readMemoryMapped(Path path,int bufferSize) {
        return fileReader.loadBigFile(path.toFile(), config.getCharset(),bufferSize);
    }

    // ------------------------------------------------------------------------
    // 🔹 Line Streaming APIs
    // ------------------------------------------------------------------------
    public void readLines(File file, Consumer<String> lineConsumer) {
        lineReader.streamLines(file, lineConsumer, config.getCharset());
    }

    public void readLines(Path path, Consumer<String> lineConsumer) {
    	 lineReader.streamLines(path.toFile(), lineConsumer, config.getCharset());
    }

    public void readLinesFilter(File file, Predicate<String> filter, Consumer<String> consumer) {
        lineReader.streamFilteredLines(file, filter, consumer, config.getCharset());
    }

    public void readLinesFilter(Path path, Predicate<String> filter, Consumer<String> consumer) {
    	 lineReader.streamFilteredLines(path.toFile(), filter, consumer, config.getCharset());
    }

    public void readLinesRange(File file, int startLine, int endLine, Consumer<String> consumer) {
        lineReader.streamLinesRange(file, config.getCharset(), startLine, endLine, consumer);
    }

    public void readLinesRange(Path path, int startLine, int endLine, Consumer<String> consumer) {
    	 lineReader.streamLinesRange(path.toFile(), config.getCharset(), startLine, endLine, consumer);
    }

    // ------------------------------------------------------------------------
    // 🔹 Structured Data & Metadata APIs
    // ------------------------------------------------------------------------
    public List<String[]> readColumns(File file, String delimiter, int[] columns) {
        return columnReader.readColumns(file, delimiter, columns,config.getCharset());
    }

    public List<String[]> readColumns(Path path, String delimiter, int[] columns) {
    	 return columnReader.readColumns(path.toFile(), delimiter, columns,config.getCharset());
    }
    public List<String[]> readColumns(File file, String delimiter) {
        return columnReader.readColumns(file, delimiter,config.getCharset());
    }

    public List<String[]> readColumns(Path path, String delimiter) {
    	 return columnReader.readColumns(path.toFile(), delimiter,config.getCharset());
    }

    public Map<String, Object> readMetadata(File file) {
        return metaReader.readMetadata(file);
    }

    public Map<String, Object> readMetadata(Path path) {
    	 return metaReader.readMetadata(path.toFile());
    }
}

