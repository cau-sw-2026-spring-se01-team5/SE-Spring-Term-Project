package main.support;

import session.UserSession;

public class ProjectContextGuard {

    @FunctionalInterface
    public interface MessageSink {
        void showMessage(String message);
    }

    private final UserSession session;

    public ProjectContextGuard(UserSession session) {
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
