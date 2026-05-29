# Collections III: Set, Map & PriorityQueue — Exercises

## Exercise 1 — Unique visitors

A web server log contains a `String` per entry in the format `"timestamp,ipAddress"`.
Write a method:

```java
public static int countUniqueVisitors(List<String> logEntries)
```

that returns the number of **distinct IP addresses** seen across all entries.

**Package:** `t07_collections_3_set_map.exercises.ex01`

---

## Exercise 2 — Word frequency

Write a method:

```java
public static Map<String, Integer> wordFrequency(String sentence)
```

that splits `sentence` on whitespace, lowercases each word, and returns a map of word → count.

**Package:** `t07_collections_3_set_map.exercises.ex02`

---

## Exercise 3 — Sorted leaderboard

Given a `Map<String, Integer>` of player names to scores, write a method:

```java
public static List<String> leaderboard(Map<String, Integer> scores)
```

that returns player names sorted **highest score first** using a `PriorityQueue`.

**Package:** `t07_collections_3_set_map.exercises.ex03`

---

## Exercise 4 — Group by first letter

Write a method:

```java
public static Map<Character, List<String>> groupByFirstLetter(List<String> words)
```

that groups words by their first character. The returned map should have keys in **alphabetical order** (`TreeMap`).

**Package:** `t07_collections_3_set_map.exercises.ex04`

---

## Exercise 5 — Set intersection

Write a method:

```java
public static Set<Integer> intersection(List<Integer> a, List<Integer> b)
```

that returns elements present in **both** lists, with no duplicates.

**Package:** `t07_collections_3_set_map.exercises.ex05`

---

## Exercise 6 — Simple LRU-style cache (extension)

Implement a `SimpleCache<K, V>` class backed by a `LinkedHashMap` that:
- accepts a capacity in its constructor,
- stores key–value pairs via `put(K key, V value)`,
- evicts the **oldest** entry when capacity is exceeded,
- retrieves a value via `Optional<V> get(K key)`.

**Package:** `t07_collections_3_set_map.exercises.ex06`
