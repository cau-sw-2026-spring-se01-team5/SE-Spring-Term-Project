package issue.dto.resolveIssue.v1;

public record ResolveIssueOutput(
        boolean success,
        Integer issueId,
        String message
) {
}
