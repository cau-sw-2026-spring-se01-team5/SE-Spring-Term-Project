package interfaces.issue.dto.deleteIssue.v1;

public record DeleteIssueOutput(
        boolean success,
        String message
) {
}