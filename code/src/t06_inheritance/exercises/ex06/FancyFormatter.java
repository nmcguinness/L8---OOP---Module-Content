package t06_inheritance.exercises.ex06;

public class FancyFormatter extends BaseFormatter {

    @Override
    public String format() {
        return "Fancy format";
    }

    // Overloaded convenience method with a prefix parameter.
    public String format(String prefix) {
        return prefix + " " + format();
    }

    /*
     * The original format(String prefix) did NOT override BaseFormatter.format()
     * because the parameter list was different — that creates a new overload
     * instead of matching the parent's signature. Adding @Override to the
     * intended overriding method forces the compiler to check the signature
     * and would have highlighted this bug immediately.
     */
}
