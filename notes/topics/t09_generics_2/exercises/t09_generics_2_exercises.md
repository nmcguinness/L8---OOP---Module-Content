---
title: "Generics II — Exercises"
subtitle: "Week 2 of 2 — Wildcards, Variance & PECS"
module: "COMP C8Z03 Object-Oriented Programming"
language: "Java"
created: 2026-01-24
generated_at: 2026-01-24T09:20:27+00:00
version: 2.1
tags: [java, generics, wildcards, pecs, variance, exercises]
---

# Generics II — Exercises 

> These exercises build directly on the **Generics II** notes.  

## Ground rules

- Don’t “fix” wildcard errors using casts.
- Don’t use raw types.
- When an exercise is compile-time focused, keep the “failing line” commented out.

## How to run

Each exercise assumes a package like:

```java
t09_generics.exercises.exNN
```

Create a class `Exercise` in that package with a static entry point:

```java
package t09_generics.exercises.ex01;

public final class Exercise {
    public static void run() {
        // call your methods here; print results for quick checks
    }
}
```

---

## Exercise 01 — Prove invariance (compile-time)

**Story hook (software + games):**
- **Software dev:** `AdminUser extends User`, but `List<AdminUser>` still isn’t a `List<User>`.
- **Games dev:** `Enemy extends Entity`, but `List<Enemy>` still isn’t a `List<Entity>`.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
final class Entity { }

final class Enemy extends Entity { }

final class Pickup extends Entity { }

public final class Exercise {
    public static void run() {
        Enemy e = new Enemy();
        Entity x = e; // normal inheritance works

        java.util.List<Enemy> enemies = new java.util.ArrayList<>();

        // java.util.List<Entity> entities = enemies;
        // Not allowed: List<Enemy> is not a List<Entity> (generics are invariant).
        System.out.println(x);
        System.out.println(enemies.size());
    }
}
```

  </div>
</details>

---

## Exercise 02 — printAll(List<?>)

**Story hook (software + games):**
- **Software dev:** a logging helper that prints any list (users, orders, strings).
- **Games dev:** a debug helper that prints any list (entities, scores, item names).

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public final class Lists {
    public static void printAll(java.util.List<?> items) {
        if (items == null)
            return;

        for (Object x : items)
            System.out.println(x);
    }
}

public final class Exercise {
    public static void run() {
        Lists.printAll(java.util.List.of("A", "B"));
        Lists.printAll(java.util.List.of(10, 20));

        java.util.List<Entity> entities = java.util.List.of(new Entity(), new Entity());
        Lists.printAll(entities);
    }
}
```

  </div>
</details>

---

## Exercise 03 — Producer method: sumNumbers(List<? extends Number>)

**Story hook (software + games):**
- **Software dev:** sum invoices/analytics counters/response times.
- **Games dev:** sum damage events/XP ticks/score deltas.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public final class Numbers {
    public static double sumNumbers(java.util.List<? extends Number> nums) {
        if (nums == null || nums.isEmpty())
            return 0.0;

        double sum = 0.0;

        for (Number n : nums)
            sum += n.doubleValue();

        return sum;
    }
}

public final class Exercise {
    public static void run() {
        System.out.println(Numbers.sumNumbers(java.util.List.of(1, 2, 3)));      // 6.0
        System.out.println(Numbers.sumNumbers(java.util.List.of(0.5, 1.5, 2.0))); // 4.0
        System.out.println(Numbers.sumNumbers(null));                             // 0.0
    }
}
```

  </div>
</details>

---

## Exercise 04 — Consumer method: fill(List<? super T>, T, int)

**Story hook (software + games):**
- **Software dev:** generate placeholder rows/test data quickly.
- **Games dev:** fill a spawn queue or placeholder debug events.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public final class Fillers {
    public static <T> void fill(java.util.List<? super T> out, T value, int count) {
        if (out == null)
            throw new NullPointerException("out must not be null");

        if (count < 0)
            throw new IllegalArgumentException("count must be >= 0");

        for (int i = 0; i < count; i++)
            out.add(value);
    }
}

public final class Exercise {
    public static void run() {
        java.util.ArrayList<Object> out = new java.util.ArrayList<>();
        Fillers.fill(out, "hi", 3);

        System.out.println(out); // [hi, hi, hi]
    }
}
```

  </div>
</details>

---

## Exercise 05 — PECS in action: copy(src extends T, dst super T)

**Story hook (software + games):**
- **Software dev:** copy `List<Integer>` into `List<Number>` when preparing chart data.
- **Games dev:** copy `List<Enemy>` into `List<Entity>` when building a scene list.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public final class Copier {
    public static <T> void copy(java.util.List<? extends T> src, java.util.List<? super T> dst) {
        if (src == null)
            throw new NullPointerException("src must not be null");

        if (dst == null)
            throw new NullPointerException("dst must not be null");

        for (T item : src)
            dst.add(item);
    }
}

public final class Exercise {
    public static void run() {
        java.util.List<Integer> src = java.util.List.of(1, 2, 3);

        java.util.ArrayList<Number> dst1 = new java.util.ArrayList<>();
        java.util.ArrayList<Object> dst2 = new java.util.ArrayList<>();

        Copier.copy(src, dst1);
        Copier.copy(src, dst2);

        System.out.println(dst1); // [1, 2, 3]
        System.out.println(dst2); // [1, 2, 3]
    }
}
```

  </div>
</details>

---

## Exercise 06 — addAllEntities (domain-specific: extends → super)

**Story hook (software + games):**
- **Software dev:** merge subtype lists into one base-type list.
- **Games dev:** merge enemies + pickups into one world entity list.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public final class Entities {
    public static void addAllEntities(java.util.List<? extends Entity> src,
                                      java.util.List<? super Entity> dst) {

        if (src == null)
            throw new NullPointerException("src must not be null");

        if (dst == null)
            throw new NullPointerException("dst must not be null");

        for (Entity e : src)
            dst.add(e);
    }
}

public final class Exercise {
    public static void run() {
        java.util.List<Enemy> enemies = java.util.List.of(new Enemy(), new Enemy());
        java.util.List<Pickup> pickups = java.util.List.of(new Pickup());

        java.util.ArrayList<Entity> all = new java.util.ArrayList<>();
        Entities.addAllEntities(enemies, all);
        Entities.addAllEntities(pickups, all);

        System.out.println(all.size()); // 3
    }
}
```

  </div>
</details>

---

## Exercise 07 — Fix a broken API signature (consumer list)

**Story hook (software + games):**
- **Software dev:** you wrote `addAuditEvents(List<AuditEvent>)` but callers have `List<Object>` or `List<BaseEvent>`.
- **Games dev:** you wrote `addEnemies(List<Enemy>)` but callers collect into `List<Entity>`.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public final class Spawner {
    public static void addEnemies(java.util.List<? super Enemy> out) {
        if (out == null)
            throw new NullPointerException("out must not be null");

        out.add(new Enemy());
        out.add(new Enemy());
    }
}

public final class Exercise {
    public static void run() {
        java.util.ArrayList<Enemy> a = new java.util.ArrayList<>();
        java.util.ArrayList<Entity> b = new java.util.ArrayList<>();
        java.util.ArrayList<Object> c = new java.util.ArrayList<>();

        Spawner.addEnemies(a);
        Spawner.addEnemies(b);
        Spawner.addEnemies(c);

        System.out.println(a.size()); // 2
        System.out.println(b.size()); // 2
        System.out.println(c.size()); // 2
    }
}
```

  </div>
</details>

---

## Exercise 08 — Comparator<? super T> practice

**Story hook (software + games):**
- **Software dev:** sort `List<Employee>` using a `Comparator<Person>`.
- **Games dev:** sort `List<Enemy>` using a `Comparator<Entity>`.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public final class Sorters {
    public static <T> void sortWith(java.util.List<T> items, java.util.Comparator<? super T> cmp) {
        if (items == null)
            throw new NullPointerException("items must not be null");

        if (cmp == null)
            throw new NullPointerException("cmp must not be null");

        items.sort(cmp);
    }
}

public final class Exercise {
    public static void run() {
        java.util.ArrayList<Enemy> enemies = new java.util.ArrayList<>();
        enemies.add(new Enemy());
        enemies.add(new Enemy());

        java.util.Comparator<Entity> cmp = (a, b) -> 0;
        Sorters.sortWith(enemies, cmp);

        System.out.println(enemies.size()); // 2
    }
}
```

  </div>
</details>

---

## Exercise 09 — Wildcard capture: swapFirstTwo(List<?>)

**Story hook (software + games):**
- **Software dev:** swap items in a generic “recent items” list.
- **Games dev:** swap first two inventory slots for any item type.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public final class Swaps {
    public static void swapFirstTwo(java.util.List<?> items) {
        if (items == null || items.size() < 2)
            return;

        swapFirstTwoCaptured(items);
    }

    private static <T> void swapFirstTwoCaptured(java.util.List<T> items) {
        T a = items.get(0);
        T b = items.get(1);

        items.set(0, b);
        items.set(1, a);
    }
}

public final class Exercise {
    public static void run() {
        java.util.ArrayList<String> a = new java.util.ArrayList<>();
        a.add("A");
        a.add("B");

        Swaps.swapFirstTwo(a);
        System.out.println(a); // [B, A]

        java.util.ArrayList<Integer> b = new java.util.ArrayList<>();
        b.add(10);
        b.add(20);

        Swaps.swapFirstTwo(b);
        System.out.println(b); // [20, 10]
    }
}
```

  </div>
</details>

---

## Exercise 10 — What can I add? What can I read?

**Story hook (software + games):**
- **Software dev:** you’re debugging wildcard signatures and need quick rules.
- **Games dev:** you’re wiring systems together and want to avoid type mistakes.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

- `List<?> a`  
  - Add: `null` only  
  - Read: `Object` (because element type is unknown)

- `List<? extends Number> b`  
  - Add: `null` only  
  - Read: `Number` (safe upper bound)

- `List<? super Integer> c`  
  - Add: `Integer` (and its subclasses), plus `null`  
  - Read: `Object` (because the list might actually be `List<Object>`)

  </div>
</details>

---

## Optional challenge — merge two producers into one consumer

**Story hook (software + games):**
- **Software dev:** merge cached results + live results into one output list.
- **Games dev:** merge base content + DLC content into one output list.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ✅ Solution
  </summary>
  <div style="margin-top:0.8rem;">

```java
public final class Merge {
    public static <T> void merge(java.util.List<? extends T> a,
                                 java.util.List<? extends T> b,
                                 java.util.List<? super T> out) {

        if (out == null)
            throw new NullPointerException("out must not be null");

        if (a != null) {
            for (T x : a)
                out.add(x);
        }

        if (b != null) {
            for (T x : b)
                out.add(x);
        }
    }
}

public final class Exercise {
    public static void run() {
        java.util.List<Integer> a = java.util.List.of(1, 2);
        java.util.List<Integer> b = java.util.List.of(3);

        java.util.ArrayList<Number> out = new java.util.ArrayList<>();
        Merge.merge(a, b, out);

        System.out.println(out); // [1, 2, 3]
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
  week_in_topic: "2_of_2"
  primary_domain_emphasis: "Balanced"
  difficulty_tier: "Intermediate"
```
