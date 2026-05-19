package issue.dto.resolveIssue.v1;

public record ResolveIssueInput(
        Integer issueId,
        Integer requesterUserId
) {
}
