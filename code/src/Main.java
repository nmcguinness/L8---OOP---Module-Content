
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
                System.out.println("******************** Topic:t01 ********************\n");
                run_t01_Arrays();
                break;

            case 2:
                System.out.println("******************** Topic:t02 ********************\n");
                run_t02_Ordering();
                
                break;

            case 10:
                System.out.println("******************** Challenge Exercises ********************\n");
                runChallengeExercises();
                break;

            default:
                System.out.println("No matching topic block for selection: " + selection);
                break;
        }
    }

    public void runChallengeExercises()
    {
        System.out.println("Challenge Exercise 01...");
        t01_arrays.challenges.ce01.Exercise.run();
    }

    public void run_t01_Arrays() {
        System.out.println("********** Demo 01 **********\n");
        t01_arrays.demos.de01.Demo.run();

        System.out.println("********** Demo 02 **********\n");
        t01_arrays.demos.de02.Demo.run();

        System.out.println("********** Exercise 01 **********\n");
        t01_arrays.exercises.ex01.Exercise.run();

        System.out.println("********** Exercise 02 **********\n");
        t01_arrays.exercises.ex02.Exercise.run();

        System.out.println("********** Exercise 03 **********\n");
        t01_arrays.exercises.ex03.Exercise.run();

        System.out.println("********** Exercise 04 **********\n");
        t01_arrays.exercises.ex04.Exercise.run();

        System.out.println("********** Exercise 08 **********\n");
        t01_arrays.exercises.ex08.Exercise.run();

        System.out.println("********** Exercise 09 **********\n");
        t01_arrays.exercises.ex09.Exercise.run();

        System.out.println("********** Exercise 11 **********\n");
        t01_arrays.exercises.ex11.Exercise.run();
    }

    public void run_t02_Ordering()
    {
        System.out.println("********** Demo 01 **********\n");
        t02_ordering.demos.de01.Demo.run();
    }

}
