package model;

import enums.user.v1.UserRole;

import java.util.List;

public final class JavaFxData {

    private JavaFxData() {
    }

    public record LoginUser(Integer userId, String loginId, UserRole role) {
    }

    public record ProjectItem(Integer id, String name, String description) {
        @Override
        public String toString() {
            return name;
        }
    }

    public record UserItem(Integer id, String loginId, String password, UserRole role, Integer projectId) {
        @Override
        public String toString() {
            return loginId + " (" + role + ")";
        }
    }

    public record CommentItem(Integer id, String author, String createdAt, String comment) {
        @Override
        public String toString() {
            return "[" + createdAt + "] " + author + ": " + comment;
        }
    }

    public record IssueItem(
            Integer id,
            Integer projectId,
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

    public record RegisterIssueResult(boolean success, Integer issueId, String message) {
    }
}
