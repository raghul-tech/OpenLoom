<h1 align="center">🧵 OpenLoom - Modern Java File I/O Library</h1>

<p align="center">
  <em>Lightweight, dependency-free Java file I/O toolkit for reading, writing, searching, and managing files efficiently.</em>
</p>

<p align="center">
  <strong>📂 File Reading • ✍️ File Writing • 🔍 Text Search • 📁 File Management • ⚡ High Performance</strong>
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/io.github.raghul-tech/openloom">
    <img src="https://img.shields.io/maven-central/v/io.github.raghul-tech/openloom?style=for-the-badge&color=blueviolet" alt="Maven Central" />
  </a>
  <a href="https://github.com/raghul-tech/OpenLoom/actions/workflows/maven.yml">
    <img src="https://img.shields.io/github/actions/workflow/status/raghul-tech/OpenLoom/maven.yml?label=Build&style=for-the-badge&color=brightgreen" alt="Build Status" />
  </a>
  <a href="https://github.com/raghul-tech/OpenLoom/actions/workflows/codeql.yml">
    <img src="https://img.shields.io/github/actions/workflow/status/raghul-tech/OpenLoom/codeql.yml?label=CodeQL&style=for-the-badge&color=informational" alt="CodeQL Security" />
  </a>
  <a href="https://javadoc.io/doc/io.github.raghul-tech/openloom">
    <img src="https://img.shields.io/badge/Javadoc-1.0.0-blue?style=for-the-badge&logo=java" alt="Javadoc" />
  </a>
  <a href="https://github.com/raghul-tech/OpenLoom/releases">
    <img src="https://img.shields.io/github/release/raghul-tech/OpenLoom?label=Release&style=for-the-badge&color=success" alt="Latest Release" />
  </a>
 <a href="https://buymeacoffee.com/raghultech">
    <img src="https://img.shields.io/badge/Buy%20Me%20a%20Coffee-Support-orange?style=for-the-badge&logo=buy-me-a-coffee" alt="Buy Me A Coffee" />
  </a>
</p>

---

## 🎯 Why Developers Choose OpenLoom

### 🚀 **Modern API Design**
- **Single entry point** - No more searching through multiple utility classes
- **Fluent interface** - Chain operations naturally
- **Consistent naming** - Intuitive method names that make sense

### 📦 **Zero Dependency Architecture**
- **Pure Java 11+** - No external dependencies to manage
- **Lightweight footprint** - Minimal impact on your project
- **Easy updates** - No dependency conflicts or version hell

### 🔍 **Advanced Search Capabilities**
- **Regex-powered search** with range limitations
- **Line-level operations** - insert, delete, modify specific lines
- **Safe mode operations** - automatic backups before modifications

### ⚡ **Performance Optimized**
- **Memory-mapped I/O** for large file handling
- **Customizable buffers** for optimal memory usage
- **Efficient algorithms** for fast search and replace operations

### 🛡️ **Built-in Safety**
- **Recursion protection** - prevents accidental directory loops
- **Comprehensive validation** - catch errors before they happen
- **Detailed error messages** - know exactly what went wrong

---

## ⚡ Quick Start

```java
import io.github.raghultech.openloom.OpenLoom;
import io.github.raghultech.openloom.model.Match;
import java.io.File;
import java.util.List; 
import java.util.Map;   

public class QuickStart {
    public static void main(String[] args) {
        OpenLoom loom = new OpenLoom();
        
        // Read files with automatic encoding detection
        String content = loom.read().read(new File("data.txt"));
        
        // Write files with safe operations
        loom.write().write(new File("output.txt"), "Hello OpenLoom!");
        
        // Advanced text search with regex support
        List<Match> results = loom.search().findLineRegex(new File("log.txt"), "ERROR.*");
        
        // File management with recursion protection
        loom.file().copyDir(new File("source"), new File("backup"), true);
    }
}
```


---

## 🚀 Features Overview

| Category | Description |
|-----------|--------------|
| 🧠 **ReadManager** | Read text, metadata, and structured columns with custom buffer size |
| ✍️ **WriteManager** | Write and append, with small and large files safely |
| 🔍 **SearchManager** | Find, modify, replace, delete, or insert lines with regex support and safe modes |
| 📂 **FileManager** | Copy, move, create, and delete files or directories |

---

## 🎯 Solve Common Java File I/O Challenges

| Problem | Traditional Solution | OpenLoom Solution |
|---------|---------------------|-------------------|
| **Complex file search** | Manual loops + regex | `findLineRegex()` + `findLineRegexInRange()` |
| **Safe file modifications** | Manual backup creation | `replaceTextSafe()` auto-backup |
| **Multiple utility classes** | Apache Commons IO + custom code | Single `OpenLoom` entry point |
| **Dependency management** | Multiple JARs | **Zero dependencies** |
| **Large file handling** | Complex memory management | `readMemoryMapped()` + `readInChunk()` |

---

## 📦 Installation

### Maven
```xml
<dependency>
  <groupId>io.github.raghul-tech</groupId>
  <artifactId>openloom</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Gradle
```groovy
implementation 'io.github.raghul-tech:openloom:1.0.0'
```

---

## 🚀 Complete Feature Set
### 🧠 ReadManager - Comprehensive File Reading

```java
import io.github.raghultech.openloom.OpenLoom;
import java.io.File;
import java.util.List;
import java.util.Map;

public class ReadExample {
    public static void main(String[] args) {
        OpenLoom loom = new OpenLoom();
		    // Text file reading with various strategies
			String content = loom.read().read(file);                    // Auto strategy
			String buffered = loom.read().readBuffered(file, 8192);    // Custom buffer
			String fast = loom.read().readUsingChannel(file);          // NIO channels
			
			// Structured data extraction
			List<String[]> columns = loom.read().readColumns(file, ",", new int[]{0, 2});
			
			// File metadata and properties
			Map<String, Object> metadata = loom.read().readMetadata(file);
			
			// Stream processing for large files
			loom.read().readLines(file, line -> processLine(line));
    }
}

```

### ✍️ WriteManager - Reliable File Writing

```java
import io.github.raghultech.openloom.OpenLoom;
import java.io.File;

public class WriteExample {
    public static void main(String[] args) {
        OpenLoom loom = new OpenLoom();
        
        // Write new file
        loom.write().write(new File("output.txt"), "Hello OpenLoom!");
        
        // Append to existing file
        loom.write().append(new File("output.txt"), "\nAdditional content");
        
        // Write with custom bufferSize 
        loom.write().write(new File("output.txt"), "New content",64*1024);
        
        // Write large files efficiently
        loom.write().writeLarge(new File("bigfile.txt"), largeContent);
    }
}
```
### 🔍 SearchManager - Powerful Text Operations

```java
import io.github.raghultech.openloom.OpenLoom;
import io.github.raghultech.openloom.model.Match;
import java.io.File;
import java.util.List;

public class SearchExample {
    public static void main(String[] args) {
        OpenLoom loom = new OpenLoom();
        
	// Flexible search operations
	List<Match> results = loom.search().findLine(file, "searchTerm");
	List<Match> rangeResults = loom.search().findLineInRange(file, "term", 10, 50);
	
	// Regex-powered search capabilities
	List<Match> regexResults = loom.search().findLineRegex(file, "pattern.*");
	List<Match> rangeRegex = loom.search().findLineRegexInRange(file, "pattern", 0, 100);
	
	// Content modification with precision
	loom.search().replaceText(file, "old", "new");
	loom.search().deleteLine(file, 5);
	loom.search().insertLine(file, 3, "new content");
	
	// Safe operations with automatic backups
	loom.search().replaceTextSafe(file, "old", "new");
    }
}
```

### 📂 FileManager - Robust File System Operations

```java
import io.github.raghultech.openloom.OpenLoom;
import java.io.File;

public class FileOpsExample {
    public static void main(String[] args) {
        OpenLoom loom = new OpenLoom();
        
        // Copy files
        loom.file().copyFile(new File("input.txt"), new File("backup/input_copy.txt"));
        
        // Move files
        loom.file().moveFile(new File("backup/input_copy.txt"), new File("moved/input.txt"));
        
        // Copy entire directories
        loom.file().copyDir(new File("project"), new File("backup/project"), true);
        
        // Move directories
        loom.file().moveDir(new File("old_location"), new File("new_location"), true);
        
        // Delete files and directories
        loom.file().deleteFile(new File("temp.txt"));
        loom.file().deleteDir(new File("old_backup"));
    }
}

```

## 💡 Ideal For These Use Cases
### 🔧 Configuration Management Systems

```java
// Safely update application configurations
OpenLoom loom = new OpenLoom();
loom.search().replaceTextSafe(
    new File("application.properties"), 
    "database.url=localhost", 
    "database.url=production-db"
);
```

### 📊 Log Processing & Analysis

```java
// Extract insights from application logs
List<Match> errors = loom.search().findLineRegex(
    new File("application.log"), 
    "ERROR.*Exception"
);

// Process structured log files
List<String[]> logEntries = loom.read().readColumns(
    new File("access.log"), 
    " ", 
    new int[]{0, 3, 6} // timestamp, IP, status code
);
```

## 🗂️ Data Migration & ETL Process

```java
// Bulk file operations for data pipelines
loom.file().copyDir(
    new File("/data/legacy-system"), 
    new File("/data/modern-system"), 
    true
);

// Process data files during migration
List<String[]> customerData = loom.read().readColumns(
    new File("customers.csv"), ",", 
    new int[]{0, 1, 2} // ID, Name, Email
);
```

## 📝 Document Processing Workflows

```java
// Large-scale text processing
loom.read().readLines(new File("large-document.txt"), line -> {
   if (!loom.search().findLineRegex(line, "confidential|secret").isEmpty()) {
        processSensitiveLine(line);
    }
});
```

---

## 💡 Run Using JAR
### **🧵 Compile:**

```bash
javac -cp openloom-1.0.0.jar ExampleRead.java
```
### **▶️ Run:**

> Windows:
```bash
java -cp .;openloom-1.0.0.jar ExampleRead   
```

> Linux/macOS:
```bash
java -cp .:openloom-1.0.0.jar ExampleRead   
```
---

## 📂 Example Files
### ✅ Ready-to-run examples in the [examples/](examples/) folder:

- [ExampleRead.java](examples/ExampleRead.java) - Comprehensive reading examples

- [ExampleWrite.java](examples/ExampleWrite.java) - Writing and appending patterns

- [ExampleSearch.java](examples/ExampleSearch.java) - Search and modification techniques

- [ExampleFile.java](examples/ExampleFile.java) - File and directory operations

---

## 🧩 Requirements
- Java 11 or higher

- Works on all major operating systems

- No third-party dependencies

---


## 🆕 Changelog:

* View all releases on the [Releases Page.](https://github.com/raghul-tech/OpenLoom/releases)

* For a detailed log of all changes, refer to the [CHANGELOG.md](CHANGELOG.md) file.

---

## 🤝 Contributing
* We welcome PRs for:

	* 🐛 Bug fixes

	* 🚀 New features

	* 🧪 More examples

	* 📝 Documentation

> Read the [Contributing Guide](CONTRIBUTING.md) before starting..

---

## 🐞 Report a Bug
   * If you've encountered a bug, please report it by clicking the link below. 
   	This will guide you through the bug-reporting process:
   	➡️ [Click here to report a bug](https://github.com/raghul-tech/OpenLoom/issues)
 
---

## 📄 License
- This project is licensed under the [ Apache License 2.0](LICENSE).

---

## 📬 Contact
Email: [raghultech.app@gmail.com](mailto:raghultech.app@gmail.com)

---

## ☕ Support
> If OpenLoom helped you, you can support it here ❤️

<a href="https://buymeacoffee.com/raghultech"> <img src="https://img.shields.io/badge/Buy%20Me%20A%20Coffee-Support-orange.svg?style=flat-square" alt="Buy Me A Coffee" /> </a> 

---

<p align="center"> <strong>Built with ❤️ for the Java Community</strong><br> <em>Making file I/O operations simple and efficient</em> </p>
