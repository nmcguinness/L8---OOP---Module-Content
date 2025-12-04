package t07_interfaces.exercises.ex01;

public class FormalGreeter implements Greeter {

    @Override
    public void greet(String name) {
        System.out.println("Good evening, " + name + ".");
    }
}
