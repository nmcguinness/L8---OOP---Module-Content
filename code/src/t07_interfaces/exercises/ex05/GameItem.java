package t07_interfaces.exercises.ex05;

public class GameItem implements Named {

    private final String name;

    public GameItem(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
