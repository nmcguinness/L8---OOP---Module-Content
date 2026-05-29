---
title: "OOP Fundamentals: Classes, Objects & Encapsulation"
subtitle: "COMP C8Z03 — Year 2 OOP"
topic_code: t00_oop_fundamentals
description: "Java class anatomy, constructors, fields, methods, access modifiers, and the four pillars of OOP — the conceptual foundation every subsequent topic builds on."
created: 2026-05-27
last_updated: 2026-05-27
version: 1.0
status: published
authors: ["OOP Teaching Team"]
tags: [java, oop, classes, objects, encapsulation, constructors, access-modifiers, year2, comp-c8z03]
difficulty_tier: Foundation
mlos: [MLO2]
previous_topic: null
prerequisites:
  - Basic programming in any language (variables, conditionals, loops, methods/functions)
---

# OOP Fundamentals: Classes, Objects & Encapsulation

> **Prerequisites:**
> - You can write variables, `if` statements, loops, and functions/methods in at least one language
> - No prior Java or OOP experience is assumed beyond that

---

## What you'll learn

| Skill Type | You will be able to… |
| :-- | :-- |
| Understand | Describe what a class is and how it differs from an object. |
| Understand | Name and explain the four pillars of OOP at a high level. |
| Use | Write a Java class with private fields, a constructor, and public methods. |
| Use | Apply access modifiers (`private`, `public`) to fields and methods. |
| Use | Create objects with `new` and call methods on them. |
| Use | Write a basic constructor with argument validation (guard clauses). |
| Debug | Identify `NullPointerException` when a reference is not initialised. |

---

## Why this matters

Every topic in this module builds on one idea: organising code around **objects** that combine state (data) and behaviour (methods) into a single unit.

Before diving into arrays, collections, inheritance, or design patterns, you need a solid answer to two questions:

1. **What is a class?** — A blueprint that describes what data an object holds and what it can do.
2. **What is an object?** — A specific instance of a class, created at runtime, with its own copy of the data.

Everything else in OOP is about composing, extending, or coordinating objects. Get this right and the rest follows naturally.

---

## The four pillars of OOP

| Pillar | One-line definition | Where you'll see it |
| :-- | :-- | :-- |
| **Encapsulation** | Hide internal state; expose only what is needed | `private` fields + `public` methods; this topic |
| **Abstraction** | Work with *what* something does, not *how* | Interfaces, abstract classes; t08, t07 |
| **Inheritance** | A class can extend another, inheriting its behaviour | `extends`; t07 |
| **Polymorphism** | One type reference, many possible implementations | Method overriding, interface types; t07, t08 |

This topic covers **encapsulation** in full and introduces the vocabulary needed to understand the others.

---

## Part 1: Anatomy of a Java class

```java
public class Player {                          // class declaration

    // --- Fields (state) ---
    private String _name;                      // private: only accessible inside this class
    private int    _score;

    // --- Constructor ---
    public Player(String name) {               // called when you write: new Player("Alice")
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        _name  = name;
        _score = 0;                            // sensible default
    }

    // --- Methods (behaviour) ---
    public String getName()  { return _name; }
    public int    getScore() { return _score; }

    public void addPoints(int points) {
        if (points < 0) throw new IllegalArgumentException("Points must be non-negative");
        _score += points;
    }

    public void resetScore() {
        _score = 0;
    }

    @Override
    public String toString() {
        return _name + " (score: " + _score + ")";
    }
}
```

Key vocabulary:

| Term | What it means |
| :-- | :-- |
| **Field** | A variable that belongs to the object; holds its state |
| **Constructor** | Special method that runs when `new` is called; initialises fields |
| **Method** | A function that belongs to the object; defines its behaviour |
| **`this`** | A reference to the current object instance |

---

## Part 2: Access modifiers

| Modifier | Visible to… | Use for… |
| :-- | :-- | :-- |
| `private` | This class only | Fields (almost always); internal helpers |
| `public` | Any class | Constructors; methods that form the API |
| `protected` | This class + subclasses + same package | Inheritance hooks (covered in t07) |
| *(none)* | Same package | Rarely used deliberately |

**Rule of thumb:** make fields `private`, make the constructor and useful methods `public`.

```java
public class Counter {
    private int _count;          // hidden — callers cannot directly set or read this

    public Counter()             { _count = 0; }
    public void increment()      { _count++; }
    public void decrement()      { if (_count > 0) _count--; }
    public int  getCount()       { return _count; }
}
```

Callers never see `_count` directly. They can only use `increment()`, `decrement()`, and `getCount()`. That means you can change how counting works internally without breaking any caller — this is encapsulation at work.

---

## Part 3: Creating and using objects

```java
// Create objects with 'new'
Player alice = new Player("Alice");
Player bob   = new Player("Bob");

// Each object has its own copy of the fields
alice.addPoints(10);
bob.addPoints(5);

System.out.println(alice);  // Alice (score: 10)
System.out.println(bob);    // Bob (score: 5)

// Call methods
alice.resetScore();
System.out.println(alice.getScore()); // 0

// Object reference vs object
Player ref1 = alice;        // ref1 and alice point to the SAME object
ref1.addPoints(20);
System.out.println(alice.getScore()); // 20 — same object was modified
```

> **NullPointerException:** If you declare a variable but never assign it, it holds `null`. Calling any method on `null` crashes at runtime. Always initialise references before use.

```java
Player ghost = null;
ghost.getName();    // NullPointerException — ghost is not pointing at any object
```

---

## Part 4: Guard clauses (defensive constructors)

A **guard clause** is a check at the top of a method or constructor that rejects invalid input immediately.

```java
public class Task {

    private final String _title;
    private final int    _priority;      // 1 (highest) to 5 (lowest)

    public Task(String title, int priority) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must not be blank");
        }
        if (priority < 1 || priority > 5) {
            throw new IllegalArgumentException("Priority must be 1–5, got: " + priority);
        }
        _title    = title.strip();       // normalise whitespace
        _priority = priority;
    }

    public String getTitle()    { return _title; }
    public int    getPriority() { return _priority; }

    @Override
    public String toString() {
        return "[P" + _priority + "] " + _title;
    }
}
```

Guard clauses mean an object is **always valid** once constructed. You never need to check inside methods whether the state makes sense.

---

## Part 5: Multiple constructors and `this(...)`

A class can have more than one constructor — each with a different parameter list.

```java
public class Item {

    private final String _name;
    private       int    _quantity;

    // Full constructor
    public Item(String name, int quantity) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        _name     = name.strip();
        _quantity = quantity;
    }

    // Convenience constructor — delegates to the full one
    public Item(String name) {
        this(name, 0);      // calls Item(String, int) with default quantity
    }

    public String getName()     { return _name; }
    public int    getQuantity() { return _quantity; }

    public void add(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount must be non-negative");
        _quantity += amount;
    }
}
```

`this(name, 0)` calls the other constructor in the same class. This keeps validation in one place and avoids duplication.

---

## Games example: `Player` and `Inventory`

```java
public class GamePlayer {

    private final String _username;
    private       int    _health;
    private       int    _level;

    public GamePlayer(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username required");
        }
        _username = username.strip();
        _health   = 100;
        _level    = 1;
    }

    public String getUsername() { return _username; }
    public int    getHealth()   { return _health; }
    public int    getLevel()    { return _level; }
    public boolean isAlive()    { return _health > 0; }

    public void takeDamage(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Damage must be non-negative");
        _health = Math.max(0, _health - amount);
    }

    public void heal(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Heal amount must be non-negative");
        _health = Math.min(100, _health + amount);
    }

    public void levelUp() {
        _level++;
        _health = 100;      // full heal on level-up
    }

    @Override
    public String toString() {
        return _username + " [Lv." + _level + ", HP:" + _health + "]";
    }
}
```

Usage:

```java
GamePlayer hero = new GamePlayer("Riona");
hero.takeDamage(30);
hero.heal(10);
System.out.println(hero);          // Riona [Lv.1, HP:80]
System.out.println(hero.isAlive()); // true

hero.takeDamage(200);
System.out.println(hero.isAlive()); // false — health clamped to 0
```

---

## Software example: `Task` and `TaskList`

```java
public class TodoTask {

    private final String  _title;
    private final String  _category;
    private       boolean _done;

    public TodoTask(String title, String category) {
        if (title    == null || title.isBlank())    throw new IllegalArgumentException("Title required");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Category required");
        _title    = title.strip();
        _category = category.strip();
        _done     = false;
    }

    public String  getTitle()    { return _title; }
    public String  getCategory() { return _category; }
    public boolean isDone()      { return _done; }
    public void    markDone()    { _done = true; }

    @Override
    public String toString() {
        return (_done ? "[x] " : "[ ] ") + _title + " (" + _category + ")";
    }
}
```

```java
import java.util.ArrayList;
import java.util.List;

public class TaskList {

    private final List<TodoTask> _tasks = new ArrayList<>();

    public void add(TodoTask task) {
        if (task == null) throw new IllegalArgumentException("Task must not be null");
        _tasks.add(task);
    }

    public int totalCount()  { return _tasks.size(); }

    public int doneCount() {
        int count = 0;
        for (TodoTask t : _tasks) if (t.isDone()) count++;
        return count;
    }

    public void printAll() {
        for (TodoTask t : _tasks) System.out.println(t);
    }
}
```

Usage:

```java
TaskList list = new TaskList();
list.add(new TodoTask("Set up database", "Backend"));
list.add(new TodoTask("Write unit tests", "Testing"));
list.add(new TodoTask("Create README",    "Docs"));

list.printAll();
// [ ] Set up database (Backend)
// [ ] Write unit tests (Testing)
// [ ] Create README (Docs)

list.add(new TodoTask("Set up database", "Backend")).markDone();  // illustration only
```

---

## Common pitfalls

| Pitfall | Symptom | Fix |
| :-- | :-- | :-- |
| `NullPointerException` | Crash at runtime when calling a method on a variable | Initialise every variable before use; guard against null in constructors |
| Public fields | Other classes modify state directly, bypassing any validation | Always `private` fields; expose state through methods |
| No guard clauses | Object constructed with invalid state; method fails later with a cryptic error | Validate in the constructor; throw `IllegalArgumentException` early |
| Mutable `public` field | Callers bypass `addPoints` and set score directly | Use `private final` where possible; use `private` otherwise |
| Forgetting `toString()` | `System.out.println(obj)` prints `Player@1b6d3586` — the memory address | Override `toString()` for readable output |

---

## Reflective questions

1. What is the difference between a **class** and an **object**? Give a concrete example.
2. Why should fields nearly always be `private`? What goes wrong if they are `public`?
3. What is a guard clause and why should it be at the top of a constructor rather than at the point where the field is used?
4. If `ref1` and `ref2` both refer to the same object, what happens when you call `ref1.setName("Dave")`? What does `ref2.getName()` return?
5. When would you add a second constructor to a class rather than just using the main one?

---

## Lesson Context

```yaml
previous_lesson:
  topic_code: null
  domain_emphasis: Balanced

this_lesson:
  topic_code: t00_oop_fundamentals
  primary_domain_emphasis: Balanced
  difficulty_tier: Foundation
```
