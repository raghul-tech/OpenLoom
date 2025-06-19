# 📦 Changelog

All notable changes to **OpenLoom** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)  
and this project adheres to [Semantic Versioning](https://semver.org/).

---

## [0.0.2] — 2025-06-19

### 🛠️ Improvements

- ✅ Added `<packaging>jar</packaging>` to `pom.xml` for proper artifact packaging
- 📦 OpenLoom is now downloaded as a `.jar` dependency instead of a folder
- 🧹 Minor internal cleanup for smoother Maven Central consumption

🔧 *No functional changes or new features introduced in this release.*

---


## [0.0.1] - 2025-06-17

### 🚀 Initial Release

- Introduced the `OpenLoom` utility class for efficient file reading in Java.
- Adaptive reading strategy based on file size:
  - **< 20 MB** → BufferedReader
  - **20–60 MB** → FileChannel with ByteBuffer
  - **60–90 MB** → BufferedReader with manual buffer
  - **> 90 MB** → Memory-mapped file using `MappedByteBuffer`
- Support for both `File` and `Path` input
- Synchronous read support via `read(File)` and `read(Path)`
- Asynchronous read via `readAsync(File)` and `readAsync(Path)`
- Stream-based reading using `streamLines(File|Path, Consumer<String>)`
- Asynchronous streaming with `streamLinesAsync(File|Path, Consumer<String>)`
- Exception handling with custom `OpenLoomFileException` and `OpenLoomMemoryException`
- Default encoding: `UTF-8`, with ability to set custom `Charset`

---

## 📌 Upcoming Features (planned)

- Write and append support
- Line offset indexing
- Real-time streaming with flow control
