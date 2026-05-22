package backend;

import enums.user.v1.UserRole;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 * JavaFX 화면이 사용하는 기능 목록을 모아 둔 UI용 백엔드 연결 인터페이스이다.
 *
 * 설계 의도:
 * - 화면 클래스가 구체 저장소나 데이터 처리 클래스를 직접 알면 View와 데이터 처리 코드가 강하게 결합된다.
 * - 그래서 화면은 이 인터페이스만 바라보게 하고, 실제 처리 방식은 구현체가 담당하도록 분리했다.
 * - 이는 UI가 구현체가 아니라 추상화에 의존하도록 하려는 DIP 관점의 설계이다.
 * - 또한 화면에서 필요한 데이터 형태만 record로 정의하여 View가 내부 저장 구조에 끌려가지 않게 했다.
 */
public interface JavaFxBackend {

    /*
     * 로그인 성공 후 화면 전환에 필요한 최소 사용자 정보이다.
     * View는 사용자 전체 객체가 아니라 로그인 ID와 역할만 알면 된다.
     */
    record LoginUser(String loginId, UserRole role) {
    }

    /*
     * 프로젝트 목록 화면에 필요한 값만 담는다.
     * ListView에서 프로젝트명이 바로 보이도록 toString을 화면 표시용으로 정의했다.
     */
    record ProjectItem(int id, String name, String description) {
        @Override
        public String toString() {
            return name;
        }
    }

    /*
     * 프로젝트별 계정 목록에 필요한 값만 담는다.
     * 현재 과제 데모에서는 관리자 화면에서 계정 정보를 확인해야 하므로 비밀번호도 표시 대상에 포함했다.
     */
    record UserItem(int id, String loginId, String password, UserRole role, Integer projectId) {
        @Override
        public String toString() {
            return loginId + " (" + role + ")";
        }
    }

    /*
     * 이슈 상세 정보에서 코멘트 이력을 보여주기 위한 화면용 데이터이다.
     */
    record CommentItem(String writer, String body, String createdAt) {
        @Override
        public String toString() {
            return "[" + createdAt + "] " + writer + ": " + body;
        }
    }

    /*
     * 이슈 목록과 상세 정보에 필요한 최소 필드를 모은 화면용 이슈 데이터이다.
     * 내부 모델을 그대로 노출하지 않고 화면에 필요한 형태로 한 번 끊어 주기 위한 목적이다.
     */
    record IssueItem(
            int id,
            int projectId,
            String title,
            String description,
            String reporter,
            String reportedDate,
            String priority,
            String status,
            String assignee,
            String fixer,
            List<CommentItem> comments
    ) {
    }

    Optional<LoginUser> login(String loginId, String password);

    int countByStatus(String status);

    List<ProjectItem> projects();

    List<ProjectItem> projectsForUser(String loginId, UserRole role);

    List<UserItem> usersForProject(int projectId);

    ProjectItem addProject(String name, String description);

    void deleteProject(int projectId);

    boolean hasLoginId(String loginId);

    void addUser(String loginId, String password, UserRole role, int projectId);

    void deleteUser(String loginId);

    List<String> developerLoginIds();

    List<String> developerLoginIdsForProject(int projectId);

    List<String> testerLoginIds();

    List<IssueItem> issuesForRole(String loginId, UserRole role);

    void registerIssue(int projectId, String title, String description, String reporter, String priority);

    void assignIssue(int issueId, String assignee, String writer, String comment);

    void markFixed(int issueId, String fixer, String comment);

    void resolveIssue(int issueId, String writer, String comment);

    void reopenIssue(int issueId, String writer, String comment);

    void closeIssue(int issueId, String writer, String comment);

    void addComment(int issueId, String writer, String comment);

    List<String> recommendAssignees(IssueItem issue);

    Map<String, Long> dailyIssueCounts();
}
