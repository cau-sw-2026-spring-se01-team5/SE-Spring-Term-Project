package main;

public record NamedItem<T>(
        T value,
        String label
) {
    @Override
    public String toString() {
        return label;
    }
}