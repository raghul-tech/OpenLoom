<h1 align="center">🧵 OpenLoom</h1>

<p align="center">
  <em>The intelligent file reader for Java — blazing fast, zero config, and async-ready.</em>
</p>

<p align="center">
  <strong>🔍 Read any file. 🧠 Choose the best method. 🚀 Perform like a pro.</strong>
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
  <a href="https://javadoc.io/doc/io.github.raghul-tech/openloom/0.0.1">
    <img src="https://img.shields.io/badge/Javadoc-0.0.1-blue?style=for-the-badge&logo=java" alt="Javadoc" />
  </a>
  <a href="https://github.com/raghul-tech/OpenLoom/releases">
    <img src="https://img.shields.io/github/release/raghul-tech/OpenLoom?label=Release&style=for-the-badge&color=success" alt="Latest Release" />
  </a>
 <a href="https://buymeacoffee.com/raghultech">
    <img src="https://img.shields.io/badge/Buy%20Me%20a%20Coffee-Support-orange?style=for-the-badge&logo=buy-me-a-coffee" alt="Buy Me A Coffee" />
  </a>
</p>

---

## ✨ Why Use OpenLoom?

✅ **Zero Config** – Just drop in and start reading files.  
✅ **Adaptive Engine** – Chooses the best reading method based on file size.  
✅ **Built-in Async** – Boost performance with `CompletableFuture`.  
✅ **Stream-Friendly** – Sync & async line streaming.  
✅ **Charset Control** – Easily switch encodings.  
✅ **Lightweight** – Minimal dependencies, fast and focused.

---

## 🚀 Features

- 📄 Read text files into memory (`StringBuilder`)
- 🤖 Automatically chooses efficient read strategy (Buffered, NIO, Mapped)
- ⏱️ Read files asynchronously
- 📥 Line-by-line streaming (sync & async)
- 🌍 Support for multiple charsets (UTF-8, ISO-8859-1, UTF-16 etc.)
- 🧪 Simple API ideal for logging, parsing, and file analysis

---

## 📦 Installation

### Maven
```xml
<dependency>
  <groupId>io.github.raghul-tech</groupId>
  <artifactId>openloom</artifactId>
  <version>0.0.1</version>
</dependency>
```

### Gradle
```groovy
implementation 'io.github.raghul-tech:openloom:0.0.1'
```

---

## ✍️ Usage Examples
### 🔹 Basic File Read
#### ➡️ BasicReadExample.java

```java
OpenLoom reader = new OpenLoom();
StringBuilder content = reader.read(new File("example.txt"));
System.out.println(content);
```

### 🔹 Read using Path
#### ➡️ PathReadExample.java

```java
StringBuilder content = reader.read(Paths.get("data/example.txt"));
```
### 🔹 Async Read
#### ➡️ AsyncReadExample.java

```java
reader.readAsync(new File("large.txt")).thenAccept(content -> {
    System.out.println("First 100 chars: " + content.substring(0, 100));
});
```

### 🔹 Stream Lines (Sync & Async)
#### ➡️ StreamLineExample.java

```java
reader.streamLines(new File("log.txt"), System.out::println);
reader.streamLinesAsync(Paths.get("errors.log"), line -> {
    if (line.contains("ERROR")) System.err.println("Error line: " + line);
});
```

### 🔹 Change Charset (Mid-execution)
#### ➡️ CharsetExample.java

```java
OpenLoom reader = new OpenLoom(StandardCharsets.ISO_8859_1);
StringBuilder content = reader.read(Paths.get("legacy.txt"));
reader.setCharset(StandardCharsets.UTF_16);
```

---

## 💡 Run Using JAR
### **🧵 Compile:**

```bash
javac -cp openloom-0.0.1.jar ReadExampleWithFile.java
```
### **▶️ Run:**

> Windows:
```bash
java -cp .;openloom-0.0.1.jar ReadExampleWithFile   
```

> Linux/macOS:
```bash
java -cp .:openloom-0.0.1.jar ReadExampleWithFile   
```
---

## 🧩 Requirements
- Java 8 or above (JDK 17+ recommended)

---

## 📂 Example Files
### ✅ Ready-to-run examples in the [examples/](examples/) folder:

- [ReadExampleWithFile.java](examples/ReadExampleWithFile.java)

- [ReadExampleWithPath.java](examples/ReadExampleWithPath.java)

- [ReadAsyncExampleWithFile.java](examples/ReadAsyncExampleWithFile.java)

- [ReadAsyncExampleWithPath.java](examples/ReadAsyncExampleWithPath.java)

- [StreamLinesExampleWithFile.java](examples/StreamLinesExampleWithFile.java)

- [StreamLinesExampleWithPath.java](examples/StreamLinesExampleWithPath.java)

- [StreamLinesAsyncExampleWithFile.java](examples/StreamLinesAsyncExampleWithFile.java)

- [StreamLinesAsyncExampleWithPath.java](examples/StreamLinesAsyncExampleWithPath.java)

- [CharsetExample.java](examples/CharsetExample.java)

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



