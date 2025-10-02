# Arrays (1D & 2D)
> Prereqs: variables, `if`, loops, simple methods  

---

## What you'll learn
A quick summary of what **you** should be able to do after this lesson:

| Skill Type | You will be able to… |
|:-----------|:-----------------------|
| Understand | Explain **1D** vs **2D** arrays and what `length` means |
| Use        | Create, fill, read, and loop over arrays to solve problems |
| Debug      | Spot off‑by‑one errors, null inner rows, and bounds issues |
| Check      | Write tiny tests/prints to confirm your code works |

---

## Core Ideas — 1D Arrays (step by step)

### 1) Create
```java
// Literal (known values now)
int[] nums = {3, 5, 8};

// With length (values default to 0)
int[] grades = new int[4]; // {0,0,0,0}
```

### 2) Add / Change values
```java
grades[0] = 72;  // write
grades[1] = 65;
int first = grades[0]; // read
```

### 3) Length (how many slots?)
```java
int n = grades.length; // 4
System.out.println("Size: " + n);
```

### 4) Iterate (visit every item)
**Index loop (lets you read & write):**
```java
for (int i = 0; i < nums.length; i++) {
    System.out.println("i=" + i + ", val=" + nums[i]);
}
```
**Enhanced for (read-only loop variable):**
```java
for (int val : nums) {
    System.out.println(val);
}
```

### 5) Find (search / min / count)
**Find first index of a target (or -1 if not found):**
```java
static int indexOf(int[] xs, int target) {
    for (int i = 0; i < xs.length; i++) {
        if (xs[i] == target) return i;
    }
    return -1;
}
```

**Find minimum value (throws if empty):**
```java
static int min(int[] xs) {
    if (xs == null || xs.length == 0) throw new IllegalArgumentException("empty");
    int m = xs[0];
    for (int i = 1; i < xs.length; i++) m = Math.min(m, xs[i]);
    return m;
}
```

**Count matches:**
```java
static int count(int[] xs, int target) {
    int c = 0;
    for (int x : xs) if (x == target) c++;
    return c;
}
```

### Common problems & fixes — 1D
| Problem | Why it happens | Quick Fix |
|:-------|:-----------------|:-----------|
| `ArrayIndexOutOfBoundsException` | You looped to `i <= arr.length` | Loop with `i < arr.length` |
| “It crashed on empty input” | You didn’t check `length == 0` | Add an early guard (return or throw) |
| Wrong average (0.0) | Integer division before cast | Cast before divide: `(double) sum / n` |
| “Enhanced for didn’t change my array” | Loop var is a copy | Use index loop to write: `xs[i] = ...` |
| Null array | You passed `null` | Decide: return default/throw when `xs == null` |

---

## Core Ideas — 2D Arrays (step by step)

### 1) Create (rectangular vs jagged)
```java
// Rectangular: same columns in every row
int[][] rect = new int[3][4]; // 3 rows, 4 cols each

// Jagged: different columns per row
int[][] jag = new int[3][];       // rows allocated, inner rows null
jag[0] = new int[2];              // row 0 has 2 columns
jag[1] = new int[5];              // row 1 has 5 columns
jag[2] = new int[3];              // row 2 has 3 columns
```

### 2) Add / Change values
```java
rect[0][2] = 7; // row 0, col 2
int v = rect[0][2];
```

### 3) Dimensions (rows and per‑row columns)
```java
int rows = rect.length;        // number of rows
int cols = rect[0].length;     // columns in row 0 (rectangular case)

// Jagged: use the current row's length each time
for (int r = 0; r < jag.length; r++) {
    int colsR = jag[r].length; // may differ each row
}
```

### 4) Iterate (visit every cell)
**Index loops (row‑major):**
```java
for (int r = 0; r < rect.length; r++) {
    for (int c = 0; c < rect[r].length; c++) {
        System.out.println("r=" + r + ",c=" + c + " -> " + rect[r][c]);
    }
}
```
**Enhanced for (clean read):**
```java
for (int[] row : rect) {
    for (int cell : row) {
        System.out.print(cell + " ");
    }
    System.out.println();
}
```

### 5) Find / Count (2D)
**Find first coordinates of a target (or `{-1,-1}`):**
```java
static int[] find2D(int[][] g, int target) {
    for (int r = 0; r < g.length; r++) {
        for (int c = 0; c < g[r].length; c++) {
            if (g[r][c] == target) return new int[]{r, c};
        }
    }
    return new int[]{-1, -1};
}
```

**Count matches:**
```java
static int count2D(int[][] g, int target) {
    int count = 0;
    for (int r = 0; r < g.length; r++)
        for (int c = 0; c < g[r].length; c++)
            if (g[r][c] == target) count++;
    return count;
}
```

**Pretty print (one row per line):**
```java
static void print2D(int[][] g) {
    for (int r = 0; r < g.length; r++) {
        for (int c = 0; c < g[r].length; c++) {
            System.out.print(g[r][c] + (c == g[r].length - 1 ? "" : " "));
        }
        System.out.println();
    }
}
```

### Common problems & fixes — 2D
| Problem | Why it happens | Quick Fix |
|:-------|:-----------------|:-----------|
| `NullPointerException` on `g[r][c]` | Inner row not created (jagged) | Ensure `g[r] = new int[len]` before use |
| Bounds error | Used `g[0].length` for every row | Use `g[r].length` inside the row loop |
| Swapped row/col | Used `[c][r]` by mistake | Remember: `g[row][col]` |
| Mixed rectangular & jagged assumptions | Treated jagged like rectangular | Always read `g[r].length` per row |
| “Printed gibberish like `[I@1a2b3c`” | Printed the array object directly | Loop and print values (see `print2D`) |

---

## Try it step‑by‑step (progressive code that builds up)
Each step compiles on its own. Paste and run them one by one.

**Step A — Read and average a list (1D)**
```java
int[] scores = {72, 65, 90, 84};
int sum = 0;
for (int i = 0; i < scores.length; i++) {
    sum += scores[i];
}
double avg = (double) sum / scores.length;
System.out.println("Avg = " + avg); // 77.75
```

**Step B — Be safe with empty or null arrays**
```java
static double average(int[] xs) {
    if (xs == null || xs.length == 0) return 0.0; // safe default
    int sum = 0;
    for (int x : xs) sum += x;
    return (double) sum / xs.length;
}
System.out.println(average(new int[]{})); // 0.0 (no crash)
```

**Step C — Tidy up with helpers + tiny tests**
```java
static int sum(int[] xs) {
    if (xs == null) return 0;
    int s = 0; for (int x : xs) s += x; return s;
}
static double average2(int[] xs) {
    return (xs == null || xs.length == 0) ? 0.0 : (double) sum(xs) / xs.length;
}
// Quick checks (mini tests)
System.out.println(sum(new int[]{1,2,3}) == 6);
System.out.println(average2(new int[]{2,2}) == 2.0);
```

---

## Build a small project: cinema seating (end‑to‑end)
**Goal:** You’ll create a tiny system to manage seating in a cinema where rows can have different lengths (a jagged 2D array). You’ll book a couple of seats and count how many are free.

> *IPO means **Input → Process → Output** — a simple way to plan programs by listing what comes in, what you do, and what comes out.*

| Inputs | Process | Output |
|:------|:---------|:--------|
| Row lengths: `{4,6,5}` and bookings `(0,1),(1,4)` | Create `boolean[][]` seats, check bounds, mark booked | Free seats (e.g., `12`) |

```java
final class Cinema {
    private final boolean[][] seats; // true = booked, false = free
    public Cinema(int[] seatsPerRow) {
        seats = new boolean[seatsPerRow.length][];
        for (int r = 0; r < seats.length; r++) seats[r] = new boolean[seatsPerRow[r]];
    }
    public void book(int row, int col) {
        if (!inBounds(row, col)) throw new IllegalArgumentException("Bad seat");
        if (seats[row][col]) throw new IllegalStateException("Already booked");
        seats[row][col] = true;
    }
    public int freeCount() {
        int free = 0;
        for (int r = 0; r < seats.length; r++)
            for (int c = 0; c < seats[r].length; c++)
                if (!seats[r][c]) free++;
        return free;
    }
    private boolean inBounds(int r, int c) {
        return r >= 0 && c >= 0 && r < seats.length && c < seats[r].length;
    }
}
public class Main {
    public static void main(String[] args) {
        Cinema c = new Cinema(new int[]{4,6,5});
        c.book(0, 1); c.book(1, 4);
        System.out.println("Free: " + c.freeCount()); // expect 12
    }
}
```

---

## Practice time
| Task | Time | What to do | Expected result |
|:----|:-----|:------------|:-----------------|
| A | 10–15m | Write `min(int[] xs)` that throws if empty; otherwise return the smallest value. | `{3,1,5} → 1`; `{}` throws |
| B | 15–20m | Make a `5×5` `int[][] heat`; set edges to `1` and center to `9`; print it. | A grid with border `1`s and center `9` |
| C | 10–15m | Turn a **jagged** `int[][]` into a **rectangular** one by padding missing cells with `-1`. | New array with same rows and equal columns |

---


## Useful snippets (copy‑paste)
```java
static boolean inBounds(int[][] g, int r, int c) {
    return g != null && r >= 0 && c >= 0 && r < g.length && g[r] != null && c < g[r].length;
}
```
```java
static void print2D(int[][] g) {
    for (int r = 0; r < g.length; r++) {
        for (int c = 0; c < g[r].length; c++) {
            System.out.print(g[r][c] + (c == g[r].length - 1 ? "" : " "));
        }
        System.out.println();
    }
}
```

---

## Reflect more (optional, 5–10 minutes)
- In your own words, explain **IPO** for the cinema example. What are the inputs, the key steps, and the final output?
- Where did you add **guards** (safety checks) in 1D and 2D code? Could any be turned into reusable helpers?
- Pick one bug you hit (or could hit): what **symptom** would you see, and what **fix** would you apply?
- For 2D arrays, how would you adapt your loops if the grid became **jagged** mid‑run (rows resized)?
- What naming choice did you change to make your code clearer? Why is the new name better for a future reader?
- If you had to test your `min`, `indexOf`, or `find2D` methods, what **3 test cases** would you start with and why?

