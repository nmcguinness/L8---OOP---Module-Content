---
title: "Equality & Hashing in Java — Foundations"
subtitle: "COMP C8Z03 — Year 2 OOP"
description: "A practical introduction to identity vs value equality, the equals/hashCode contract, and how hashing works — without using collection types yet."
created: 2025-10-06
version: 1.0
authors: ["Teaching Team"]
tags: [java, equality, hashing, equals, hashcode]
---

# Equality & Hashing in Java

> **Prequisities:**
> - Classes/objects; Strings; basic method overriding.

---

## What you'll learn

A quick summary of what **you** should be able to do after this lesson:

| Skill Type                  | You will be able to…                                                                                                                              |
| :-------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------ |
| Understand                    | Distinguish **identity** (`==`) from **value equality** (`equals`) and state the **equals/hashCode contract** clearly.                            |
| Understand                    | Explain, in plain terms, what **hashing** is and why it enables fast lookups.                                                                     |
| Use        | Compute simple **string hashes** (e.g., toy sum and rolling/polynomial) and combine **field hashes** for objects using the 31-multiplier pattern. |
| Use        | Map a hash code to a **bucket index** using `mod` or bit-masking and reason about **collisions** at a high level.                                 |
| Use              | Implement correct `equals` and `hashCode` for a small class, keeping fields consistent across both methods.                                       |
| Debug         | Detect and explain bugs caused by **mutable identifying fields** or inconsistent `equals`/`hashCode`.                                             |


> We will **not** use `HashSet`, `HashMap`, or any collection types here. Those appear in a later lesson.

## Identity vs Value Equality

- `==` checks **identity**: “Is this the exact same object in memory?”
- `equals` checks **value equality**: “Do these two objects represent the same value?”

```java
String a = new String("hello");
String b = new String("hello");

System.out.println(a == b);      // false (different objects)
System.out.println(a.equals(b)); // true  (same characters)
```

Use `equals` when “sameness” is about data, not the instance.

---

## The equals/hashCode contract (do not break this)

If two objects are **equal** by `equals`, they **must** have the **same hash code**.

Why? Hash-based lookups group values by their hash codes before checking equality. If equal objects produced different hash codes, lookups could fail.

**Key rules:**
- If `a.equals(b)` is true → `a.hashCode() == b.hashCode()` must be true.
- `equals` should be **reflexive, symmetric, transitive**, and **consistent**.
- `hashCode` should be stable for the object’s lifetime (don’t base it on fields you’ll mutate).

---

## How hashing works (collection-independent)

Hashing turns a value into an integer (**hash code**). A lookup table can use that integer to quickly narrow down where a value might be stored.

Typical membership check idea:
1) Compute the hash code of the value.
2) Convert that hash to a **bucket index** within a fixed-size table.
3) Compare with `equals` only against the small group in that bucket to confirm.

This is fast because we avoid scanning all items. With a good hash function and a sensible table size, lookups are close to constant time for practical purposes.

**Terminology (kept simple):**
- **Bucket**: a group where items that share an index are stored.
- **Collision**: different values landing in the same bucket (normal and handled by the table).
- **Resizing**: as items grow, the table may expand to keep buckets from getting crowded.

### Simple, concrete hashing examples (string → number)

These are **illustrations** to build intuition, not Java’s real implementation.

**A) Sum of character codes (toy example)**
```text
Let S = "CAB"
ASCII: 'C'=67, 'A'=65, 'B'=66
h = 67 + 65 + 66 = 198
BucketIndex = h mod TableSize
```
> This is easy to compute but not very robust (many collisions).

**B) Rolling (polynomial) hash (common pattern)**
```text
Given a base b (often 31) and starting h = 0:
for each character c in S:
    h = (b * h + c)

Example: S = "CAB", b = 31
Step 1 (C=67): h = 31*0 + 67 = 67
Step 2 (A=65): h = 31*67 + 65 = 2072
Step 3 (B=66): h = 31*2072 + 66 = 64238
BucketIndex = h mod TableSize
```
> Using a base and multiplication spreads values better than a plain sum.

### Object → number (combining field hashes)

For objects, we combine field hashes in a consistent way. A common pattern is a multiplier like 31:

```text
Let obj have fields: id, email
Let h_id = hash(id), h_email = hash(email)

h = 31 * h_id + h_email
BucketIndex = h mod TableSize
```

If an object has more fields, you keep folding them in with the same pattern:
```text
h = 31*h + hash(fieldN)
```

**Important:**
- Use the **same fields** here as you use in `equals`.
- Prefer fields that don’t change over the object’s lifetime (immutability).
- In real Java code, `Objects.hash(...)` or your IDE can generate a good implementation.

### 3.3 From hash code to bucket index

Tables use the hash code to pick a bucket:
```text
BucketIndex = h mod TableSize
```
We use this formula to map a large integer to a valid index range.

## Writing correct equals/hashCode

### Records (recommended when suitable)
Records provide **value-based equality and hash codes** automatically.

```java
public record Customer(String id, String email) { }

var c1 = new Customer("C1","a@x.com");
var c2 = new Customer("C1","a@x.com");
System.out.println(c1.equals(c2));   // true
System.out.println(c1.hashCode()==c2.hashCode()); // true
```

### Classes (override both consistently)
When using a regular class, override both methods using the **same identifying fields**.

```java
import java.util.Objects;

public final class Customer {
    private final String id;
    private final String email;

    public Customer(String id, String email) {
        this.id = id;
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

**Guidance**
- Prefer **immutable** identifying fields for equality (e.g., an ID).
- Use the **same fields** in `equals` and `hashCode`.
- Avoid basing equality on fields you plan to mutate.

---

## What makes a good hash code?

- Use `Objects.hash(...)` or your IDE’s generator for correctness and readability.
- Ensure fields used in `equals` are also used in `hashCode`.
- For performance-sensitive scenarios, hand-crafted functions are possible—but prioritize **correctness** and **distribution** at this stage.

**Example (hand-crafted)**
```java
@Override
public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (email != null ? email.hashCode() : 0);
    return result;
}
```

---

## Pitfalls to avoid

- **Overriding one without the other**: equal objects must share the same hash code.  
- **Mutable keys**: changing fields used for equality after storing an object in a hashed structure can break lookups.  
- **Inconsistent equality**: breaking symmetry or transitivity leads to hard‑to‑trace bugs.

---

## Worked example: duplicate leak and fix

Below we demonstrate the *principle* by comparing equality results directly—no collections involved yet.

```java
class ProductBroken {
    String sku;
    ProductBroken(String sku) { this.sku = sku; }
    // No equals/hashCode → different objects are never "equal" by value
}

final class Product {
    private final String sku;
    Product(String sku) { this.sku = sku; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product other)) return false;
        return java.util.Objects.equals(sku, other.sku);
    }
    @Override public int hashCode() { return java.util.Objects.hash(sku); }
}

public class Demo {
    public static void main(String[] args) {
        var p1 = new ProductBroken("SKU-1");
        var p2 = new ProductBroken("SKU-1");
        System.out.println(p1.equals(p2)); // false (no value equality)

        var q1 = new Product("SKU-1");
        var q2 = new Product("SKU-1");
        System.out.println(q1.equals(q2)); // true  (value equality based on sku)
        System.out.println(q1.hashCode()==q2.hashCode()); // true (contract holds)
    }
}
```

---

## Key takeaways

- `==` is identity; `equals` is value equality.  
- If `equals` says two objects are equal, `hashCode` **must** be the same.  
- Hashing converts values to integers so lookup tables can jump to candidate locations quickly.  
- Prefer **records** for value types; for classes, override both methods with the same identifying fields.  
- Keep identifying fields **immutable**.

---

## Reflective Questions

- Which types in your current codebase should behave like **values** (same data ⇒ equal)?  
- Which fields define identity for those types? Should they be immutable?  
- Where might a hashed lookup table (covered next) be useful in your project?

---

## Lesson Context 

```yaml
previous_lesson:
  topic_code: t04_collections_2_notes
  domain_emphasis: Balanced

this_lesson:
  primary_domain_emphasis: Balanced
  difficulty_tier: Foundation
```

---

## Appendix A: Modulo arithmetic (a quick primer)

**Idea:** Modulo (“mod”) gives the **remainder** after division.
We write `a mod m` (in Java: `a % m`) and read it as “the remainder when `a` is divided by `m`.”

### Examples

* `13 mod 5 = 3` because `13 = 2×5 + 3`.
* `42 mod 10 = 2` → last digit of 42 is 2.
* `7 mod 7 = 0` → multiples of `m` have remainder 0.

### Why it matters here

* We turn a (possibly huge) hash code `h` into a valid **bucket index** by reducing it to the range `0..m-1` with `h mod m`.
* If the table size `m` is a **power of two**, `h mod m` can be computed as a fast **bit mask**: `h & (m - 1)`.

### Properties you’ll use

* **Range:** `0 ≤ (a mod m) < m` for positive `a` and `m>0`.
* **Congruence:** If `a mod m == b mod m`, then `a` and `b` fall in the **same bucket** (same remainder).
* **Add/multiply:**
  `(a + b) mod m = ((a mod m) + (b mod m)) mod m`
  `(a × b) mod m = ((a mod m) × (b mod m)) mod m`

### Java specifics (sign of result)

* In Java, `a % m` has the **same sign as `a`**.
  E.g., `(-3) % 5 == -3`.
  For bucket indices you usually want non-negative, so use:

  ```java
  int idx = Math.floorMod(h, m);   // always 0..m-1
  ```

  or normalize manually:

  ```java
  int idx = (h % m + m) % m;
  ```

### Quick intuition

* Modulo is **wrap-around** arithmetic: counting on a clock is “mod 12”.
* We use it to wrap any integer hash into a **fixed index range** for array-backed tables.

