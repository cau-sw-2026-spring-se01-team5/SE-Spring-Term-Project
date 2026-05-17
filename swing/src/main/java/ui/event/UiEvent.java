package ui.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UiEvent {

    private final List<Runnable> listeners = new CopyOnWriteArrayList<>();

    public AutoCloseable subscribe(Runnable listener) {
        listeners.add(listener);
        return () -> listeners.remove(listener);
    }

    public void emit() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}
