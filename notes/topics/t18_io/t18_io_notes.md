---
title: "Java I/O: Files, Paths & Streams"
subtitle: "COMP C8Z03 — Year 2 OOP"
topic_code: t18_io
description: "Reading and writing files using NIO.2 (Path, Files), text I/O with BufferedReader/Writer, byte array I/O, basic CSV parsing, and try-with-resources for safe resource management."
created: 2026-05-27
last_updated: 2026-05-27
version: 1.0
status: published
authors: ["OOP Teaching Team"]
tags: [java, io, files, path, nio2, bufferedreader, csv, try-with-resources, year2, comp-c8z03]
difficulty_tier: Intermediate
mlos: [MLO1, MLO3]
previous_topic: t17_streams_api
prerequisites:
  - Exception handling (checked exceptions, try-with-resources)
  - Streams API (Stream<String>, collect, filter — for Files.lines())
  - Collections I: ArrayList
---

# Java I/O: Files, Paths & Streams

> **Prerequisites:**
> - Exception handling: checked exceptions, `try-with-resources` (`AutoCloseable`)
> - Streams API: you can use `Stream<String>` pipelines (needed for `Files.lines()`)
> - Collections: `List<String>`, basic loops

---

## What you'll learn

| Skill Type | You will be able to… |
| :-- | :-- |
| Understand | Distinguish the legacy `File` API from the modern NIO.2 `Path`/`Files` API. |
| Use | Construct `Path` objects and resolve relative paths. |
| Use | Read all lines from a text file with `Files.readAllLines()`. |
| Use | Stream a file line-by-line with `Files.lines()` for large files. |
| Use | Write text to a file with `Files.write()` and `Files.writeString()`. |
| Use | Read and write raw bytes with `Files.readAllBytes()` and `Files.write(path, bytes)`. |
| Use | Open a `BufferedReader`/`BufferedWriter` inside try-with-resources. |
| Use | Parse a simple CSV file into a list of records. |
| Debug | Identify and fix `IOException`, `NoSuchFileException`, and encoding issues. |

---

## Why this matters

Most programs need to persist data: load a configuration, read a dataset, save results, exchange files with other systems. Java's NIO.2 API (`java.nio.file`) provides a clean, expressive way to do all of this — far safer and more readable than the older `File`/`FileInputStream` approach.

Combined with `try-with-resources` (from the exception handling topic) and `Stream<String>` (from the Streams topic), NIO.2 enables concise, safe file processing in very few lines.

---

## How this builds on previous content

| Earlier topic | Concept carried forward |
| :-- | :-- |
| Exception handling | `IOException` is checked — callers must handle or propagate; try-with-resources closes resources automatically |
| Streams API | `Files.lines(path)` returns a `Stream<String>` — plug directly into `.filter()`, `.map()`, `.collect()` |
| Collections | Results are usually collected into `List<String>` or `List<YourRecord>` |

---

## Part 1: Path and Files — the NIO.2 API

### Path

`Path` represents a file or directory location. It does not require the file to exist.

```java
import java.nio.file.Path;
import java.nio.file.Paths;

Path p1 = Path.of("data", "scores.txt");      // relative: data/scores.txt
Path p2 = Path.of("C:/data/scores.txt");      // absolute (Windows)
Path p3 = Paths.get("data/scores.txt");       // legacy factory — equivalent

Path dir  = Path.of("data");
Path file = dir.resolve("scores.txt");        // data/scores.txt
Path parent = file.getParent();               // data
String name = file.getFileName().toString();  // scores.txt
```

### Files utility class

`Files` provides static methods for almost every file operation:

```java
import java.nio.file.Files;

boolean exists = Files.exists(p1);
boolean isFile = Files.isRegularFile(p1);
boolean isDir  = Files.isDirectory(p1);
long size      = Files.size(p1);              // bytes

Files.createDirectories(Path.of("data", "output")); // mkdir -p
Files.deleteIfExists(p1);
Files.copy(p1, Path.of("backup.txt"));
Files.move(p1, Path.of("archive", "scores.txt"));
```

---

## Part 2: Reading text files

### Read all lines at once

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.List;

public static List<String> readLines(Path path) throws IOException {
    return Files.readAllLines(path, StandardCharsets.UTF_8);
}
```

Use when the file fits comfortably in memory (< a few hundred MB).

---

### Stream lines lazily

```java
import java.util.stream.Stream;

public static long countNonEmpty(Path path) throws IOException {
    try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
        return lines.filter(l -> !l.isBlank()).count();
    }
}
```

> **Always** close the stream from `Files.lines()` — use try-with-resources. It holds an open file handle until closed.

---

### BufferedReader (lower-level)

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public static List<String> readWithBuffer(Path path) throws IOException {
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
    }
    return lines;
}
```

Use `BufferedReader` when you need line-by-line control (e.g. skip header, stop early).

---

## Part 3: Writing text files

```java
import java.nio.file.StandardOpenOption;
import java.util.List;

// Write a list of strings (one per line)
public static void writeLines(Path path, List<String> lines) throws IOException {
    Files.write(path, lines, StandardCharsets.UTF_8);
    // Overwrites by default; add StandardOpenOption.APPEND to append
}

// Write a single string
public static void writeString(Path path, String content) throws IOException {
    Files.writeString(path, content, StandardCharsets.UTF_8);
}

// Append to existing file
public static void appendLine(Path path, String line) throws IOException {
    Files.writeString(path, line + System.lineSeparator(),
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND);
}
```

### BufferedWriter (lower-level)

```java
import java.io.BufferedWriter;
import java.io.IOException;

public static void writeWithBuffer(Path path, List<String> lines) throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
    }
}
```

---

## Part 4: Binary files (byte arrays)

```java
// Read entire file as bytes
public static byte[] readBytes(Path path) throws IOException {
    return Files.readAllBytes(path);
}

// Write bytes to file
public static void writeBytes(Path path, byte[] data) throws IOException {
    Files.write(path, data);
}
```

```java
// Copy a binary file via byte array
Path src  = Path.of("image.png");
Path dest = Path.of("copy.png");
Files.write(dest, Files.readAllBytes(src));

// Smarter: Files.copy handles streams internally
Files.copy(src, dest);
```

---

## Part 5: CSV parsing

Java has no built-in CSV library, but splitting on commas handles simple cases:

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public record PlayerRecord(String name, int score) {}

public static List<PlayerRecord> parseCsv(Path path) throws IOException {
    List<String> lines = Files.readAllLines(path);
    List<PlayerRecord> records = new ArrayList<>();

    boolean firstLine = true;
    for (String line : lines) {
        if (firstLine) { firstLine = false; continue; } // skip header
        if (line.isBlank()) continue;

        String[] parts = line.split(",", -1);           // -1 keeps empty trailing fields
        String name  = parts[0].trim();
        int    score = Integer.parseInt(parts[1].trim());
        records.add(new PlayerRecord(name, score));
    }
    return records;
}
```

Example CSV:
```
name,score
Alice,95
Ben,82
Clara,78
```

> For production CSV (quoted fields, embedded commas), use a library like **Apache Commons CSV** or **OpenCSV**.

---

## Progressive coding steps (A ? B ? C)

### Step A — Read and print a file

```java
Path path = Path.of("data.txt");
List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
lines.forEach(System.out::println);
```

### Step B — Filter lines with a stream

```java
long commentCount;
try (Stream<String> lines = Files.lines(Path.of("config.txt"))) {
    commentCount = lines.filter(l -> l.startsWith("#")).count();
}
System.out.println("Comment lines: " + commentCount);
```

### Step C — Read, transform, write

```java
Path input  = Path.of("scores.csv");
Path output = Path.of("scores_upper.csv");

List<String> lines = Files.readAllLines(input, StandardCharsets.UTF_8);
List<String> upper = lines.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
Files.write(output, upper, StandardCharsets.UTF_8);
```

---

## Games example: load level config from file

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

// File format: key=value, one per line, # for comments
// width=20
// height=15
// difficulty=hard

public class LevelConfig {

    private final Map<String, String> _props = new HashMap<>();

    public static LevelConfig load(Path path) throws IOException {
        LevelConfig config = new LevelConfig();
        for (String line : Files.readAllLines(path)) {
            String trimmed = line.strip();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
            String[] parts = trimmed.split("=", 2);
            if (parts.length == 2) {
                config._props.put(parts[0].strip(), parts[1].strip());
            }
        }
        return config;
    }

    public String get(String key, String defaultValue) {
        return _props.getOrDefault(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        String val = _props.get(key);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
```

Usage:
```java
LevelConfig cfg = LevelConfig.load(Path.of("levels", "level1.cfg"));
int width  = cfg.getInt("width",  20);
int height = cfg.getInt("height", 15);
String diff = cfg.get("difficulty", "normal");
```

---

## Software example: append-only task log

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TaskLog {

    private final Path _logFile;

    public TaskLog(Path logFile) {
        _logFile = logFile;
    }

    public void log(String event) throws IOException {
        String entry = LocalDateTime.now() + " | " + event;
        Files.writeString(_logFile, entry + System.lineSeparator(),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND);
    }

    public List<String> readAll() throws IOException {
        if (!Files.exists(_logFile)) return List.of();
        return Files.readAllLines(_logFile, StandardCharsets.UTF_8);
    }

    public List<String> search(String keyword) throws IOException {
        try (var lines = Files.lines(_logFile, StandardCharsets.UTF_8)) {
            return lines
                .filter(l -> l.contains(keyword))
                .collect(Collectors.toList());
        }
    }
}
```

---

## Common pitfalls

| Pitfall | Problem | Fix |
| :-- | :-- | :-- |
| Forgetting to close `Files.lines()` | File handle leak — OS runs out of handles | Always wrap in try-with-resources |
| Using `File` instead of `Path` | Legacy API is more error-prone; returns `boolean` instead of throwing | Use `Path` + `Files` for all new code |
| Ignoring `StandardCharsets.UTF_8` | Default encoding varies by platform — files read on one machine may fail on another | Always specify charset explicitly |
| `split(",")` for CSV with quoted fields | Fails on `"Smith, Jr.",90` — splits inside the quotes | Use a CSV library for complex files |
| `Files.readAllLines()` on a 2 GB log | Loads entire file into heap | Use `Files.lines()` (lazy stream) instead |
| Constructing `Path` with OS-specific separators | `"data\\scores.txt"` fails on Linux | Use `Path.of("data", "scores.txt")` — handles separators |

---

## Reflective Questions

- Why is `Files.lines()` preferred over `Files.readAllLines()` for large files?
- What would happen if you forgot the `try-with-resources` around `Files.lines()`?
- Why must `IOException` be handled or declared when calling `Files.readAllLines()`?
- How would you read only the first 10 lines of a large file efficiently?
- You need to write a file only if it does not already exist. Which `StandardOpenOption` values would you use?

---

## Lesson Context

```yaml
previous_lesson:
  topic_code: t17_streams_api
  domain_emphasis: Balanced

this_lesson:
  topic_code: t18_io
  primary_domain_emphasis: Balanced
  difficulty_tier: Intermediate
mlos: [MLO1, MLO3]
```
