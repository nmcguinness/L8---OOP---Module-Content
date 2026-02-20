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

Each exercise uses:

- **Folder:** `/exercises/topics/t16_binary_io/exercises/eXX/`
- **Filename:** `Exercise.java`
- **Package:** `t16_binary_io.exercises.eXX`

```java
package t16_binary_io.exercises.e01;

public class Exercise {
    public static void run() throws Exception {
        // your code here
    }
}
```

## Before you start

### Prerequisites checklist

- [ ] Completed the Binary I/O notes
- [ ] Completed the DB Connectivity / DAO exercises (t12)
- [ ] You understand `PreparedStatement` and `ResultSet`
- [ ] You have a working MySQL database from the DAO exercises

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

    public static void run() throws Exception {
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

## Exercise 02 — Extend the schema with a BLOB table

**Objective:** Add a `game_assets` table to your MySQL database with a `MEDIUMBLOB` column for binary data and metadata columns for name, type, and size. Verify the schema with a smoke test.

**Context (software + games):**

- **Software dev:** Schema design separates metadata (cheap to query) from binary payload (expensive to load). Getting this right at the schema level prevents costly migrations later.
- **Games dev:** Asset management systems (Unity AssetBundles, Unreal pak files) always separate asset metadata from binary content in their databases.

### What you are building

- A `game_assets` table in MySQL
- A smoke test that inserts a tiny asset and reads the metadata back

### Tasks

1. In phpMyAdmin, run this SQL against your existing database:

   ```sql
   CREATE TABLE game_assets (
       asset_id      INT            NOT NULL AUTO_INCREMENT,
       asset_name    VARCHAR(255)   NOT NULL,
       asset_type    VARCHAR(50)    NOT NULL,
       file_size     INT            NOT NULL,
       asset_data    MEDIUMBLOB     NOT NULL,
       uploaded_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
       PRIMARY KEY (asset_id)
   );
   ```

2. Insert one row manually in phpMyAdmin using SQL (use a small hex literal for the BLOB):

   ```sql
   INSERT INTO game_assets (asset_name, asset_type, file_size, asset_data)
   VALUES ('hero_sprite.png', 'image/png', 4, 0x89504E47);
   ```

3. In `Exercise.run()`:
   - Connect with JDBC.
   - Run `SELECT asset_id, asset_name, asset_type, file_size FROM game_assets` (no `asset_data`).
   - Print each row: `"[id] name (type, N bytes)"`.
   - Run `SELECT COUNT(*) FROM game_assets` and print it.

### Sample output

```text
[1] hero_sprite.png (image/png, 4 bytes)
Total assets: 1
```

### Constraints

- The metadata `SELECT` must not include `asset_data` — prove you can query metadata cheaply.
- Use `PreparedStatement` even for the count query.

### Done when…

- The row inserted via phpMyAdmin appears in the Java output.
- No BLOB data is loaded by the metadata query.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">✅ Solution</summary>
  <div style="margin-top:0.8rem;">

```java
package t16_binary_io.exercises.e02;

import java.sql.*;

public class Exercise {

    public static void run() throws Exception {
        String url  = "jdbc:mysql://localhost:3306/car_rental?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "car_rental_user";
        String pass = "your_password";

        try (Connection c = DriverManager.getConnection(url, user, pass)) {

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

    public static void run() throws Exception {
        String url  = "jdbc:mysql://localhost:3306/car_rental?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "car_rental_user";
        String pass = "your_password";

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

## Exercise 04 — Metadata-only DAO: the case for excluding the BLOB

**Objective:** Add `findMetadataById(int id)` and `findAllMetadata()` to the DAO — queries that deliberately exclude the `asset_data` column. Compare the cost of both queries by timing them.

**Context (software + games):**

- **Software dev:** A document library or media server never loads binary content just to display a file list. Metadata-only queries are a fundamental performance pattern.
- **Games dev:** A game asset browser shows thumbnails and names before the player chooses to download a full asset. The same principle applies.

### What you are building

- `AssetMetadata` value class (assetId, assetName, assetType, fileSize) — no `byte[]`
- `findMetadataById(int id)` — SELECT without `asset_data`
- `findAllMetadata()` — SELECT all rows without `asset_data`
- A timing comparison between a full `findById` and a metadata-only `findMetadataById`

### Required API

```java
public class AssetMetadata {
    public AssetMetadata(int assetId, String assetName, String assetType, int fileSize);
    // getters; toString()
}

// Add to GameAssetDao:
Optional<AssetMetadata> findMetadataById(int id) throws Exception;
List<AssetMetadata> findAllMetadata() throws Exception;
```

### Tasks

1. Implement `AssetMetadata` as a simple value class (no `assetData` field).
2. Add `findMetadataById` and `findAllMetadata` to `JdbcGameAssetDao`:
   - Both `SELECT` statements must list columns explicitly and **omit `asset_data`**.
3. In `Exercise.run()`:
   - Insert three assets with large-ish synthetic byte arrays (e.g., `new byte[50_000]`).
   - Time a full `findById` on one asset: record start/end with `System.nanoTime()`.
   - Time a `findMetadataById` on the same ID.
   - Print both times in milliseconds.
   - Print all metadata with `findAllMetadata()`.
   - Clean up (delete the three test rows).

### Sample output

```text
Full findById:      12 ms
Metadata only:       1 ms
[1] hero_sprite.png (image/png, 50000 bytes)
[2] player_map.dat  (application/octet-stream, 50000 bytes)
[3] theme.ogg       (audio/ogg, 50000 bytes)
Cleaned up 3 rows
```

### Constraints

- Both SELECT statements must be written explicitly — do not use `SELECT *`.
- `AssetMetadata` must not have a `getAssetData()` method — enforce the separation at compile time.

### Done when…

- `findAllMetadata()` does not load any BLOB data (prove it by checking the SQL).
- The metadata query is measurably faster than the full query (may require larger byte arrays to see a clear difference).
- All test rows are cleaned up at the end.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">✅ Solution</summary>
  <div style="margin-top:0.8rem;">

```java
package t16_binary_io.exercises.e04;

import java.sql.*;
import java.util.*;

public class Exercise {

    public static void run() throws Exception {
        String url  = "jdbc:mysql://localhost:3306/car_rental?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "car_rental_user";
        String pass = "your_password";

        JdbcGameAssetDao dao = new JdbcGameAssetDao(url, user, pass);

        byte[] large = new byte[50_000];

        int id1 = dao.insert(new GameAsset(0, "hero_sprite.png", "image/png",                  large.length, large));
        int id2 = dao.insert(new GameAsset(0, "player_map.dat",  "application/octet-stream",   large.length, large));
        int id3 = dao.insert(new GameAsset(0, "theme.ogg",       "audio/ogg",                  large.length, large));

        // Time full retrieval
        long startFull = System.nanoTime();
        dao.findById(id1);
        long msFull = (System.nanoTime() - startFull) / 1_000_000;
        System.out.println("Full findById:     " + msFull + " ms");

        // Time metadata retrieval
        long startMeta = System.nanoTime();
        dao.findMetadataById(id1);
        long msMeta = (System.nanoTime() - startMeta) / 1_000_000;
        System.out.println("Metadata only:     " + msMeta + " ms");

        for (AssetMetadata m : dao.findAllMetadata()) {
            System.out.println("[" + m.getAssetId() + "] " + m.getAssetName()
                + " (" + m.getAssetType() + ", " + m.getFileSize() + " bytes)");
        }

        dao.deleteById(id1);
        dao.deleteById(id2);
        dao.deleteById(id3);
        System.out.println("Cleaned up 3 rows");
    }
}

class AssetMetadata {

    // === Fields ===
    private int    _assetId;
    private String _assetName;
    private String _assetType;
    private int    _fileSize;

    // === Constructors ===
    // Creates: an asset metadata record — no binary data
    public AssetMetadata(int assetId, String assetName, String assetType, int fileSize) {
        _assetId   = assetId;
        _assetName = assetName;
        _assetType = assetType;
        _fileSize  = fileSize;
    }

    // === Public API ===
    public int    getAssetId()   { return _assetId; }
    public String getAssetName() { return _assetName; }
    public String getAssetType() { return _assetType; }
    public int    getFileSize()  { return _fileSize; }
}

// JdbcGameAssetDao — additions (findMetadataById + findAllMetadata):

// Gets: metadata only for the given ID — asset_data is not loaded
// public Optional<AssetMetadata> findMetadataById(int id) throws Exception {
//     if (id <= 0) return Optional.empty();
//     String sql = "SELECT asset_id, asset_name, asset_type, file_size FROM game_assets WHERE asset_id = ?";
//     try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql)) {
//         ps.setInt(1, id);
//         try (ResultSet rs = ps.executeQuery()) {
//             if (!rs.next()) return Optional.empty();
//             return Optional.of(new AssetMetadata(
//                 rs.getInt("asset_id"), rs.getString("asset_name"),
//                 rs.getString("asset_type"), rs.getInt("file_size")));
//         }
//     }
// }

// Gets: metadata for all assets — asset_data is not loaded
// public List<AssetMetadata> findAllMetadata() throws Exception {
//     String sql = "SELECT asset_id, asset_name, asset_type, file_size FROM game_assets ORDER BY asset_id";
//     try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
//         List<AssetMetadata> out = new ArrayList<>();
//         while (rs.next())
//             out.add(new AssetMetadata(
//                 rs.getInt("asset_id"), rs.getString("asset_name"),
//                 rs.getString("asset_type"), rs.getInt("file_size")));
//         return out;
//     }
// }

// Full JdbcGameAssetDao available in Exercise 03 — add these two methods to it.
class JdbcGameAssetDao {
    private String _url, _user, _pass;
    public JdbcGameAssetDao(String url, String user, String pass) { _url=url; _user=user; _pass=pass; }
    private Connection open() throws SQLException { return DriverManager.getConnection(_url,_user,_pass); }
    public int insert(GameAsset asset) throws Exception {
        String sql = "INSERT INTO game_assets (asset_name, asset_type, file_size, asset_data) VALUES (?, ?, ?, ?)";
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, asset.getAssetName()); ps.setString(2, asset.getAssetType());
            ps.setInt(3, asset.getFileSize()); ps.setBytes(4, asset.getAssetData());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { keys.next(); return keys.getInt(1); }
        }
    }
    public Optional<GameAsset> findById(int id) throws Exception {
        if (id <= 0) return Optional.empty();
        String sql = "SELECT asset_id, asset_name, asset_type, file_size, asset_data FROM game_assets WHERE asset_id = ?";
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new GameAsset(rs.getInt("asset_id"), rs.getString("asset_name"),
                    rs.getString("asset_type"), rs.getInt("file_size"), rs.getBytes("asset_data")));
            }
        }
    }
    public Optional<AssetMetadata> findMetadataById(int id) throws Exception {
        if (id <= 0) return Optional.empty();
        String sql = "SELECT asset_id, asset_name, asset_type, file_size FROM game_assets WHERE asset_id = ?";
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new AssetMetadata(rs.getInt("asset_id"), rs.getString("asset_name"),
                    rs.getString("asset_type"), rs.getInt("file_size")));
            }
        }
    }
    public List<AssetMetadata> findAllMetadata() throws Exception {
        String sql = "SELECT asset_id, asset_name, asset_type, file_size FROM game_assets ORDER BY asset_id";
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<AssetMetadata> out = new ArrayList<>();
            while (rs.next())
                out.add(new AssetMetadata(rs.getInt("asset_id"), rs.getString("asset_name"),
                    rs.getString("asset_type"), rs.getInt("file_size")));
            return out;
        }
    }
    public boolean deleteById(int id) throws Exception {
        if (id <= 0) return false;
        String sql = "DELETE FROM game_assets WHERE asset_id = ?";
        try (Connection c = open(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id); return ps.executeUpdate() == 1;
        }
    }
}

class GameAsset {
    private int _assetId; private String _assetName, _assetType; private int _fileSize; private byte[] _assetData;
    public GameAsset(int id, String name, String type, int size, byte[] data) {
        _assetId=id; _assetName=name.trim(); _assetType=type.trim().toLowerCase(); _fileSize=size; _assetData=data;
    }
    public int getAssetId() { return _assetId; }
    public String getAssetName() { return _assetName; }
    public String getAssetType() { return _assetType; }
    public int getFileSize() { return _fileSize; }
    public byte[] getAssetData() { return _assetData; }
}
```

  </div>
</details>

---

## Exercise 05 — Base64 encoding and socket protocol integration

**Objective:** Encode a binary asset as Base64 to embed it in a JSON string, then send it over a socket using the leaderboard server pattern from t15. The receiver decodes the Base64, stores the bytes in the database, and retrieves them to prove byte-for-byte integrity.

**Context (software + games):**

- **Software dev:** REST APIs, S3 pre-signed uploads, and email attachments all use Base64 to carry binary data through text-based protocols.
- **Games dev:** Game clients uploading screenshots, replays, or user-generated content to a backend use Base64 encoding over an HTTP or TCP channel.

### What you are building

- `ASSET_UPLOAD` and `ASSET_DOWNLOAD` request handlers in a small server
- Base64 encode on the client side; Base64 decode on the server side
- End-to-end integrity check: bytes in == bytes out

### Required request/response shapes

```
ASSET_UPLOAD
  request:  { "requestType": "ASSET_UPLOAD",
               "payload": { "assetName": "hero.bin",
                            "assetType": "image/png",
                            "assetData": "<base64 string>" } }
  response: ServerResponse<AssetMetadata> — metadata of the stored asset

ASSET_DOWNLOAD
  request:  { "requestType": "ASSET_DOWNLOAD", "payload": { "id": 3 } }
  response: ServerResponse<Map> — { assetId, assetName, assetType, assetData (base64) }

ASSET_METADATA
  request:  { "requestType": "ASSET_METADATA", "payload": { "id": 3 } }
  response: ServerResponse<AssetMetadata> — no binary data
```

### Tasks

1. Reuse `GameAsset`, `AssetMetadata`, `JdbcGameAssetDao`, and `ServerResponse<T>` from earlier exercises.
2. Implement an `AssetServer` (modelled on `LeaderboardServer` from t15) with a `ClientHandler` that dispatches:
   - `ASSET_UPLOAD`: decode Base64 from payload → create `GameAsset` → `dao.insert()` → return metadata
   - `ASSET_DOWNLOAD`: `dao.findById(id)` → Base64-encode `assetData` → return in a map
   - `ASSET_METADATA`: `dao.findMetadataById(id)` → return metadata
3. Implement an `AssetClient` with methods:
   - `upload(out, in, filePath)` — reads file, encodes to Base64, sends `ASSET_UPLOAD`
   - `download(out, in, id, savePath)` — sends `ASSET_DOWNLOAD`, decodes Base64, writes to disk
4. In `Exercise.run()`:
   - Start `AssetServer` (port 9200) on a daemon thread.
   - Client: create a synthetic 1,024-byte test file and upload it. Print the returned metadata.
   - Client: download by the returned ID and save to `"data/downloaded_asset.bin"`.
   - Read both files and print: `"Integrity check: true/false"`.
   - Clean up the database row.

### Sample output

```text
Uploaded: AssetMetadata{id=7, name=test_asset.bin, type=application/octet-stream, size=1024}
Downloaded to: data/downloaded_asset.bin (1024 bytes)
Integrity check: true
Cleaned up id=7
```

### Constraints

- Use `Base64.getEncoder().encodeToString(bytes)` before sending.
- Use `Base64.getDecoder().decode(string)` when receiving.
- Use `Arrays.equals(original, downloaded)` for the integrity check.
- The server must never load the BLOB when handling `ASSET_METADATA`.

### Done when…

- `"Integrity check: true"` prints every time.
- `ASSET_METADATA` returns correctly without loading the BLOB (check the SQL).
- A deliberately corrupted Base64 string (e.g. inject a bad character) causes a structured `ServerResponse.error(...)` rather than an exception propagating to the client.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">✅ Solution (key sections)</summary>
  <div style="margin-top:0.8rem;">

```java
package t16_binary_io.exercises.e05;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class Exercise {

    public static void run() throws Exception {
        JdbcGameAssetDao dao = new JdbcGameAssetDao(
            "jdbc:mysql://localhost:3306/car_rental?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
            "car_rental_user", "your_password");

        Thread st = new Thread(() -> {
            try { new AssetServer(9_200, dao).start(); }
            catch (Exception e) { e.printStackTrace(); }
        });
        st.setDaemon(true);
        st.start();
        Thread.sleep(300);

        // Create a 1024-byte synthetic file
        byte[] original = new byte[1_024];
        for (int i = 0; i < original.length; i++) original[i] = (byte)(i % 127);
        BinaryFileUtil.writeFile("data/test_upload.bin", original);

        ObjectMapper mapper = new ObjectMapper();
        int uploadedId;

        // Upload
        try (Socket s = new Socket("localhost", 9_200);
             BufferedReader in  = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter    out = new PrintWriter(s.getOutputStream(), true)) {

            String encoded = Base64.getEncoder().encodeToString(original);
            Map<String, Object> req = new HashMap<>();
            req.put("requestType", "ASSET_UPLOAD");
            req.put("payload", Map.of("assetName", "test_asset.bin",
                                      "assetType", "application/octet-stream",
                                      "assetData", encoded));
            out.println(mapper.writeValueAsString(req));

            String reply = in.readLine();
            System.out.println("Uploaded: " + reply);

            // Extract the ID from the response
            Map<?,?> resp = mapper.readValue(reply, Map.class);
            Map<?,?> data = (Map<?,?>) resp.get("data");
            uploadedId = Integer.parseInt(data.get("assetId").toString());
        }

        // Download
        try (Socket s = new Socket("localhost", 9_200);
             BufferedReader in  = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter    out = new PrintWriter(s.getOutputStream(), true)) {

            Map<String, Object> req = new HashMap<>();
            req.put("requestType", "ASSET_DOWNLOAD");
            req.put("payload", Map.of("id", uploadedId));
            out.println(mapper.writeValueAsString(req));

            String reply = in.readLine();
            Map<?,?> resp = mapper.readValue(reply, Map.class);
            Map<?,?> data = (Map<?,?>) resp.get("data");

            byte[] downloaded = Base64.getDecoder().decode(data.get("assetData").toString());
            BinaryFileUtil.writeFile("data/downloaded_asset.bin", downloaded);

            System.out.println("Downloaded to: data/downloaded_asset.bin (" + downloaded.length + " bytes)");
            System.out.println("Integrity check: " + Arrays.equals(original, downloaded));
        }

        // Clean up
        dao.deleteById(uploadedId);
        System.out.println("Cleaned up id=" + uploadedId);
    }
}

// AssetServer follows the same structure as LeaderboardServer from t15.
// ClientHandler.dispatch() additions:

// ASSET_UPLOAD handler:
// String encoded  = req.getString("assetData");
// if (encoded == null || encoded.isBlank()) return ServerResponse.error("assetData is required");
// try {
//     byte[] data = Base64.getDecoder().decode(encoded);
//     String name = req.getString("assetName");
//     String type = req.getString("assetType");
//     GameAsset asset = new GameAsset(0, name, type != null ? type : "application/octet-stream", data.length, data);
//     int id = _dao.insert(asset);
//     Optional<AssetMetadata> meta = _dao.findMetadataById(id);
//     return meta.isPresent() ? ServerResponse.ok("asset uploaded", meta.get()) : ServerResponse.error("stored but not found");
// } catch (IllegalArgumentException e) {
//     return ServerResponse.error("invalid base64: " + e.getMessage());
// }

// ASSET_DOWNLOAD handler:
// int id = req.getInt("id");
// Optional<GameAsset> file = _dao.findById(id);
// if (file.isEmpty()) return ServerResponse.error("no asset with id=" + id);
// Map<String,Object> resp = new HashMap<>();
// resp.put("assetId",   file.get().getAssetId());
// resp.put("assetName", file.get().getAssetName());
// resp.put("assetType", file.get().getAssetType());
// resp.put("assetData", Base64.getEncoder().encodeToString(file.get().getAssetData()));
// return ServerResponse.ok("asset retrieved", resp);

// ASSET_METADATA handler:
// int id = req.getInt("id");
// Optional<AssetMetadata> meta = _dao.findMetadataById(id);
// if (meta.isEmpty()) return ServerResponse.error("no asset with id=" + id);
// return ServerResponse.ok("metadata retrieved", meta.get());
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
