package main.issue;

import backend.JavaFxBackend;
import backend.JavaFxBackend.IssueItem;
import session.UserSession;

import java.util.List;
import java.util.stream.Collectors;

/*
 * 이슈 기능의 사용자 이벤트와 backend 호출 흐름을 담당하는 Controller이다.
 *
 * IssuePanel은 화면 입력과 표시만 담당하고, 이슈 등록/배정/상태 변경/추천/통계 처리는
 * 이 Controller가 JavaFxBackend에 요청한다.
 */
public class IssueController {

    private final IssueView view;
    private final JavaFxBackend backend;
    private final UserSession session;

    public IssueController(IssueView view, JavaFxBackend backend, UserSession session) {
        this.view = view;
        this.backend = backend;
        this.session = session;
        bind();
    }

    public void start() {
        /*
         * 화면이 처음 열릴 때 검색 필터의 선택지를 backend에서 가져온다.
         * 개발자/테스터 계정 목록은 데이터에 따라 달라질 수 있으므로 View에 고정하지 않았다.
         */
        view.setFilterOptions(backend.developerLoginIds(), backend.testerLoginIds());
        refreshTable();
    }

    private void bind() {
        /*
         * View에서 발생하는 사용자 이벤트를 Controller 메서드에 연결한다.
         * View는 버튼 클릭 사실만 알려주고, 실제로 무엇을 할지는 Controller가 결정한다.
         */
        view.onSearch(this::refreshTable);
        view.onRegisterIssue(this::registerIssue);
        view.onShowIssueDetail(this::showSelectedIssue);
        view.onAddComment(this::addComment);
        view.onAssignIssue(this::assignIssue);
        view.onRecommendAssignee(this::recommendAssignee);
        view.onCloseIssue(this::closeIssue);
        view.onShowStatistics(this::showStatistics);
        view.onMarkFixed(this::markFixed);
        view.onResolveIssue(this::resolveIssue);
        view.onReopenIssue(this::reopenIssue);
    }

    private void refreshTable() {
        /*
         * 역할별로 볼 수 있는 이슈 목록은 backend가 1차로 결정한다.
         * 그 이후 키워드, 상태, 우선순위 같은 화면 검색 조건은 View가 가진 필터 조건으로 거른다.
         */
        List<IssueItem> filtered = backend.issuesForRole(session.loginId(), session.role()).stream()
                .filter(view::matchesFilter)
                .collect(Collectors.toList());
        view.setIssues(filtered);
    }

    private void registerIssue() {
        /*
         * 이슈 등록은 테스터가 입력한 프로젝트, 제목, 설명, 우선순위를 받아 backend에 요청한다.
         * reporter는 화면 입력값이 아니라 현재 로그인 사용자(session.loginId)로 자동 지정한다.
         */
        view.showRegisterIssueDialog(backend.projectsForUser(session.loginId(), session.role())).ifPresent(form -> {
            backend.registerIssue(form.project().id(), form.title(), form.description(), session.loginId(), form.priority());
            refreshTable();
        });
    }

    private void assignIssue() {
        /*
         * 배정은 선택된 이슈가 있어야 하고, NEW 또는 REOPENED 상태에서만 가능하다.
         * 이 규칙을 View가 아니라 Controller에서 확인해서 화면 코드에 업무 규칙이 섞이지 않게 했다.
         */
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }
        if (!"NEW".equals(selected.status()) && !"REOPENED".equals(selected.status())) {
            view.showWarning("PL은 NEW 또는 REOPENED 상태의 이슈만 배정할 수 있습니다.");
            return;
        }
        view.showAssignIssueDialog(backend.developerLoginIdsForProject(selected.projectId()), session.loginId()).ifPresent(form -> {
            backend.assignIssue(selected.id(), form.assignee(), session.loginId(), form.comment());
            refreshTable();
        });
    }

    private void markFixed() {
        /*
         * 개발자는 자신에게 배정된 이슈만 FIXED로 변경할 수 있다.
         * 현재 로그인 사용자와 선택 이슈의 assignee를 비교해서 잘못된 상태 변경을 막는다.
         */
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }
        if (!session.loginId().equals(selected.assignee())) {
            view.showWarning("자신에게 배정된 이슈만 수정 완료 처리할 수 있습니다.");
            return;
        }
        view.showCommentDialog("수정 완료 처리", "수정이 완료되었으며 테스트 검증을 요청합니다.").ifPresent(comment -> {
            backend.markFixed(selected.id(), session.loginId(), comment);
            refreshTable();
        });
    }

    private void resolveIssue() {
        /*
         * 테스터는 FIXED 상태의 이슈를 확인한 뒤 RESOLVED로 바꿀 수 있다.
         * 상태 전이 조건을 Controller에 두어 View는 단순히 버튼과 메시지만 담당한다.
         */
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }
        if (!"FIXED".equals(selected.status())) {
            view.showWarning("FIXED 상태의 이슈만 해결 확인할 수 있습니다.");
            return;
        }
        backend.resolveIssue(selected.id(), session.loginId(), "테스터가 수정 내용을 확인함");
        refreshTable();
    }

    private void reopenIssue() {
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }
        if (!"FIXED".equals(selected.status())) {
            view.showWarning("FIXED 상태의 이슈만 재오픈할 수 있습니다.");
            return;
        }
        backend.reopenIssue(selected.id(), session.loginId(), "테스터가 수정 부족으로 재오픈함");
        refreshTable();
    }

    private void closeIssue() {
        /*
         * PL은 RESOLVED 상태의 이슈를 CLOSED로 종료한다.
         * 이슈 상태 흐름이 Controller에 모여 있어 데모 시나리오를 따라가기 쉽다.
         */
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }
        if (!"RESOLVED".equals(selected.status())) {
            view.showWarning("RESOLVED 상태의 이슈만 종료할 수 있습니다.");
            return;
        }
        backend.closeIssue(selected.id(), session.loginId(), "PL이 해결된 이슈를 종료 처리함");
        refreshTable();
    }

    private void addComment() {
        IssueItem selected = requireSelectedIssue();
        if (selected == null) {
            return;
        }
        view.showCommentDialog("코멘트 추가", "").ifPresent(comment -> {
            backend.addComment(selected.id(), session.loginId(), comment);
            refreshTable();
        });
    }

    private void showSelectedIssue() {
        IssueItem selected = requireSelectedIssue();
        if (selected != null) {
            view.showIssueDetail(selected);
        }
    }

    private void recommendAssignee() {
        IssueItem selected = requireSelectedIssue();
        if (selected != null) {
            view.showRecommendations(backend.recommendAssignees(selected));
        }
    }

    private void showStatistics() {
        String daily = backend.dailyIssueCounts().entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));
        view.showStatistics(
                "상태별 요약\n" +
                        "NEW: " + backend.countByStatus("NEW") + "\n" +
                        "ASSIGNED: " + backend.countByStatus("ASSIGNED") + "\n" +
                        "FIXED: " + backend.countByStatus("FIXED") + "\n" +
                        "RESOLVED: " + backend.countByStatus("RESOLVED") + "\n" +
                        "CLOSED: " + backend.countByStatus("CLOSED") + "\n\n" +
                        "일별 발생 횟수\n" + daily
        );
    }

    private IssueItem requireSelectedIssue() {
        /*
         * 이슈 상세, 배정, 코멘트, 상태 변경 기능은 모두 선택된 이슈가 필요하다.
         * 중복 검사를 한 메서드로 모아 경고 메시지와 null 처리를 통일했다.
         */
        IssueItem selected = view.selectedIssue();
        if (selected == null) {
            view.showWarning("먼저 이슈를 선택하세요.");
        }
        return selected;
    }
}
