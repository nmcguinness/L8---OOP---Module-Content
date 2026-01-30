---
title: "CE09 – Frequency Forge"
module: COMP C8Z03 Object-Oriented Programming
level: Year 2
type: Challenge Exercise
---

# CE09 – Frequency Forge 

## Overview

In this challenge, you will build a **frequency analysis tool** using Java **Maps** and **Generics**.
The scenario is grounded in both **software systems** and **game development**:

- Counting country codes from a real CSV dataset
- Taking stock of weapon inventory in a game factory from an XML file

The same *core algorithm* is reused in both contexts.

---

## Learning Objectives

By completing this exercise, you should be able to:

- Use `Map<K, V>` to count frequencies
- Apply generics to avoid code duplication
- Read structured data from CSV and XML files
- Measure execution time using `System.nanoTime()`
- Produce sorted reports from map data

---

## Part A – Country Code Frequency (CSV)

You are given a CSV file:

```
country_codes.csv
```

This file contains a list of country codes separated by commas.

### Task A.1
- Load the country codes from the CSV file into a `List<String>`
- Count how often each country code appears
- Print the results sorted alphabetically by country code

---

## Part B – Weapon Inventory Frequency (XML)

A game factory stores weapons in an XML file:

```
weapon_inventory.xml
```

Each weapon has:
- `name` (String)
- `strength` (int)

### Task B.1
- Load the weapon data from XML into a `List<Weapon>`
- Count how many weapons exist for each weapon name
- Print the results sorted alphabetically by weapon name

---

## Part C – Benchmarking

For **both parts**, measure the time taken to perform the frequency count:

- Use `System.nanoTime()`
- Convert the result to milliseconds
- Output the elapsed time

---

## Provided Classes

You are provided with the following helper and model classes.
You should **not modify** these files.

<details>
<summary><strong>Weapon.java</strong></summary>

```java
package t08_generics.challenges.ce03;

import java.util.Objects;

public class Weapon {
    private String name;
    private int strength;

    public Weapon(String name, int strength) {
        this.name = name;
        this.strength = strength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    @Override
    public String toString() {
        return "Weapon{" +
                "name='" + name + '\'' +
                ", strength=" + strength +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Weapon weapon)) return false;
        return strength == weapon.strength && Objects.equals(name, weapon.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, strength);
    }
}

```
</details>

<details>
<summary><strong>FileHelper.java</strong></summary>

```java
package t08_generics.challenges.ce03;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileHelper
{
    /// <summary>
    /// Reads a list of strings from a CSV file (first column only).
    /// </summary>
    public static List<String> readStringsFromCsv(Path csvPath, boolean hasHeader) throws IOException
    {
        if (csvPath == null)
            throw new IllegalArgumentException("csvPath is null.");

        if (!Files.exists(csvPath))
            throw new IOException("CSV file not found: " + csvPath);

        List<String> results = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(csvPath))
        {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null)
            {
                if (firstLine && hasHeader)
                {
                    firstLine = false;
                    continue;
                }
                firstLine = false;

                line = line.trim();
                if (line.isEmpty())
                    continue;

                // Your dataset is "token,token,token" (often all on one line),
                // so we must collect *every* token, not just parts[0].
                String[] tokens = line.split(",");

                for (String token : tokens)
                {
                    String value = token.trim();

                    if (!value.isEmpty())
                        results.add(value);
                }
            }
        }

        return results;
    }

    /// <summary>
    /// Reads Weapon objects from an XML file.
    /// Expected structure:
    /// &lt;weapons&gt;
    ///   &lt;weapon&gt;&lt;name&gt;...&lt;/name&gt;&lt;strength&gt;...&lt;/strength&gt;&lt;/weapon&gt;
    /// &lt;/weapons&gt;
    /// </summary>
    public static List<Weapon> readWeaponsFromXml(Path xmlPath) throws Exception
    {
        if (xmlPath == null)
            throw new IllegalArgumentException("xmlPath is null.");

        if (!Files.exists(xmlPath))
            throw new IOException("XML file not found: " + xmlPath);

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(xmlPath.toFile());

        doc.getDocumentElement().normalize();

        NodeList weaponNodes = doc.getElementsByTagName("weapon");
        List<Weapon> weapons = new ArrayList<>();

        for (int i = 0; i < weaponNodes.getLength(); i++)
        {
            Element weaponEl = (Element) weaponNodes.item(i);

            String name = getChildText(weaponEl, "name").trim();
            String strengthText = getChildText(weaponEl, "strength").trim();

            if (name.isEmpty())
                throw new IllegalStateException("Weapon name is missing/blank at index " + i);

            int strength;
            try
            {
                strength = Integer.parseInt(strengthText);
            }
            catch (NumberFormatException ex)
            {
                throw new IllegalStateException("Weapon strength is not a valid integer at index " + i + ": " + strengthText);
            }

            weapons.add(new Weapon(name, strength));
        }

        return weapons;
    }

    private static String getChildText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0)
            return "";

        return nodes.item(0).getTextContent();
    }
}

```
</details>

<details>
<summary><strong>PrintHelper.java</strong></summary>

```java
package t08_generics.challenges.ce03;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PrintHelper
{
    /// <summary>
    /// Prints map contents sorted by key using the key's toString() ordering.
    /// Keeps things simple for Year 2: no Comparable constraint needed.
    /// </summary>
    public static <K, V> void printSortedByKey(Map<K, V> map)
    {
        if (map == null)
            throw new IllegalArgumentException("map is null.");

        List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Comparator.comparing(e -> e.getKey().toString()));

        for (Map.Entry<K, V> entry : entries)
        {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}


```
</details>

---

## Your Task

Complete the `Exercise.java` file by:

1. Loading the CSV and XML datasets
2. Applying a generic frequency-counting method
3. Printing sorted reports
4. Displaying benchmark timings in milliseconds

<details>
<summary><strong>Exercise.java</strong></summary>

```java
package t08_generics.challenges.ce03;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Exercise {

    public static void run() {
        new Exercise().start();
    }

    public void start() {

        //helper to tell you what directory to put your data file in
        System.out.println("Put your data file in: " + System.getProperty("user.dir"));

        System.out.println("=== Part A: Country code frequency (CSV) ===");
        runStringTest();

        System.out.println("=== Part B: Weapon inventory frequency (XML) ===");
        runXMLTest();
    }

    public void runStringTest()
    {
        String filePath = "code/data/ce03/country_codes.csv";
        Path csvPath = Path.of(filePath);

        List<String> codes;

        try {
            codes = FileHelper.readStringsFromCsv(csvPath, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        long start = System.nanoTime();
        Map<String, Integer> counts = countByKey(codes);
        long end = System.nanoTime();



        System.out.println("Rows read: " + codes.size());
        System.out.println("Unique codes: " + counts.size());
        long elapsedNs = end - start;
        double elapsedMs = elapsedNs / 1_000_000.0;
        System.out.println("Elapsed: " + elapsedMs + " ms");
        System.out.println();

        System.out.println("Sorted report:");
        PrintHelper.printSortedByKey(counts);
    }

    public void runXMLTest(){
        String filePath = "code/data/ce03/weapon_inventory.xml";
        Path xmlPath = Path.of(filePath);

        List<Weapon> weapons;
        try {
            weapons = FileHelper.readWeaponsFromXml(xmlPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        long start = System.nanoTime();
        Map<Weapon, Integer> counts = countByKey(weapons);
        long end = System.nanoTime();

        System.out.println("=== Weapon inventory frequency (XML) ===");
        System.out.println("Weapons read: " + weapons.size());
        System.out.println("Unique weapon names: " + counts.size());
        long elapsedNs = end - start;
        double elapsedMs = elapsedNs / 1_000_000.0;
        System.out.println("Elapsed: " + elapsedMs + " ms");
        System.out.println();

        System.out.println("Sorted report:");
        PrintHelper.printSortedByKey(counts);
    }

    public <T> Map<T, Integer> countByKey(List<T> items)
    {
        Map<T, Integer> counts = new HashMap<>();
        for (T item : items)
        {
            Integer current = counts.get(item);
            if (current == null)
                counts.put(item, 1);
            else
                counts.put(item, current + 1);
        }
        return counts;
    }
}

```
</details>

---

## Note

- Do **not** hard-code data
- Focus on clarity, correctness, and reuse

---

## Stretch Ideas (Optional)

- Compare `HashMap` vs `TreeMap`
- Count weapons by *strength* instead of name
- Increase dataset size and observe benchmark changes
