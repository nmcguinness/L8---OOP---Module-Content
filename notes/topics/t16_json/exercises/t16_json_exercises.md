---
title: "Binary I/O — Streams, BLOBs, and File Storage — Exercises"
subtitle: "Game Asset Store (progressive build: byte arrays → schema → DAO → metadata → Base64 + protocol integration)"
module: "COMP C8Z03 Object-Oriented Programming"
language: "Java"
created: 2026-02-20
version: 1.0
tags: [java, binary-io, blob, jdbc, base64, streams, file-storage, exercises]
---

# Binary I/O — Streams, BLOBs, and File Storage — Exercises

These exercises reinforce **Binary I/O — Streams, BLOBs, and File Storage** by building a **Game Asset Store** — a server that stores game assets (sprites, maps, audio clips) as binary data in MySQL and lets clients upload, download, and query them.

## Ground rules

- Never use character streams (`BufferedReader`, `PrintWriter`) to read or write binary data.
- Always use `getBytes("column_name")` for BLOB columns — never `getString(...)`.
- Always Base64-encode binary data before embedding it in a JSON string.
- Use `Arrays.equals(a, b)` to compare byte arrays — not `==` or `.equals()`.
- Use **PreparedStatement** throughout — no SQL string concatenation.

## How to run

Since each class listed in the Solution section has a `main(String[] args)` method you can just run each class directly.

## Before you start

### Prerequisites checklist

- [ ] Completed the Binary I/O notes
- [ ] Completed the DB Connectivity / DAO exercises (t12)
- [ ] You understand `PreparedStatement` and `ResultSet`
- [ ] MySQL is running locally and you can connect with MySQL Workbench or the CLI

---

## Database setup

Run the following SQL **once** in MySQL Workbench (or the MySQL CLI) before attempting any exercise.

```sql
-- =========================================================
-- Game Asset Store — one-time database setup
-- =========================================================

-- 1. Drop and recreate the schema
DROP DATABASE IF EXISTS game_assets_db;
CREATE DATABASE game_assets_db;
USE game_assets_db;

-- 2. Create the game_assets table
--    asset_data stores the raw binary payload as a MEDIUMBLOB (up to 16 MB)
CREATE TABLE game_assets (
    asset_id    INT          AUTO_INCREMENT PRIMARY KEY,
    asset_name  VARCHAR(255) NOT NULL,
    asset_type  VARCHAR(100) NOT NULL,
    file_size   INT          NOT NULL,
    asset_data  MEDIUMBLOB   NOT NULL
);

-- 3. Seed four rows with placeholder binary data
--    REPEAT fills the column with repeated ASCII characters so each row has a real,
--    non-empty payload. The actual bytes are replaced when you run the exercises.
INSERT INTO game_assets (asset_name, asset_type, file_size, asset_data) VALUES
    ('hero_idle.png',    'image/png',                1024, REPEAT('A', 1024)),
    ('background.wav',   'audio/wav',                 512, REPEAT('B',  512)),
    ('level_01.map',     'application/octet-stream', 2048, REPEAT('C', 2048)),
    ('enemy_sprite.png', 'image/png',                 768, REPEAT('D',  768));
```

---

## Exercise 01 — Reading, writing, and round-tripping a file as `byte[]`

**Objective:** Read a small file from disk as a `byte[]`, write it back to a different path, then prove byte-for-byte equality with `Arrays.equals()`.

**Context (software + games):**

- **Software dev:** Any system that transfers files (document storage, image upload, backup) must reliably preserve every byte. Testing with `Arrays.equals` is how you prove it.
- **Games dev:** Game asset pipelines (exporting sprites, maps, or audio clips) depend on lossless binary reads and writes.

### What you are building

- A `BinaryFileUtil` class with `readFile(path)` and `writeFile(path, bytes)` methods
- A round-trip test using a real file on disk

### Required API

```java
public class BinaryFileUtil {
    public static byte[] readFile(String path) throws Exception;
    public static void writeFile(String path, byte[] data) throws Exception;
}
```

### Tasks

1. Implement `BinaryFileUtil`:
   - `readFile(path)`: validates path is non-null/non-blank and the file exists; returns `Files.readAllBytes(Path.of(path))`.
   - `writeFile(path, data)`: validates path and data; creates parent directories if needed; writes with `Files.write(...)`.
2. Create a small test file — either use any image/PNG you have, or create a synthetic one:

   ```java
   byte[] synthetic = new byte[256];
   for (int i = 0; i < synthetic.length; i++) synthetic[i] = (byte) i;
   BinaryFileUtil.writeFile("data/test_asset.bin", synthetic);
   ```

3. In `Exercise.run()`:
   - Create (or use) a test file at `"data/test_asset.bin"`.
   - Read it as `original`.
   - Write `original` to `"data/test_asset_copy.bin"`.
   - Read the copy as `copy`.
   - Print: `"File size: N bytes"`, `"Round-trip OK: true/false"`.
   - Print the first 8 bytes of the original as decimal values.

### Sample output

```text
File size: 256 bytes
Round-trip OK: true
First 8 bytes: [0, 1, 2, 3, 4, 5, 6, 7]
```

### Constraints

- Use `Files.readAllBytes` — not a manual `InputStream` loop.
- Use `Arrays.equals(original, copy)` — not `==`.

### Done when…

- `"Round-trip OK: true"` prints every time.
- Both files exist on disk and are identical in size.
- Modifying one byte in `original` makes the check print `false`.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">✅ Solution</summary>
  <div style="margin-top:0.8rem;">

```java
package t16_binary_io.exercises.e01;

import java.nio.file.*;
import java.util.Arrays;

public class Exercise {

    public static void main(String[] args) throws Exception {
        // Create a synthetic test file
        byte[] synthetic = new byte[256];
        for (int i = 0; i < synthetic.length; i++)
            synthetic[i] = (byte) i;
        BinaryFileUtil.writeFile("data/test_asset.bin", synthetic);

        // Round-trip test
        byte[] original = BinaryFileUtil.readFile("data/test_asset.bin");
        BinaryFileUtil.writeFile("data/test_asset_copy.bin", original);
        byte[] copy = BinaryFileUtil.readFile("data/test_asset_copy.bin");

        System.out.println("File size: " + original.length + " bytes");
        System.out.println("Round-trip OK: " + Arrays.equals(original, copy));

        // Print first 8 bytes
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 8; i++) {
            sb.append(original[i] & 0xFF);
            if (i < 7) sb.append(", ");
        }
        sb.append("]");
        System.out.println("First 8 bytes: " + sb);
    }
}

class BinaryFileUtil {

    // === Public API ===
    // Reads: a file from disk and returns its bytes
    public static byte[] readFile(String path) throws Exception {
        if (path == null || path.isBlank())
            throw new IllegalArgumentException("path is required");

        Path p = Path.of(path);
        if (!Files.exists(p))
            throw new IllegalArgumentException("file not found: " + path);

        return Files.readAllBytes(p);
    }

    // Writes: a byte array to disk at the given path
    public static void writeFile(String path, byte[] data) throws Exception {
        if (path == null || path.isBlank())
            throw new IllegalArgumentException("path is required");
        if (data == null)
            throw new IllegalArgumentException("data is required");

        Path p = Path.of(path);
        if (p.getParent() != null)
            Files.createDirectories(p.getParent());

        Files.write(p, data);
    }
}
```

  </div>
</details>

---

## Exercise 02 — Connect to the game_assets database and query metadata

**Objective:** Connect to the `game_assets_db` database (created in the **Database setup** section above) using JDBC and list the seeded assets. The query must read metadata only — `asset_data` must not appear in the `SELECT`.

**Context (software + games):**

- **Software dev:** Schema design separates metadata (cheap to query) from binary payload (expensive to load). A metadata-only SELECT avoids pulling megabytes of binary data when you only need filenames and sizes.
- **Games dev:** Asset management systems (Unity AssetBundles, Unreal pak files) always separate asset metadata from binary content — listing assets never loads the binary payload.

### What you are building

- A JDBC connection to `game_assets_db`
- A metadata-only query that lists every row and a count query — neither touches `asset_data`

### Tasks

1. In `Exercise.run()`:
   - Connect with JDBC using the credentials from the **Database setup** section.
   - Run `SELECT asset_id, asset_name, asset_type, file_size FROM game_assets ORDER BY asset_id` (no `asset_data`).
   - Print each row: `"[id] name (type, N bytes)"`.
   - Run `SELECT COUNT(*) FROM game_assets` and print the total.

### Sample output

```text
[1] hero_idle.png (image/png, 1024 bytes)
[2] background.wav (audio/wav, 512 bytes)
[3] level_01.map (application/octet-stream, 2048 bytes)
[4] enemy_sprite.png (image/png, 768 bytes)
Total assets: 4
```

### Constraints

- The metadata `SELECT` must not include `asset_data` — prove you can query metadata cheaply.
- Use `PreparedStatement` even for the count query.

### Done when…

- All four seeded rows appear in the output.
- No BLOB data is loaded by either query.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">✅ Solution</summary>
  <div style="margin-top:0.8rem;">

```java
package t16_json.exercises.ex02;

import java.sql.*;

public class Exercise {

    private static final String URL     = "jdbc:mysql://localhost:3306/game_assets_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public static void main(String[] args) throws Exception {

        try (Connection c = DriverManager.getConnection(URL, DB_USER, DB_PASS)) {

            // Metadata-only query — asset_data deliberately excluded
            String sql = "SELECT asset_id, asset_name, asset_type, file_size FROM game_assets ORDER BY asset_id";

            try (PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int    id       = rs.getInt("asset_id");
                    String name     = rs.getString("asset_name");
                    String type     = rs.getString("asset_type");
                    int    fileSize = rs.getInt("file_size");
                    System.out.println("[" + id + "] " + name + " (" + type + ", " + fileSize + " bytes)");
                }
            }

            try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM game_assets");
                 ResultSet rs = ps.executeQuery()) {

                rs.next();
                System.out.println("Total assets: " + rs.getInt(1));
            }
        }
    }
}
```

  </div>
</details>

---

## Exercise 03 — DAO: `setBytes()` insert and `getBytes()` retrieval

**Objective:** Implement a `GameAssetDao` with `insert()` using `PreparedStatement.setBytes()` and `findById()` using `ResultSet.getBytes()`. Verify the round-trip with `Arrays.equals()`.

**Context (software + games):**

- **Software dev:** BLOB storage is identical in principle to any other JDBC column type — it is just a wider placeholder (`?`) filled with bytes.
- **Games dev:** Storing compiled shader bytecode, serialised level data, or compressed texture atlases follows exactly this pattern.

### What you are building

- `GameAsset` domain class (assetId, assetName, assetType, fileSize, assetData)
- `GameAssetDao` interface and `JdbcGameAssetDao` implementation
- A round-trip test: insert bytes → retrieve by ID → compare

### Required API

```java
public class GameAsset {
    public GameAsset(int assetId, String assetName, String assetType, int fileSize, byte[] assetData);
    // getters; validated constructor
}

public interface GameAssetDao {
    int insert(GameAsset asset) throws Exception;
    java.util.Optional<GameAsset> findById(int id) throws Exception;
    boolean deleteById(int id) throws Exception;
}
```

### Tasks

1. Implement `GameAsset`:
   - Validate: `assetName` and `assetType` required; `fileSize >= 0`; `assetData` not null and not empty; `assetId >= 0`
   - Trim strings, lowercase `assetType`
2. Implement `JdbcGameAssetDao.insert(asset)`:
   - SQL: `INSERT INTO game_assets (asset_name, asset_type, file_size, asset_data) VALUES (?, ?, ?, ?)`
   - Use `ps.setBytes(4, asset.getAssetData())` for the BLOB
   - Return the auto-generated `asset_id`
3. Implement `JdbcGameAssetDao.findById(id)`:
   - SQL: `SELECT asset_id, asset_name, asset_type, file_size, asset_data FROM game_assets WHERE asset_id = ?`
   - Use `rs.getBytes("asset_data")` to retrieve the BLOB
   - Return `Optional.of(new GameAsset(...))` or `Optional.empty()`
4. Implement `JdbcGameAssetDao.deleteById(id)`.
5. In `Exercise.run()`:
   - Create a known byte array: `new byte[]{10, 20, 30, 40, 50}`.
   - Insert it as an asset named `"test_sprite.bin"` with type `"application/octet-stream"`.
   - Print the generated ID.
   - Retrieve by ID.
   - Print: `"Round-trip OK: true/false"`.
   - Delete the row.

### Sample output

```text
Inserted asset with id=5
Round-trip OK: true
Deleted: true
```

### Constraints

- Use `ps.setBytes(...)` — not `ps.setString(...)` for the BLOB column.
- Use `rs.getBytes(...)` — not `rs.getString(...)`.
- Use `Arrays.equals(original, retrieved)` — not `==`.

### Done when…

- `"Round-trip OK: true"` prints consistently.
- A manually inserted row (from Exercise 02) does not affect the result.
- The test row is cleaned up by `deleteById` at the end.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">✅ Solution</summary>
  <div style="margin-top:0.8rem;">

```java
package t16_binary_io.exercises.e03;

import java.sql.*;
import java.util.*;

public class Exercise {

    public static void main(String[] args) throws Exception {
        String url  = "jdbc:mysql://localhost:3306/game_assets_db";
        String user = "root";
        String pass = "";

        GameAssetDao dao = new JdbcGameAssetDao(url, user, pass);

        byte[] original = new byte[]{10, 20, 30, 40, 50};
        GameAsset asset = new GameAsset(0, "test_sprite.bin", "application/octet-stream", original.length, original);

        int id = dao.insert(asset);
        System.out.println("Inserted asset with id=" + id);

        Optional<GameAsset> found = dao.findById(id);
        boolean ok = found.isPresent() && Arrays.equals(original, found.get().getAssetData());
        System.out.println("Round-trip OK: " + ok);

        System.out.println("Deleted: " + dao.deleteById(id));
    }
}

class GameAsset {

    // === Fields ===
    private int    _assetId;
    private String _assetName;
    private String _assetType;
    private int    _fileSize;
    private byte[] _assetData;

    // === Constructors ===
    // Creates: a validated game asset
    public GameAsset(int assetId, String assetName, String assetType, int fileSize, byte[] assetData) {
        if (assetId < 0)
            throw new IllegalArgumentException("assetId must be >= 0");
        if (assetName == null || assetName.isBlank())
            throw new IllegalArgumentException("assetName is required");
        if (assetType == null || assetType.isBlank())
            throw new IllegalArgumentException("assetType is required");
        if (fileSize < 0)
            throw new IllegalArgumentException("fileSize must be >= 0");
        if (assetData == null || assetData.length == 0)
            throw new IllegalArgumentException("assetData is required");

        _assetId   = assetId;
        _assetName = assetName.trim();
        _assetType = assetType.trim().toLowerCase();
        _fileSize  = fileSize;
        _assetData = assetData;
    }

    // === Public API ===
    public int    getAssetId()   { return _assetId; }
    public String getAssetName() { return _assetName; }
    public String getAssetType() { return _assetType; }
    public int    getFileSize()  { return _fileSize; }
    public byte[] getAssetData() { return _assetData; }
}

interface GameAssetDao {
    int insert(GameAsset asset) throws Exception;
    Optional<GameAsset> findById(int id) throws Exception;
    boolean deleteById(int id) throws Exception;
}

class JdbcGameAssetDao implements GameAssetDao {

    // === Fields ===
    private String _url;
    private String _user;
    private String _pass;

    // === Constructors ===
    // Creates: a DAO backed by the given database connection details
    public JdbcGameAssetDao(String url, String user, String pass) {
        if (url == null || url.isBlank())
            throw new IllegalArgumentException("url is required");
        _url  = url;
        _user = user;
        _pass = pass;
    }

    // === Public API ===
    // Inserts: an asset record and returns the auto-generated ID
    @Override
    public int insert(GameAsset asset) throws Exception {
        if (asset == null)
            throw new IllegalArgumentException("asset is required");

        String sql = "INSERT INTO game_assets (asset_name, asset_type, file_size, asset_data) VALUES (?, ?, ?, ?)";

        try (Connection c = open();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, asset.getAssetName());
            ps.setString(2, asset.getAssetType());
            ps.setInt(3, asset.getFileSize());
            ps.setBytes(4, asset.getAssetData());   // <-- BLOB

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

    // Gets: the full asset record including binary data
    @Override
    public Optional<GameAsset> findById(int id) throws Exception {
        if (id <= 0)
            return Optional.empty();

        String sql = "SELECT asset_id, asset_name, asset_type, file_size, asset_data FROM game_assets WHERE asset_id = ?";

        try (Connection c = open();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return Optional.empty();

                int    assetId   = rs.getInt("asset_id");
                String assetName = rs.getString("asset_name");
                String assetType = rs.getString("asset_type");
                int    fileSize  = rs.getInt("file_size");
                byte[] assetData = rs.getBytes("asset_data");  // <-- BLOB

                return Optional.of(new GameAsset(assetId, assetName, assetType, fileSize, assetData));
            }
        }
    }

    // Deletes: an asset record by ID
    @Override
    public boolean deleteById(int id) throws Exception {
        if (id <= 0)
            return false;

        String sql = "DELETE FROM game_assets WHERE asset_id = ?";

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
}
```

  </div>
</details>

---

## Exercise 04 — Send a binary file from client to server (upload)

**Objective:** Write a minimal upload server that receives a Base64-encoded file over a socket, decodes it, and stores the bytes in the database using `PreparedStatement.setBytes()`. The client reads a file from disk, encodes it, and sends it as a JSON request.

**Context (software + games):**

- **Software dev:** Every file-upload feature — document storage, profile image upload, backup — follows this pattern: client encodes → sends → server decodes → persists.
- **Games dev:** Uploading a player avatar, a custom map, or a replay file to a game backend uses exactly these steps.

### What you are building

- An `UploadServer` class with a `main()` method: starts on port `9_206`, accepts `UPLOAD_FILE` requests, decodes the Base64 payload, and stores the bytes with `setBytes()`
- An `UploadClient` class with a `main()` method: reads a synthetic file, encodes it, sends the upload request, and prints the returned auto-generated ID

### Required request/response shapes

```
UPLOAD_FILE
  request:  { "type": "UPLOAD_FILE",
               "payload": { "fileName":    "upload_test.bin",
                             "contentType": "application/octet-stream",
                             "fileSize":    512,
                             "fileData":    "<base64 string>" } }
  response: { "status": "OK", "id": <generated asset_id> }
```

### Tasks

1. Implement `UploadServer` — run this first:
   - Add a `main(String[] args)` method that prints a startup message and calls `new UploadServer(PORT, URL, DB_USER, DB_PASS).start()`.
   - Loop on `ServerSocket.accept()` until interrupted.
   - In `handleUpload(Socket)`: read one JSON line, extract `fileData`, decode with `Base64.getDecoder().decode(...)`, insert into `game_assets` with `ps.setBytes(4, bytes)`, return `{ "status": "OK", "id": <id> }`.

2. Implement `UploadClient` — run this second:
   - `main(String[] args)` creates a 512-byte synthetic `byte[]` (e.g. values `0–199` repeating) and writes it to `"data/upload_test.bin"`.
   - Connects to `UploadServer` on port `9_206`; Base64-encodes the bytes; builds and sends the request map.
   - Reads the response; parses the returned ID; prints it.
   - Cleans up: deletes the stored row via a direct JDBC `DELETE`.

### Sample output

```text
Upload OK — stored id: 12
Cleaned up id=12
```

### Constraints

- Base64-encode the `byte[]` before placing it in the JSON string — never embed raw bytes.
- Use `ps.setBytes(4, bytes)` in the server — not `ps.setString(...)`.
- Use `StandardCharsets.UTF_8` on all socket stream wrappers.
- Use `PreparedStatement` throughout — no SQL string concatenation.

### Done when…

- A positive integer ID is printed on every run.
- The row is no longer in the database after `deleteById`.
- Changing one byte in `original` before sending produces a different stored result (verify by running Exercise 07 on the same ID).

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">✅ Solution</summary>
  <div style="margin-top:0.8rem;">

**`UploadServer.java`** — server (start this first; accepts `UPLOAD_FILE` requests, Base64-decodes, stores with `setBytes()`)

```java
package t16_binary_io.exercises.e04;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class UploadServer {

    // === Constants ===
    private static final String URL     = "jdbc:mysql://localhost:3306/game_assets_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private static final int    PORT    = 9_206;

    // === Fields ===
    private int    _port;
    private String _url;
    private String _user;
    private String _pass;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // === Entry point ===
    // Starts: the upload server; run this class before running UploadClient
    public static void main(String[] args) throws Exception {
        System.out.println("UploadServer listening on port " + PORT + " ...");
        new UploadServer(PORT, URL, DB_USER, DB_PASS).start();
    }

    // === Constructors ===
    // Creates: an upload-only server bound to the given port and database
    public UploadServer(int port, String url, String user, String pass) {
        _port = port;
        _url  = url;
        _user = user;
        _pass = pass;
    }

    // === Public API ===
    // Starts: the server loop; accepts connections until interrupted
    public void start() throws Exception {
        try (ServerSocket ss = new ServerSocket(_port)) {
            while (!Thread.currentThread().isInterrupted())
                handleUpload(ss.accept());
        }
    }

    // === Helpers ===
    // Handles: one UPLOAD_FILE request — decodes Base64 payload and stores in game_assets
    private void handleUpload(Socket client) {
        try (client;
             BufferedReader in  = new BufferedReader(new InputStreamReader(client.getInputStream(),  StandardCharsets.UTF_8));
             PrintWriter    out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line = in.readLine();
            if (line == null) return;

            Map<?,?> req     = MAPPER.readValue(line, Map.class);
            Map<?,?> payload = (Map<?,?>) req.get("payload");

            String b64  = (String)  payload.get("fileData");
            String name = (String)  payload.get("fileName");
            String type = (String)  payload.get("contentType");
            int    size = ((Number) payload.get("fileSize")).intValue();
            byte[] data = Base64.getDecoder().decode(b64);

            int id = insertAsset(name, type, size, data);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", "OK");
            response.put("id",     id);
            out.println(MAPPER.writeValueAsString(response));

        } catch (Exception e) {
            System.err.println("Upload handler error: " + e.getMessage());
        }
    }

    // Inserts: a binary asset into game_assets; returns the auto-generated asset_id
    private int insertAsset(String name, String type, int size, byte[] data) throws Exception {
        String sql = "INSERT INTO game_assets (asset_name, asset_type, file_size, asset_data) VALUES (?, ?, ?, ?)";
        try (Connection        c  = DriverManager.getConnection(_url, _user, _pass);
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, type);
            ps.setInt(3,    size);
            ps.setBytes(4,  data);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next())
                    throw new IllegalStateException("no generated key returned");
                return keys.getInt(1);
            }
        }
    }
}
```

**`UploadClient.java`** — client (start this second; creates the test file, connects to `UploadServer`, uploads, verifies the returned ID)

```java
package t16_binary_io.exercises.e04;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class UploadClient {

    private static final ObjectMapper MAPPER  = new ObjectMapper();
    private static final int          PORT    = 9_206;

    // Entry point: create the test file, upload to UploadServer, verify the returned ID
    public static void main(String[] args) throws Exception {
        // Create a synthetic 512-byte test file
        byte[] original = new byte[512];
        for (int i = 0; i < original.length; i++) original[i] = (byte)(i % 200);

        Files.createDirectories(Path.of("data"));
        Files.write(Path.of("data/upload_test.bin"), original);

        // Base64-encode the file and send an UPLOAD_FILE request
        int storedId;
        try (Socket         socket = new Socket("localhost", PORT);
             BufferedReader in     = new BufferedReader(new InputStreamReader(socket.getInputStream(),  StandardCharsets.UTF_8));
             PrintWriter    out    = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String encoded = Base64.getEncoder().encodeToString(original);

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("fileName",    "upload_test.bin");
            payload.put("contentType", "application/octet-stream");
            payload.put("fileSize",    original.length);
            payload.put("fileData",    encoded);

            Map<String, Object> request = new LinkedHashMap<>();
            request.put("type",    "UPLOAD_FILE");
            request.put("payload", payload);

            out.println(MAPPER.writeValueAsString(request));

            String   responseJson = in.readLine();
            Map<?,?> response     = MAPPER.readValue(responseJson, Map.class);
            storedId = ((Number) response.get("id")).intValue();
            System.out.println("Upload OK — stored id: " + storedId);
        }
    }
}
```

  </div>
</details>

---

## Exercise 05 — Retrieve a stored file from a server (download)

**Objective:** Write a retrieve server that fetches a `MEDIUMBLOB` from the database using `ResultSet.getBytes()`, Base64-encodes it, and sends it back to the client as a JSON response. The client decodes the Base64 string and reconstructs the file on disk, then verifies byte-for-byte equality with `Arrays.equals()`.

**Context (software + games):**

- **Software dev:** Any system that serves stored files — a CDN origin, a document API, a media server — follows this retrieval pattern on the server side.
- **Games dev:** A game client downloading a purchased skin, a saved level, or a leaderboard replay receives exactly this kind of binary payload in a JSON envelope.

### What you are building

- A `RetrieveServer` class with a `main()` method: starts on port `9_207`, accepts `RETRIEVE_FILE` requests, calls `rs.getBytes("asset_data")`, and returns the bytes Base64-encoded in a JSON response
- A `RetrieveClient` class with a `main()` method: seeds the database with a known test asset via JDBC, connects to the server, decodes the response, writes the file to disk, and prints an integrity check

### Required request/response shapes

```
RETRIEVE_FILE
  request:  { "type": "RETRIEVE_FILE", "payload": { "id": <asset_id> } }
  response: { "status": "OK",
               "data": { "id":          <asset_id>,
                          "fileName":    "retrieve_test.bin",
                          "contentType": "application/octet-stream",
                          "fileSize":    512,
                          "fileData":    "<base64 string>" } }
```

### Tasks

1. Implement `RetrieveServer` — run this first:
   - Add a `main(String[] args)` method that prints a startup message and calls `new RetrieveServer(PORT, URL, DB_USER, DB_PASS).start()`.
   - Loop on `ServerSocket.accept()` until interrupted.
   - In `handleRetrieve(Socket)`: read one JSON line, extract `id`, run `SELECT ... asset_data ... WHERE asset_id = ?`, call `rs.getBytes("asset_data")`, Base64-encode the result, return the full data map.
   - If no row is found for the given ID, return `{ "status": "ERROR", "message": "not found" }`.

2. Implement `RetrieveClient` — run this second:
   - `main(String[] args)` creates a known 512-byte `byte[]` (values `0–199` repeating).
   - Inserts it directly into `game_assets` via JDBC (`setBytes()`); captures the returned ID.
   - Connects a socket to `RetrieveServer` on port `9_207`; sends `{ "type": "RETRIEVE_FILE", "payload": { "id": <testId> } }`.
   - Reads the response; extracts `fileData`; Base64-decodes to `byte[]`; writes to `"data/retrieved_test.bin"`.
   - Prints the filename, byte count, and `Arrays.equals(original, downloaded)`.
   - Cleans up: deletes the test row.

### Sample output

```text
Pre-inserted test asset — id: 8
Retrieved: retrieve_test.bin (512 bytes)
Integrity check: true
Cleaned up id=8
```

### Constraints

- Use `rs.getBytes("asset_data")` — not `rs.getString(...)`.
- Use `Base64.getEncoder().encodeToString(bytes)` in the server; `Base64.getDecoder().decode(...)` in the client.
- Use `Arrays.equals(original, downloaded)` — not `==`.
- Use `StandardCharsets.UTF_8` on all socket stream wrappers.

### Done when…

- `"Integrity check: true"` prints on every run.
- `data/retrieved_test.bin` exists on disk and matches the original byte-for-byte.
- Running the server with a non-existent ID (e.g. id=`999_999`) returns an error response rather than throwing an exception.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">✅ Solution</summary>
  <div style="margin-top:0.8rem;">

**`RetrieveServer.java`** — server (start this first; accepts `RETRIEVE_FILE` requests, fetches the BLOB with `getBytes()`, Base64-encodes, returns in JSON)

```java
package t16_binary_io.exercises.e05;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class RetrieveServer {

    // === Constants ===
    private static final String URL     = "jdbc:mysql://localhost:3306/game_assets_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private static final int    PORT    = 9_207;

    // === Fields ===
    private int    _port;
    private String _url;
    private String _user;
    private String _pass;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // === Entry point ===
    // Starts: the retrieve server; run this class before running RetrieveClient
    public static void main(String[] args) throws Exception {
        System.out.println("RetrieveServer listening on port " + PORT + " ...");
        new RetrieveServer(PORT, URL, DB_USER, DB_PASS).start();
    }

    // === Constructors ===
    // Creates: a retrieve-only server bound to the given port and database
    public RetrieveServer(int port, String url, String user, String pass) {
        _port = port;
        _url  = url;
        _user = user;
        _pass = pass;
    }

    // === Public API ===
    // Starts: the server loop; accepts connections until interrupted
    public void start() throws Exception {
        try (ServerSocket ss = new ServerSocket(_port)) {
            while (!Thread.currentThread().isInterrupted())
                handleRetrieve(ss.accept());
        }
    }

    // === Helpers ===
    // Handles: one RETRIEVE_FILE request — fetches the BLOB and Base64-encodes it for the client
    private void handleRetrieve(Socket client) {
        try (client;
             BufferedReader in  = new BufferedReader(new InputStreamReader(client.getInputStream(),  StandardCharsets.UTF_8));
             PrintWriter    out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line = in.readLine();
            if (line == null) return;

            Map<?,?> req     = MAPPER.readValue(line, Map.class);
            Map<?,?> payload = (Map<?,?>) req.get("payload");
            int      id      = ((Number) payload.get("id")).intValue();

            String sql = "SELECT asset_name, asset_type, file_size, asset_data "
                       + "FROM game_assets WHERE asset_id = ?";

            try (Connection        c  = DriverManager.getConnection(_url, _user, _pass);
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        out.println(MAPPER.writeValueAsString(
                            Map.of("status", "ERROR", "message", "not found id=" + id)));
                        return;
                    }

                    String name  = rs.getString("asset_name");
                    String type  = rs.getString("asset_type");
                    int    size  = rs.getInt("file_size");
                    byte[] bytes = rs.getBytes("asset_data");   // load the BLOB

                    Map<String, Object> data = new LinkedHashMap<>();
                    data.put("id",          id);
                    data.put("fileName",    name);
                    data.put("contentType", type);
                    data.put("fileSize",    size);
                    data.put("fileData",    Base64.getEncoder().encodeToString(bytes));

                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("status", "OK");
                    response.put("data",   data);
                    out.println(MAPPER.writeValueAsString(response));
                }
            }

        } catch (Exception e) {
            System.err.println("Retrieve handler error: " + e.getMessage());
        }
    }
}
```

**`RetrieveClient.java`** — client (start this second; seeds the DB via JDBC, connects to `RetrieveServer`, decodes the response, verifies integrity)

```java
package t16_binary_io.exercises.e05;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class RetrieveClient {

    private static final ObjectMapper MAPPER  = new ObjectMapper();
    private static final int          PORT    = 9_207;

    // Entry point: seed the DB, download from RetrieveServer, verify byte-for-byte integrity
    public static void main(String[] args) throws Exception {
        // Retrieve row with ID=1
        int testId = 1;

        // Send a RETRIEVE_FILE request and reconstruct the file on disk
        try (Socket         socket = new Socket("localhost", PORT);
             BufferedReader in     = new BufferedReader(new InputStreamReader(socket.getInputStream(),  StandardCharsets.UTF_8));
             PrintWriter    out    = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            Map<String, Object> request = new LinkedHashMap<>();
            request.put("type",    "RETRIEVE_FILE");
            request.put("payload", Map.of("id", testId));

            out.println(MAPPER.writeValueAsString(request));

            String   responseJson = in.readLine();
            Map<?,?> response     = MAPPER.readValue(responseJson, Map.class);
            Map<?,?> data         = (Map<?,?>) response.get("data");

            byte[] downloaded = Base64.getDecoder().decode((String) data.get("fileData"));

            Files.createDirectories(Path.of("data"));
            Files.write(Path.of("data/retrieved_test.bin"), downloaded);

            System.out.println("Retrieved: " + data.get("fileName") + " (" + downloaded.length + " bytes)");
            System.out.println("Integrity check: " + Arrays.equals(original, downloaded));
        }
    }
}
```

  </div>
</details>

---

## Exercise 06 — Request file metadata without loading the BLOB

**Objective:** Write a metadata server whose SQL deliberately omits the `asset_data` column. The server returns only the filename, content type, and file size. The client prints the metadata and confirms that no binary data was transferred.

**Context (software + games):**

- **Software dev:** A file manager showing a directory listing, a search results page, or a pagination endpoint must return metadata cheaply — fetching 500 BLOBs to render a list of filenames would be unusably slow.
- **Games dev:** An in-game asset browser showing a list of downloadable skins or maps loads thumbnails and names first; the binary payload is fetched only when the player clicks download.

### What you are building

- A `MetadataServer` class with a `main()` method: starts on port `9_208`, handles `GET_METADATA` requests using a SELECT that **omits `asset_data`**
- A `MetadataClient` class with a `main()` method: seeds the database with a test asset via JDBC, requests metadata from the server, and confirms the response contains no `fileData` field

### Required request/response shapes

```
GET_METADATA
  request:  { "type": "GET_METADATA", "payload": { "id": <asset_id> } }
  response: { "status": "OK",
               "data": { "id":          <asset_id>,
                          "fileName":    "hero_sprite.png",
                          "contentType": "image/png",
                          "fileSize":    2048 } }
```

Note: the response **does not contain a `fileData` field** — the BLOB is never fetched.

### Tasks

1. Implement `MetadataServer` — run this first:
   - Add a `main(String[] args)` method that prints a startup message and calls `new MetadataServer(PORT, URL, DB_USER, DB_PASS).start()`.
   - Loop on `ServerSocket.accept()` until interrupted.
   - In `handleMetadata(Socket)`: write the SQL as `SELECT asset_name, asset_type, file_size FROM game_assets WHERE asset_id = ?` — `asset_data` must not appear anywhere in the query string.
   - Return the metadata map on success; `{ "status": "ERROR", "message": "not found" }` if the row does not exist.

2. Implement `MetadataClient` — run this second:
   - `main(String[] args)` creates a 2 048-byte `byte[]` (e.g. `Arrays.fill(data, (byte) 42)`).
   - Inserts it directly into `game_assets` via JDBC; captures the returned ID.
   - Connects a socket to `MetadataServer` on port `9_208`; sends `{ "type": "GET_METADATA", "payload": { "id": <testId> } }`.
   - Reads the response; parses the `data` map; prints `fileName`, `contentType`, `fileSize`.
   - Asserts the response map does **not** contain a key `"fileData"`.
   - Cleans up: deletes the test row.

### Sample output

```text
Pre-inserted asset — id: 15
File name:    hero_sprite.png
Content type: image/png
File size:    2048 bytes
fileData absent from response: true
Cleaned up id=15
```

### Constraints

- `asset_data` must not appear in the server's SELECT statement — verify this by reading the SQL string in your solution.
- Use `PreparedStatement` — no SQL string concatenation.
- Use `StandardCharsets.UTF_8` on all socket stream wrappers.

### Done when…

- The four metadata fields print correctly on every run.
- `"fileData absent from response: true"` prints — the response map must not contain that key.
- Requesting a non-existent ID returns an error response, not an exception.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">✅ Solution</summary>
  <div style="margin-top:0.8rem;">

**`MetadataServer.java`** — server (start this first; handles `GET_METADATA` requests; `asset_data` is deliberately absent from the SELECT)

```java
package t16_binary_io.exercises.e06;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class MetadataServer {

    // === Constants ===
    private static final String URL     = "jdbc:mysql://localhost:3306/game_assets_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private static final int    PORT    = 9_208;

    // === Fields ===
    private int    _port;
    private String _url;
    private String _user;
    private String _pass;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // === Entry point ===
    // Starts: the metadata server; run this class before running MetadataClient
    public static void main(String[] args) throws Exception {
        System.out.println("MetadataServer listening on port " + PORT + " ...");
        new MetadataServer(PORT, URL, DB_USER, DB_PASS).start();
    }

    // === Constructors ===
    // Creates: a metadata-only server bound to the given port and database
    public MetadataServer(int port, String url, String user, String pass) {
        _port = port;
        _url  = url;
        _user = user;
        _pass = pass;
    }

    // === Public API ===
    // Starts: the server loop; accepts connections until interrupted
    public void start() throws Exception {
        try (ServerSocket ss = new ServerSocket(_port)) {
            while (!Thread.currentThread().isInterrupted())
                handleMetadata(ss.accept());
        }
    }

    // === Helpers ===
    // Handles: one GET_METADATA request — SELECTs metadata columns only; asset_data is never fetched
    private void handleMetadata(Socket client) {
        try (client;
             BufferedReader in  = new BufferedReader(new InputStreamReader(client.getInputStream(),  StandardCharsets.UTF_8));
             PrintWriter    out = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line = in.readLine();
            if (line == null) return;

            Map<?,?> req     = MAPPER.readValue(line, Map.class);
            Map<?,?> payload = (Map<?,?>) req.get("payload");
            int      id      = ((Number) payload.get("id")).intValue();

            // asset_data deliberately excluded from this SELECT
            String sql = "SELECT asset_name, asset_type, file_size "
                       + "FROM game_assets WHERE asset_id = ?";

            try (Connection        c  = DriverManager.getConnection(_url, _user, _pass);
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        out.println(MAPPER.writeValueAsString(
                            Map.of("status", "ERROR", "message", "not found id=" + id)));
                        return;
                    }

                    Map<String, Object> metadata = new LinkedHashMap<>();
                    metadata.put("id",          id);
                    metadata.put("fileName",    rs.getString("asset_name"));
                    metadata.put("contentType", rs.getString("asset_type"));
                    metadata.put("fileSize",    rs.getInt("file_size"));

                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("status", "OK");
                    response.put("data",   metadata);
                    out.println(MAPPER.writeValueAsString(response));
                }
            }

        } catch (Exception e) {
            System.err.println("Metadata handler error: " + e.getMessage());
        }
    }
}
```

**`MetadataClient.java`** — client (start this second; seeds the DB via JDBC, connects to `MetadataServer`, prints metadata, asserts no binary payload)

```java
package t16_binary_io.exercises.e06;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

public class MetadataClient {

    private static final ObjectMapper MAPPER  = new ObjectMapper();
    private static final int          PORT    = 9_208;

    // Entry point: seed the DB, request metadata from MetadataServer, assert no fileData in response
    public static void main(String[] args) throws Exception {
       // Retrieve row with ID=1
        int testId = 1;

        // Request metadata only — no binary payload
        try (Socket         socket = new Socket("localhost", PORT);
             BufferedReader in     = new BufferedReader(new InputStreamReader(socket.getInputStream(),  StandardCharsets.UTF_8));
             PrintWriter    out    = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            Map<String, Object> request = new LinkedHashMap<>();
            request.put("type",    "GET_METADATA");
            request.put("payload", Map.of("id", testId));

            out.println(MAPPER.writeValueAsString(request));

            String   responseJson = in.readLine();
            Map<?,?> response     = MAPPER.readValue(responseJson, Map.class);
            Map<?,?> metadata     = (Map<?,?>) response.get("data");

            System.out.println("File name:    " + metadata.get("fileName"));
            System.out.println("Content type: " + metadata.get("contentType"));
            System.out.println("File size:    " + metadata.get("fileSize") + " bytes");
            System.out.println("fileData absent from response: " + !metadata.containsKey("fileData"));
        }
    }
}
```

  </div>
</details>

---

## Lesson Context
```yaml
linked_lesson:
  topic_code: "t16_binary_io"
  lesson_path: "/notes/topics/t16_binary_io/t16_binary_io_notes.md"
  primary_domain_emphasis: "Balanced"
  difficulty_tier: "Intermediate"
```
