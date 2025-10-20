import t01_arrays.demos.de01.Demo;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public void run() {

        System.out.println("Topics...\n");
        run_t01_Arrays();

       /* System.out.println("Challenge Exercises...\n");
        runChallengeExercises();*/
    }

    public void run_t01_Arrays() {
        System.out.println("Demo 01...\n");
        t01_arrays.demos.de01.Demo.run();

        System.out.println("Demo 02...\n");
        t01_arrays.demos.de02.Demo.run();

        System.out.println("Exercise 01...\n");
        t01_arrays.exercises.ex01.Exercise.run();

        System.out.println("Exercise 04...\n");
        t01_arrays.exercises.ex04.Exercise.run();
    }

    public void runChallengeExercises()
    {
        System.out.println("Challenge Exercise 01...");
        t01_arrays.challenges.ce01.Exercise.run();
    }
}
