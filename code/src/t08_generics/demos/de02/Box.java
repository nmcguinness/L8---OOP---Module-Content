package t08_generics.demos.de02;

class Box<T> {
    private T value;

    public Box(T value) {
        this.value = value;
    }

    public T value() {
        return value;
    }
}
