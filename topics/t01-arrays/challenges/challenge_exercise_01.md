# COMP C8Z03 — Object-Oriented Programming  
## Exercise: Array of Suspects - Crime License Plate Hunt

### Scenario
A tollbooth operator saw a suspicious car passing through the toll booth moments after a crime in the area. Strangely, they remember:

1. The **county code** contained an **“L”**.  
2. The **year digits (YYY)** summed to **10**.  
3. The **serial contained at least two 6’s** in any position.  

The Irish license plate was in the format (simplified, post-2013) shown below:  

```
[YYY] [CC] [SERIAL]
Example: 181 LH 626
```

- `YYY` → 3 digits (`18` = year, `1` = Jan–Jun, `2` = Jul–Dec).  
- `CC` → county code (`LH`, `LK`, `LM`, etc.; Dublin is just `D`).  
- `SERIAL` → 1–6 digit number.  

---

### Starter Tasks 

** Model a `LicensePlate` as a class **
- Create a class to represent a license plate with fields for year, period, county, and serial.  
- Add methods to:  
  - Return the sum of the digits of the year.  
  - Check if the county contains an "L".  
  - Check if the serial has at least two '6' digits.  
- Override `toString()` for easy display.  

---

### Hints
- Use a **class** to structure the plate data.  
- Think about **arrays vs ArrayLists** — where would you use each?  
- Consider using **regular expressions** to validate the format of input plates.  
- When checking for two sixes in the serial, loop through characters or use a counting approach.  
- Start small: test your class with a few known plates before generating thousands.  
- Remember: arrays in Java have fixed size, but ArrayLists grow dynamically.  

---

### Extensions (optional)
- Allow the user to type in a license plate and check if it matches the witness rules.  
- Support multiple witnesses with different conditions (combine with AND/OR logic).  
- Add sorting of results.  

---

## Utility: Reading Plates from a File

Sometimes you may want to load license plate data from a text file rather than generating it randomly. The file will contain comma-separated plate entries.

Here is a helper method that reads the file and returns an `ArrayList<String>`:

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileUtils {

    public static ArrayList<String> readCommaSeparatedFile(String filePath) throws IOException {
        // Read all lines and join into one string (in case file has line breaks)
        String content = String.join("", Files.readAllLines(Paths.get(filePath)));

        // Split by comma, trim whitespace, collect into ArrayList
        String[] parts = content.split(",");
        ArrayList<String> list = new ArrayList<>();
        for (String part : parts) {
            list.add(part.trim());
        }
        return list;
    }

    // Example usage
    public static void main(String[] args) {
        try {
            ArrayList<String> plates = readCommaSeparatedFile("plates_10k.txt");
            System.out.println("Read " + plates.size() + " entries.");
            System.out.println("First 5: " + plates.subList(0, Math.min(5, plates.size())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

*Hint:* Consider where in your project you might put this helper method so it can be reused.
