package app;

import issue.dto.getIssueDetail.v1.GetIssueDetailOutput;
import issue.dto.getIssueList.v1.IssueSummaryOutput;
import model.JavaFxData.CommentItem;
import model.JavaFxData.IssueItem;
import model.JavaFxData.LoginUser;
import model.JavaFxData.ProjectItem;
import model.JavaFxData.RegisterIssueResult;
import model.JavaFxData.UserItem;
import project.dto.getProjectList.v1.ProjectInfoOutput;
import user.dto.getProjectUserList.v1.UserInfoOutput;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// controller 계층 DTO를 JavaFX 표시 모델로 변환한다.
public final class JavaFxMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private JavaFxMapper() {
    }

    public static LoginUser loginUser(Integer userId, String loginId, enums.user.v1.UserRole role) {
        return new LoginUser(userId, loginId, role);
    }

    public static ProjectItem projectItem(ProjectInfoOutput output) {
        return new ProjectItem(output.projectId(), output.title(), "");
    }

    public static UserItem userItem(UserInfoOutput output) {
        return new UserItem(output.userId(), output.loginId(), "", output.role(), output.projectId());
    }

    public static IssueItem issueItem(IssueSummaryOutput output) {
        return new IssueItem(
                output.issueId(),
                output.projectId(),
                output.issueTitle(),
                "",
                output.reporterUserId(),
                format(output.reportedDate()),
                output.priority().name(),
                output.status().name(),
                output.assigneeUserId(),
                output.fixerUserId(),
                List.of()
        );
    }

    public static IssueItem issueItem(GetIssueDetailOutput output, String assigneeLoginId) {
        return new IssueItem(
                output.issueId(),
                output.projectId(),
                output.issueTitle(),
                output.issueDescription(),
                output.reporterUserId(),
                format(output.reportedDate()),
                output.priority().name(),
                output.status().name(),
                assigneeLoginId == null ? "" : assigneeLoginId,
                output.fixerUserId() == null ? "" : output.fixerUserId(),
                output.comments().stream()
                        .map(comment -> new CommentItem(
                                comment.commentId(),
                                comment.authorUserId(),
                                format(comment.createdAt()),
                                comment.comment()
                        ))
                        .toList()
        );
    }

    public static RegisterIssueResult registerIssueResult(boolean success, Integer issueId, String message) {
        return new RegisterIssueResult(success, issueId, message);
    }

    public static String format(LocalDateTime value) {
        return value == null ? "" : value.format(DATE_TIME_FORMATTER);
    }
}
