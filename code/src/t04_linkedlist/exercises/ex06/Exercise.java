package t04_linkedlist.exercises.ex06;

import java.util.LinkedList;
import java.util.ListIterator;

public class Exercise {

    private static class InputEvent {
        String type;

        InputEvent(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public static void run() {
        LinkedList<InputEvent> events = new LinkedList<>();
        events.add(new InputEvent("Move"));
        events.add(new InputEvent("Attack"));
        events.add(new InputEvent("Heal"));

        ListIterator<InputEvent> it = events.listIterator();
        while (it.hasNext()) {
            InputEvent e = it.next();
            if ("Attack".equals(e.type)) {
                it.add(new InputEvent("CameraShake"));
            } else if ("Heal".equals(e.type)) {
                it.remove();
            }
        }

        for (InputEvent e : events) {
            System.out.println(e.type);
        }
    }
}
