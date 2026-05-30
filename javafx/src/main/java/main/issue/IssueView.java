package main.issue;

import model.JavaFxData.IssueItem;
import model.JavaFxData.ProjectItem;
import java.util.List;
import java.util.Optional;

/*
 * 이슈 화면이 Controller에 제공하는 View 인터페이스이다.
 *
 * Controller는 JavaFX TableView, ComboBox 같은 구체 위젯을 직접 알지 않는다.
 * 화면 입력과 출력에 필요한 기능만 이 인터페이스로 주고받는다.
 */
public interface IssueView {

    record SearchCondition(
            String keyword,
            String status,
            String priority,
            String assigneeLoginId,
            String reporterLoginId
    ) {
    }

    record CreateIssueForm(ProjectItem project, String title, String description, String priority, String comment) {
    }

    record AssignIssueForm(String assignee, String comment) {
    }

    void setFilterOptions(List<String> assignees, List<String> reporters);

    void setIssues(List<IssueItem> issues);

    SearchCondition searchCondition();

    List<IssueItem> visibleIssues();

    IssueItem selectedIssue();

    Optional<CreateIssueForm> showRegisterIssueDialog(List<ProjectItem> projects);

    Optional<AssignIssueForm> showAssignIssueDialog(List<String> developers, String writer);

    Optional<AssignIssueForm> showAssignIssueDialog(List<String> developers, String writer, String defaultDeveloper);

    Optional<String> showCommentDialog(String title, String defaultComment);

    void showIssueDetail(IssueItem issue);

    void showRecommendations(List<String> candidates);

    Optional<String> showRecommendationSelectDialog(List<String> candidates);

    void showStatistics(String message);

    void showWarning(String message);

    void onSearch(Runnable handler);

    void onRegisterIssue(Runnable handler);

    void onShowIssueDetail(Runnable handler);

    void onAddComment(Runnable handler);

    void onAssignIssue(Runnable handler);

    void onRecommendAssignee(Runnable handler);

    void onDeleteIssue(Runnable handler);

    void onCloseIssue(Runnable handler);

    void onShowStatistics(Runnable handler);

    void onMarkFixed(Runnable handler);

    void onResolveIssue(Runnable handler);

    void onReopenIssue(Runnable handler);
}
