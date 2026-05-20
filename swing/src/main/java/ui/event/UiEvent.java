package ui.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* UI 이벤트 관리 */
/* 버튼 눌리면 실행할 함수들을 저장하고 emit을 통해서 등록된 함수들을 전부 실행할 수 있도록 함 */
public class UiEvent {

    private final List<Runnable> listeners = new CopyOnWriteArrayList<>(); // Thread-safe한 리스트 구현체 -> 반복 중에도 안전하게 수정 가능

    // 이벤트 등록 메서드
    public AutoCloseable subscribe(Runnable listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    // 이벤트 실행 메서드
    public void emit() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}
