# Design Patterns II — Structure, Coordination & Extension

> **Prerequisites:**
> - You understand Strategy and Command patterns  
> - You can reason about interfaces and composition  
> - You can identify tight coupling and conditional logic smells  
> - You are comfortable with basic collections and queues  

---

## What you’ll learn

| Skill Type | You will be able to… |
| :- | :- |
| Understand | Explain why object creation and coordination become design problems at scale. |
| Apply | Use Factory to remove creation logic from clients. |
| Apply | Use Observer to decouple state change from reaction. |
| Apply | Use Adapter to integrate incompatible interfaces safely. |
| Analyse | Identify when multiple patterns must collaborate to solve a single problem. |
| Debug | Diagnose over-coupling caused by misplaced responsibilities. |

---

## Why this matters

In Design Patterns I, we learned how to:
- encapsulate behaviour (Strategy),
- encapsulate work (Command).

As systems grow, new problems appear:
- object creation logic spreads,
- components need to react to events,
- external APIs do not fit our design.

This lesson focuses on **structural and coordination pressure**: how parts of a system are created, how they react to change, and how they evolve without collapsing into conditional logic.

---

## How this builds on Design Patterns I

From last week, we already have:
- Commands representing work,
- Queues executing commands,
- Strategies defining execution policy.

That design works — **until it doesn’t**.  
Today we fix the cracks.

---

## Pattern 1: Factory

### The pain: creation logic leakage

```java
Command cmd =
    new ExecuteTaskCommand(
        task,
        new ValidatedExecution()
    );
```

Problems:
- clients must know which strategy to use,
- object graphs grow complex,
- changes ripple outward.

Creation is becoming a **responsibility**, not a detail.

---

### Intent
Encapsulate object creation so clients depend on *what* they want, not *how* it is built.

### Pattern roles
- **Client**: requests an object
- **Factory**: decides how objects are created
- **Product**: interface of the created object

### Trade-offs
- Adds indirection and extra types
- Centralises responsibility (can become a bottleneck)
- Overkill for very small systems

---

### Factory interface

```java
public interface TaskCommandFactory {
    Command createFor(Task task);
}
```

---

### Concrete factory

```java
public final class DefaultTaskCommandFactory
        implements TaskCommandFactory {

    private final TaskExecutionStrategy _strategy;

    public DefaultTaskCommandFactory(TaskExecutionStrategy strategy) {
        _strategy = strategy;
    }

    @Override
    public Command createFor(Task task) {
        return new ExecuteTaskCommand(task, _strategy);
    }
}
```

---

<details style="background:#fffdf0; border:1px solid rgba(161,98,7,0.22); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    💡 Aside: Why Factory beats scattered <code>new</code>
  </summary>
  <div>

Using <code>new</code> is not bad — **spreading creation logic everywhere is**.

Factory allows:
- configuration-based creation,
- testing with substitutes,
- future extension without client changes.

  </div>
</details>

---

## Pattern 2: Observer

### The pain: hidden coordination

Suppose we want to:
- log task completion,
- update a UI,
- trigger follow-up work.

Embedding this logic directly into execution reintroduces **tight coupling**.

---

### Intent
Define a one-to-many dependency so that when one object changes state, all dependents are notified automatically.

### Pattern roles
- **Subject**: publishes events
- **Observer**: reacts to changes
- **ConcreteObserver**: performs specific reactions

### Trade-offs
- Control flow becomes indirect
- Debugging event chains can be harder
- Poor naming leads to confusion

---

### Observer interface

```java
public interface TaskListener {
    void onCompleted(Task task);
}
```

---

### Subject (publisher)

```java
public final class ObservableTaskQueue {

    private final List<TaskListener> _listeners = new ArrayList<>();
    private final Queue<Command> _queue = new ArrayDeque<>();

    public void addListener(TaskListener listener) {
        _listeners.add(listener);
    }

    public void submit(Command command) {
        _queue.add(command);
    }

    public void processAll() {
        while (!_queue.isEmpty()) {
            _queue.poll().execute();
            notifyListeners();
        }
    }

    private void notifyListeners() {
        for (TaskListener l : _listeners)
            l.onCompleted(null); // simplified
    }
}
```

---

### Why this is non-trivial

This introduces **coordination without dependency**:
- the queue does not know who is listening,
- listeners evolve independently,
- reactions can be added without modification.

---

## Pattern 3: Adapter

### The pain: incompatible interfaces

Assume an external API:

```java
public interface ExternalJob {
    void runJob();
}
```

Your system expects `Command`.

---

### Intent
Convert the interface of a class into another interface clients expect.

### Pattern roles
- **Target**: expected interface (`Command`)
- **Adaptee**: existing incompatible class
- **Adapter**: bridges the two

### Trade-offs
- Adds an extra layer
- Can hide deeper design problems
- Should not patch poor internal design

---

### Adapter example

```java
public final class ExternalJobAdapter implements Command {

    private final ExternalJob _job;

    public ExternalJobAdapter(ExternalJob job) {
        _job = job;
    }

    @Override
    public void execute() {
        _job.runJob();
    }
}
```

---

<details style="background:#fff5f5; border:1px solid rgba(220,38,38,0.25); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ⚠️ Gotchas: Adapter vs rewrite
  </summary>
  <div>

Adapter is appropriate when:
- you do not control the external API,
- rewriting would duplicate logic,
- isolation from change is required.

If you control the code, redesign may be better.

  </div>
</details>

---

## 🌍 Real-world: Coordinated task processing

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    🌍 Real-world: Coordinated task processing
  </summary>
  <div>

**Context**  
We now have:
- Commands representing work,
- Factories controlling creation,
- Observers reacting to execution,
- Adapters integrating foreign jobs.

**Patterns in play**
- Factory: controls creation of Commands
- Command: encapsulates work units
- Observer: reacts to execution events
- Adapter: integrates external jobs

**Why this matters**
- Creation, execution, and reaction are separated.
- New features attach around the system.
- This is the minimum structure needed before concurrency.

Next week, this design will **fail under parallel load** — and we will fix it with threads.

  </div>
</details>

---

## Pattern comparison (important)

- **Factory vs Strategy** — creation vs behaviour  
- **Observer vs Command** — reaction to state vs representation of work  
- **Adapter vs Refactoring** — protection vs redesign  

---

<details style="background:#fff5f5; border:1px solid rgba(220,38,38,0.25); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">
    ⚠️ Anti-pattern: Pattern soup
  </summary>
  <div>

Combining many patterns without real design pressure leads to unnecessary indirection,
harder debugging, and unclear ownership.

Patterns should remove complexity — not introduce it.

  </div>
</details>

---

### Design prompt (no coding)

You need to add:
- task retries,
- failure logging,
- delayed execution.

Which patterns would you introduce or extend? Justify your choices.

---

### Pattern smell checklist

- Is object creation scattered across the system?
- Do components know too much about each other?
- Does adding behaviour require modifying many classes?

If yes, a pattern may be missing — or misused.

---

## Reflective questions

1. Why does Factory reduce coupling compared to direct construction?
2. What problem does Observer solve that Strategy does not?
3. When is Adapter preferable to refactoring existing code?
4. Which pattern would you introduce first in a growing system — and why?
5. How do these patterns prepare the system for concurrency?

---

## Further reading

- Refactoring Guru — Factory Method  
  https://refactoring.guru/design-patterns/factory-method
- Refactoring Guru — Observer  
  https://refactoring.guru/design-patterns/observer
- Refactoring Guru — Adapter  
  https://refactoring.guru/design-patterns/adapter
- Martin Fowler — Patterns of Enterprise Application Architecture  
  https://martinfowler.com/books/eaa.html

---

## Lesson Context (YAML footer)

```yaml
previous_lesson:
  topic_code: t10_design_patterns_1
  domain_emphasis: Balanced

this_lesson:
  topic_code: t11_design_patterns_2
  primary_domain_emphasis: Balanced
  difficulty_tier: Intermediate
```
