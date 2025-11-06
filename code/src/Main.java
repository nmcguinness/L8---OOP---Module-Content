
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public void run() {

        // 1 = t01 Arrays, 10 = t01 Challenge Exercises, 2 = t02 Ordering
        int selection = 2; // <— set which block to run

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
        System.out.println("Challenge Exercise 01...");
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
    }

}
