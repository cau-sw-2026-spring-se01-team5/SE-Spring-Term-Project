package backend;

/*
 * JavaFX 애플리케이션에서 사용할 JavaFxBackend 구현체를 생성하는 조립 지점이다.
 *
 * Swing의 app.Main이 repository와 controller 구현체를 조립하는 것처럼,
 * JavaFX에서는 이 클래스에서 mock backend와 실제 backend 중 하나를 선택한다.
 *
 * 기본값은 REAL이다.
 * mock으로 돌리고 싶으면 DEFAULT_MODE를 MOCK으로 바꾸거나,
 * 실행 옵션에 -Djavafx.backend=mock 을 주면 된다.
 */
public final class BackendFactory {

    private static final BackendMode DEFAULT_MODE = BackendMode.REAL;

    private BackendFactory() {
    }

    public static JavaFxBackend create() {
        BackendMode mode = mode();
        return switch (mode) {
            case REAL -> RealJavaFxBackend.create();
            case MOCK -> new MockJavaFxBackend();
        };
    }

    private static BackendMode mode() {
        String value = System.getProperty("javafx.backend");
        if (value == null || value.isBlank()) {
            return DEFAULT_MODE;
        }
        return BackendMode.valueOf(value.trim().toUpperCase());
    }
}
