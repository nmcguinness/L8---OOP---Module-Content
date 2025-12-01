# Stage 2 Report – Implementation & Verification
## COMP C8Z03 — Object-Oriented Programming (GCA1)
## Student Name A, Student Name B

<!-- Code blocks or figures are now included in your word count.
-->

## 1. Equality & Hashing Rationale (180–300 words)
<!-- student fills -->

## 2. Testing Summary (180–300 words)
<!-- student fills -->

## 3. Defensive Coding 2 (4–6 examples, 240–420 words total)
<!-- student fills -->

> Here is an example us null checking in our solution. Obviously, you need to replace the code below with your own examples.

```java
public Contact format(Contact input) {
        String email = input.getEmail();
        if (email == null || email.isBlank()) {
            return input;
        }

        String trimmed = email.trim();
        String html = "<a href=\"mailto:" + trimmed + "\">" + trimmed + "</a>";
        return input.withEmail(html);
    }
```

## 4. Reflections (150–250 words)
<!-- student fills -->

## 5. Commit Contributions (60–120 words)
<!-- student fills -->

## 6. Final References (Harvard style)
<!-- student fills -->

## 7. Implementation Matrix (Y/N)

| Component | Implemented? | Notes |
|---|:--:|---|
| Extended Dataset (1000 rows) |  |  |
| Entity Enhancements |  |  |
| Collections & Lookup |  |  |
| Equality & Hashing |  |  |
| Advanced Queries |  |  |
| CSV Export |  |  |
| Testing (JUnit + Coverage) |  |  |
| Defensive Coding |  |  |

## 8. Appendix (Optional)
<!-- optional section -->


> You may find the Markdown Guide located [here]([www.dkit.ie](https://www.markdownguide.org/)) useful in preparing your report.
