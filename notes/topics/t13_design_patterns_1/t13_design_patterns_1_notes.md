---
title: "Design Patterns I � Behaviour & Decoupling"
subtitle: "COMP C8Z03 � Year 2 OOP"
topic_code: t13_design_patterns_1
description: "Why conditional-heavy designs become brittle, and how Strategy and Command help you encapsulate behaviour and work to reduce coupling."
created: 2026-02-04
last_updated: 2026-04-14
version: 1.0
status: published
authors: ["OOP Teaching Team"]
tags: [java, design-patterns, strategy, command, decoupling, oop, year2, comp-c8z03]
difficulty_tier: Intermediate
mlos: [MLO2]
previous_topic: t12_generics_2
prerequisites:
  - Classes (fields, constructors, methods)
  - Collections (ArrayList / LinkedList + loops)
  - Inheritance and interface references (polymorphism)
---

# Design Patterns I � Behaviour & Decoupling

> **Prerequisites:**
> - You can create classes with fields, constructors, and methods  
> - You can use `ArrayList` / `LinkedList` with loops  
> - You understand inheritance and interface references (polymorphism)

---

## What you�ll learn



| Skill Type | You will be able to� |
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

## Supporting types used in examples

Both patterns below operate on a simple `Task` domain object and a `TaskRepository`. These are defined once here so the pattern code stays focused.

```java
// The work item that strategies operate on
public interface Task {
    String getTitle();
    void validate();         // throws IllegalStateException if invalid
    void run();              // normal execution
    void runUnchecked();     // fast path, no validation
}

// Persistence abstraction used by the Command examples
public interface TaskRepository {
    void save(Task task);
    Optional<Task> findById(int id);
}
```

> In your own projects, these would be concrete classes or interfaces you define. The patterns do not care what `Task` contains � they only depend on the interface.

---

## Pattern 1: Strategy

### Intent
> Define a family of algorithms, encapsulate each one, and make them interchangeable.

```kroki-plantuml
' alt: Strategy pattern — Context delegates to an interchangeable Strategy implementation
@startuml
skinparam backgroundColor white
skinparam ClassFontName monospaced
skinparam ClassBackgroundColor #F8F8F8
skinparam ClassBorderColor #777
skinparam ArrowColor #444

interface ExecutionStrategy {
    + execute(task : Task)
}

class TaskRunner {
    - _strategy : ExecutionStrategy
    + TaskRunner(strategy : ExecutionStrategy)
    + setStrategy(s : ExecutionStrategy)
    + run(task : Task)
}

class FastExecution {
    + execute(task : Task)
}

class SafeExecution {
    + execute(task : Task)
}

TaskRunner o-right-> ExecutionStrategy : delegates to
ExecutionStrategy <|.. FastExecution
ExecutionStrategy <|.. SafeExecution
@enduml
```

---

### Step A � Strategy interface

```java
public interface ExecutionStrategy {
    void execute(Task task);
}
```

---

### Step B � Concrete strategies

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

### Step C � Context uses the strategy

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

### Trade-offs: Strategy

| Pro | Con |
| :-- | :-- |
| Replaces `if`/`switch` on type with polymorphism | One class per strategy � more files |
| New behaviour = new class, no existing code changed | Client must choose which strategy to inject |
| Strategy can be swapped at runtime | Overkill when only one strategy will ever exist |

---

## Pattern 2: Command

### Intent
> Encapsulate a request as an object.

```kroki-plantuml
' alt: Command pattern — Invoker queues Commands; each Command calls a Receiver
@startuml
skinparam backgroundColor white
skinparam ClassFontName monospaced
skinparam ClassBackgroundColor #F8F8F8
skinparam ClassBorderColor #777
skinparam ArrowColor #444

interface Command {
    + execute()
    + undo()
}

class TaskInvoker {
    - _history : List<Command>
    + submit(cmd : Command)
    + undoLast()
}

class SaveTaskCommand {
    - _task : Task
    - _repo : TaskRepository
    + execute()
    + undo()
}

class DeleteTaskCommand {
    - _task : Task
    - _repo : TaskRepository
    + execute()
    + undo()
}

class TaskRepository {
    + save(task : Task)
    + delete(task : Task)
}

TaskInvoker o-right-> Command : queues
Command <|.. SaveTaskCommand
Command <|.. DeleteTaskCommand
SaveTaskCommand   -right-> TaskRepository : calls
DeleteTaskCommand -right-> TaskRepository : calls
@enduml
```

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

### Trade-offs: Command

| Pro | Con |
| :-- | :-- |
| Decouples the caller from execution � caller never knows how work is done | One class per command type � more files |
| Commands can be queued, logged, retried, or threaded | Can be over-engineered for simple one-shot calls |
| Enables undo/redo by storing executed commands | All command state must be captured at creation time |

---

## Games example

### Strategy � AI behaviour that changes at runtime

```java
// Strategy interface: all behaviours share this contract
public interface AiBehaviour {
    void update(Enemy self, Player target);
}

public class PatrolBehaviour implements AiBehaviour {
    @Override
    public void update(Enemy self, Player target) {
        self.moveAlongWaypoints();
    }
}

public class ChaseBehaviour implements AiBehaviour {
    @Override
    public void update(Enemy self, Player target) {
        self.moveToward(target.getPosition());
    }
}

public class FleeBehaviour implements AiBehaviour {
    @Override
    public void update(Enemy self, Player target) {
        self.moveAwayFrom(target.getPosition());
    }
}

// Context: switches strategy based on game state
public class Enemy {

    private AiBehaviour _behaviour = new PatrolBehaviour();

    public void setBehaviour(AiBehaviour behaviour) {
        _behaviour = behaviour;
    }

    public void update(Player target) {
        if (getHealth() < 20) {
            setBehaviour(new FleeBehaviour()); // runtime swap
        }
        _behaviour.update(this, target);
    }
    // ... fields, getHealth(), moveToward(), etc.
}
```

*No `if (behaviour == PATROL) ... else if (behaviour == CHASE)` anywhere. Adding `StealthBehaviour` means one new class � nothing else changes.*

---

### Command � input action queue for replay

```java
// Command interface with optional undo
public interface PlayerAction {
    void execute();
}

public class AttackCommand implements PlayerAction {

    private final Enemy  _target;
    private final int    _damage;

    public AttackCommand(Enemy target, int damage) {
        _target = target;
        _damage = damage;
    }

    @Override
    public void execute() {
        _target.takeDamage(_damage);
    }
}

public class UseItemCommand implements PlayerAction {

    private final Player _player;
    private final Item   _item;

    public UseItemCommand(Player player, Item item) {
        _player = player;
        _item   = item;
    }

    @Override
    public void execute() {
        _item.applyTo(_player);
    }
}

// Invoker: replay or defer action sequences
public class ActionReplay {

    private final List<PlayerAction> _log = new ArrayList<>();

    public void record(PlayerAction action) {
        action.execute();
        _log.add(action);
    }

    public void replay() {
        for (PlayerAction a : _log) a.execute();
    }
}
```

*Saving `_log` to a file lets you replay a match. Adding `void undo()` to the interface enables a full undo stack.*

---

## Software example

### Strategy � pluggable notification channel

```java
public interface NotificationStrategy {
    void send(String recipient, String message);
}

public class EmailNotification implements NotificationStrategy {
    @Override
    public void send(String recipient, String message) {
        System.out.println("EMAIL to " + recipient + ": " + message);
    }
}

public class LogNotification implements NotificationStrategy {
    @Override
    public void send(String recipient, String message) {
        System.out.println("[LOG] " + recipient + " | " + message);
    }
}

// Context � works with any strategy
public class AlertService {

    private NotificationStrategy _strategy;

    public AlertService(NotificationStrategy strategy) {
        _strategy = strategy;
    }

    public void setStrategy(NotificationStrategy strategy) {
        _strategy = strategy;
    }

    public void alert(String recipient, String message) {
        _strategy.send(recipient, message);
    }
}
```

Usage:

```java
AlertService alerts = new AlertService(new EmailNotification());
alerts.alert("admin@example.com", "Server CPU > 90%");

alerts.setStrategy(new LogNotification()); // switch channel at runtime
alerts.alert("admin@example.com", "Disk usage normal");
```

---

### Command � undoable text editor operations

```java
public interface EditorCommand {
    void execute();
    void undo();
}

public class InsertTextCommand implements EditorCommand {

    private final StringBuilder _buffer;
    private final String        _text;
    private final int           _position;

    public InsertTextCommand(StringBuilder buffer, String text, int position) {
        _buffer   = buffer;
        _text     = text;
        _position = position;
    }

    @Override public void execute() { _buffer.insert(_position, _text); }
    @Override public void undo()    { _buffer.delete(_position, _position + _text.length()); }
}

public class Editor {

    private final StringBuilder      _buffer  = new StringBuilder();
    private final Deque<EditorCommand> _history = new ArrayDeque<>();

    public void execute(EditorCommand cmd) {
        cmd.execute();
        _history.push(cmd);
    }

    public void undo() {
        if (!_history.isEmpty()) _history.pop().undo();
    }

    public String content() { return _buffer.toString(); }
}
```

---

## Reflective questions

1. A `TaskRunner` has an `if (type.equals("FAST")) ... else if (type.equals("SAFE")) ...` branch. Why is this a problem when a third execution mode is added?
2. What is the structural difference between Strategy and Command? Both use interfaces with a single method � what distinguishes them?
3. Why must a Command capture all its state at construction time rather than reading it at execution time?
4. You have five concrete strategies and only one will ever be active at a time. Is this still better than an `if`/`switch`? Justify your answer.
5. How does the Command pattern enable undo? What additional method would you add to the `Command` interface, and what state must each concrete command store?

---


<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ?? Real-world: Task processing with Strategy &amp; Command
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
- Commands turn �work� into data that can later be logged, threaded, retried, or sent across a network.
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

- **Refactoring Guru � Strategy Pattern**  
  https://refactoring.guru/design-patterns/strategy  
  Clear motivation, UML diagrams, and refactoring examples.

- **Refactoring Guru � Command Pattern**  
  https://refactoring.guru/design-patterns/command  
  Excellent for understanding commands as objects and how they enable undo/queueing.

- **Martin Fowler � Refactoring**  
  https://martinfowler.com/books/refactoring.html  
  Foundational text linking patterns directly to improving existing codebases.

- **SourceMaking � Design Patterns**  
  https://sourcemaking.com/design_patterns  
  Useful for pattern comparison and recognising �when you already have one�.

---

## Lesson Context

```yaml
previous_lesson:
  topic_code: t12_generics_2
  domain_emphasis: Balanced

this_lesson:
  topic_code: t13_design_patterns_1
  primary_domain_emphasis: Balanced
  difficulty_tier: Intermediate
mlos: [MLO2]
```
