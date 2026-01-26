---
title: "Generics I — Exercises"
subtitle: "Week 1 of 2 — Type Parameters & Type Safety"
module: "COMP C8Z03 Object-Oriented Programming"
language: "Java"
created: 2026-01-24
generated_at: 2026-01-24T09:20:27+00:00
version: 3.1
tags: [java, generics, exercises, type-safety, collections]
---

# Generics I — Exercises

> These exercises build directly on the **Generics I** notes. Work from top to bottom.  

## Ground rules

- No raw types (e.g., `ArrayList` → `ArrayList<T>`).
- Don’t use casts as a “fix”.
- Use loops unless a loop is clearly worse than a library call.

## How to run

Each exercise assumes a package like:

```java
t08_generics.exercises.w1.exNN
```

Create a class `Exercise` in that package with a static entry point:

```java
package t08_generics.exercises.w1.ex01;

public final class Exercise {
    public static void run() {
        // call your methods here; print results for quick checks
    }
}
```

---

## Exercise 01 — The crash you never want again (legacy Object container)

**Objective:** Feel the pain generics remove: casts + runtime crashes.

**Context (software + games):**
- **Software dev:** a legacy service layer returns `Object` and forces you to cast.
- **Games dev:** an inventory container accidentally mixes item types.

### What you are building

You will create **two versions** of a container:

| Class | What it stores | Type safety |
| :- | :- | :- |
| `InventoryLegacy` | `Object` | Unsafe (casts required) |
| `Inventory<T>` | `T` | Safe (no casts) |

### Tasks

1. **Run the legacy container**
   - Create an `InventoryLegacy`.
   - Add **two different types** (e.g., a `String` and an `Integer`).
   - Retrieve the first value and call `toUpperCase()` (this forces a cast).
   - Add a **commented-out** cast line that *could* crash at runtime, and explain why in a short comment.

2. **Refactor to generics**
   - Create `final class Inventory<T>`.
   - Use `ArrayList<T>` internally (not raw `ArrayList`).
   - Implement:
     - `void add(T item)`
     - `T get(int index)`

3. **Prove the benefit**
   - In `Exercise.run()`, demonstrate that `Inventory<String>` rejects adding an `Integer` (comment out the failing line).

### Done when…

- Your `Inventory<T>` usage contains **no casts**.
- You have **zero** raw types.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
final class InventoryLegacy {
    private final java.util.ArrayList _items = new java.util.ArrayList();

    public void add(Object item) {
        _items.add(item);
    }

    public Object get(int index) {
        return _items.get(index);
    }
}

final class Inventory<T> {
    private final java.util.ArrayList<T> _items = new java.util.ArrayList<>();

    public void add(T item) {
        _items.add(item);
    }

    public T get(int index) {
        return _items.get(index);
    }
}

public final class Exercise {
    public static void run() {
        InventoryLegacy legacy = new InventoryLegacy();
        legacy.add("hi");
        legacy.add(123);

        String s = (String) legacy.get(0);
        System.out.println(s.toUpperCase());

        // Integer n = (Integer) legacy.get(0); // could crash at runtime (wrong cast)

        Inventory<String> safe = new Inventory<>();
        safe.add("hello");
        // safe.add(123); // does not compile

        System.out.println(safe.get(0).toUpperCase());
    }
}
```

  </div>
</details>

---

## Exercise 02 — Generic UndoStack<T>

**Objective:** Implement a reusable generic structure.

**Context (software + games):**
- **Software dev:** undo/redo in an editor or admin dashboard.
- **Games dev:** action history (player commands) or tooling command rollback.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
final class UndoStack<T> {
    private final java.util.ArrayList<T> _items = new java.util.ArrayList<>();

    public void push(T item) {
        _items.add(item);
    }

    public T pop() {
        if (_items.isEmpty())
            return null;
        return _items.remove(_items.size() - 1);
    }

    public T peek() {
        if (_items.isEmpty())
            return null;
        return _items.get(_items.size() - 1);
    }

    public int size() {
        return _items.size();
    }
}

public final class Exercise {
    public static void run() {
        UndoStack<String> s = new UndoStack<>();
        s.push("MoveLeft");
        s.push("Jump");
        s.push("UsePotion");

        System.out.println(s.size()); // 3
        System.out.println(s.pop());  // UsePotion
        System.out.println(s.peek()); // Jump
        System.out.println(s.size()); // 2
    }
}
```

  </div>
</details>

---

## Exercise 03 — Generic map (no streams)

**Objective:** Write a generic method where type parameters flow from input to output.

**Context (software + games):**
- **Software dev:** map models to DTOs or UI labels.
- **Games dev:** map entities to HUD text, IDs, or debug strings.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public final class Maps {
    public static <T, R> java.util.ArrayList<R> map(
            java.util.List<T> items,
            java.util.function.Function<T, R> transform) {

        if (transform == null)
            throw new NullPointerException("transform must not be null");

        java.util.ArrayList<R> out = new java.util.ArrayList<>();

        if (items == null)
            return out;

        for (T item : items)
            out.add(transform.apply(item));

        return out;
    }
}

public final class Exercise {
    public static void run() {
        java.util.ArrayList<Integer> lengths =
                Maps.map(java.util.List.of("ant", "zebra"), String::length);

        java.util.ArrayList<String> labels =
                Maps.map(java.util.List.of(10, 20), n -> "HP:" + n);

        System.out.println(lengths); // [3, 5]
        System.out.println(labels);  // [HP:10, HP:20]
    }
}
```

  </div>
</details>

---

## Exercise 04 — Generic where/filter (no streams)

**Objective:** Use a generic predicate to select items safely.

**Context (software + games):**
- **Software dev:** filter users/orders/logs.
- **Games dev:** filter active enemies/visible objects/valid pickups.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public final class Filters {
    public static <T> java.util.ArrayList<T> where(
            java.util.List<T> items,
            java.util.function.Predicate<T> predicate) {

        if (predicate == null)
            throw new NullPointerException("predicate must not be null");

        java.util.ArrayList<T> out = new java.util.ArrayList<>();

        if (items == null)
            return out;

        for (T item : items) {
            if (predicate.test(item))
                out.add(item);
        }

        return out;
    }
}

public final class Exercise {
    public static void run() {
        java.util.ArrayList<String> a =
                Filters.where(java.util.List.of("Amy", "Bob", "Ada"), s -> s.startsWith("A"));

        java.util.ArrayList<Integer> b =
                Filters.where(java.util.List.of(5, 11, 12, 3), n -> n > 10);

        System.out.println(a); // [Amy, Ada]
        System.out.println(b); // [11, 12]
    }
}
```

  </div>
</details>

---

## Exercise 05 — TopN<T extends Comparable<T>> (Week 1 capstone)

**Objective:** Use a bounded type parameter to build a component with real behaviour.

**Context (software + games):**
- **Software dev:** top N sales totals / top N response times.
- **Games dev:** top N leaderboard scores / top N damage events.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
final class TopN<T extends Comparable<T>> {
    private final int _capacity;
    private final java.util.ArrayList<T> _values = new java.util.ArrayList<>();

    public TopN(int capacity) {
        if (capacity < 1)
            throw new IllegalArgumentException("capacity must be >= 1");
        _capacity = capacity;
    }

    public void add(T item) {
        if (item == null)
            throw new NullPointerException("item must not be null");

        int insertIndex = 0;

        while (insertIndex < _values.size()) {
            T current = _values.get(insertIndex);

            // Descending: place item before the first element that is smaller than it.
            if (item.compareTo(current) > 0)
                break;

            insertIndex++;
        }

        _values.add(insertIndex, item);

        while (_values.size() > _capacity)
            _values.remove(_values.size() - 1);
    }

    public java.util.ArrayList<T> valuesDescending() {
        return new java.util.ArrayList<>(_values);
    }
}

public final class Exercise {
    public static void run() {
        TopN<Integer> best = new TopN<>(3);
        best.add(10);
        best.add(7);
        best.add(25);
        best.add(3);
        best.add(19);

        System.out.println(best.valuesDescending()); // [25, 19, 10]
    }
}
```

  </div>
</details>

---

## Exercise 06 — Registry<K, V> (type-safe lookup)

**Objective:** Practise multiple type parameters (`K`, `V`) with a familiar API.

**Context (software + games):**
- **Software dev:** userId → profile, configKey → value.
- **Games dev:** entityId → entity, itemId → item definition.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
final class Registry<K, V> {
    private final java.util.HashMap<K, V> _map = new java.util.HashMap<>();

    public void put(K key, V value) {
        if (key == null)
            throw new NullPointerException("key must not be null");
        _map.put(key, value);
    }

    public V get(K key) {
        if (key == null)
            throw new NullPointerException("key must not be null");
        return _map.get(key);
    }

    public boolean containsKey(K key) {
        if (key == null)
            throw new NullPointerException("key must not be null");
        return _map.containsKey(key);
    }
}

public final class Exercise {
    public static void run() {
        Registry<String, Integer> scores = new Registry<>();
        scores.put("Zara", 100);
        scores.put("Kai", 55);

        System.out.println(scores.containsKey("Zara")); // true
        System.out.println(scores.get("Kai"));          // 55
        System.out.println(scores.get("Nope"));         // null
    }
}
```

  </div>
</details>

---

## Exercise 07 — Refactor MessageBusLegacy into MessageBus<T>

**Objective:** Refactor a realistic “bag of Objects” into a generic type.

**Context (software + games):**
- **Software dev:** event queue between UI and service layer.
- **Games dev:** event/message bus between gameplay systems.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
final class MessageBus<T> {
    private final java.util.ArrayList<T> _queue = new java.util.ArrayList<>();

    public void enqueue(T msg) {
        _queue.add(msg);
    }

    public T dequeue() {
        if (_queue.isEmpty())
            return null;
        return _queue.remove(0);
    }

    public int size() {
        return _queue.size();
    }

    public boolean isEmpty() {
        return _queue.isEmpty();
    }
}

public final class Exercise {
    public static void run() {
        MessageBus<String> bus = new MessageBus<>();
        bus.enqueue("A");
        bus.enqueue("B");
        bus.enqueue("C");

        System.out.println(bus.size());    // 3
        System.out.println(bus.dequeue()); // A
        System.out.println(bus.dequeue()); // B
        System.out.println(bus.dequeue()); // C
        System.out.println(bus.dequeue()); // null

        // bus.enqueue(123); // does not compile
    }
}
```

  </div>
</details>

---

## Exercise 08 — Type erasure reality check: create T[] safely

**Objective:** Make the “no `new T[]`” rule concrete and learn the standard workaround.

**Context (software + games):**
- **Software dev:** generic caches sometimes want arrays for performance.
- **Games dev:** pooling/batching code sometimes wants arrays for tight loops.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public final class ArraysEx {
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<T> elementType, int length) {
        if (elementType == null)
            throw new NullPointerException("elementType must not be null");

        if (length < 0)
            throw new IllegalArgumentException("length must be >= 0");

        Object arr = java.lang.reflect.Array.newInstance(elementType, length);
        return (T[]) arr;
    }
}

public final class Exercise {
    public static void run() {
        String[] names = ArraysEx.newArray(String.class, 3);
        System.out.println(names.length); // 3

        Integer[] nums = ArraysEx.newArray(Integer.class, 2);
        nums[0] = 10;
        nums[1] = 20;

        System.out.println(nums[0] + ", " + nums[1]); // 10, 20
    }
}
```

  </div>
</details>

---

## Exercise 09 — Result<T> + map

**Objective:** Combine a generic type with a useful generic method.

**Context (software + games):**
- **Software dev:** “success or error” from validation/parsing.
- **Games dev:** “loaded asset or error” from resource loading.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
final class Result<T> {
    private final T _value;
    private final String _error;

    private Result(T value, String error) {
        _value = value;
        _error = error;
    }

    public static <T> Result<T> ok(T value) {
        return new Result<>(value, null);
    }

    public static <T> Result<T> fail(String error) {
        if (error == null)
            throw new NullPointerException("error must not be null");
        return new Result<>(null, error);
    }

    public boolean isOk() {
        return _error == null;
    }

    public T value() {
        return _value;
    }

    public String error() {
        return _error;
    }

    public <R> Result<R> map(java.util.function.Function<T, R> f) {
        if (f == null)
            throw new NullPointerException("f must not be null");

        if (!isOk())
            return Result.fail(_error);

        return Result.ok(f.apply(_value));
    }
}

public final class Exercise {
    public static void run() {
        Result<Integer> hp = Result.ok(10);
        Result<String> label = hp.map(n -> "HP:" + n);
        System.out.println(label.value()); // HP:10

        Result<Integer> bad = Result.fail("No HP");
        Result<String> stillBad = bad.map(n -> "HP:" + n);
        System.out.println(stillBad.isOk());  // false
        System.out.println(stillBad.error()); // No HP
    }
}
```

  </div>
</details>

---

## Optional challenge — Generic ObjectPool<T>

**Context (software + games):**
- **Software dev:** reuse expensive objects (buffers/builders).
- **Games dev:** pool bullets/particles/events to avoid GC spikes.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
final class ObjectPool<T> {
    private final java.util.function.Supplier<T> _factory;
    private final java.util.ArrayList<T> _pool = new java.util.ArrayList<>();

    public ObjectPool(java.util.function.Supplier<T> factory) {
        if (factory == null)
            throw new NullPointerException("factory must not be null");
        _factory = factory;
    }

    public T acquire() {
        if (_pool.isEmpty())
            return _factory.get();
        return _pool.remove(_pool.size() - 1);
    }

    public void release(T obj) {
        // Choice: we will NOT allow null releases
        if (obj == null)
            throw new NullPointerException("obj must not be null");
        _pool.add(obj);
    }
}

public final class Exercise {
    public static void run() {
        ObjectPool<StringBuilder> pool = new ObjectPool<>(StringBuilder::new);

        StringBuilder a = pool.acquire();
        a.append("hi");
        pool.release(a);

        StringBuilder b = pool.acquire();
        System.out.println(a == b); // true (reused)
    }
}
```

  </div>
</details>

---

## Lesson Context

```yaml
linked_lesson:
  topic_code: "t08_generics"
  week_in_topic: "1_of_2"
  primary_domain_emphasis: "Balanced"
  difficulty_tier: "Intermediate"
```
