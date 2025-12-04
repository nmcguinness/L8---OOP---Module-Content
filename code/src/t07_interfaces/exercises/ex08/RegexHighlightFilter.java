package t07_interfaces.exercises.ex08;

public class RegexHighlightFilter implements TextFilter {

    @Override
    public String apply(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("(\\d{7,})", "<u>$1</u>");
    }
}
