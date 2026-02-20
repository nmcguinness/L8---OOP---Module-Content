---
title: "Binary I/O — Streams, BLOBs, and File Storage"
subtitle: "COMP C8Z03 — Year 2 OOP"
description: "How to read and write binary files as byte arrays in Java, store them in MySQL as BLOBs, and retrieve them or their metadata independently over a socket connection."
created: 2026-02-20
version: 1.0
authors: ["OOP Teaching Team"]
tags: [java, binary-io, blob, jdbc, streams, byte-array, file-storage, year2, comp-c8z03]
prerequisites:
  - DB Connectivity / DAO (t12)
  - Networking I — TCP Sockets (t15)
  - Basic file I/O (Files.readAllBytes)
---

# Binary I/O — Streams, BLOBs, and File Storage

> **Prerequisites:**
> - You have a working JDBC DAO layer (t12)
> - You understand `PreparedStatement` and `ResultSet`
> - You understand the client-server socket protocol (t15)

---

## What you'll learn

| Skill Type | You will be able to… |
| :- | :- |
| Understand | Explain the difference between character streams and binary (byte) streams. |
| Understand | Describe what a BLOB column is and when to use it. |
| Apply | Read a file from disk as a `byte[]` using `Files.readAllBytes()`. |
| Apply | Store binary data in MySQL using `PreparedStatement.setBytes()`. |
| Apply | Retrieve binary data using `ResultSet.getBytes()`. |
| Apply | Implement a metadata-only query that returns filename, content type, and size without reading the BLOB payload. |
| Apply | Send and receive a file as a Base64 string within your JSON socket protocol. |
| Analyse | Explain why metadata-only queries matter for performance. |

---

## Why this matters

GCA2 Stage 3 requires binary file storage end-to-end:

- A client reads a file from disk and sends it to the server.
- The server stores the raw bytes in a MySQL `BLOB` column.
- A client can later retrieve the full file, or query metadata (filename, size, type) without downloading the binary payload.

This involves three distinct skills: reading/writing binary files in Java, storing and retrieving binary data via JDBC, and integrating binary transfer into your existing JSON socket protocol.

---

## Key terms

### Character stream
A stream that works with **text** — it encodes and decodes characters. `BufferedReader` and `PrintWriter` are character streams. They are not safe for binary data because they may corrupt bytes when applying encoding.

### Binary stream (byte stream)
A stream that works with **raw bytes** — no encoding. `InputStream` and `OutputStream` are byte streams. Use these for anything that is not plain text: images, PDFs, executables, compressed files.

### `byte[]`
A **byte array** is the simplest way to hold binary data in memory. `Files.readAllBytes(path)` reads an entire file and returns it as a `byte[]`.

### BLOB
**BLOB (Binary Large Object)** is a MySQL column type for storing arbitrary binary data. Use `BLOB` for files up to ~65 KB, `MEDIUMBLOB` for up to ~16 MB, and `LONGBLOB` for larger content.

### Base64
**Base64** is an encoding scheme that converts arbitrary binary data into a string of printable ASCII characters. It is used here to embed binary file content inside a JSON message, since JSON cannot contain raw binary bytes.

### Metadata
In the context of stored files, **metadata** is descriptive information about the file — its name, MIME type, and size — stored in ordinary columns alongside the BLOB. You can query metadata without touching the BLOB column, which is important for performance.

---

## Part 1: Binary streams in Java

### Reading a file as bytes

```java
import java.nio.file.*;

public class BinaryFileDemo {

    // Reads: a file from disk and returns its bytes
    public static byte[] readFile(String filePath) throws Exception {
        if (filePath == null || filePath.isBlank())
            throw new IllegalArgumentException("filePath is required");

        Path path = Path.of(filePath);
        if (!Files.exists(path))
            throw new IllegalArgumentException("file not found: " + filePath);

        return Files.readAllBytes(path);
    }

    // Writes: a byte array back to disk at the given path
    public static void writeFile(String filePath, byte[] data) throws Exception {
        if (filePath == null || filePath.isBlank())
            throw new IllegalArgumentException("filePath is required");
        if (data == null)
            throw new IllegalArgumentException("data is required");

        Path path = Path.of(filePath);
        if (path.getParent() != null)
            Files.createDirectories(path.getParent());

        Files.write(path, data);
    }

    public static void main(String[] args) throws Exception {
        byte[] original = readFile("data/sample.png");
        System.out.println("Read " + original.length + " bytes");

        writeFile("data/sample_copy.png", original);
        System.out.println("Wrote copy");

        byte[] copy = readFile("data/sample_copy.png");
        System.out.println("Round-trip OK: " + java.util.Arrays.equals(original, copy));
    }
}
```

`Files.readAllBytes()` is appropriate for files up to a few MB — suitable for the kinds of files students will use in GCA2 (images, documents). For very large files you would use a streaming approach instead.

---

### Streams versus arrays

For JDBC `PreparedStatement`, you can pass either a `byte[]` (via `setBytes`) or an `InputStream` (via `setBinaryStream`). For files of modest size, `setBytes()` with a `byte[]` is simpler and clearer. The two approaches produce the same database result.

---

## Part 2: Extending the schema for BLOB storage

### Schema extension

Add a dedicated table (or extend an existing one) to store uploaded files:

```sql
CREATE TABLE file_uploads (
    file_id       INT            NOT NULL AUTO_INCREMENT,
    filename      VARCHAR(255)   NOT NULL,
    content_type  VARCHAR(100)   NOT NULL,
    file_size     INT            NOT NULL,
    file_data     MEDIUMBLOB     NOT NULL,
    uploaded_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (file_id)
);
```

Key decisions:
- `filename`, `content_type`, and `file_size` are **plain columns** — they are cheap to query.
- `file_data` is the `MEDIUMBLOB` — it holds the raw bytes.
- Keeping metadata in separate columns lets you query file information without loading the binary payload.

---

### DTO: `FileUpload`

```java
public class FileUpload {

    // === Fields ===
    private int _fileId;
    private String _filename;
    private String _contentType;
    private int _fileSize;
    private byte[] _fileData;

    // === Constructors ===
    // Creates: a FileUpload with all fields — used when reading from the database
    public FileUpload(int fileId, String filename, String contentType, int fileSize, byte[] fileData) {
        _fileId      = fileId;
        _filename    = filename;
        _contentType = contentType;
        _fileSize    = fileSize;
        _fileData    = fileData;
    }

    // Creates: a FileUpload without an ID — used before insertion
    public FileUpload(String filename, String contentType, int fileSize, byte[] fileData) {
        this(0, filename, contentType, fileSize, fileData);
    }

    // === Public API ===
    // Gets: the database-assigned file ID
    public int getFileId() { return _fileId; }

    // Gets: the original filename
    public String getFilename() { return _filename; }

    // Gets: the MIME type (e.g. "image/png")
    public String getContentType() { return _contentType; }

    // Gets: the file size in bytes
    public int getFileSize() { return _fileSize; }

    // Gets: the raw file bytes
    public byte[] getFileData() { return _fileData; }
}
```

---

### Metadata-only DTO: `FileMetadata`

When a client asks only for metadata, there is no need to load the BLOB at all:

```java
public class FileMetadata {

    // === Fields ===
    private int _fileId;
    private String _filename;
    private String _contentType;
    private int _fileSize;

    // === Constructors ===
    // Creates: a metadata record — no file bytes
    public FileMetadata(int fileId, String filename, String contentType, int fileSize) {
        _fileId      = fileId;
        _filename    = filename;
        _contentType = contentType;
        _fileSize    = fileSize;
    }

    // === Public API ===
    // Gets: the file ID
    public int getFileId() { return _fileId; }

    // Gets: the filename
    public String getFilename() { return _filename; }

    // Gets: the MIME type
    public String getContentType() { return _contentType; }

    // Gets: the size in bytes
    public int getFileSize() { return _fileSize; }

    // Overrides ===
    @Override
    public String toString() {
        return _filename + " (" + _contentType + ", " + _fileSize + " bytes, id=" + _fileId + ")";
    }
}
```

---

## Part 3: DAO — storing and retrieving BLOBs

### Interface

```java
import java.util.List;
import java.util.Optional;

public interface FileUploadDao {

    // Inserts: a file upload record and returns the generated ID
    int insert(FileUpload upload) throws Exception;

    // Gets: the full file record including binary data
    Optional<FileUpload> findById(int id) throws Exception;

    // Gets: metadata only — does not load the file_data column
    Optional<FileMetadata> findMetadataById(int id) throws Exception;

    // Gets: metadata for all uploaded files — does not load binary data
    List<FileMetadata> findAllMetadata() throws Exception;

    // Deletes: a file upload record by ID
    boolean deleteById(int id) throws Exception;
}
```

---

### JDBC implementation

```java
import java.sql.*;
import java.util.*;

public class JdbcFileUploadDao implements FileUploadDao {

    // === Fields ===
    private String _url;
    private String _user;
    private String _pass;

    // === Constructors ===
    // Creates: a DAO that connects to the given database
    public JdbcFileUploadDao(String url, String user, String pass) {
        if (url == null || url.isBlank())
            throw new IllegalArgumentException("url is required");
        if (user == null)
            throw new IllegalArgumentException("user is required");
        if (pass == null)
            throw new IllegalArgumentException("pass is required");

        _url  = url;
        _user = user;
        _pass = pass;
    }

    // === Public API ===
    // Inserts: a FileUpload record and returns the auto-generated file_id
    @Override
    public int insert(FileUpload upload) throws Exception {
        if (upload == null)
            throw new IllegalArgumentException("upload is required");
        if (upload.getFilename() == null || upload.getFilename().isBlank())
            throw new IllegalArgumentException("filename is required");
        if (upload.getFileData() == null || upload.getFileData().length == 0)
            throw new IllegalArgumentException("file data is required");

        String sql = "INSERT INTO file_uploads (filename, content_type, file_size, file_data) VALUES (?, ?, ?, ?)";

        try (Connection c = open();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, upload.getFilename());
            ps.setString(2, upload.getContentType());
            ps.setInt(3, upload.getFileSize());
            ps.setBytes(4, upload.getFileData());    // <-- the BLOB

            int rows = ps.executeUpdate();
            if (rows != 1)
                throw new IllegalStateException("insert failed, rows=" + rows);

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next())
                    throw new IllegalStateException("no generated key returned");
                return keys.getInt(1);
            }
        }
    }

    // Gets: the full FileUpload including binary data
    @Override
    public Optional<FileUpload> findById(int id) throws Exception {
        if (id <= 0)
            return Optional.empty();

        String sql = "SELECT file_id, filename, content_type, file_size, file_data FROM file_uploads WHERE file_id = ?";

        try (Connection c = open();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return Optional.empty();

                int    fileId      = rs.getInt("file_id");
                String filename    = rs.getString("filename");
                String contentType = rs.getString("content_type");
                int    fileSize    = rs.getInt("file_size");
                byte[] fileData    = rs.getBytes("file_data");  // <-- retrieve BLOB

                return Optional.of(new FileUpload(fileId, filename, contentType, fileSize, fileData));
            }
        }
    }

    // Gets: metadata only — the SELECT deliberately omits file_data
    @Override
    public Optional<FileMetadata> findMetadataById(int id) throws Exception {
        if (id <= 0)
            return Optional.empty();

        // Note: file_data is intentionally excluded from this query
        String sql = "SELECT file_id, filename, content_type, file_size FROM file_uploads WHERE file_id = ?";

        try (Connection c = open();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return Optional.empty();

                return Optional.of(mapMetadata(rs));
            }
        }
    }

    // Gets: metadata for all files — no binary data loaded
    @Override
    public List<FileMetadata> findAllMetadata() throws Exception {
        String sql = "SELECT file_id, filename, content_type, file_size FROM file_uploads ORDER BY file_id";

        try (Connection c = open();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<FileMetadata> result = new ArrayList<>();
            while (rs.next()) {
                result.add(mapMetadata(rs));
            }
            return result;
        }
    }

    // Deletes: a file record by ID
    @Override
    public boolean deleteById(int id) throws Exception {
        if (id <= 0)
            return false;

        String sql = "DELETE FROM file_uploads WHERE file_id = ?";

        try (Connection c = open();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    // === Helpers ===
    // Opens: a new database connection
    private Connection open() throws SQLException {
        return DriverManager.getConnection(_url, _user, _pass);
    }

    // Maps: a ResultSet row (without file_data) to a FileMetadata object
    private static FileMetadata mapMetadata(ResultSet rs) throws SQLException {
        int    fileId      = rs.getInt("file_id");
        String filename    = rs.getString("filename");
        String contentType = rs.getString("content_type");
        int    fileSize    = rs.getInt("file_size");
        return new FileMetadata(fileId, filename, contentType, fileSize);
    }
}
```

The metadata query (`findMetadataById`, `findAllMetadata`) deliberately omits `file_data` from the `SELECT`. MySQL does not load BLOB column data unless the column is in the `SELECT` list, so this is a meaningful performance optimisation for clients that only need to list available files.

---

## Part 4: Integrating binary transfer into the JSON protocol

### The challenge

JSON is a text format. Raw bytes cannot be embedded in a JSON string directly. The standard solution is **Base64 encoding**: convert the byte array to a string before including it in JSON, and decode it back on arrival.

Java's `java.util.Base64` class handles this.

---

### Encoding and decoding

```java
import java.util.Base64;

// Encode bytes to a Base64 string (for sending in JSON)
byte[] original = Files.readAllBytes(Path.of("data/photo.jpg"));
String encoded  = Base64.getEncoder().encodeToString(original);

// Decode a Base64 string back to bytes (on the receiving end)
byte[] decoded  = Base64.getDecoder().decode(encoded);

// Verify round-trip
System.out.println("Round-trip OK: " + java.util.Arrays.equals(original, decoded));
```

---

### Protocol: FILE_UPLOAD request

Add new request types to your protocol:

```json
{
  "requestType": "FILE_UPLOAD",
  "payload": {
    "filename": "photo.jpg",
    "contentType": "image/jpeg",
    "fileData": "<base64-encoded string>"
  }
}
```

```json
{
  "requestType": "FILE_DOWNLOAD",
  "payload": { "id": 3 }
}
```

```json
{
  "requestType": "FILE_METADATA",
  "payload": { "id": 3 }
}
```

---

### Adding FILE_UPLOAD to `ClientHandler.dispatch()`

Extend the `dispatch` method in your `ClientHandler` (from t15):

```java
// Handles: FILE_UPLOAD — decodes Base64 data and stores as BLOB
if ("FILE_UPLOAD".equals(type)) {
    String filename     = req.getString("filename");
    String contentType  = req.getString("contentType");
    String encoded      = req.getString("fileData");

    if (filename == null || filename.isBlank())
        return ServerResponse.error("filename is required");
    if (encoded == null || encoded.isBlank())
        return ServerResponse.error("fileData is required");

    byte[] data = java.util.Base64.getDecoder().decode(encoded);

    FileUpload upload = new FileUpload(filename, contentType, data.length, data);
    int newId = _fileDao.insert(upload);

    Optional<FileMetadata> meta = _fileDao.findMetadataById(newId);
    return meta.isPresent()
        ? ServerResponse.ok("file uploaded", meta.get())
        : ServerResponse.error("upload succeeded but metadata not found");
}

// Handles: FILE_DOWNLOAD — retrieves full binary data as Base64
if ("FILE_DOWNLOAD".equals(type)) {
    int id = req.getInt("id");
    Optional<FileUpload> file = _fileDao.findById(id);

    if (file.isEmpty())
        return ServerResponse.error("no file with id=" + id);

    String encoded = java.util.Base64.getEncoder().encodeToString(file.get().getFileData());

    java.util.Map<String, Object> response = new java.util.HashMap<>();
    response.put("fileId",      file.get().getFileId());
    response.put("filename",    file.get().getFilename());
    response.put("contentType", file.get().getContentType());
    response.put("fileData",    encoded);

    return ServerResponse.ok("file retrieved", response);
}

// Handles: FILE_METADATA — returns metadata without loading the binary payload
if ("FILE_METADATA".equals(type)) {
    int id = req.getInt("id");
    Optional<FileMetadata> meta = _fileDao.findMetadataById(id);

    if (meta.isEmpty())
        return ServerResponse.error("no file with id=" + id);

    return ServerResponse.ok("metadata retrieved", meta.get());
}
```

---

### Client — uploading a file

```java
// Reads: a file from disk, encodes it as Base64, and sends a FILE_UPLOAD request
public void uploadFile(PrintWriter out, BufferedReader in, String filePath) throws Exception {
    byte[] data = Files.readAllBytes(Path.of(filePath));
    String encoded = java.util.Base64.getEncoder().encodeToString(data);

    String filename    = Path.of(filePath).getFileName().toString();
    String contentType = detectContentType(filename);

    java.util.Map<String, Object> request = new java.util.HashMap<>();
    request.put("requestType", "FILE_UPLOAD");

    java.util.Map<String, Object> payload = new java.util.HashMap<>();
    payload.put("filename",    filename);
    payload.put("contentType", contentType);
    payload.put("fileData",    encoded);
    request.put("payload", payload);

    out.println(_mapper.writeValueAsString(request));

    String reply = in.readLine();
    System.out.println("Upload response: " + reply);
}

// Detects: a simple MIME type based on file extension
private String detectContentType(String filename) {
    if (filename == null)
        return "application/octet-stream";

    String lower = filename.toLowerCase();
    if (lower.endsWith(".png"))  return "image/png";
    if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
    if (lower.endsWith(".pdf"))  return "application/pdf";
    if (lower.endsWith(".txt"))  return "text/plain";
    return "application/octet-stream";
}
```

### Client — downloading and saving a file

```java
// Downloads: a file by ID and saves it to the output directory
public void downloadFile(PrintWriter out, BufferedReader in, int fileId, String outputDir) throws Exception {
    java.util.Map<String, Object> request = new java.util.HashMap<>();
    request.put("requestType", "FILE_DOWNLOAD");
    request.put("payload", java.util.Map.of("id", fileId));

    out.println(_mapper.writeValueAsString(request));

    String reply = in.readLine();
    ServerResponse<?> response = _mapper.readValue(reply,
        _mapper.getTypeFactory().constructParametricType(ServerResponse.class, Object.class));

    if (!response.isOk()) {
        System.out.println("Download failed: " + response.getMessage());
        return;
    }

    // The data is a map: filename, contentType, fileData (Base64)
    java.util.Map<?, ?> data = _mapper.convertValue(response.getData(), java.util.Map.class);
    String filename = data.get("filename").toString();
    String encoded  = data.get("fileData").toString();

    byte[] bytes = java.util.Base64.getDecoder().decode(encoded);
    Files.write(Path.of(outputDir, filename), bytes);

    System.out.println("Downloaded: " + filename + " (" + bytes.length + " bytes)");
}
```

---

## Part 5: Smoke test — verify round-trip integrity

Before writing JUnit tests, do a manual round-trip:

1. Choose a small file (e.g., a PNG under 100 KB).
2. Read it as bytes. Record `Arrays.toString(bytes)` for a small slice (first 4 bytes).
3. Upload it to the server.
4. Download it using the generated file ID.
5. Save it to a new filename.
6. Open the file — does it look correct? For an image, does it display?
7. Compare byte-for-byte with `Arrays.equals(original, downloaded)`.

This manual check is valuable before writing the automated JUnit test.

---

## Part 6: JUnit testing for binary storage

A binary storage test must:
1. Upload a known byte array (e.g., `new byte[]{1, 2, 3, 4, 5}`).
2. Retrieve the stored record by ID.
3. Assert `Arrays.equals(original, retrieved)`.

```java
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FileUploadDaoTest {

    private FileUploadDao _dao;

    @BeforeEach
    void setUp() {
        // Use a test-specific schema or a dedicated test database
        String url  = "jdbc:mysql://localhost:3306/taskhub_test?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "taskhub_user";
        String pass = "your_password";
        _dao = new JdbcFileUploadDao(url, user, pass);
    }

    @Test
    void insert_thenFindById_returnsSameBytes() throws Exception {
        byte[] original = new byte[]{10, 20, 30, 40, 50};
        FileUpload upload = new FileUpload("test.bin", "application/octet-stream", original.length, original);

        int id = _dao.insert(upload);
        assertTrue(id > 0, "generated ID should be positive");

        Optional<FileUpload> found = _dao.findById(id);
        assertTrue(found.isPresent(), "file should be found after insert");

        assertArrayEquals(original, found.get().getFileData(),
            "retrieved bytes must match uploaded bytes exactly");
    }

    @Test
    void findMetadataById_doesNotReturnFileData() throws Exception {
        byte[] data  = new byte[]{1, 2, 3};
        FileUpload up = new FileUpload("meta-test.txt", "text/plain", data.length, data);
        int id = _dao.insert(up);

        Optional<FileMetadata> meta = _dao.findMetadataById(id);
        assertTrue(meta.isPresent());
        assertEquals("meta-test.txt", meta.get().getFilename());
        assertEquals(3, meta.get().getFileSize());
        // FileMetadata has no getFileData() — confirmed by compile-time check
    }

    @Test
    void deleteById_removesRecord() throws Exception {
        byte[] data = new byte[]{99};
        int id = _dao.insert(new FileUpload("delete-me.bin", "application/octet-stream", 1, data));

        boolean deleted = _dao.deleteById(id);
        assertTrue(deleted);

        Optional<FileUpload> gone = _dao.findById(id);
        assertTrue(gone.isEmpty(), "record should be absent after deletion");
    }
}
```

---

## Common mistakes

| Mistake | What happens | Fix |
| :- | :- | :- |
| Including `file_data` in metadata queries | All BLOB data loaded on every list request — expensive and slow | Write a separate `SELECT` that explicitly omits `file_data` |
| Using `getString("file_data")` on a BLOB | Data is truncated or corrupted | Use `getBytes("file_data")` or `getBinaryStream("file_data")` |
| Not encoding bytes as Base64 before embedding in JSON | JSON parser throws on non-text bytes | Always Base64-encode before JSON serialisation |
| Forgetting to decode Base64 on the receiving end | Stored/compared as the Base64 string, not the original bytes | Always `Base64.getDecoder().decode(...)` before using the data |
| Comparing byte arrays with `==` or `.equals()` | `==` compares references; `.equals()` on arrays does the same | Use `Arrays.equals(a, b)` or `assertArrayEquals` in tests |
| Using `BLOB` column for large files without checking size | `BLOB` is capped at ~65 KB; larger files are silently truncated | Use `MEDIUMBLOB` (16 MB) or `LONGBLOB` (4 GB) as appropriate |

---

## Practice tasks

1. Add a `findAllMetadata()` endpoint to your server that returns a list of all stored file metadata (no binary data).
2. Write a client method that lists all available files, lets the user choose one by ID, downloads it, and saves it.
3. Write a JUnit test that uploads a real PNG file from `src/test/resources`, retrieves it, and verifies byte-for-byte equality.
4. Extend the schema with an `uploader_name` column. Update the DTO, DAO, and protocol payload accordingly.
5. Add a maximum file size check in the server handler: reject uploads larger than 5 MB with a structured error.

---

## Reflective questions

1. Why is it important that the metadata query explicitly excludes `file_data` from the `SELECT`?
2. What would happen if you tried to store a PNG image in a `VARCHAR` column?
3. Why is `Arrays.equals()` needed to compare byte arrays rather than `==`?
4. A colleague says "just store the file path in the database, not the bytes". What are the trade-offs compared to BLOB storage?
5. What would need to change in the protocol and the DAO if you wanted to support streaming a very large file (e.g. 500 MB) rather than reading it all into memory at once?

---

## Further reading

- Oracle Docs — `PreparedStatement.setBytes()`  
  https://docs.oracle.com/en/java/docs/api/java.sql/java/sql/PreparedStatement.html
- Baeldung — Read a File as a Byte Array  
  https://www.baeldung.com/java-file-to-byte-array
- MySQL Reference — BLOB and TEXT Types  
  https://dev.mysql.com/doc/refman/8.0/en/blob.html
- Java `Base64` API  
  https://docs.oracle.com/en/java/docs/api/java.base/java/util/Base64.html

---
