# COMP C8Z03 – Object‑Oriented Programming (Year 2)

Welcome to the module repo. This space holds your weekly topics, exercises, shared resources, and assessment briefs. Use it alongside Moodle and the official [module descriptor](https://courses.dkit.ie/index.cfm/page/module/moduleId/55497/deliveryperiodid/1066).

---

## Folder Map (What goes where)

```text
/                                     # repo root (L8---OOP---Module-Content)
├─ README.md                          # overview + how to run
├─ code/                              # runnable Java code (solutions, demos, harnesses)
│  └─ src/
│     ├─ common/                      # helpers used by multiple exercises/challenges
│     └─ t01_arrays/                  # mirrors a topic in notes/topics
│        ├─ exercises/
│        │  └─ ex01_basics/           # package per exercise; safe to reuse class name Main
│        └─ challenges/
│           └─ ce01/
│
├─ lecturer/                          # staff-only WIP (drafts, keys, lesson plans)
│
└─ notes/                             # student-facing learning material (non-runnable)
   ├─ shared/
   │  ├─ cheat sheets/
   │  ├─ general/
   │  └─ mind maps/
   ├─ syllabus/
   ├─ assessments/
   │  └─ briefs/
   └─ topics/
      └─ t01_arrays/
         ├─ exercises/                # task descriptions/prompts
         └─ challenges/               # larger/open-ended problems
```

---

### How to use this repo
- Start with **syllabus/** to understand *what* we assess and *why*.
- Check **assessments/briefs/** for the current CA/ICA brief and rubric.
- Each week, open the matching folder in **topics/** (e.g., `t01-arrays/`):
  - Work through **exercises/** first (core skills), then try **challenges/** (apply + extend).
  - **Do not peek** at the **solutions/** until after we cover the material in class.
- Use **shared/** for general setup notes, style guidance, and cheat sheets.

---

## Running exercises from `Main`

Below is a commented code block you can paste near your `Main` class. It explains how each exercise’s `Exercise.run()` is called.

```java
//Every exercise/challenge package contains an Exercise class with:
public final class Exercise {
  public static void run() 
  { 
    //code to run a specific exercise solution or demo code
  }
}

//Call an exercise from Main using the fully-qualified path to the class (e.g. t01_arrays.challenges.ce01.Exercise):
public class Main {
    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public void run(){
        System.out.println("Running challenge exercise 01...");
        t01_arrays.challenges.ce01.Exercise.run();
    }
}

/* Notes:
 - Keep packages aligned with notes/topics, e.g. t01_arrays.exercises.ex01_basics.
 - Multiple Main/Exercise class names are fine because packages make them unique.
 - Shared helpers live in package 'common', e.g. 'common.FileUtils'.
*/
```

---

## General Directions to Improve as a Programmer

- **Show up**: Consistent **class attendance** opens time for questions, feedback, and debugging together.
- **Ask early, ask often**: If you’re stuck for 15–20 minutes, **ask**. Small gaps compound quickly.
- **Take structured notes**: Keep a simple, dated log of *what you learned* and *what still confuses you*. Include short
  code snippets and diagrams—especially for tricky ideas (e.g., references vs values, parameter passing).
- **Practice deliberately**: Write small programs daily. Re‑implement examples *from scratch* without looking.
- **Read code**: Study the sample solutions **after** attempting the exercises. Compare naming, layout, and tests.
- **Use a style guide**: Follow `shared/general/style-guide.md`. Clean, consistent code is easier to fix and extend.
- **Test as you go**: Add small `main` demos or assertions. Run code often. Don’t leave all testing to the end.
- **Version control**: Commit early and often with meaningful messages. Branch for experiments.
- **Balance matters**: Sleep, nutrition, movement, and breaks improve problem‑solving. Overwork slows you down.
- **Be patient**: Skill grows with time-on-task. Focus on steady improvement, not perfection on day one.

---

## Getting Started (Plain Java)
- Ensure a recent JDK is installed and selected in your IDE.
- Use your IDE’s Markdown preview for `notes.md` and Mermaid diagrams, or view on GitHub.
- Build and run small Java files directly in your IDE while working through exercises.

> Tip: Keep a “bug diary”. For each bug you hit, note the cause and the fix. Patterns will emerge, and you’ll get faster.
