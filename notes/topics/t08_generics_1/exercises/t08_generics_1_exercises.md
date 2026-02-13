---
title: "Generics I — Exercises"
subtitle: "Week 1 of 2 — Type Parameters & Type Safety"
module: "COMP C8Z03 Object-Oriented Programming"
language: "Java"
created: 2026-01-24
generated_at: 2026-01-26T11:16:36+00:00
version: 3.2
tags: [java, generics, exercises, type-safety, collections]
---

# Generics I — Exercises

> These exercises build directly on the **Generics I** notes.  
> This week is about learning **why** generics exist (type-safety) and getting confident writing your own generic classes and methods.

## Ground rules

- No raw types (e.g., `ArrayList` → `ArrayList<T>`).
- Don’t use casts as a “fix” (casts are allowed only where the Java type system forces it, e.g. reflective array creation).
- Prefer simple loops (no streams) unless an exercise explicitly says otherwise.

## How to run

Each exercise assumes a package like:

```java
t08_generics.exercises.exNN
```

Create a class `Exercise` in that package with a static entry point:

```java
package t08_generics.exercises.ex01;

public class Exercise {
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
- **Games dev:** an inventory/container accidentally mixes item types.

### What you are building

You will create **two versions** of the same idea: “a container that stores items and returns them by index”.

| Class | What it stores | Type safety |
| :- | :- | :- |
| `InventoryLegacy` | `Object` | Unsafe (casts required) |
| `Inventory<T>` | `T` | Safe (no casts) |

You will then demonstrate the key difference:

- the legacy container **compiles** even when you mix types, but you can crash later at runtime
- the generic container **prevents the mistake** at compile time

### Required API

Implement these two classes:

```java
class InventoryLegacy {
    public void add(Object item) { }
    public Object get(int index) { }
}

class Inventory<T> {
    public void add(T item) { }
    public T get(int index) { }
}
```

### Tasks

1. **Run the legacy container**
   - Create an `InventoryLegacy`.
   - Add **two different types** (e.g., `String`, `Integer`).
   - Retrieve the first value and call `toUpperCase()` (this forces a cast).
   - Add a **commented-out** cast line that *could* crash at runtime (and explain why in a short comment).

2. **Refactor to generics**
   - Create `class Inventory<T>`.
   - Use `ArrayList<T>` internally (not a raw `ArrayList`).
   - Implement `add` and `get`.

3. **Prove the benefit**
   - In `Exercise.run()`, demonstrate that `Inventory<String>` rejects adding an `Integer` (comment out the failing line).

### Done when…

- Your `Inventory<T>` usage contains **no casts**.
- You have **zero** raw types.
- You can explain, in 1–2 sentences, why the generic version is safer.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
class InventoryLegacy {
    private java.util.ArrayList _items = new java.util.ArrayList();

    public void add(Object item) {
        _items.add(item);
    }

    public Object get(int index) {
        return _items.get(index);
    }
}

class Inventory<T> {
    private java.util.ArrayList<T> _items = new java.util.ArrayList<>();

    public void add(T item) {
        _items.add(item);
    }

    public T get(int index) {
        return _items.get(index);
    }
}

public class Exercise {
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

**Objective:** Implement a reusable generic structure (LIFO stack) with clear empty-case behaviour.

**Context (software + games):**
- **Software dev:** undo/redo in an editor, form builder, or admin tool.
- **Games dev:** command history for tools, or action history for input replay.

### What you are building

A last-in-first-out stack backed by an `ArrayList<T>`.

You must decide and document the empty behaviour:

- `pop()` on empty → returns `null`
- `peek()` on empty → returns `null`

### Required API

```java
class UndoStack<T> {
    public void push(T item) { }
    public T pop() { }     // null if empty
    public T peek() { }    // null if empty
    public int size() { }
}
```

### Rules/behaviour

- `push` should add to the “top” of the stack.
- `pop` removes and returns the top item, or returns `null` if empty.
- `peek` returns the top item without removing it, or returns `null` if empty.
- `size` returns the current count.

### Tasks

1. Implement `UndoStack<T>`.
2. In `Exercise.run()`:
   - create `UndoStack<String>`
   - push 3 strings
   - print `size()`
   - `pop()` once and print the returned value
   - `peek()` and print the new top
   - pop until empty and confirm you get `null`
3. Repeat quickly with `UndoStack<Integer>` (at least 2 pushes + one pop).

### Done when…

- No exceptions are thrown when popping/peeking an empty stack.
- The class works for at least two types without code changes.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
class UndoStack<T> {
    private java.util.ArrayList<T> _items = new java.util.ArrayList<>();

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

public class Exercise {
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
- **Software dev:** transform domain objects into UI labels/DTOs.
- **Games dev:** transform entities into debug strings, IDs, or HUD text.

### What you are building

A `map` utility that converts a `List<T>` into an `ArrayList<R>` using a transformer function.

### Required signature

```java
public static <T, R> java.util.ArrayList<R> map(
        java.util.List<T> items,
        java.util.function.Function<T, R> transform)
```

### Rules/behaviour

- If `transform` is `null` → throw `NullPointerException`.
- If `items` is `null` → return an **empty** `ArrayList<R>` (not `null`).
- Preserve order.
- Use a loop (no streams).

### Tasks

1. Implement the method in a utility class (e.g., `Maps`).
2. In `Exercise.run()` demonstrate:
   - `["ant", "zebra"]` → `[3, 5]` (string length)
   - `[10, 20]` → `["HP:10", "HP:20"]` (prefix formatting)
3. Print the outputs so you can visually confirm ordering.

### Done when…

- The same method compiles for different type pairs (e.g., `String → Integer`, `Integer → String`).
- No casts and no raw types.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public class Maps {
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

public class Exercise {
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
- **Software dev:** filter users/orders/log entries.
- **Games dev:** filter active enemies/visible objects/valid pickups.

### What you are building

A `where` utility that returns only the items matching a `Predicate<T>`.

### Required signature

```java
public static <T> java.util.ArrayList<T> where(
        java.util.List<T> items,
        java.util.function.Predicate<T> predicate)
```

### Rules/behaviour

- If `predicate` is `null` → throw `NullPointerException`.
- If `items` is `null` → return an empty `ArrayList<T>`.
- Preserve order.
- Do not modify the original list.
- Use a loop (no streams).

### Tasks

1. Implement `where` in a utility class (e.g., `Filters`).
2. In `Exercise.run()` demonstrate:
   - filtering strings that start with `"A"`
   - filtering integers greater than `10`
3. Print the results.

### Done when…

- Your method returns a new list containing only matching items, in the original order.
- The original list is unchanged.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public class Filters {
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

public class Exercise {
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
- **Software dev:** top N sales totals / top N response times / top N search results.
- **Games dev:** top N leaderboard scores / top N damage events / top N loot rolls.

### What you are building

A class that keeps only the **best N** values seen so far, always stored in **descending order** (largest first).

This is your first “mini data structure” built with generics and constraints.

### Required API

```java
class TopN<T extends Comparable<T>> {
    public TopN(int capacity) { }
    public void add(T item) { }
    public java.util.ArrayList<T> valuesDescending() { } // returns defensive copy
}
```

### Rules/behaviour

- `capacity` must be `>= 1` → otherwise throw `IllegalArgumentException`.
- `add(null)` should throw `NullPointerException`.
- `add(item)` must:
  - insert `item` into the correct position so the list remains **descending**
  - trim the list if it exceeds capacity
- `valuesDescending()` returns a **new** list (defensive copy).
- Use loops only (no `sort`, no streams).

### Tasks

1. Implement `TopN<T>`.
2. In `Exercise.run()`:
   - create `TopN<Integer>` with capacity `3`
   - add: `10, 7, 25, 3, 19`
   - print the result and verify: `[25, 19, 10]`
3. Also try `TopN<String>` with capacity `2` and confirm it orders lexicographically in descending order.

### Done when…

- Internal size never exceeds capacity.
- Order is always descending after every `add`.
- `valuesDescending()` cannot be used to mutate the internal list.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
class TopN<T extends Comparable<T>> {
    private int _capacity;
    private java.util.ArrayList<T> _values = new java.util.ArrayList<>();

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

public class Exercise {
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
- **Software dev:** `userId → profile`, `configKey → value`, `email → account`.
- **Games dev:** `entityId → entity`, `itemId → definition`, `assetKey → asset`.

### What you are building

A tiny wrapper around `HashMap<K, V>` that enforces one rule:

- **keys must not be null**

(Values may be null.)

### Required API

```java
class Registry<K, V> {
    public void put(K key, V value) { }
    public V get(K key) { }
    public boolean containsKey(K key) { }
}
```

### Rules/behaviour

- If `key` is `null` in any method → throw `NullPointerException`.
- `get` returns `null` if the key is missing (standard `Map` behaviour).

### Tasks

1. Implement `Registry<K, V>`.
2. In `Exercise.run()`:
   - store and retrieve scores using `Registry<String, Integer>`
   - store and retrieve an id→name mapping using `Registry<Integer, String>`
   - show `containsKey` working
   - show `get` returning `null` for a missing key

### Done when…

- You can use the same class with different key/value types without any changes.
- Null keys are rejected consistently.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
class Registry<K, V> {
    private java.util.HashMap<K, V> _map = new java.util.HashMap<>();

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

public class Exercise {
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
- **Games dev:** event/message bus between gameplay systems (input → gameplay → audio).

### What you are building

A FIFO (first-in-first-out) message bus backed by `ArrayList<T>`.

### Required API

```java
class MessageBus<T> {
    public void enqueue(T msg) { }
    public T dequeue() { }   // null if empty
    public int size() { }
    public boolean isEmpty() { }
}
```

### Rules/behaviour

- `enqueue` adds to the end of the queue.
- `dequeue` removes from the front of the queue.
- If empty, `dequeue` returns `null`.
- `isEmpty` returns `true` when `size() == 0`.

> Note: `remove(0)` is O(n). That’s fine here — focus on type-safety and API behaviour.

### Tasks

1. Implement `MessageBus<T>`.
2. In `Exercise.run()`:
   - create `MessageBus<String>`
   - enqueue at least 3 messages
   - dequeue until you get `null`
   - print each dequeued value
3. Add one commented-out line that shows the compiler rejects the wrong type.

### Done when…

- Your queue returns messages in the same order they were enqueued.
- No casts and no raw types.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
class MessageBus<T> {
    private java.util.ArrayList<T> _queue = new java.util.ArrayList<>();

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

public class Exercise {
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

### What you are building

A helper that creates a `T[]` using `Class<T>` and reflection.

### Required signature

```java
public static <T> T[] newArray(Class<T> elementType, int length)
```

### Rules/behaviour

- If `elementType` is `null` → throw `NullPointerException`.
- If `length < 0` → throw `IllegalArgumentException`.
- Use `java.lang.reflect.Array.newInstance(elementType, length)`.
- The cast is unavoidable here (type erasure). Add a `@SuppressWarnings("unchecked")` as shown in the solution.

### Tasks

1. Implement `newArray`.
2. In `Exercise.run()`:
   - create `String[]` length `3`, print its length
   - create `Integer[]` length `2`, assign values, print them

### Done when…

- You can create arrays of different element types using the same method.
- Your method validates arguments and doesn’t crash for valid inputs.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public class ArraysEx {
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

public class Exercise {
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

**Objective:** Combine a generic type with a genuinely useful generic method.

**Context (software + games):**
- **Software dev:** return “success or error” from validation/parsing without throwing.
- **Games dev:** return “loaded asset or error” from resource loading/config parsing.

### What you are building

An immutable `Result<T>` with two states:

- **ok** → contains a value (`T`)
- **fail** → contains an error message (`String`)

…and a `map(...)` method that transforms an ok value into a new ok value, without touching failures.

### Required API

```java
class Result<T> {
    public static <T> Result<T> ok(T value) { }
    public static <T> Result<T> fail(String error) { }

    public boolean isOk() { }
    public T value() { }       // valid only when ok
    public String error() { }  // valid only when fail

    public <R> Result<R> map(java.util.function.Function<T, R> f) { }
}
```

### Rules/behaviour

- `fail(null)` → throw `NullPointerException`.
- `map(null)` → throw `NullPointerException`.
- If this result is **fail**, `map` returns a **fail** with the same error.
- If this result is **ok**, `map` returns `ok(f(value))`.

### Tasks

1. Implement `Result<T>`.
2. In `Exercise.run()` demonstrate:
   - `Result.ok(10).map(x -> "HP:" + x)` gives an ok result containing `"HP:10"`.
   - `Result.fail("bad").map(...)` stays failed (same error).
3. Print `isOk()` and either `value()` or `error()` for each case.

### Done when…

- Mapping an ok changes the type safely (`Result<Integer>` → `Result<String>`).
- Mapping a fail does not call the function and preserves the error.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
class Result<T> {
    private T _value;
    private String _error;

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

public class Exercise {
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

**Objective:** Build a generic object pool that reduces allocations by reusing instances.

**Context (software + games):**
- **Software dev:** reuse expensive objects (buffers/builders) to reduce GC pressure.
- **Games dev:** pool bullets/particles/events to avoid frame spikes.

### What you are building

A pool with two operations:

- `acquire()`:
  - returns an existing pooled object if available
  - otherwise creates a new one using a factory (`Supplier<T>`)
- `release(obj)`:
  - returns an object back to the pool for reuse

### Required API

```java
class ObjectPool<T> {
    public ObjectPool(java.util.function.Supplier<T> factory) { }
    public T acquire() { }
    public void release(T obj) { }
}
```

### Rules/behaviour

- `factory == null` → throw `NullPointerException`.
- `release(null)` → throw `NullPointerException` (keep the pool clean).
- Prefer LIFO reuse (take from the end of an internal list).

### Tasks

1. Implement `ObjectPool<T>` using an `ArrayList<T>` as the internal storage.
2. In `Exercise.run()`:
   - create `ObjectPool<StringBuilder>` using `StringBuilder::new`
   - acquire one builder, append text, then release it
   - acquire again and show you got a usable instance back (print something to prove it works)
3. Add a short comment describing why pooling can help performance (1–2 lines).

### Done when…

- Acquiring returns a new object when the pool is empty.
- Releasing and re-acquiring reuses objects.
- Your pool is generic and works for any type `T`.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
class ObjectPool<T> {
    private java.util.function.Supplier<T> _factory;
    private java.util.ArrayList<T> _pool = new java.util.ArrayList<>();

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

public class Exercise {
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
