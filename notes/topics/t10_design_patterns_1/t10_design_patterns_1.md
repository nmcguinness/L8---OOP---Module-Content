(file regenerated to refresh sandbox link)

# Design Patterns I — Behaviour & Decoupling

> **Prerequisites:**
> - You can create classes with fields, constructors, and methods  
> - You can use `ArrayList` / `LinkedList` with loops  
> - You understand inheritance and interface references (polymorphism)

---

## What you’ll learn



| Skill Type | You will be able to… |
| :- | :- |
| Understand | Explain why repeated conditional logic leads to brittle designs. |
| Understand | Describe the intent of the Strategy and Command patterns. |
| Apply | Implement Strategy to replace conditional behaviour. |
| Apply | Implement Command to decouple request invocation from execution. |
| Analyse | Compare pattern-based designs with conditional-based alternatives and justify trade-offs. |
| Debug | Identify when a pattern has been misapplied or over-engineered. |

---

## Why this matters

As programs grow, **decisions multiply**.  
If those decisions are encoded as `if`/`switch` logic scattered throughout the codebase, change becomes risky and expensive.

Design patterns give us **names and structures** for solving recurring problems:
- varying behaviour,
- decoupling responsibilities,
- controlling when and how actions occur.

In games, patterns help manage AI behaviours and input handling.  
In software systems, they underpin workflows, services, and event handling.

---

## How this builds on previous content

You already know how to:
- define interfaces,
- implement polymorphism,
- compose objects.

Design patterns **combine those tools** to solve problems of **scale and change**, not syntax.

---

## Core idea: patterns are responses to pain

Before learning any pattern, ask:

> *What problem is this pattern trying to reduce or eliminate?*

Patterns are not recipes.  
They are **documented responses to design pressure**.

---

## Pattern 1: Strategy

### Intent
> Define a family of algorithms, encapsulate each one, and make them interchangeable.

---

### Step A — Strategy interface

```java
public interface ExecutionStrategy {
    void execute(Task task);
}
```

---

### Step B — Concrete strategies

```java
public class FastExecution implements ExecutionStrategy {
    public void execute(Task task) {
        task.runUnchecked();
    }
}

public class SafeExecution implements ExecutionStrategy {
    public void execute(Task task) {
        task.validate();
        task.run();
    }
}
```

---

### Step C — Context uses the strategy

```java
public class TaskRunner {

    private final ExecutionStrategy _strategy;

    public TaskRunner(ExecutionStrategy strategy) {
        _strategy = strategy;
    }

    public void run(Task task) {
        _strategy.execute(task);
    }
}
```

---

## Pattern 2: Command

### Intent
> Encapsulate a request as an object.

---

### Command interface

```java
public interface Command {
    void execute();
}
```

---

### Concrete command

```java
public class SaveTaskCommand implements Command {

    private final Task _task;
    private final TaskRepository _repository;

    public SaveTaskCommand(Task task, TaskRepository repository) {
        _task = task;
        _repository = repository;
    }

    @Override
    public void execute() {
        _repository.save(_task);
    }
}
```

---

### Invoker

```java
public class CommandQueue {

    private final Queue<Command> _queue = new ArrayDeque<>();

    public void add(Command command) {
        _queue.add(command);
    }

    public void processAll() {
        while (!_queue.isEmpty()) {
            _queue.poll().execute();
        }
    }
}
```

---


<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    🌍 Real-world: Task processing with Strategy &amp; Command
  </summary>
  <div style="margin-top:0.8rem;">

**Context**  
In many real systems, work is submitted in one place, executed elsewhere, and processed differently depending on configuration or runtime conditions.  
This example models a simplified task-processing backend where:
- *how* a task is executed varies (Strategy), and
- *what* work is to be done can be queued or deferred (Command).

This prepares the ground for **thread pools**, **server-side handlers**, and **distributed execution** later in the semester.

```java
public interface TaskExecutionStrategy {
    void execute(Task task);
}

public final class ImmediateExecution implements TaskExecutionStrategy {
    @Override
    public void execute(Task task) {
        task.run();
    }
}

public final class ValidatedExecution implements TaskExecutionStrategy {
    @Override
    public void execute(Task task) {
        task.validate();
        task.run();
    }
}
```

```java
public interface Command {
    void execute();
}

public final class ExecuteTaskCommand implements Command {

    private final Task _task;
    private final TaskExecutionStrategy _strategy;

    public ExecuteTaskCommand(Task task, TaskExecutionStrategy strategy) {
        _task = task;
        _strategy = strategy;
    }

    @Override
    public void execute() {
        _strategy.execute(_task);
    }
}
```

```java
public final class TaskQueue {

    private final Queue<Command> _queue = new ArrayDeque<>();

    public void submit(Command command) {
        _queue.add(command);
    }

    public void processAll() {
        while (!_queue.isEmpty())
            _queue.poll().execute();
    }
}
```

**Why this matters**
- Strategies allow execution policies to change without modifying the queue or commands.
- Commands turn “work” into data that can later be logged, threaded, retried, or sent across a network.
- This structure naturally extends to:
  - multi-threaded execution (ExecutorService),
  - server-side request handlers,
  - distributed task processing.

In later weeks, this queue will stop executing tasks directly and instead **dispatch them to worker threads or remote services**.

  </div>
</details>

---

## Further Reading

The following resources provide clear explanations, diagrams, and refactoring-oriented perspectives on the patterns covered in this lesson:

- **Refactoring Guru — Strategy Pattern**  
  https://refactoring.guru/design-patterns/strategy  
  Clear motivation, UML diagrams, and refactoring examples.

- **Refactoring Guru — Command Pattern**  
  https://refactoring.guru/design-patterns/command  
  Excellent for understanding commands as objects and how they enable undo/queueing.

- **Martin Fowler — Refactoring**  
  https://martinfowler.com/books/refactoring.html  
  Foundational text linking patterns directly to improving existing codebases.

- **SourceMaking — Design Patterns**  
  https://sourcemaking.com/design_patterns  
  Useful for pattern comparison and recognising “when you already have one”.

---

## Lesson Context (YAML footer)

```yaml
previous_lesson:
  topic_code: t09_generics_2
  domain_emphasis: Balanced

this_lesson:
  topic_code: t10_design_patterns_1
  primary_domain_emphasis: Balanced
  difficulty_tier: Intermediate
```
