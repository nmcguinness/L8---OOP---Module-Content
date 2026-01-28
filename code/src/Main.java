import t07_interfaces.challenges.ce02.Exercise;

import java.util.HashSet;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

   public void run() {

       HashSet<String> set = new HashSet<String>();
       // 1 = t01 Arrays, 2 = t02 Ordering, etc
        int selection = 8; // <— set which block to run

        switch (selection) {
            case 1:
                System.out.println("\n******************** Topic:t01 - arrays ********************\n");
                run_t01_Arrays();
                break;

            case 2:
                System.out.println("\n******************** Topic:t02 - ordering ********************\n");
                run_t02_Ordering();
                break;

            case 3:
                System.out.println("\n******************** Topic:t03 - arraylist ********************\n");
                run_t03_ArrayList();
                break;

            case 4:
                System.out.println("\n******************** Topic:t04 - linkedlist ********************\n");
                run_t04_LinkedList();
                break;

            case 5:
                System.out.println("\n******************** Topic:t05 - equality & hashing ********************\n");
                run_t05_EqualityHashing();
                break;

            case 6:
                System.out.println("\n******************** Topic:t06 - Inheritance ********************\n");
                run_t06_Inheritance();
                break;

            case 7:
                System.out.println("\n******************** Topic:t07 - Interface ********************\n");
                run_t07_Interface();
                break;

            case 8:
                System.out.println("\n******************** Topic:t08 - Generics I ********************\n");
                run_t08_Generics();
                break;

            case 10:
                System.out.println("\n******************** Challenge Exercises ********************\n");
                runChallengeExercises();
                break;

            default:
                System.out.println("No matching topic block for selection: " + selection);
                break;
        }
    }

    public void runChallengeExercises() {
        System.out.println("Challenge Exercise t01...");
        t01_arrays.challenges.ce01.Exercise.run();
    }

    public void run_t01_Arrays() {
        System.out.println("\n********** Demo 01 **********\n");
        t01_arrays.demos.de01.Demo.run();

        System.out.println("\n********** Demo 02 **********\n");
        t01_arrays.demos.de02.Demo.run();

        System.out.println("\n********** Exercise 01 — Fill, sum, average **********\n");
        t01_arrays.exercises.ex01.Exercise.run();

        System.out.println("\n********** Exercise 02 — indexOf & count **********\n");
        t01_arrays.exercises.ex02.Exercise.run();

        System.out.println("\n********** Exercise 03 — Min/Max with guards **********\n");
        t01_arrays.exercises.ex03.Exercise.run();

        System.out.println("\n********** Exercise 04 — Grade histogram **********\n");
        t01_arrays.exercises.ex04.Exercise.run();

        System.out.println("\n********** Exercise 08 — Heatmap normalization **********\n");
        t01_arrays.exercises.ex08.Exercise.run();

        System.out.println("\n********** Exercise 09 — Tic-Tac-Toe checker **********\n");
        t01_arrays.exercises.ex09.Exercise.run();

        System.out.println("\n********** Exercise 11 — Steps data wrangling **********\n");
        t01_arrays.exercises.ex11.Exercise.run();
    }

    public void run_t02_Ordering() {
        System.out.println("\n********** Demo 01 - NetworkConnection **********\n");
        t02_ordering.demos.de01.Demo.run();

        System.out.println("\n********** Exercise 01 — Comparable: score ranking **********\n");
        t02_ordering.exercises.ex01.Exercise.run();

        System.out.println("\n********** Exercise 02 — Comparator: name A-Z **********\n");
        t02_ordering.exercises.ex02.Exercise.run();

        System.out.println("\n********** Exercise 03 — Anon comparator: price asc **********\n");
        t02_ordering.exercises.ex03.Exercise.run();

        System.out.println("\n********** Exercise 04 — Multi-key: rating desc, name **********\n");
        t02_ordering.exercises.ex04.Exercise.run();

        System.out.println("\n********** Exercise 05 — Stable sort demo **********\n");
        t02_ordering.exercises.ex05.Exercise.run();

        System.out.println("\n********** Exercise 06 — Nulls-last policy **********\n");
        t02_ordering.exercises.ex06.Exercise.run();

        System.out.println("\n********** Exercise 07 — Comparable: due date **********\n");
        t02_ordering.exercises.ex07.Exercise.run();

        System.out.println("\n********** Exercise 08 — Chain: price->rating->name **********\n");
        t02_ordering.exercises.ex08.Exercise.run();
    }

    public void run_t03_ArrayList() {
        System.out.println("\n********** Demo 01 - ArrayList basics **********\n");
        t03_arraylist.demos.de01.Demo.run();

        System.out.println("\n********** Demo 02 - ArrayList basics **********\n");
        t03_arraylist.demos.de02.Demo.run();

        System.out.println("\n********** Demo 03 - ArrayList basics **********\n");
        t03_arraylist.demos.de03.Demo.run();

        System.out.println("\n********** Demo 04 - ArrayList basics **********\n");
        t03_arraylist.demos.de04.Demo.run();

        System.out.println("\n********** Exercise 01 — Basic ArrayList ops **********\n");
        t03_arraylist.exercises.ex01.Exercise.run();

        System.out.println("\n********** Exercise 02 — Convert between array & list **********\n");
        t03_arraylist.exercises.ex02.Exercise.run();

        System.out.println("\n********** Exercise 03 — Remove even numbers (iterator) **********\n");
        t03_arraylist.exercises.ex03.Exercise.run();

        System.out.println("\n********** Exercise 04 — Unique values, preserve order **********\n");
        t03_arraylist.exercises.ex04.Exercise.run();

        System.out.println("\n********** Exercise 05 — Time appends vs front inserts **********\n");
        t03_arraylist.exercises.ex05.Exercise.run();

        System.out.println("\n********** Exercise 06 — Find & update task by title **********\n");
        t03_arraylist.exercises.ex06.Exercise.run();

        System.out.println("\n********** Exercise 07 — Split into evens and odds **********\n");
        t03_arraylist.exercises.ex07.Exercise.run();

        System.out.println("\n********** Exercise 08 — Merge two sorted lists **********\n");
        t03_arraylist.exercises.ex08.Exercise.run();

        System.out.println("\n********** Exercise 09 — Group names by first letter **********\n");
        t03_arraylist.exercises.ex09.Exercise.run();

        System.out.println("\n********** Exercise 10 — Simple high-score leaderboard **********\n");
        t03_arraylist.exercises.ex10.Exercise.run();
    }

    public void run_t04_LinkedList() {

        System.out.println("\n********** Demo 01 — LinkedList basics **********\n");
        t04_linkedlist.demos.de01.Demo.run();

        System.out.println("\n********** Demo 02 — LinkedList basics **********\n");
        t04_linkedlist.demos.de02.Demo.run();

        System.out.println("\n********** Demo 03 — LinkedList basics **********\n");
        t04_linkedlist.demos.de03.Demo.run();

        System.out.println("\n********** Exercise 01 — Remove non-positive values **********\n");
        t04_linkedlist.exercises.ex01.Exercise.run();

        System.out.println("\n********** Exercise 02 — Insert group separators **********\n");
        t04_linkedlist.exercises.ex02.Exercise.run();

        System.out.println("\n********** Exercise 03 — Iterate forwards and backwards **********\n");
        t04_linkedlist.exercises.ex03.Exercise.run();

        System.out.println("\n********** Exercise 04 — Job queue with inserted tasks **********\n");
        t04_linkedlist.exercises.ex04.Exercise.run();

        System.out.println("\n********** Exercise 05 — Undo/redo using two stacks **********\n");
        t04_linkedlist.exercises.ex05.Exercise.run();

        System.out.println("\n********** Exercise 06 — Event list with inline edits **********\n");
        t04_linkedlist.exercises.ex06.Exercise.run();

        System.out.println("\n********** Exercise 07 — Josephus survivor index **********\n");
        t04_linkedlist.exercises.ex07.Exercise.run();

        System.out.println("\n********** Exercise 08 — In-place merge of sorted lists **********\n");
        t04_linkedlist.exercises.ex08.Exercise.run();

        System.out.println("\n********** Exercise 09 — Sliding window maximum **********\n");
        t04_linkedlist.exercises.ex09.Exercise.run();

        System.out.println("\n********** Exercise 10 — Priority turn scheduler **********\n");
        t04_linkedlist.exercises.ex10.Exercise.run();

    }

    public void run_t05_EqualityHashing() {

        System.out.println("\n********** Exercise 01 — Identity vs value equality **********\n");
        t05_equality_hashing.exercises.ex01.Exercise.run();

        System.out.println("\n********** Exercise 02 — equals/hashCode contract **********\n");
        t05_equality_hashing.exercises.ex02.Exercise.run();

        System.out.println("\n********** Exercise 03 — Hand-calculating a string hash **********\n");
        t05_equality_hashing.exercises.ex03.Exercise.run();

        System.out.println("\n********** Exercise 04 — Combining field hashes **********\n");
        t05_equality_hashing.exercises.ex04.Exercise.run();

        System.out.println("\n********** Exercise 05 — From hash value to bucket index **********\n");
        t05_equality_hashing.exercises.ex05.Exercise.run();

        System.out.println("\n********** Exercise 06 — equals/hashCode for Customer **********\n");
        t05_equality_hashing.exercises.ex06.Exercise.run();

        System.out.println("\n********** Exercise 07 — Hash collisions with sum hash **********\n");
        t05_equality_hashing.exercises.ex07.Exercise.run();

        System.out.println("\n********** Exercise 08 — Mutable keys in hashed collections **********\n");
        t05_equality_hashing.exercises.ex08.Exercise.run();
    }

    public void run_t06_Inheritance() 
    {
        System.out.println("\n********** Exercise 01 — is-a vs has-a & AdminUser **********\n");
        t06_inheritance.exercises.ex01.Exercise.run();

        System.out.println("\n********** Exercise 02 — Basic Entity / Player / Enemy hierarchy **********\n");
        t06_inheritance.exercises.ex02.Exercise.run();

        System.out.println("\n********** Exercise 03 — BossEnemy and constructor chaining **********\n");
        t06_inheritance.exercises.ex03.Exercise.run();

        System.out.println("\n********** Exercise 04 — Weapon polymorphism (Sword, Staff) **********\n");
        t06_inheritance.exercises.ex04.Exercise.run();

        System.out.println("\n********** Exercise 05 — Abstract GameEntity update/render loop **********\n");
        t06_inheritance.exercises.ex05.Exercise.run();

        System.out.println("\n********** Exercise 06 — Overriding vs overloading (Formatter) **********\n");
        t06_inheritance.exercises.ex06.Exercise.run();
    }

    private void run_t07_Interface() {
        System.out.println("\n********** Challenge Exercise 02 — Contact Consolidation Tool **********\n");
        t07_interfaces.challenges.ce02.Exercise.run();

        System.out.println("\n********** Exercise 01 - Basic Greeter interface with casual and formal implementations **********\n");
        t07_interfaces.exercises.ex01.Exercise.run();

        System.out.println("\n********** Exercise 02 - Interactable interface with doors, chests, and NPCs + interaction range **********\n");
        t07_interfaces.exercises.ex02.Exercise.run();

        System.out.println("\n********** (NOT IMPLEMENTED AS QUITE STRAIGHTFORWARD) Exercise 03 - Multiple interfaces (Moveable, Damageable) with Player and Enemy **********\n");

        System.out.println("\n********** (NOT IMPLEMENTED AS QUITE STRAIGHTFORWARD) Exercise 04 - Strategy pattern for enemy attacks (swap melee/ranged behaviour) **********\n");

        System.out.println("\n********** Exercise 05 - Named interface with Comparators for sorting by name and length **********\n");
        t07_interfaces.exercises.ex05.Exercise.run();

        System.out.println("\n********** Exercise 06 - Command interface for queued actions (execute() loop) **********\n");
        t07_interfaces.exercises.ex06.Exercise.run();

        System.out.println("\n********** Exercise 07 - XmlSerializable interface with PlayerProfile toXml()/fromXml **********\n");
        t07_interfaces.exercises.ex07.Exercise.run();

        System.out.println("\n********** Exercise 08 - TextFilter pipeline (trim, lowercase, regex highlight) **********\n");
        t07_interfaces.exercises.ex08.Exercise.run();

        System.out.println("\n********** Exercise 09 - TextFilter pipeline with profanity blacklist from CSV **********\n");
        t07_interfaces.exercises.ex09.Exercise.run();
    }

    private void run_t08_Generics() {
        System.out.println("\n********** Demo 01 — BoxObject **********\n");
        t08_generics.demos.de01.Demo.run();

        System.out.println("\n********** Demo 02 — Box **********\n");
        t08_generics.demos.de02.Demo.run();

        System.out.println("\n********** Demo 03 — Diamond operator **********\n");
        t08_generics.demos.de03.Demo.run();

        System.out.println("\n********** Demo 04 — Generic methods (first) **********\n");
        t08_generics.demos.de04.Demo.run();

        System.out.println("\n********** Demo 05 — Generic methods (max) **********\n");
        t08_generics.demos.de05.Demo.run();
    }
}
