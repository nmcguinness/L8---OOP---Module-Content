---
title: "Interfaces"
subtitle: "COMP C8Z03 — Year 2 OOP"
description: "A clear, structured introduction to Java interfaces, why they matter, how they support polymorphism, and how they’re used in Games and Software."
created: 2025-11-24
version: 1.1
authors: ["OOP Teaching Team"]
tags: [java, interfaces, polymorphism, year2, comp-c8z03]
prerequisites:
  - Inheritance basics (superclasses, method overriding)
  - Collections I–II (ArrayList, LinkedList, Iterators)
---

# Interfaces  
> **Prerequisites:** 
> - You should be comfortable with inheritance, basic polymorphism, and using Java’s Collections API.

---

## **What you’ll learn**

| Skill Type | You will be able to… |
| :-- | :-- |
| **Conceptual Understanding** | Explain what an interface is, why Java uses them, and how they differ from classes and abstract classes. |
| **Design Skills** | Decide when to use interfaces in your own programs (games or software). |
| **Code Implementation** | Declare interfaces, implement them in classes, and use interface references to enable polymorphism. |
| **Problem-Solving** | Use interfaces to simplify code, reduce duplication, and improve flexibility. |

---

## **Why this matters**
Interfaces solve a major design problem: **how do we express shared behaviour when inheritance alone isn’t enough?**  
In games, systems often need to treat different objects *the same way* (e.g., anything damageable, anything interactable).  
In software, interfaces help decouple logic from implementation, allowing plug-and-play replacements, test doubles, and modular design.

Interfaces make your code **more flexible**, **more reusable**, and **easier to extend** without rewriting everything.

---

## **How this builds on previous content**
You have already seen:
- How subclasses inherit structure/behaviour from a base class.
- How overriding enables polymorphism.
- How Collections store groups of objects.

Interfaces extend this by allowing:
- A **shared behavioural contract** without shared fields.
- **Multiple implementations** with different internal data.
- **Objects of different class hierarchies** to be treated uniformly.

---

# **Core Ideas / Concepts**

> For each core idea, we explain the concept, provide a small Java snippet, and then a short explanation of what the snippet demonstrates.

---

### **Core Idea 1 — Interfaces define behaviour, not structure**

Interfaces list *what* an object must be able to do, not *how* it does it.

```java
public interface Interactable {
    void interact();
}
```

**Snippet explanation:**  
Any class implementing `Interactable` must provide an `interact()` method. No instance fields, no constructors — just a behavioural promise.

---

### **Core Idea 2 — Interfaces can define constants (`public static final`)**

Interfaces cannot have normal instance fields, but they **can** declare constants.  
All fields in an interface are implicitly `public static final`, even if you don’t write those keywords.

```java
public interface PhysicsSettings {
    double GRAVITY = 9.81;   // public static final automatically
    int MAX_SPEED = 20;
}
```

**Snippet explanation:**  
You access constants as `PhysicsSettings.GRAVITY`.  
These are compile-time constants, shared across the entire application.  
Interfaces **still cannot store per-object state**.

---

### **Core Idea 3 — Classes implement interfaces**

```java
public class Door implements Interactable {
    public void interact() {
        System.out.println("You open the door.");
    }
}
```

**Snippet explanation:**  
`Door` is now an “Interactable thing.” You can treat it as a `Door` or as an `Interactable`.

---

### **Core Idea 4 — Interface references enable polymorphism**

```java
Interactable obj = new Door();
obj.interact();
```

**Snippet explanation:**  
Even though the variable's type is `Interactable`, Java calls the method from the actual object (`Door`).  
This is standard polymorphic behaviour.

---

### **Core Idea 5 — A class can implement multiple interfaces**

```java
public interface Damageable { void takeDamage(int amount); }

public class Barrel implements Interactable, Damageable {
    public void interact() { System.out.println("You tap the barrel."); }
    public void takeDamage(int amt) { System.out.println("Barrel cracks!"); }
}
```

**Snippet explanation:**  
Multiple behaviours, unrelated systems. Interfaces allow cross-cutting behaviours cleanly.

---

### **Core Idea 6 — Interfaces + Collections = Power**

```java
List<Interactable> interactables = new ArrayList<>();
interactables.add(new Door());
interactables.add(new Barrel());

for (Interactable i : interactables)
    i.interact();
```

**Snippet explanation:**  
Objects from unrelated hierarchies can be processed uniformly because they share the interface.

---

# **Progressive Coding Steps (A → B → C)**

### Step A — Create a behaviour
```java
public interface Moveable {
    void move();
}
```

### Step B — Implement twice
```java
public class Player implements Moveable {
    public void move() { System.out.println("Player walks."); }
}

public class Enemy implements Moveable {
    public void move() { System.out.println("Enemy shuffles."); }
}
```

### Step C — Use polymorphically
```java
Moveable m = new Enemy();
m.move();
```

**Observation:**  
The calling code only cares that the object can move — not how it’s implemented.

---

# **Useful Snippets (Guards & Helpers)**

### Null-safe usefulness wrapper

```java
public static void safeInteract(Interactable obj) {
    if (obj == null)
        System.out.println("Nothing to interact with.");
    else
        obj.interact();
}
```

**Why?**  
Students frequently call methods on null references. This pattern avoids common crashes.

---

# **Games Example — Interactable World Objects + Interface Constants**

Here we add constants to an interface to demonstrate **useful, non-contrived cases**.

```java
public interface Interactable {
    int DEFAULT_INTERACTION_RANGE = 2; // constant

    void interact();
}

public class Chest implements Interactable {
    public void interact() {
        System.out.println("Chest opens. Loot spills out.");
    }
}

public class NPC implements Interactable {
    public void interact() {
        System.out.println("NPC: Hello, traveller!");
    }
}
```

**Example usage:**

```java
if (distanceToPlayer < Interactable.DEFAULT_INTERACTION_RANGE)
    target.interact();
```

**Explanation:**  
Game designers/systems engineers frequently want **shared constants** for ranges, layers, tags, or cooldowns.  
Interfaces can group these constants when they conceptually belong to a unified behaviour.

---

# **Software Development Example — Logging System with Interface Constants**

```java
public interface Logger {
    String INFO = "[INFO]";      // constants
    String ERROR = "[ERROR]";

    void log(String message);
}

public class ConsoleLogger implements Logger {
    public void log(String msg) {
        System.out.println(INFO + " " + msg);
    }
}

public class FileLogger implements Logger {
    public void log(String msg) {
        // (Pseudo) write ERROR/INFO-coded log to file
    }
}
```

**Explanation:**  
The constants allow consistent formatting across all implementations.  
In real applications, interface constants are commonly used for **log levels, format strings, or system tags**.

---

# **Debugging & Pitfalls**

| Mistake | Why it happens | Fix |
| :-- | :-- |:-|
| Trying to put instance fields in an interface | Confusion with abstract classes | Use an abstract class if shared state is needed |
| Forgetting to implement all interface methods | The class cannot compile | Implement all or mark class `abstract` |
| Trying to instantiate an interface | Interfaces have no constructors | Instantiate concrete implementing classes |
| Overusing interface constants | Students use interface as a “constants dump” | Prefer enums or config classes unless constants are tied to behaviour |
| Mixing “is-a” and “can-do” thinking | Misunderstanding the purpose | Interface = ability; Class = identity + data |

---

# **Reflective Questions**

- When is an interface better than an abstract class?
- In your last assignment, which behaviours would make good interfaces?
- How do interface constants help with consistency in design?
- Can you name a behaviour in your game engine project that deserves a shared interface?

---

```yaml
previous_lesson:
  topic_code: t06_inheritance
  domain_emphasis: Balanced

this_lesson:
  primary_domain_emphasis: Balanced
  difficulty_tier: Intermediate
```
