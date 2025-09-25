# COMP C8Z03 – Object‑Oriented Programming (Year 2)

Welcome to the module repo. This space holds your weekly topics, exercises, shared resources, and assessment briefs.
Use it alongside Moodle and the official module descriptor.

**Module descriptor:** https://courses.dkit.ie/index.cfm/page/module/moduleId/57171/deliveryperiodid/1066

---

## Folder Map (What goes where)

```
/comp-c8z03/
├─ syllabus/
│  ├─ learning_outcomes.md         # LO mapping table for the module
│  ├─ mindmap.md                   # Big-picture mind map of the syllabus
│  └─ assessments/
│     └─ briefs/
│        ├─ ca1.md                 # CA overview, deadlines, LO coverage, requirements, screencast/interview notes
│        └─ rubric_ca1.md          # Marking rubric for CA1
│
├─ topics/
│  ├─ t01-intro-oop/
│  │  ├─ notes.md                  # Theory, examples, diagrams, references
│  │  ├─ exercises.md              # Exercise ladder (core → stretch)
│  │  ├─ topic-mindmap.md          # Mind map of this topic’s keywords
│  │  ├─ solutions/                # Instructor solutions (added after delivery)
│  │  ├─ quiz/                     # GIFT/XML banks for Moodle quizzes
│  │  └─ assets/                   # Diagrams, Mermaid, sample data
│  ├─ t02-classes-objects/
│  │  └─ (same structure as above)
│  └─ …
│
└─ shared/
   ├─ general/
   │  ├─ style-guide.md            # Code style (naming, braces, JavaDoc template)
   │  └─ tools.md                  # JDK/IDE notes, build/run, basic logging
   ├─ cheat sheets/
   │  └─ patterns-cheatsheet.md    # Quick reference (intro-level patterns)
   └─ mind maps/
      └─ collections.md            # Concept maps used across topics
```

### How to use this repo
- Start with `syllabus/learning_outcomes.md` to understand **what** we assess and **why**.
- Each week, open the matching folder in `topics/`:
  - **Read** `notes.md`, skim `topic-mindmap.md`, then **attempt** `exercises.md` (core first, stretch after).
  - Use `assets/` for diagrams/data and **do not peek** at `solutions/` until we’ve covered the topic in class.
- Refer to `shared/` for style and quick reminders while coding.

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
