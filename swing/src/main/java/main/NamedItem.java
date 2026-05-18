package main;

// 실제 값과 화면에 보여줄 값을 함께 저장하는 객체
public record NamedItem<T>(T value, String label) {
    @Override
    public String toString() {
        return label;
    }
}