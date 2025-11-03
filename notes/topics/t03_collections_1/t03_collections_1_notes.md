---
title: "Collections I: ArrayList"
subtitle: "COMP C8Z03 — Year 2 OOP"
description: "Year‑2 introduction to Java ArrayList with iteration patterns, common APIs, pitfalls, and balanced Games/Software examples."
created: 2025-10-07
version: 1.0
authors: ["Teaching Team"]
tags: ["java", "collections", "arraylist", "iteration", "year2", "comp-c8z03"]
prerequisites: ["Arrays (1D & 2D)", "equals/hashCode basics", "Ordering basics (reading only)"]
---

# Collections I: ArrayList 
> **Prequisities:**
> - Arrays (1D & 2D): indexing, iteration, basic algorithms (search, simple transforms)
> - equals/hashCode basics: what identity means for objects
> - Ordering basics (reading only): idea of natural order vs custom order (using Comparing and Lambda functions)

---

## What you'll learn
A quick summary of what **you** should be able to do after this lesson:

| Skill Type | You will be able to… |
| :-- | :-- |
| Understand | Distinguish fixed arrays vs `ArrayList<T>` and justify when `ArrayList` is the better fit. |
| Use | Construct and manipulate `ArrayList<T>` (`add`, `get`, `set`, `remove`, `addAll`, `toArray`). |
| Use | Iterate with index `for`, enhanced `for`, and safely remove using an `Iterator`. |
| Use | Convert between arrays and `ArrayList` and apply simple bulk operations. |
| Use | Use Comparing and lambda functions to sort and sort by tie-breakers |
| Debug | Prevent and fix common issues (`IndexOutOfBoundsException`, null handling). |

---

## Why this matters
Arrays are great when the size never changes. Real programs rarely stay that tidy. **`ArrayList<T>`** gives you a resizable, indexable list with familiar bracket‑style thinking, but with safer APIs. It underpins many tasks we’ll do this semester: loading CSVs, sorting records, and building simple reports.

> If you can reason about a 1D array, you can master `ArrayList`—just trade fixed length for automatic resizing and a rich set of methods.

---

## How this builds on previous content
- Backed by a dynamically resized array (capacity grows automatically).
- **Index‑based** random access is O(1) average.
- Inserting/removing near the **end** is cheap; near the **front** can be costly (elements shift).

> Rule of thumb: lots of **random reads** + **append at end** → `ArrayList` is a strong default.

---

## Core Ideas 1
```java
import java.util.ArrayList;
import java.util.Iterator;

ArrayList<String> names = new ArrayList<>();          // empty
ArrayList<Integer> scores = new ArrayList<>(128);     // with initial capacity

// Add
names.add("Aoife");           // append
names.add(0, "Alex");        // insert at index (shifts right)

// Read / Update
String first = names.get(0);   // read
names.set(1, "Maya");         // replace element at index

// Remove
names.remove(0);               // by index
names.remove("Maya");         // by value (first match)

int n = names.size();
boolean empty = names.isEmpty();
boolean hasAlex = names.contains("Alex");
int firstIndex = names.indexOf("Aoife");
int lastIndex  = names.lastIndexOf("Aoife");

// Bulk ops
ArrayList<String> more = new ArrayList<>();
more.add("Cara"); more.add("Ben");
names.addAll(more);            // extend

// Iteration patterns
for (int i = 0; i < names.size(); i++) { System.out.println(i + ": " + names.get(i)); }
for (String s : names) { System.out.println(s); }      // read‑only iteration

// Safe removal during iteration (use Iterator)
Iterator<String> it = names.iterator();
while (it.hasNext()) {
    if (it.next().startsWith("A")) it.remove();
}
```

### Useful utilities
```java
String[] arr = {"red","green","blue"};
ArrayList<String> list = new ArrayList<>();
for (String s : arr) list.add(s);                      // array → list (simple & safe)

// list → array
String[] out = list.toArray(new String[0]);            // size‑aware copy

// Clearing
list.clear();
```

> Avoid `Arrays.asList` for a resizable list: it creates a fixed‑size view; `add/remove` will throw.

---

### Iteration patterns & when to use each
- **Index `for`**: when you need positions, random access, or to write back with `set(i,…)`.
- **Enhanced `for`**: clean read‑only traversal.
- **`Iterator`**: when **removing** elements as you scan. Don’t remove from the list inside an enhanced `for` → `ConcurrentModificationException`.

---

## Progressive coding steps (A → B → C)
### Step A — Append vs insert benchmark (tiny diagnostic)
```java
ArrayList<Integer> a = new ArrayList<>();
long t0 = System.nanoTime();
for (int i = 0; i < 50_000; i++) a.add(i);             // append
long t1 = System.nanoTime();
for (int i = 0; i < 10_000; i++) a.add(0, i);          // insert at front
long t2 = System.nanoTime();
System.out.printf("append: %d µs, front-insert: %d µs\n", (t1-t0)/1000, (t2-t1)/1000);
```
*Observation:* front inserts shift many elements. Prefer appending + sorting later if order is flexible.

### Step B — Filtering
```java
ArrayList<Integer> nums = new ArrayList<>();
for (int i = 0; i < 20; i++) nums.add(i);
Iterator<Integer> it = nums.iterator();
while (it.hasNext()) {
    if (it.next() % 2 == 0) it.remove();               // remove evens
}
```

### Step C — De‑duplication (preserve first occurrence)
```java
ArrayList<String> input = new ArrayList<>();
// ... fill with values
ArrayList<String> unique = new ArrayList<>();
for (String s : input) {
    if (!unique.contains(s)) unique.add(s);            // O(n^2) in worst case
}
```
> Later we’ll prefer a `HashSet` for O(n) de‑dup (see *Collections III*), but this version shows list‑only logic.

---

## Useful snippets (guards & helpers)
- `IndexOutOfBoundsException`: check `0 <= i && i < list.size()` before `get/set/remove(i)`.
- `NullPointerException`: avoid storing `null` unless it’s a real “no value” signal.
- Document **ownership**: who creates/modifies the list? Pass as `List<T>` where possible.

---

## Performance & trade‑offs
| Operation                         | Typical cost |
|----------------------------------|--------------|
| Read `get(i)` / write `set(i)`   | O(1)
| Append `add(x)`                   | Amortized O(1)
| Insert/remove at middle/front     | O(n)
| `contains(x)` / `indexOf(x)`      | O(n)

> “Amortized” means occasional capacity growth is paid for by many cheap appends.

---

## Core ideas 2 
### 9.1 Games: Player inventory (stackable items)
```java
class Item { final String id; int qty; Item(String id,int q){this.id=id;this.qty=q;} }
class Inventory {
    private final ArrayList<Item> items = new ArrayList<>();
    public void addItem(String id, int q){
        int idx = indexOf(id);
        if (idx >= 0) items.get(idx).qty += q; else items.add(new Item(id,q));
    }
    public boolean removeItem(String id, int q){
        int idx = indexOf(id);
        if (idx < 0) return false;
        Item it = items.get(idx);
        if (it.qty < q) return false;
        it.qty -= q; if (it.qty == 0) items.remove(idx);
        return true;
    }
    private int indexOf(String id){
        for (int i=0;i<items.size();i++) if (items.get(i).id.equals(id)) return i;
        return -1;
    }
}
```

### 9.2 Software Dev: To‑do list with priority buckets
```java
class Task { final String title; final int pri; Task(String t,int p){title=t;pri=p;} }
class Backlog {
    private final ArrayList<Task> tasks = new ArrayList<>();
    public void add(String title, int pri){ tasks.add(new Task(title, pri)); }
    public ArrayList<Task> byPriority(int pri){
        ArrayList<Task> out = new ArrayList<>();
        for (Task t : tasks) if (t.pri == pri) out.add(t);
        return out;
    }
}
```

---


## Comparator factories: `comparing(...)` with tie-breakers (quick win)

> Goal: sort an `ArrayList<T>` by one field, then break ties by another, using tiny, readable helpers from `Comparator`.

**Key API (java.util.Comparator):**
- `Comparator.comparing(keyExtractor)`
- `Comparator.comparingInt/Long/Double(...)` (avoid boxing)
- `thenComparing(...)` (tie-breakers)
- `reversed()` (flip order)
- `nullsFirst(...)` / `nullsLast(...)` (handle missing data)

### Example — Products sorted by price (asc), then rating (desc), then name (asc)
```java
import java.util.ArrayList;
import java.util.Comparator;

final class Product {
    private final String name;
    private final double price;
    private final double rating;

    public Product(String name, double price, double rating) {
        this.name = name; this.price = price; this.rating = rating;
    }
    public String name()   { return name; }
    public double price()  { return price; }
    public double rating() { return rating; }

    @Override public String toString() {
        return name + " (€" + price + ", " + rating + "★)";
    }
}

class DemoComparing {
    public static void main(String[] args) {
        var items = new ArrayList<Product>();
        items.add(new Product("Mouse", 15.0, 4.2));
        items.add(new Product("Keyboard", 45.0, 4.5));
        items.add(new Product("Monitor", 120.0, 4.1));
        items.add(new Product("Mat", 15.0, 4.5));

        // Primary: price ↑, Tie1: rating ↓, Tie2: name ↑
        Comparator<Product> cmp =
            Comparator.comparingDouble(Product::price)
                      .thenComparing(Comparator.comparingDouble(Product::rating).reversed())
                      .thenComparing(Product::name);

        items.sort(cmp);
        System.out.println(items);
    }
}
```

### Example — Students by surname (case-insensitive), then first name, then ID
```java
import java.util.Comparator;

// Case-insensitive surname, then firstname, then id
Comparator<Student> byName =
    Comparator.comparing((Student s) -> s.surname().toLowerCase())
              .thenComparing(s -> s.firstname().toLowerCase())
              .thenComparing(Student::id);
```

### Handling `null` safely (optional)
```java
Comparator<Product> byNameNullSafe =
    Comparator.comparing(Product::name,
        Comparator.nullsLast(String::compareToIgnoreCase));
```

**Why this matters here:** you already know how to store things in an `ArrayList`; now you can produce ordered views of that list **without** changing the class itself. The code reads like English and is easy to extend with more tie-breakers.

---

### Mini-aside: Lambdas in 90 seconds (so `comparing(...)` makes sense)
A **lambda** is a tiny function you can pass inline. Think: “an argument that is a function.”

**Shape:** `(parameters) -> expression` or `(parameters) -> { statements; return x; }`

**Examples used above:**
```java
// Extract a key for comparing(...)
s -> s.firstname()                  // takes Student s, returns firstname
(Product p) -> p.price()            // explicit parameter type (often optional)
String::length                      // method reference (equivalent to s -> s.length())
Product::name                       // instance method reference as key extractor
```

**When to use here:** `Comparator.comparing(...)` needs a *key extractor* function to pull the field you want to sort by; lambdas (or method references) are the neatest way to provide it.

> Tip: Prefer the primitive versions `comparingInt/Long/Double` when your key is numeric → avoids boxing and is a touch faster.

---

### Quick practice
1) **Backlog tasks**: sort by priority ↑ (int), tie-break by due date ↑ (long epoch), tie-break by title A→Z.  
2) **Game scores**: value ↓, then player name A→Z, and treat `null` names as last.

Starter:
```java
Comparator<Task> byPriThenDueThenTitle =
    Comparator.comparingInt(Task::priority)
              .thenComparingLong(Task::dueEpoch)
              .thenComparing(Task::title, String::compareToIgnoreCase);
```

## Reflective Questions
- When would you still prefer a raw array over `ArrayList`?
- Why is removing while using `for‑each` problematic?
- What’s the trade‑off between `contains` on a list vs a set?
- How could you test that your list‑manipulation methods behave for empty lists?

---

## Lesson Context 
```yaml
previous_lesson:
  topic_code: t02_ordering_notes
  domain_emphasis: Balanced

this_lesson:
  primary_domain_emphasis: Balanced
  difficulty_tier: Foundation
```

