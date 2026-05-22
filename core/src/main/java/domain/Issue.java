package domain;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@RequiredArgsConstructor
public class Issue {
    private Integer id;
    @NonNull
    private Integer projectId;
    @NonNull
    private String title;
    @NonNull
    private String description;
    @NonNull
    private IssuePriority priority;
    @NonNull
    private IssueStatus status;
    private Integer reporterId;
    private Integer assigneeId;
    private Integer fixerId;
    private LocalDateTime reportedDate;
}
