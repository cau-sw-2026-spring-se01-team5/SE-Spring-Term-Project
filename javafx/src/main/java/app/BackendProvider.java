package app;

/*
 * JavaFX 화면이 사용할 JavaFxBackend 구현체를 한 곳에서 제공한다.
 *
 * 설계 의도:
 * - 각 View에서 new DemoJavaFxBackend()를 직접 만들면 구현체 교체 지점이 여러 곳으로 퍼진다.
 * - 제공 위치를 이 클래스 하나로 모으면 화면 코드의 변경 범위를 줄일 수 있다.
 * - 화면 입장에서는 BackendProvider.backend()만 호출하면 되므로 생성 방식과 구현체 선택을 몰라도 된다.
 */
public final class BackendProvider {

    /*
     * 현재는 데모 실행을 위해 DemoJavaFxBackend를 사용한다.
     * 나중에 다른 구현체를 쓰더라도 View 파일을 직접 수정하지 않기 위해 이곳에만 생성 코드를 둔다.
     */
    private static final JavaFxBackend BACKEND = new DemoJavaFxBackend();

    private BackendProvider() {
    }

    public static JavaFxBackend backend() {
        return BACKEND;
    }
}
