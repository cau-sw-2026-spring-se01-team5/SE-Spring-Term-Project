package issue.dto.closeIssue.v1;

public record CloseIssueOutput(
        boolean success,
        Integer issueId,
        String message
) {
}
