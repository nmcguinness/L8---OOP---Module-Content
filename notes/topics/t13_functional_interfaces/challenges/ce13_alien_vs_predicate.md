---
title: "Challenge Exercise: Alien vs Predicate — Shipboard Incident Triage"
subtitle: "Functional interfaces + Comparator factory + one design pattern"
description: "Build a playful but realistic incident triage pipeline on a spaceship. Ingest JSON incidents, filter with predicates, prioritise via a comparator factory, and execute responses using one design pattern."
created: 2026-02-11
generated_at: "2026-02-12T10:45:03Z"
version: 1.0
authors: ["OOP Teaching Team"]
tags: ["java", "challenge", "functional-interfaces", "java.util.function", "comparator", "collections", "design-patterns", "year2", "comp-c8z03"]
prerequisites:
  - "t13_functional_interfaces.md"
  - "Design Patterns I/II notes"
  - "Collections I/II"
---

# Challenge Exercise: Alien vs Predicate — Shipboard Incident Triage

## Scenario
The commercial towing ship **NOSTROMO-ISH** is returning home when **MOTHER** starts receiving incident reports from a third‑party “sensor aggregation service” (in JSON format).

Some incidents are harmless ship noise. Some are crew error. Some… look *alien*.

Your job is to build a small **incident triage** system that:

1. loads incidents from JSON,
2. filters them using configurable rules,
3. prioritises them using a chosen priority mode,
4. executes a response plan made of small actions.

<details style="background:#f5f7ff; border:1px solid rgba(0,0,0,0.15); border-radius:10px; padding:0.9rem 1rem; margin:1rem 0;">
  <summary style="cursor:pointer; font-weight:800; list-style:none; margin:0;">Definition: What does “triage” mean here?</summary>
  <div style="margin-top:0.8rem;">

In this challenge, **triage** means:

1. **Sort the incoming reports into groups**
   - *ignore* (safe / not important)
   - *review* (unclear / low confidence)
   - *act* (important enough to respond to)

2. **Decide what to deal with first**
   - use a prioritisation rule (for example: highest threat first)

3. **Apply a response**
   - log it, raise an alert, quarantine a zone, etc.

This is the same idea used in real systems (monitoring and alerting): lots of incoming events arrive, and your software helps decide **what matters**, **what comes first**, and **what happens next**.

  </div>
</details>


---

## Ground rules
- Prefer simple loops over streams unless explicitly asked.
- Validate inputs defensively (null checks, range checks).
- Choose the **most appropriate** functional interface:
  - `Predicate<T>` for yes/no tests
  - `Function<T, R>` for transformations
  - `Consumer<T>`/`BiConsumer<T, U>` for side effects
  - `Supplier<T>` for defaults / object creation
- Predicates should be *pure* (no mutation). Put side effects in Consumers or Commands.
- Do not use `record` or `final`.

---

## How to run
Create a class per exercise package with a single entry point:

```java
public class Exercise {
    public static void run() {
        // load, filter, sort, report
    }
}
```

Your `run()` must print the **required outputs** listed below.

---

## Source data
Use **both** JSON files as your input datasets:

- [ce13_incidents_08.json](ce13_incidents_08.json) (small test set)
- [ce13_incidents_100](ce13_incidents_100.json) (larger set)

Your code should be able to run against either dataset without changes (e.g., by switching a file path).

**Important:** These datasets intentionally contain messy cases (duplicates, blanks, out-of-range values) so you can demonstrate defensive coding.

---

## What you are building
You are building a small program that does this:

1. **Read** incidents from a JSON file.
2. **Clean/validate** them (because real data is messy).
3. **Decide** which incidents matter (using **predicates**).
4. **Sort** the important ones (using a **comparator factory**).
5. **Carry out a response** (using **Consumers** OR your chosen design pattern).
6. **Print a clear report** so someone can understand what happened.

---

## Tasks 

### Task 01 — Create the `Incident` class
Make a normal Java class called `Incident`. It should store the incident data from JSON.

**Minimum fields to include**
- `id` (String)
- `timestampUtc` (long)
- `deck` (int)
- `zone` (String)
- `type` (String)
- `threat` (int)
- `confidence` (int)
- `notes` (String)

**What you must do in this task**
- Add a constructor (or a static factory) so you can create an `Incident`.
- Add a `toString()` that prints something readable (not every field if you don’t want).
- Decide how you will handle bad data. Pick **one** approach:
  - **Reject** bad incidents (skip them and count them), OR
  - **Fix** minor issues (trim strings, clamp ranges) and still accept them.

**Examples of “bad data” you should expect**
- `zone` might have extra spaces at the start/end
- `notes` might be blank
- `threat` or `confidence` might be outside `0..100`
- duplicate `id` values may appear

**Good sign you’re done**
- You can create 3–5 `Incident` objects in code and print them nicely.

---

### Task 02 — Load incidents from JSON
Load incidents from the provided files:

- [ce13_incidents_08.json](ce13_incidents_08.json)
- [ce13_incidents_100.json](ce13_incidents_100.json)

**What “load” means here**
- Read the JSON file from disk
- Parse it into objects your program can use
- Convert each JSON entry into an `Incident`

**Rules**
- Your program must not crash if the data is messy.
- If an incident is invalid, skip it and record that you skipped it.

> **Tip**
> Start with the **08** file first. Only move to **100** when your logic works.
> See Appendix B below for a helper class to read/write a list of objects of type `T` to/from a JSON file.
---

### Task 03 — Choose ONE main collection and say why
Pick one main collection you’ve studied and use it as your “main way of storing/organising” incidents.

Choose one:
- `ArrayList<Incident>` (good general-purpose choice)
- `LinkedList<Incident>` (nice if you treat it like a queue)
- `Map<String, List<Incident>>` (good for grouping by zone)
- `Set<String>` (good for tracking duplicate ids)

**What you must write**
Add a short comment in your code (1–2 lines):
- which one you chose
- why it makes sense for your solution

You can still use other collections where needed, but one should be clearly your main one.

---

### Task 04 — Write rules using `Predicate<Incident>`
Write at least **two** rules using `Predicate<Incident>`.

Example rule ideas (you may invent your own):
- Keep incidents where `threat >= 70`
- Keep incidents where `type` is `"ACID"` or `"MOTION"`
- Keep incidents where `confidence >= 60`
- Keep incidents in certain zones (e.g., `"MedBay"`, `"Airlock"`)

**Important**
- A predicate should only **check** and return true/false.
- Do not print, count, or modify collections inside the predicate.

**You must also do**
Combine rules using:
- `.and(...)`
- `.or(...)`
- `.negate()`

**Good sign you’re done**
- You can filter a list of incidents and the result “makes sense”.

---

### Task 05 — Comparator factory (required)
Create a class that can return different comparators based on a mode.

**What a comparator mode means**
It is a named sorting rule, e.g.:
- sort by threat (highest first)
- sort by confidence (highest first)
- sort by time (newest first)

**Requirements**
- At least **3** modes
- At least **1** mode must be a chained comparator (e.g., zone then threat)

Example mode names (you may use different names):
- `"threat_desc"`
- `"confidence_desc"`
- `"timestamp_desc"`
- `"zone_then_threat"`

**Good sign you’re done**
- You can sort the same list in two different ways by changing the mode.

---

### Task 06 — Use ONE design pattern (Strategy OR Command)
Pick **one** design pattern from your Design Patterns notes and use it properly.

You must have at least **two concrete versions** of the pattern.

#### If you choose Strategy
Use Strategy to represent different “ship operating modes” (policies).

Example idea (you choose your own names):
- “Stealth Mode” (only react to very high threat)
- “Quarantine Mode” (react to more types of incidents)

A strategy should be able to change things like:
- which incidents you keep (rule), OR
- how you sort, OR
- which response approach you choose

**Key point**
You must be able to switch strategies without rewriting your main engine.

#### If you choose Command
Use Command to represent actions as objects.

Example actions:
- “Log incident”
- “Raise alert”
- “Quarantine zone”
- “Increase danger score”

**Key point**
Your main engine should not be one huge `switch` that does every action.

---

### Task 07 — Produce a clear report in `Exercise.run()`
Your `Exercise.run()` must show your system working.

It must print:
- total incidents loaded
- total incidents rejected (invalid) **with a short summary reason**
- total incidents discarded by your rules (not important)
- top 5 incidents after sorting (show: id, zone, type, threat, confidence, timestamp)
- one grouped summary (e.g., count per zone OR highest-risk zone)
- a short summary of what response happened

**Output should be readable**
A human should be able to look at your console output and understand what your program did.

---

## Required outputs
Your `Exercise.run()` must print:

- Total incidents loaded
- Total incidents rejected (invalid) and why (summary count, not spam)
- Total incidents discarded by rules (non-actionable)
- Top 5 incidents after sorting (show key fields: id, zone, type, threat, confidence, timestamp)
- One grouped summary (e.g., incidents per zone, or highest-risk zone)
- A clear indication of what response happened (summarised)

---

## Suggested build order
1. Get loading working with `ce13_incidents_08.json`.
2. Convert JSON entries into valid `Incident` objects (skip bad ones).
3. Make your predicates filter incidents correctly.
4. Implement the comparator factory and verify each mode.
5. Add your chosen design pattern (Strategy or Command).
6. Run on `ce13_incidents_100.json` and keep the output readable.

---

## Hints
- Treat JSON fields as untrusted: blanks, duplicates, missing data, and bad ranges are normal.
- Keep predicates pure. If you need counters/logging, do it outside the predicate.
- Keep your console output readable: summaries beat spam.
- If you’re stuck: make your triage pipeline work first without the design pattern, then refactor to introduce the pattern cleanly.

---

## Extensions
- Add a “manual review” lane for incidents that are suspicious but low confidence.
- Add a cooldown escalation rule: repeated incidents in the same zone within a time window.
- Add a second feed and merge incidents (dedupe by id).
- Add a small text menu: choose dataset + policy + comparator mode at runtime.

---

## Appendix A: Real-World Parallels

This challenge is playful, but the architecture mirrors real incident triage systems (monitoring/alerting/response).  
This table is here so you can see the *application* of what you’re building, not to suggest a specific implementation.

| In-story concept | Real-world analogue | What the concept represents |
| :- | :- | :- |
| Incident report | Event / alert | A unit of incoming telemetry that may need attention |
| Remote JSON feed | External telemetry | Untrusted third-party data source |
| Zone / Deck | Service / subsystem / region | Where the issue is “owned” or located |
| Threat + Confidence | Severity + confidence | Two key signals used to triage |
| Rule tests | Filtering rules | Decide what is relevant/suspicious |
| Risk profiling | Scoring/enrichment | Transform raw data into a decision-friendly form |
| Comparator modes | Priority policies | Different ways to sort urgency |
| Response steps | Runbook/playbook actions | Concrete actions taken after triage |
| Action queue | Work queue | Sequenced execution of response work |

| Pattern in your solution | Real-world analogue | Why teams use it |
| :- | :- | :- |
| Strategy (if chosen) | Policy profile | Swap triage rules/priorities per mode without rewriting the engine |
| Command (if chosen) | Playbook steps | Queue/execute response actions cleanly without hard-coding behaviour |
| Comparator factory | Priority policy selector | Select a sorting policy at runtime by mode |

| Functional interface idea | Real-world analogue | Meaning |
| :- | :- | :- |
| Predicate | Rule | Decide yes/no about an incident |
| Function | Enrichment/extraction | Compute a derived value used by triage |
| Consumer / BiConsumer | Action | Perform an effect (log, count, update state) |
| Supplier | Default provider | Create defaults when data/state is missing |

---

## Appendix B: Useful Code

```java
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JSONSerialiser<T> implements AutoCloseable {
    private final ObjectMapper _mapper;
    private final Class<T> _elementType;

    private Path _path;
    private boolean _isOpen;

    /// <summary>
    /// Creates a JSON list session for reading/writing a JSON array of objects of type <typeparamref name="T"/>.
    /// </summary>
    /// <param name="elementType">The class type of the list element.</param>
    public JSONSerialiser(Class<T> elementType) {
        if (elementType == null)
            throw new IllegalArgumentException("elementType is null.");

        _elementType = elementType;
        _mapper = new ObjectMapper();
    }

    /// <summary>
    /// Opens the session for a given file path. You must call this before reading or writing.
    /// </summary>
    /// <param name="path">Path to a JSON file that contains a JSON array.</param>
    public void open(Path path) {
        if (path == null)
            throw new IllegalArgumentException("path is null.");

        if (_isOpen)
            throw new IllegalStateException("session is already open.");

        _path = path;
        _isOpen = true;
    }

    /// <summary>
    /// Reads a JSON file containing a JSON array and returns a list of <typeparamref name="T"/>.
    /// </summary>
    /// <returns>A new list containing the loaded elements.</returns>
    public List<T> readList() {
        ensureOpen();

        if (!Files.exists(_path))
            throw new IllegalArgumentException("file does not exist: " + _path);

        try {
            JavaType listType = _mapper.getTypeFactory()
                .constructCollectionType(List.class, _elementType);

            List<T> data = _mapper.readValue(_path.toFile(), listType);

            // Jackson may return an internal list type; return a plain ArrayList for students.
            return new ArrayList<>(data);
        } catch (IOException ex) {
            throw new IllegalArgumentException("failed to read json list: " + _path, ex);
        }
    }

    /// <summary>
    /// Writes a list of <typeparamref name="T"/> to the opened path as a formatted JSON array.
    /// </summary>
    /// <param name="items">The items to write.</param>
    public void writeList(List<T> items) {
        ensureOpen();

        if (items == null)
            throw new IllegalArgumentException("items is null.");

        try {
            if (_path.getParent() != null)
                Files.createDirectories(_path.getParent());

            _mapper.writerWithDefaultPrettyPrinter().writeValue(_path.toFile(), items);
        } catch (IOException ex) {
            throw new IllegalArgumentException("failed to write json list: " + _path, ex);
        }
    }

    /// <summary>
    /// Closes the session and clears the currently opened path.
    /// </summary>
    @Override
    public void close() {
        _path = null;
        _isOpen = false;
    }

    /// <summary>
    /// Ensures the session has been opened before performing any file operations.
    /// </summary>
    private void ensureOpen() {
        if (!_isOpen)
            throw new IllegalStateException("session is not open.");
    }

    // -------------------------------------------------------------------------
    // Demo (commented out)
    // -------------------------------------------------------------------------
    /*
    import java.util.ArrayList;

    public static class Book {
        private String _title;
        private String _author;
        private int _year;

        public Book() { } // Jackson needs a no-arg constructor

        public Book(String title, String author, int year) {
            _title = title;
            _author = author;
            _year = year;
        }

        public String getTitle() { return _title; }
        public void setTitle(String title) { _title = title; }

        public String getAuthor() { return _author; }
        public void setAuthor(String author) { _author = author; }

        public int getYear() { return _year; }
        public void setYear(int year) { _year = year; }

        @Override
        public String toString() {
            return _title + " (" + _year + "), by " + _author;
        }
    }

    public static void demo() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Dune", "Frank Herbert", 1965));
        books.add(new Book("The Left Hand of Darkness", "Ursula K. Le Guin", 1969));

        try (JSONSerialiser<Book> session = new JSONSerialiser<>(Book.class)) {
            session.open(Path.of("data/books.json"));
            session.writeList(books);

            List<Book> loaded = session.readList();
            for (Book b : loaded)
                System.out.println(b);
        }
    }
    */
}

```