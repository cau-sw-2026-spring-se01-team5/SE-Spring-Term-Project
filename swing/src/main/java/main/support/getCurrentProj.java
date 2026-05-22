package main.support;

import session.UserSession;

// 현재 선택된 프로젝트가 있는지를 검증
public class getCurrentProj {

    @FunctionalInterface
    public interface MessageSink {
        void showMessage(String message);
    }

    private final UserSession session;

    public getCurrentProj(UserSession session) {
        this.session = session;
    }

    public Integer requireProjectId(MessageSink messageSink) {
        Integer projectId = session.selectedProjectId();

        if (projectId == null) {
            messageSink.showMessage("프로젝트를 선택하세요.");
            return null;
        }

        return projectId;
    }
}
