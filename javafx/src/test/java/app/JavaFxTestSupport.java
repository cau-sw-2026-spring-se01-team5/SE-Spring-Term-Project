package app;

// JavaFX 인터페이스 테스트에서 공통으로 쓰는 wiring 준비 헬퍼다.
final class JavaFxTestSupport {

    private JavaFxTestSupport() {
    }

    static JavaFxServices mockServices() throws Exception {
        return new MockJavaFxWiring().wire();
    }
}
