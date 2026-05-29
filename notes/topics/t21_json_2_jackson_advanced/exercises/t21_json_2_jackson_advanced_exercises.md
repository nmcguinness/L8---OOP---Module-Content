# JSON in Java II: Protocol Design & Advanced Jackson — Exercises

## Exercise 01 — Request envelope

Define a `ClientRequest` class with fields `type` (String) and `payload` (JsonNode).

Write a `main` method that:
1. Builds a JSON string: `{"type":"GET_PLAYER","payload":{"id":42}}`.
2. Deserialises it to `ClientRequest`.
3. Reads `payload.get("id").asInt()` and prints the result.

**Package:** `t21_json_2_jackson_advanced.exercises.ex01`

---

## Exercise 02 — Request router

Define a `RequestHandler` functional interface with method `String handle(JsonNode payload)`.

Build a `Map<String, RequestHandler>` with two entries:

- `"PING"` → returns `{"status":"ok"}` regardless of payload.
- `"ECHO"` → returns the payload serialised back to a JSON string.

Write a dispatcher method and test both handler types.

**Package:** `t21_json_2_jackson_advanced.exercises.ex02`

---

## Exercise 03 — Base64 encode/decode

Write two methods:

```java
public static String encodeToBase64(byte[] data)
public static byte[] decodeFromBase64(String encoded)
```

Test with a short `byte[]` (e.g. the UTF-8 bytes of `"Hello, World!"`): encode, embed in a JSON string as a field, extract the field value, decode, and verify the result matches the original bytes.

**Package:** `t21_json_2_jackson_advanced.exercises.ex03`

---

## Exercise 04 — BLOB insert and retrieval (JDBC)

Given a `game_assets` table with a `MEDIUMBLOB` column `asset_data`:

1. Write a DAO method `void insertAsset(int id, String name, byte[] data)` using `PreparedStatement.setBytes()`.
2. Write a DAO method `byte[] getAsset(int id)` using `ResultSet.getBytes()`.
3. Write a metadata-only query method `List<AssetMetadata> getAllMetadata()` that selects `id` and `name` only — no BLOB column.

**Package:** `t21_json_2_jackson_advanced.exercises.ex04`

---

## Exercise 05 — JSON over socket (round-trip)

Implement a single-threaded echo server that:
1. Accepts one connection.
2. Reads a JSON string from the client.
3. Deserialises it as a `ClientRequest`.
4. Responds with a `ServerResponse` serialised to JSON, with `success: true` and the original request type echoed back in the data field.

Write a matching client that sends a request and prints the response.

**Package:** `t21_json_2_jackson_advanced.exercises.ex05`
