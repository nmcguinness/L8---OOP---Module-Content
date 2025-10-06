---
title: "Ordering in Java — Comparable & Comparator"
subtitle: "COMP C8Z03 — Year 2 OOP"
description: "A practical, lambda-free introduction using explicit Comparator classes and anonymous classes. ArrayList-only; ends with chaining user-defined comparators."
created: 2025-10-06
version: "v1.2 (no lambdas)"
tags: [java, ordering, sorting, comparable, comparator, arraylist]
---

# Ordering in Java — Comparable & Comparator

> Prereqs: Classes/objects; Strings; basic ArrayList usage; loops. 

---

## What you'll learn

| Skill Type | You will be able to… |
| :-- | :-- |
| Understand | Explain Comparable (natural order inside the class) vs Comparator (separate ordering strategy). |
| Use | Implement Comparable<T> with a clear tie-break. |
| Use | Write explicit Comparator classes and anonymous classes (no lambdas). |
| Use | Sort ArrayList<T> with List.sort(...) using your comparators. |
| Use | Chain multiple user-defined comparators into a single ordering. |
| Debug | Avoid common issues (missing tie-breakers, nulls, inconsistent rules). |

---

## Comparable — natural order inside your class

```java
import java.util.ArrayList;
import java.util.List;

final class Score implements Comparable<Score> {
    private final String player;
    private final int value;

    public Score(String player, int value) {
        this.player = player;
        this.value = value;
    }
    public String player() { return player; }
    public int value() { return value; }

    @Override
    public int compareTo(Score other) {
        // Primary: higher value first (descending)
        int byValueDesc = Integer.compare(other.value, this.value);
        if (byValueDesc != 0) return byValueDesc;
        // Tie-break: player name ascending (A->Z)
        return this.player.compareTo(other.player);
    }

    @Override
    public String toString() {
        return player + ":" + value;
    }
}

class DemoComparable {
    public static void main(String[] args) {
        var scores = new ArrayList<Score>();
        scores.add(new Score("Zara", 20));
        scores.add(new Score("Alan", 20));
        scores.add(new Score("Mia", 50));
        scores.add(new Score("Bea", 10));

        // Use natural order (compareTo)
        scores.sort(null); // or Collections.sort(scores)
        System.out.println(scores);
    }
}
```

---

## Comparator — defining comparison externally

We keep the class unchanged and supply an external ordering.

### Example A — Simple named Comparator class (name ascending)

```java
import java.util.Comparator;

final class NameAscComparator implements Comparator<Product> {
    @Override
    public int compare(Product a, Product b) {
        return a.name().compareTo(b.name());
    }
}

final class Product {
    private final String name;
    private final double price;
    private final double rating;

    public Product(String name, double price, double rating) {
        this.name = name; this.price = price; this.rating = rating;
    }
    public String name() { return name; }
    public double price() { return price; }
    public double rating() { return rating; }

    @Override public String toString() {
        return name + " (€" + price + ", " + rating + "★)";
    }
}

class DemoComparatorA {
    public static void main(String[] args) {
        var items = new java.util.ArrayList<Product>();
        items.add(new Product("Mouse", 15.0, 4.2));
        items.add(new Product("Keyboard", 45.0, 4.5));
        items.add(new Product("Monitor", 120.0, 4.1));

        items.sort(new NameAscComparator());
        System.out.println(items);
    }
}
```

### Example B — Anonymous Comparator (price ascending)

```java
class DemoComparatorB {
    public static void main(String[] args) {
        var items = new java.util.ArrayList<Product>();
        items.add(new Product("Mouse", 15.0, 4.2));
        items.add(new Product("Keyboard", 45.0, 4.5));
        items.add(new Product("Monitor", 120.0, 4.1));

        java.util.Comparator<Product> priceAsc = new java.util.Comparator<Product>() {
            @Override
            public int compare(Product a, Product b) {
                return Double.compare(a.price(), b.price());
            }
        };

        items.sort(priceAsc);
        System.out.println(items);
    }
}
```

### Example C — Named Comparator with multi-field tie-breaks
Order by rating descending, then by name ascending.

```java
final class RatingDescThenNameAsc implements java.util.Comparator<Product> {
    @Override
    public int compare(Product a, Product b) {
        // rating descending
        int byRating = Double.compare(b.rating(), a.rating());
        if (byRating != 0) return byRating;
        // tie-break by name ascending
        return a.name().compareTo(b.name());
    }
}

class DemoComparatorC {
    public static void main(String[] args) {
        var items = new java.util.ArrayList<Product>();
        items.add(new Product("Mouse", 15.0, 4.2));
        items.add(new Product("Keyboard", 45.0, 4.5));
        items.add(new Product("Monitor", 120.0, 4.1));
        items.add(new Product("Mat", 12.0, 4.5)); // same rating as Keyboard

        items.sort(new RatingDescThenNameAsc());
        System.out.println(items);
    }
}
```
---

## Sorting notes

- `list.sort(comparator)` is the modern, preferred API.
- Sorting lists is stable: elements that compare equal keep their relative order.
- Keep rules simple and consistent; always include tie-breakers when needed.
- Handle `null` explicitly (e.g., treat unknown values as lowest or filter them out first).

---

## Reflective Questions

- For one of your project classes, write a named Comparator class. What is the primary field and at least one tie-breaker?
- What class in a project (if any) should define a natural order using Comparable?
