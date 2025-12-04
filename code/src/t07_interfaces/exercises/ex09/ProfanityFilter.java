package t07_interfaces.exercises.ex09;

import t07_interfaces.exercises.ex08.TextFilter;

import java.util.Locale;

public class ProfanityFilter implements TextFilter {

    private final Iterable<String> badWords;

    public ProfanityFilter(Iterable<String> badWords) {
        this.badWords = badWords;
    }

    @Override
    public String apply(String input) {
        if (input == null) {
            return null;
        }

        String result = input;

        for (String bad : badWords) {
            if (bad == null || bad.isBlank()) {
                continue;
            }

            String badLower = bad.toLowerCase(Locale.ROOT).trim();
            if (badLower.isEmpty()) {
                continue;
            }

            result = result.replaceAll("(?i)" + badLower, "***");
        }

        return result;
    }
}
