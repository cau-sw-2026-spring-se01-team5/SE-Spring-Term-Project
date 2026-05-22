package backend;

/*
 * JavaFX에서 사용할 backend 방식을 고르는 enum이다.
 *
 * REAL: core 모듈의 실제 구현체와 SQLite 저장소를 사용한다.
 * MOCK: JavaFX 안에 복사해 둔 mock 구현체를 사용한다.
 *
 * 이 값을 통해 실제 backend 연동 방식과 mock 방식이 공존할 수 있다.
 */
public enum BackendMode {
    REAL,
    MOCK
}
