---
title: "Exception Handling"
subtitle: "COMP C8Z03 � Year 2 OOP"
topic_code: t10_exception_handling
description: "Java exception class hierarchy, checked vs unchecked exceptions, try/catch/finally, multi-catch, custom exceptions, and when to throw vs when to return an Optional."
created: 2026-05-27
last_updated: 2026-05-27
version: 1.0
status: published
authors: ["OOP Teaching Team"]
tags: [java, exceptions, checked, unchecked, try-catch, custom-exception, year2, comp-c8z03]
difficulty_tier: Foundation
mlos: [MLO1, MLO2]
previous_topic: t09_interface
prerequisites:
  - Interfaces (interface types, method signatures)
  - Inheritance (extends, method overriding, polymorphism)
  - Collections I: ArrayList (basic usage)
---

# Exception Handling

> **Prerequisites:**
> - Interfaces: you can read an interface contract
> - Inheritance: you understand `extends` and polymorphism
> - Collections: you can use `ArrayList` and basic iteration

---

## What you'll learn

A quick summary of what **you** should be able to do after this lesson:

| Skill Type | You will be able to� |
| :-- | :-- |
| Understand | Describe the Java exception hierarchy: `Throwable`, `Error`, `Exception`, `RuntimeException`. |
| Understand | Distinguish checked exceptions (must handle) from unchecked (runtime) exceptions. |
| Use | Write `try`/`catch`/`finally` blocks to handle and recover from errors. |
| Use | Use multi-catch (`catch (A \| B e)`) to reduce repetition. |
| Use | Define and throw custom checked and unchecked exception classes. |
| Use | Declare `throws` on a method signature to propagate checked exceptions. |
| Analyse | Decide when to throw, when to catch, and when to propagate an exception. |
| Debug | Identify exception anti-patterns: swallowing, over-broad catch, and exception as control flow. |

---

## Why this matters

Programs meet unexpected situations: a file is missing, a user enters invalid input, a network call fails. Without a disciplined approach, the program either crashes with a cryptic stack trace or silently produces wrong results.

Java's exception mechanism gives you:
- a standard vocabulary for describing what went wrong,
- a way to separate normal logic from error-handling logic,
- compile-time enforcement for errors that callers **must** acknowledge.

---

## The Java exception hierarchy

```
Throwable
+-- Error                   ? JVM problems (OutOfMemoryError) � don't catch
+-- Exception
    +-- IOException          ? checked: file, network, stream failures
    +-- SQLException         ? checked: database errors
    +-- RuntimeException     ? unchecked: programming errors
    �   +-- NullPointerException
    �   +-- ArrayIndexOutOfBoundsException
    �   +-- IllegalArgumentException
    �   +-- IllegalStateException
    �   +-- UnsupportedOperationException
    +-- (your custom checked exceptions extend Exception directly)
```

- **Checked exceptions** � extend `Exception` (not `RuntimeException`). The compiler forces callers to either handle or declare them with `throws`.
- **Unchecked exceptions** � extend `RuntimeException`. No compiler enforcement; they indicate programming errors that should be fixed, not caught.
- **Errors** � extend `Error`. Represent JVM-level failures. Never catch these.

```kroki-plantuml
' alt: Java exception hierarchy — checked vs unchecked vs Error
@startuml
skinparam backgroundColor white
skinparam ClassFontName monospaced
skinparam ClassBackgroundColor #F8F8F8
skinparam ClassBorderColor #777
skinparam ArrowColor #444
skinparam NoteBackgroundColor #FFF8E1
skinparam NoteBorderColor #888

skinparam class {
    BackgroundColor<<error>>     #F3E5F5
    BackgroundColor<<checked>>   #E8F5E9
    BackgroundColor<<unchecked>> #FFEBEE
}

class Throwable

class Error <<error>>
class Exception

class IOException              <<checked>>
class SQLException             <<checked>>
class RuntimeException         <<unchecked>>
class NullPointerException     <<unchecked>>
class IllegalArgumentException <<unchecked>>
class ArrayIndexOutOfBoundsException <<unchecked>>
class IllegalStateException    <<unchecked>>

note right of Error            : JVM-level failure\nnever catch
note right of IOException      : checked: compiler forces\nhandle or declare
note right of RuntimeException : unchecked: programming\nerror — fix, don't catch

Throwable <|-- Error
Throwable <|-- Exception
Exception <|-- IOException
Exception <|-- SQLException
Exception <|-- RuntimeException
RuntimeException <|-- NullPointerException
RuntimeException <|-- IllegalArgumentException
RuntimeException <|-- ArrayIndexOutOfBoundsException
RuntimeException <|-- IllegalStateException
@enduml
```

---

## Part 1: try / catch / finally

```java
try {
    // Code that might throw
    int result = Integer.parseInt("abc"); // throws NumberFormatException
    System.out.println(result);
} catch (NumberFormatException e) {
    // Handle the specific exception
    System.out.println("Not a valid number: " + e.getMessage());
} finally {
    // Always runs � use for cleanup (close resources, log, reset state)
    System.out.println("Done");
}
```

Key rules:
- `catch` receives the exception object � use `e.getMessage()` and `e.getClass().getSimpleName()` for useful output.
- `finally` runs whether or not an exception was thrown, and whether or not it was caught.
- `finally` runs even if `catch` rethrows.

---

## Part 2: multiple catch blocks

```java
public static int readInt(String[] args, int index) {
    try {
        return Integer.parseInt(args[index]);
    } catch (ArrayIndexOutOfBoundsException e) {
        System.err.println("No argument at index " + index);
        return 0;
    } catch (NumberFormatException e) {
        System.err.println("Argument is not a number: " + args[index]);
        return 0;
    }
}
```

- Catch blocks are checked **top to bottom** � put more specific types first.
- A supertype catch (e.g. `catch (Exception e)`) placed first will absorb everything � usually wrong.

### Multi-catch (Java 7+)

```java
try {
    // ...
} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
    System.err.println("Input error: " + e.getMessage());
}
```

Use multi-catch when the handling logic is identical and the exception types are unrelated.

---

## Part 3: checked exceptions and throws

A method that calls code which throws a checked exception must either:
1. **Handle it** with `try`/`catch`, or
2. **Propagate it** by declaring `throws` in the signature.

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// Option A: handle it here
public static String readFirstLine(String path) {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        return br.readLine();
    } catch (IOException e) {
        System.err.println("Could not read file: " + e.getMessage());
        return null;
    }
}

// Option B: propagate � caller decides
public static String readFirstLineRaw(String path) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        return br.readLine();
    }
}
```

> **Try-with-resources** (`try (Resource r = ...)`) automatically closes `AutoCloseable` resources when the block exits � even on exception. Prefer it over `finally { r.close(); }`.

---

## Part 4: custom exceptions

### Unchecked (most common for domain errors)

```java
public class InvalidMoveException extends RuntimeException {

    private final int _row;
    private final int _col;

    public InvalidMoveException(int row, int col) {
        super("Invalid move at (" + row + ", " + col + ")");
        _row = row;
        _col = col;
    }

    public int getRow() { return _row; }
    public int getCol() { return _col; }
}
```

Throw it:
```java
public void makeMove(int row, int col) {
    if (row < 0 || row >= _size || col < 0 || col >= _size) {
        throw new InvalidMoveException(row, col);
    }
    // ...
}
```

---

### Checked (for recoverable errors that callers must acknowledge)

```java
public class TaskNotFoundException extends Exception {

    private final int _taskId;

    public TaskNotFoundException(int taskId) {
        super("Task not found: id=" + taskId);
        _taskId = taskId;
    }

    public int getTaskId() { return _taskId; }
}
```

```java
public Task findById(int id) throws TaskNotFoundException {
    for (Task t : _tasks) {
        if (t.getId() == id) return t;
    }
    throw new TaskNotFoundException(id);
}
```

---

## Progressive coding steps (A ? B ? C)

### Step A � Basic guard with unchecked exception

```java
public class BoundedStack<T> {

    private final Object[] _data;
    private int _top = 0;

    public BoundedStack(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive, got: " + capacity);
        }
        _data = new Object[capacity];
    }

    public void push(T item) {
        if (_top == _data.length) {
            throw new IllegalStateException("Stack is full");
        }
        _data[_top++] = item;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (_top == 0) {
            throw new IllegalStateException("Stack is empty");
        }
        return (T) _data[--_top];
    }
}
```

---

### Step B � Propagate and handle a checked exception

```java
public interface ConfigLoader {
    Map<String, String> load(String path) throws IOException;
}

public class FileConfigLoader implements ConfigLoader {
    @Override
    public Map<String, String> load(String path) throws IOException {
        Map<String, String> config = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    config.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        return config;
    }
}

// In main or a service:
ConfigLoader loader = new FileConfigLoader();
try {
    Map<String, String> cfg = loader.load("settings.properties");
    System.out.println(cfg);
} catch (IOException e) {
    System.err.println("Config load failed: " + e.getMessage());
}
```

---

### Step C � Custom exception hierarchy

```java
// Base domain exception
public class GameException extends RuntimeException {
    public GameException(String message) { super(message); }
    public GameException(String message, Throwable cause) { super(message, cause); }
}

// Specific subtypes
public class InvalidMoveException extends GameException {
    public InvalidMoveException(int row, int col) {
        super("Invalid move at (" + row + ", " + col + ")");
    }
}

public class GameOverException extends GameException {
    private final String _winner;
    public GameOverException(String winner) {
        super("Game over � winner: " + winner);
        _winner = winner;
    }
    public String getWinner() { return _winner; }
}
```

Callers can catch the base type `GameException` to handle any game error, or catch a specific subtype for targeted recovery.

---

## Games example: move validation

```java
public class ChessBoard {

    private final char[][] _board;
    private final int _size = 8;

    public ChessBoard() {
        _board = new char[_size][_size];
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        validateCoordinate(fromRow, fromCol, "from");
        validateCoordinate(toRow,   toCol,   "to");

        if (_board[fromRow][fromCol] == 0) {
            throw new InvalidMoveException(fromRow, fromCol); // no piece there
        }

        _board[toRow][toCol]     = _board[fromRow][fromCol];
        _board[fromRow][fromCol] = 0;
    }

    private void validateCoordinate(int row, int col, String label) {
        if (row < 0 || row >= _size || col < 0 || col >= _size) {
            throw new IllegalArgumentException(
                "Coordinate " + label + " (" + row + "," + col + ") out of bounds"
            );
        }
    }
}
```

Usage:
```java
ChessBoard board = new ChessBoard();
try {
    board.movePiece(0, 0, 8, 0);   // out of bounds
} catch (IllegalArgumentException e) {
    System.err.println("Bad coordinates: " + e.getMessage());
} catch (InvalidMoveException e) {
    System.err.println("No piece at: (" + e.getRow() + "," + e.getCol() + ")");
}
```

---

## Software example: DAO layer with checked exceptions

```java
public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(int id) { super("Task not found: " + id); }
}

public interface TaskDao {
    Task findById(int id) throws TaskNotFoundException;
    void save(Task task) throws IOException;
}

public class InMemoryTaskDao implements TaskDao {

    private final Map<Integer, Task> _store = new HashMap<>();

    @Override
    public Task findById(int id) throws TaskNotFoundException {
        Task t = _store.get(id);
        if (t == null) throw new TaskNotFoundException(id);
        return t;
    }

    @Override
    public void save(Task task) {
        _store.put(task.getId(), task);
    }
}

// Service layer catches and translates
public class TaskService {

    private final TaskDao _dao;

    public TaskService(TaskDao dao) { _dao = dao; }

    public Optional<Task> getTask(int id) {
        try {
            return Optional.of(_dao.findById(id));
        } catch (TaskNotFoundException e) {
            return Optional.empty();
        }
    }
}
```

---

## When to throw vs when to return Optional

| Situation | Prefer |
| :-- | :-- |
| Caller **must** acknowledge the error (e.g. file not found) | Checked exception (`throws`) |
| Programming error � invalid argument, violated invariant | Unchecked (`IllegalArgumentException`, etc.) |
| "Not found" is a normal, expected outcome | `Optional<T>` |
| Error in infrastructure (DB, network) that upper layers handle | Checked exception, or wrap in unchecked domain exception |
| Validating user input at the boundary | Return `false`/`Optional` or throw a domain `ValidationException` |

---

## Common pitfalls

| Anti-pattern | Problem | Better approach |
| :-- | :-- | :-- |
| `catch (Exception e) {}` � swallowing | Error silently disappears; root cause lost | At minimum log `e.getMessage()` and rethrow or return a sentinel |
| `catch (Exception e)` � too broad | Catches `NullPointerException`, `StackOverflowError`, everything | Catch the specific type you can actually handle |
| Exception as control flow | `try { find() } catch (NotFoundException) { return default; }` in a tight loop | Use `containsKey`/`Optional` instead; exceptions are expensive |
| Losing the cause | `throw new MyException("message")` without passing original `e` | `throw new MyException("message", e)` � preserves the stack trace chain |
| Empty `finally` closing nothing | Forgetting to close resources | Use try-with-resources |

---

## Reflective Questions

- What is the difference between a checked and an unchecked exception? Give one example of each from the Java standard library.
- When is it appropriate to define your own exception class instead of using a built-in one?
- Why is `catch (Exception e) {}` considered dangerous?
- Should a `findById` method in a DAO throw a checked exception or return an `Optional`? What factors influence the decision?
- How does try-with-resources work, and what interface must a resource implement to use it?

---

## Lesson Context

```yaml
previous_lesson:
  topic_code: t09_interface
  domain_emphasis: Balanced

this_lesson:
  topic_code: t10_exception_handling
  primary_domain_emphasis: Balanced
  difficulty_tier: Foundation
mlos: [MLO1, MLO2]
```
