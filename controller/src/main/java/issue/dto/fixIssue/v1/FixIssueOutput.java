package issue.dto.fixIssue.v1;

public record FixIssueOutput(
        boolean success,
        Integer issueId,
        String message
) {
}
