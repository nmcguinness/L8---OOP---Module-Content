---
title: "Functional Interfaces: Consumer, Function, Predicate, Supplier"
subtitle: "COMP C8Z03 — Year 2 OOP"
description: "Core functional interfaces in java.util.function; how to read their types, use them with lambdas and method references, and apply them with collections APIs. Includes balanced Games/Software examples and common composition patterns."
created: 2026-02-04
version: 1.0
authors: ["OOP Teaching Team"]
tags: ["java", "functional-interfaces", "java.util.function", "lambda", "method-reference", "year2", "comp-c8z03"]
prerequisites: ["Methods", "Generics basics", "Collections I: ArrayList essentials"]
---

# Functional Interfaces: Consumer, Function, Predicate, Supplier
> **Prequisities:**
> - Methods: parameters, return values, overloading  
> - Generics basics: `T`, `K`, `V`  
> - Collections I: `List<T>`, loops, simple helpers  

---

## What you'll learn
A quick summary of what **you** should be able to do after this lesson:

| Skill Type | You will be able to… |
| :-- | :-- |
| Understand | Read a functional interface signature and explain what its type parameters mean. |
| Use | Write lambdas for `Consumer`, `Predicate`, `Function`, `Supplier`, and their `Bi…` variants. |
| Use | Use common collection APIs that accept functional interfaces (`forEach`, `removeIf`, `sort`, `computeIfAbsent`). |
| Use | Apply method references (`Class::staticMethod`, `obj::instanceMethod`, `Type::new`) when they improve clarity. |
| Analyze | Choose between `Function`, `Predicate`, and `Consumer` based on “returns a value / returns boolean / returns void”. |
| Debug | Spot common mistakes: boxing, wrong return type, capturing variables, and side effects in predicates. |

---

## Why this matters
Modern Java APIs are designed around **passing behaviour** as a parameter: “for each element, do X”, “keep items that match Y”, “transform A into B”.

These interfaces are the *standard vocabulary* for that behaviour. If you recognise them, you can read Java code faster and write reusable utilities without inventing your own callback types.

---

## The big idea: one abstract method
A **functional interface** is (effectively) an interface with **one abstract method**.  
That single method means Java can treat a lambda as an instance of the interface.

You’ll see these interfaces mostly from the package:

```java
import java.util.function.*;
```

---

## The core set
This is the “everyday” set you’ll use constantly.

| Interface | Meaning | Single method |
| :-- | :-- | :-- |
| `Consumer<T>` | “I take a `T` and do something.” | `void accept(T t)` |
| `BiConsumer<T, U>` | “I take a `T` and a `U` and do something.” | `void accept(T t, U u)` |
| `Predicate<T>` | “I test a `T` and answer true/false.” | `boolean test(T t)` |
| `BiPredicate<T, U>` | “I test a `T` and a `U` and answer true/false.” | `boolean test(T t, U u)` |
| `Function<T, R>` | “I transform `T` into `R`.” | `R apply(T t)` |
| `BiFunction<T, U, R>` | “I combine `T` and `U` into `R`.” | `R apply(T t, U u)` |
| `Supplier<T>` | “I create/provide a `T`.” | `T get()` |

**Rule of thumb**
- **Consumer**: input only → **side effect** (prints, stores, mutates, sends)
- **Predicate**: input → **boolean**
- **Function**: input → **value**
- **Supplier**: no input → **value**

---

## Reading the types quickly
When you see an interface like `Function<T, R>`, read it left-to-right:

- `T` = input type
- `R` = result type

Examples:

```java
Function<String, Integer> length = s -> s.length();        // String -> int (boxed to Integer)
Predicate<String> isBlank = s -> s == null || s.isBlank(); // String -> boolean
Consumer<String> printer = s -> System.out.println(s);     // String -> void
Supplier<Integer> d6 = () -> 1 + (int)(Math.random() * 6); // () -> Integer
```

---

## Using them with collections APIs
These are the most common “built-in” places you’ll meet functional interfaces.

### `forEach` takes a `Consumer<T>`
```java
List<String> names = List.of("Aoife", "Ben", "Ciara");
names.forEach(n -> System.out.println(n.toUpperCase()));
```

### `removeIf` takes a `Predicate<T>`
```java
List<Integer> scores = new ArrayList<>(List.of(12, 3, 18, 7));
scores.removeIf(s -> s < 10); // keep only 10+
System.out.println(scores);   // [12, 18]
```

### `sort` takes a `Comparator<T>` (a functional interface)
`Comparator<T>` is also a functional interface:

```java
List<String> names = new ArrayList<>(List.of("Zoe", "Aoife", "Ben"));
names.sort((a, b) -> a.compareToIgnoreCase(b));
System.out.println(names);
```

### `Map.computeIfAbsent` takes a `Function<K, V>`
A classic pattern: create a value lazily if the key is missing.

```java
Map<String, List<String>> groups = new HashMap<>();

groups.computeIfAbsent("red", k -> new ArrayList<>()).add("potion");
groups.computeIfAbsent("red", k -> new ArrayList<>()).add("cloak");

System.out.println(groups.get("red")); // [potion, cloak]
```

---

## Games example: event hooks with Consumer
Imagine a tiny “event bus” for a game loop: systems subscribe to events with callbacks.

```java
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DamageEventBus {
    private List<Consumer<Integer>> _listeners;

    public DamageEventBus() {
        _listeners = new ArrayList<>();
    }

    public void subscribe(Consumer<Integer> listener) {
        _listeners.add(listener);
    }

    public void publishDamage(int amount) {
        for (Consumer<Integer> listener : _listeners)
            listener.accept(amount);
    }
}
```

Usage:

```java
DamageEventBus bus = new DamageEventBus();

bus.subscribe(dmg -> System.out.println("UI: -" + dmg));
bus.subscribe(dmg -> System.out.println("Audio: play hit sound"));

bus.publishDamage(12);
```

**Why Consumer?**  
Listeners only *react*. They don’t return a value.

---

## Software example: validation rules with Predicate
A `Predicate<T>` makes it easy to plug in rules.

```java
import java.util.function.Predicate;

Predicate<String> isValidProductCode = productCode ->
    productCode != null && productCode.length() >= 6 && productCode.chars().allMatch(Character::isLetterOrDigit);

System.out.println(isValidSku.test("A12B34")); // true
System.out.println(isValidSku.test("  "));    // false
```

You can pass predicates into helpers:

```java
public static List<String> filter(List<String> items, Predicate<String> keep) {
    List<String> result = new ArrayList<>();
    for (String item : items)
        if (keep.test(item))
            result.add(item);
    return result;
}
```

---

## `Bi…` variants: when you need two inputs
### Games example: collision test as `BiPredicate`
```java
import java.util.function.BiPredicate;

public class Point
{
    private float _x;
    private float _y;

    public Point(float x, float y)
    {
        _x = x;
        _y = y;
    }

    public float getX()
    {
        return _x;
    }

    public float getY()
    {
        return _y;
    }
}

BiPredicate<Point, Point> closeEnough = (a, b) -> {
    float dx = a.getX() - b.getX();
    float dy = a.getY() - b.getY();
    return (dx * dx + dy * dy) < 25.0f; // within radius 5
};

System.out.println(closeEnough.test(new Point(0, 0), new Point(3, 4))); // true
```

### Software example: merge rule as `BiFunction`
```java
import java.util.function.BiFunction;

BiFunction<Integer, Integer, Integer> max = (a, b) -> Math.max(a, b);

int best = max.apply(1200, 980); // 1200
```

---

## Operators: specialised Function forms
Two very common special cases are defined as their own interfaces.

| Interface | Equivalent | Meaning |
| :-- | :-- | :-- |
| `UnaryOperator<T>` | `Function<T, T>` | transform a value to the same type |
| `BinaryOperator<T>` | `BiFunction<T, T, T>` | combine two values into the same type |

Examples:

```java
import java.util.function.UnaryOperator;
import java.util.function.BinaryOperator;

UnaryOperator<String> trim = s -> s.trim();
BinaryOperator<Integer> add = (a, b) -> a + b;

System.out.println(trim.apply("  hi  "));
System.out.println(add.apply(2, 3));
```

---

## Method references
Method references are “named” versions of lambdas.

### Static method
```java
Function<String, Integer> parse = Integer::parseInt;
System.out.println(parse.apply("42"));
```

### Instance method on a specific object
```java
StringBuilder sb = new StringBuilder();
Consumer<String> append = sb::append;
append.accept("Hello ");
append.accept("World");
System.out.println(sb);
```

### Constructor reference
```java
Supplier<List<String>> makeList = ArrayList::new;
List<String> names = makeList.get();
```

---

## Composing behaviour
Some interfaces provide default methods for combining operations.

### Predicate composition
```java
Predicate<String> longEnough = s -> s != null && s.length() >= 8;
Predicate<String> hasDigit = s -> s != null && s.chars().anyMatch(Character::isDigit);

Predicate<String> strongPassword = longEnough.and(hasDigit);

System.out.println(strongPassword.test("abc"));      // false
System.out.println(strongPassword.test("abc12345")); // true
```

### Function composition
```java
Function<String, String> trim = String::trim;
Function<String, Integer> length = String::length;

Function<String, Integer> trimmedLength = trim.andThen(length);
System.out.println(trimmedLength.apply("  hello  ")); // 5
```

**Warning:** Composition is great when it improves clarity. Don’t compose for the sake of it.

---

## Primitive variants and boxing
Using `Function<Integer, Integer>` can cause **boxing/unboxing** (wrapping `int` in `Integer`).

If you’re processing lots of values, consider primitive interfaces such as:

- `IntConsumer`, `LongConsumer`, `DoubleConsumer`
- `IntPredicate`, `LongPredicate`, `DoublePredicate`
- `IntSupplier`, `LongSupplier`, `DoubleSupplier`
- `IntFunction<R>`, `ToIntFunction<T>`, `IntUnaryOperator`, `IntBinaryOperator` (and similar for long/double)

Example:

```java
import java.util.function.IntUnaryOperator;

IntUnaryOperator clampTo0_100 = x -> Math.max(0, Math.min(100, x));
System.out.println(clampTo0_100.applyAsInt(120)); // 100
```

---

## Common problems
| Problem | What it looks like | Fix |
| :-- | :-- | :-- |
| Wrong return type | Using `Consumer` but returning a value | Use `Function` (value) or `Predicate` (boolean). |
| Side effects in predicates | `removeIf(x -> { list.add(...); return ...; })` | Keep predicates pure: *no mutation*. |
| Capturing loop variables | Lambdas refer to a changing variable | Copy to a new local inside the loop. |
| Boxing overhead | `Function<Integer, Integer>` in hot loops | Use primitive interfaces (`IntUnaryOperator`, etc.). |
| Null handling | `s -> s.isBlank()` throws | Decide a policy: handle null inside predicate/function. |

---

## Key takeaways
- These interfaces are the standard “function types” of Java: input-only, boolean, transform, and factory.  
- Most modern collection APIs accept one of these interfaces.  
- Use method references when it improves readability.  
- Prefer predicates without side effects; keep mutation in consumers.  
- Consider primitive interfaces when performance matters.

---

## Reflective Questions
- In your current project, where do you already pass behaviour as parameters (sorting, filtering, callbacks)?  
- Which operations are better expressed as **predicates** (yes/no rules) vs **functions** (transformations)?  
- Can you spot any places where you invented a custom interface that could be replaced with `java.util.function`?

---

## Lesson Context 

```yaml
previous_lesson:
  topic_code: t12_generics_2_notes
  domain_emphasis: Balanced

this_lesson:
  primary_domain_emphasis: Balanced
  difficulty_tier: Foundation
```

---

## Appendix A: Mapping from common C# delegate names
| C# name | Java equivalent | Meaning |
| :-- | :-- | :-- |
| `Action<T>` | `Consumer<T>` | one input, returns void |
| `Action<T, U>` | `BiConsumer<T, U>` | two inputs, returns void |
| `Predicate<T>` | `Predicate<T>` | one input, returns boolean |
| `Func<T, R>` | `Function<T, R>` | one input, returns a value |
| `Func<T, U, R>` | `BiFunction<T, U, R>` | two inputs, returns a value |
| `Func<R>` | `Supplier<R>` | no input, returns a value |
