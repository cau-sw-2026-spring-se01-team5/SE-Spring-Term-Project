package issue.dto.closeIssue.v1;

public record CloseIssueInput(
        Integer issueId,
        Integer requesterUserId
) {
}
