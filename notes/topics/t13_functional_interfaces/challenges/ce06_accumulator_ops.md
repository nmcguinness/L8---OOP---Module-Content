---
title: "Challenge Exercise: Accumulator Ops — Telemetry Triage"
subtitle: "One reusable Accumulator + functional interfaces in the wild"
description: "Build a small telemetry triage tool. Use one generic Accumulator driven by functional interfaces (Predicate, Function/ToDoubleFunction, Consumer, Comparator). Produce a concise report that works for both games and software telemetry."
tags: ["java", "challenge", "functional-interfaces", "java.util.function", "comparator", "collections", "generics", "pecs", "year2", "comp-c8z03"]
prerequisites: ["t13_functional_interfaces.md", "t02_ordering_notes.md", "t08_generics_1_notes.md", "t09_generics_2_notes.md"]
---

# Challenge Exercise: Accumulator Ops — Telemetry Triage

## Scenario
You are building a tiny **telemetry triage** tool that can be used in two domains:

- **Games**: playtest sessions generate events like FPS samples, ping, deaths, level completion time.
- **Software**: services generate events like response time, payload size, retries, error rate.

You receive a list of **telemetry events** (messy, sometimes invalid), and your job is to:

1. **filter** what should be included,
2. **measure** numeric values from events,
3. **accumulate** useful statistics,
4. **rank** results in different ways,
5. print a short **report**.

The key requirement: you must build **one reusable Accumulator** that works across event types.

Keep it simple. Keep it robust. Keep it readable.

---

## Learning goals
By completing this challenge, you should be able to:

- Use **functional interfaces** to pass behaviour into methods/classes
- Apply **Predicate** for rules, **Function / ToDoubleFunction** for extraction, **Consumer** for actions
- Build and use **Comparator** strategies (including a comparator factory)
- Apply **generics + PECS** correctly when designing helper methods
- Produce a small, testable program with clear output

---

## Starter data model 

Create a class:

- `TelemetryEvent`

Suggested fields (you may add more):

- `id` (String)
- `timestampUtc` (long)
- `source` (String)  
  examples: `"game"`, `"server"`, `"client"`
- `category` (String)  
  examples: `"fps"`, `"ping"`, `"deaths"`, `"response_time"`, `"payload_kb"`
- `value` (double)
- `unit` (String)
- `notes` (String)

### Validation helpers
Add methods that help your triage logic, for example:

- `boolean hasValidId()`
- `boolean hasValidCategory()`
- `boolean isFiniteValue()`
- `TelemetryEvent normalisedCopy()` (optional)

Your code must handle:
- missing/blank strings
- `NaN` / infinite values
- negative values where they don’t make sense (you decide the rule)

---

## The Accumulator 

Create a generic class:

```java
import java.util.function.ToDoubleFunction;

class Accumulator<T> {
    Accumulator(ToDoubleFunction<T> measure);
    void update(T item);
    void reset();

    int getCount();
    double getMin();
    double getMax();
    double getAverage();
    double getStdDev(); // choose population or sample, document it

    String toString();
}
```

### Correctness requirement 
Your standard deviation must be **mathematically correct** for a streaming update.
Use a correct online method (e.g., Welford).

---

## Choose your primary collection 
Pick **one** primary structure and justify it in a 1–2 line comment:

- `ArrayList<TelemetryEvent>` as your main store
- `LinkedList<TelemetryEvent>` as a processing queue
- `Map<String, List<TelemetryEvent>>` grouped by `category`

You may use additional collections where appropriate (e.g., `Set<String>` for deduplication).

---

## Functional interface requirements 

Your solution must include all of the following. The aim is that your program reads like a **pipeline**:

**events** → (Predicates decide) → (Consumers react) → (measurements extracted) → (accumulated) → (summarised) → (sorted via Comparator strategy)

Keep each piece small and named. If a method is doing “filter + measure + accumulate + print” all at once, it’s too big.

### 1) Predicates as rules
Create at least **3** rules using `Predicate<TelemetryEvent>`.

A *rule* is a reusable boolean decision: **include** vs **exclude** (or **accept** vs **discard**).
Rules should be:

- **Pure**: no mutation, no printing, no counters changing
- **Combinable**: you should be able to build bigger rules from smaller rules using `.and(...)`, `.or(...)`, and `.negate()`
- **Readable**: the variable names should describe intent (e.g. `isFiniteValue`, `hasValidId`, `isSupportedCategory`)

Examples of rule ideas (choose at least three):

- valid ID + category
- value is finite
- category is one of a supported set
- ignore events older than X
- ignore events with negative values (for selected categories)

> Hint: keep “domain rules” separate from “data hygiene rules”.  
> Data hygiene: blank strings, NaN, infinities.  
> Domain rules: negative ping doesn’t make sense, FPS can’t be zero for “running samples”, etc.

### 2) Measurement strategy
Use `ToDoubleFunction<TelemetryEvent>` (or `Function<TelemetryEvent, Double>`) to extract the number you accumulate.

The accumulator should not “know” what an event means. Instead, a **measurement function** tells it *what number to pull out*.
This is the Strategy Pattern in a lightweight form.

Your measurement can be:

- **Identity**: `e -> e.getValue()`
- **Converted**: `e -> e.getValue() / 1000.0` (convert ms → seconds)
- **Derived**: compute something from multiple fields (if you add them)
- **Sanitised**: clamp values into a safe range (e.g. `0..1000`) before accumulating

Make your measurement choice visible in the code, for example:

- `ToDoubleFunction<TelemetryEvent> measureValue`
- `ToDoubleFunction<TelemetryEvent> measureSeconds`
- `ToDoubleFunction<TelemetryEvent> measureKbToMb`

### 3) Consumers as actions
Create at least **2** `Consumer<TelemetryEvent>` actions.

A `Consumer` is for a **side-effect**. That means it’s where your program is allowed to:

- print a short log line
- collect IDs into a list for later review
- increment counters in a separate stats object
- append to a “discard reasons” list (if you design one)

Examples:

- `logDiscarded`
- `logAccepted`
- `incrementCounters` (if you keep counters in a separate object)

Keep console output minimal (no spam). A good pattern is:

- print *only the first 3 discarded events*, then print a summary count
- or print one short line per category at the end, not per event

> Rule of thumb:  
> Predicates decide **what** happens.  
> Consumers do **something** once the decision has been made.

### 4) Comparator factory
Create a small **comparator factory** that returns a `Comparator<SummaryRow>` (see next section) based on a mode.

The purpose is to show that sorting is also a strategy: you can swap it without rewriting your reporting code.
The factory should:

- take a **mode** (string/enum) and return the appropriate comparator
- be the only place you encode the “sort rules”
- support at least **3 modes**
- default safely if the mode is unknown (e.g. fall back to `category_asc`)

It must support **at least 3 modes**, e.g.:

- `"category_asc"`
- `"avg_desc"`
- `"stddev_desc"`
- `"count_desc"` (bonus)
- `"range_desc"` (bonus: sort by max-min)

Demonstrate that changing the mode changes the ranking in the printed report.

---

## Reporting 

### Step A — Build per-category accumulators
You must compute stats per **category**.

Recommended approach:

- Use a `Map<String, Accumulator<TelemetryEvent>>`
- Use `computeIfAbsent(category, ...)` to create an accumulator when needed

### Step B — Produce a summary row type
Create a small class (or record if allowed) like:

- `SummaryRow(category, count, min, max, average, stdDev)`

### Step C — Sort and print
Create a `List<SummaryRow>`, sort it using your comparator factory, then print:

- total events loaded
- total accepted
- total discarded
- a table of the **top N categories** (choose N = 5 or 10)

#### Table format (example)
Use a clean console output or markdown-like output such as:

| Category | Count | Min | Max | Avg | StdDev |
| :- | :- | :- | :- | :- | :- |
| fps | 120 | 45.0 | 144.0 | 89.2 | 12.6 |
| ping | 120 | 18.0 | 220.0 | 51.7 | 19.4 |

---

## Required run behaviour (what your `run()` must show)

Your `run()` (or `main`) must:

1. Create at least **20** telemetry events (use helper code in Appendix A to load from CSV file sample(s)).
2. Include at least **5** events that should be discarded (invalid ID/category/value, etc).
3. Apply your **Predicate** rules to filter.
4. Accumulate per category using your `Accumulator<T>`.
5. Print a report including:
   - counts loaded/accepted/discarded
   - top categories sorted by **one** comparator mode
6. Re-run the sort with a **different** comparator mode and show that the ordering changes.

---

## Hints
- Start by hard-coding the list of events first.
- Keep your accumulator focused on numbers; keep “what number to measure” as a function.
- Keep rules pure (Predicates). Put logging in Consumers.
- Prefer small helpers over one huge method.
- Decide and document one rule for empty data (return zeros vs throw).

---

## Extensions (optional)
Pick any 2:

- Deduplicate events by `id` using a `Set<String>`
- Add `Predicate<TelemetryEvent> isGameEvent` and print separate reports for game vs software
- Add a unit conversion function (ms → s, kb → mb) and demonstrate it
- Add a “manual review” lane: events that are not discarded but also not accumulated
- Add a `SummaryRow` field for `range = max - min` and a comparator mode for it
- Implement a second accumulator for *derived* metric (e.g., z-score of values inside a category)

---

## Appendix A — TelemetryHelper (CSV loader)

Use this helper to load telemetry events from a **CSV file** into a `List<TelemetryEvent>`.

This keeps your `run()` clean and helps you demonstrate the **IO + defensive coding** habits from earlier topics, while still focusing the challenge on **functional interfaces**.

> If you named your event class `Telemetry` instead of `TelemetryEvent`, update the types accordingly.

### CSV format (required)

Your CSV file must include a header row with these columns in this exact order:

| Column | Type | Notes |
| :- | :- | :- |
| `id` | String | unique-ish identifier |
| `timestampUtc` | long | Unix epoch seconds (or millis if you choose—be consistent) |
| `source` | String | e.g. `game`, `server`, `client` |
| `category` | String | e.g. `fps`, `ping`, `response_time`, `payload_kb` |
| `value` | double | may include decimals |
| `unit` | String | e.g. `ms`, `fps`, `kb`, `count` |
| `notes` | String | free text (no commas unless you implement quoted parsing) |

Example:

```csv
id,timestampUtc,source,category,value,unit,notes
g-001,1700000000,game,fps,92.0,fps,Arena A
s-001,1700000010,server,response_time,240.0,ms,GET /api/items
bad-005,1700000025,game,ping,-10.0,ms,Negative ping
```

> Keep it simple: for this module, we assume `notes` does **not** contain commas.  
> If you want to support commas in notes, you must implement quoted CSV parsing (optional extension).

### TelemetryHelper.java

```java
package t13_functional_interfaces.challenge;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class TelemetryHelper {

    private TelemetryHelper() {
    }

    public static List<TelemetryEvent> loadTelemetryEventsFromCsv(Path csvPath) {

        if (csvPath == null)
            throw new IllegalArgumentException("csvPath is null.");

        if (Files.notExists(csvPath))
            throw new IllegalArgumentException("csvPath does not exist: " + csvPath);

        List<TelemetryEvent> events = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {

            String line = reader.readLine(); // header
            if (line == null)
                return events;

            int lineNumber = 1;

            while (true) {
                line = reader.readLine();
                if (line == null)
                    break;

                lineNumber++;

                // Skip blank lines
                if (line.isBlank())
                    continue;

                // Simple CSV split (assumes no commas inside fields)
                String[] parts = line.split(",", -1);
                if (parts.length != 7) {
                    // Defensive coding: ignore malformed rows
                    continue;
                }

                TelemetryEvent evt = tryParse(parts);
                if (evt == null) {
                    // Defensive coding: ignore rows that fail parsing
                    continue;
                }

                events.add(evt);
            }
        }
        catch (IOException ex) {
            // Convert to unchecked to keep caller code simple
            throw new RuntimeException("Failed to read telemetry CSV: " + csvPath, ex);
        }

        return events;
    }

    private static TelemetryEvent tryParse(String[] parts) {

        // Trim each part (CSV data often contains spaces after commas)
        String id = parts[0].trim();
        String timestampRaw = parts[1].trim();
        String source = parts[2].trim();
        String category = parts[3].trim();
        String valueRaw = parts[4].trim();
        String unit = parts[5].trim();
        String notes = parts[6].trim();

        long timestampUtc;
        try {
            timestampUtc = Long.parseLong(timestampRaw);
        }
        catch (NumberFormatException ex) {
            return null;
        }

        double value;
        try {
            value = Double.parseDouble(valueRaw);
        }
        catch (NumberFormatException ex) {
            return null;
        }

        return new TelemetryEvent(id, timestampUtc, source, category, value, unit, notes);
    }
}
```

### Usage

```java
import java.nio.file.Path;
import java.util.List;

List<TelemetryEvent> events =
    TelemetryHelper.loadTelemetryEventsFromCsv(Path.of("data", "telemetry.csv"));

System.out.println("Loaded: " + events.size());
```


### Provided sample CSV files

Two sample datasets are provided alongside this brief:

- [ce06_telemetry_10](/notes/topics/t13_functional_interfaces/challenges/ce06_telemetry_10.csv) — small dataset for quick testing (includes a couple of discard candidates)
- [ce06_telemetry_100](/notes/topics/t13_functional_interfaces/challenges/ce06_telemetry_100.csv) — larger dataset for more realistic output (includes multiple discard candidates)

Place either file in your chosen `data/` folder (or update the `Path.of(...)` accordingly), then load with:

```java
List<TelemetryEvent> events =
    TelemetryHelper.loadTelemetryEventsFromCsv(Path.of("data", "telemetry_10.csv"));
// or:
List<TelemetryEvent> events =
    TelemetryHelper.loadTelemetryEventsFromCsv(Path.of("data", "telemetry_100.csv"));
```

