package controller.issue.v1;

import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.addIssueComment.v1.AddIssueCommentInput;
import issue.dto.addIssueComment.v1.AddIssueCommentOutput;
import issue.dto.assignIssue.v1.AssignIssueInput;
import issue.dto.assignIssue.v1.AssignIssueOutput;
import issue.dto.changeIssueStatus.v1.ChangeIssueStatusInput;
import issue.dto.changeIssueStatus.v1.ChangeIssueStatusOutput;
import issue.dto.deleteIssue.v1.DeleteIssueInput;
import issue.dto.deleteIssue.v1.DeleteIssueOutput;
import issue.dto.getIssueDetail.v1.CommentOutput;
import issue.dto.getIssueDetail.v1.GetIssueDetailInput;
import issue.dto.getIssueDetail.v1.GetIssueDetailOutput;
import issue.dto.getIssueList.v1.GetIssueListInput;
import issue.dto.getIssueList.v1.GetIssueListOutput;
import issue.dto.getIssueList.v1.IssueSummaryOutput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeInput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeOutput;
import issue.dto.recommendAssignee.v1.RecommendedAssigneeOutput;
import issue.dto.registerIssue.v1.RegisterIssueInput;
import issue.dto.registerIssue.v1.RegisterIssueOutput;
import issue.v1.Issue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import repository.CommentRepository;
import repository.IssueFilter;
import repository.IssueRepository;
import repository.RecommendationRepository;
import repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class IssueImpl implements Issue {
    @NonNull private UserRepository userRepository;
    @NonNull private IssueRepository issueRepository;
    @NonNull private CommentRepository commentRepository;
    @NonNull private RecommendationRepository recommendationRepository;

    @Override
    public RegisterIssueOutput registerIssue(RegisterIssueInput input) {
        try {
            domain.User reporter = userRepository.load(input.reporterUserId());
            if (reporter.getRole() != UserRole.TESTER) {
                return new RegisterIssueOutput(false, null, "TESTER만 이슈를 등록할 수 있습니다.");
            }
        } catch (Exception e) {
            return new RegisterIssueOutput(false, null, e.getMessage());
        }

        try {
            domain.Issue issue = new domain.Issue(
                    input.projectId(),
                    input.issueTitle(),
                    input.issueDescription(),
                    input.priority() != null ? input.priority() : IssuePriority.MAJOR,
                    IssueStatus.NEW
            );
            issue.setReporterId(input.reporterUserId());
            issue.setReportedDate(LocalDateTime.now());
            Integer issueId = issueRepository.save(issue);
            return new RegisterIssueOutput(true, issueId, "이슈 등록 성공");
        } catch (Exception e) {
            return new RegisterIssueOutput(false, null, e.getMessage());
        }
    }

    @Override
    public AssignIssueOutput assignIssue(AssignIssueInput input) {
        try {
            domain.User requester = userRepository.load(input.requesterUserId());
            if (requester.getRole() != UserRole.PL && requester.getRole() != UserRole.ADMIN) {
                return new AssignIssueOutput(false, input.issueId(), "PL 또는 Admin만 이슈를 배정할 수 있습니다.");
            }
            domain.User assignee = userRepository.load(input.assigneeUserId());
            if (assignee.getRole() != UserRole.DEV) {
                return new AssignIssueOutput(false, input.issueId(), "Assignee는 DEV여야 합니다.");
            }
        } catch (Exception e) {
            return new AssignIssueOutput(false, input.issueId(), e.getMessage());
        }

        try {
            domain.Issue issue = issueRepository.load(input.issueId());
            if (issue.getStatus() != IssueStatus.NEW && issue.getStatus() != IssueStatus.REOPENED) {
                return new AssignIssueOutput(false, input.issueId(), "NEW 또는 REOPENED 상태 이슈만 배정할 수 있습니다.");
            }
            issue.setAssigneeId(input.assigneeUserId());
            issue.setStatus(IssueStatus.ASSIGNED);
            issueRepository.update(issue);
            return new AssignIssueOutput(true, input.issueId(), "이슈 배정 성공");
        } catch (Exception e) {
            return new AssignIssueOutput(false, input.issueId(), e.getMessage());
        }
    }

    @Override
    public ChangeIssueStatusOutput changeIssueStatus(ChangeIssueStatusInput input) {
        if (input.targetStatus() == null) {
            return new ChangeIssueStatusOutput(false, input.issueId(), null, "변경할 상태를 선택하세요.");
        }

        try {
            domain.Issue issue = issueRepository.load(input.issueId());
            boolean isAdmin = userRepository.load(input.requesterUserId()).getRole() == UserRole.ADMIN;
            return switch (input.targetStatus()) {
                case FIXED    -> handleFix(issue, input.requesterUserId(), isAdmin);
                case RESOLVED -> handleResolve(issue, input.requesterUserId(), isAdmin);
                case CLOSED   -> handleClose(issue, input.requesterUserId(), isAdmin);
                case REOPENED -> handleReopen(issue, input.requesterUserId(), isAdmin);
                default -> new ChangeIssueStatusOutput(false, input.issueId(), issue.getStatus(), "지원하지 않는 상태 전환입니다.");
            };
        } catch (Exception e) {
            return new ChangeIssueStatusOutput(false, input.issueId(), null, e.getMessage());
        }
    }

    @Override
    public AddIssueCommentOutput addIssueComment(AddIssueCommentInput input) {
        try {
            issueRepository.load(input.issueId());
        } catch (Exception e) {
            return new AddIssueCommentOutput(false, input.issueId(), null, "이슈가 존재하지 않습니다.");
        }

        try {
            domain.Comment comment = new domain.Comment(
                    LocalDateTime.now(),
                    input.comment() != null ? input.comment() : "(내용 없음)",
                    input.authorUserId(),
                    input.issueId()
            );
            Integer commentId = commentRepository.save(comment);
            return new AddIssueCommentOutput(true, input.issueId(), commentId, "코멘트 추가 성공");
        } catch (Exception e) {
            return new AddIssueCommentOutput(false, input.issueId(), null, e.getMessage());
        }
    }

    @Override
    public GetIssueDetailOutput getIssueDetail(GetIssueDetailInput input) {
        try {
            domain.Issue issue = issueRepository.load(input.issueId());
            List<domain.Comment> comments = commentRepository.byIssueId(input.issueId());

            List<CommentOutput> commentOutputs = comments.stream()
                    .map(c -> new CommentOutput(c.getId(), loginIdOf(c.getAuthorId()), c.getCreatedAt(), c.getBody()))
                    .collect(Collectors.toList());

            return new GetIssueDetailOutput(
                    true, "이슈 상세 조회 성공",
                    issue.getId(),
                    issue.getProjectId(),
                    issue.getTitle(),
                    issue.getDescription(),
                    loginIdOf(issue.getReporterId()),
                    issue.getReportedDate(),
                    loginIdOf(issue.getFixerId()),
                    issue.getAssigneeId(),
                    issue.getPriority(),
                    issue.getStatus(),
                    commentOutputs
            );
        } catch (Exception e) {
            return new GetIssueDetailOutput(
                    false, e.getMessage(),
                    null, null, null, null, null, null,
                    null, null, null, null,
                    List.of()
            );
        }
    }

    @Override
    public GetIssueListOutput getIssueList(GetIssueListInput input) {
        try {
            IssueFilter filter = new IssueFilter(
                    input.projectId(),
                    input.assigneeUserId(),
                    input.reporterUserId(),
                    input.fixerUserId(),
                    input.status(),
                    input.priority(),
                    input.keyword()
            );
            List<IssueSummaryOutput> summaries = issueRepository.search(filter).stream()
                    .map(this::toSummary)
                    .collect(Collectors.toList());
            return new GetIssueListOutput(true, "이슈 조회 성공", summaries);
        } catch (Exception e) {
            return new GetIssueListOutput(false, e.getMessage(), List.of());
        }
    }

    @Override
    public RecommendAssigneeOutput recommendAssignees(RecommendAssigneeInput input) {
        try {
            recommendationRepository.index(input.projectId());
            List<Integer> userIds = recommendationRepository.recommend(input.issueId());
            List<RecommendedAssigneeOutput> candidates = new ArrayList<>();
            for (int i = 0; i < userIds.size(); i++) {
                candidates.add(new RecommendedAssigneeOutput(loginIdOf(userIds.get(i)), i + 1));
            }
            return new RecommendAssigneeOutput(true, "추천 성공", candidates);
        } catch (Exception e) {
            return new RecommendAssigneeOutput(false, e.getMessage(), List.of());
        }
    }

    @Override
    public DeleteIssueOutput deleteIssue(DeleteIssueInput input) {
        try {
            domain.User requester = userRepository.load(input.requesterUserId());
            if (requester.getRole() != UserRole.PL && requester.getRole() != UserRole.ADMIN) {
                return new DeleteIssueOutput(false, "PL 또는 Admin만 이슈를 삭제할 수 있습니다.");
            }
        } catch (Exception e) {
            return new DeleteIssueOutput(false, e.getMessage());
        }

        try {
            issueRepository.delete(input.issueId());
            return new DeleteIssueOutput(true, "이슈 삭제 성공");
        } catch (Exception e) {
            return new DeleteIssueOutput(false, e.getMessage());
        }
    }

    private ChangeIssueStatusOutput handleFix(domain.Issue issue, Integer requesterId, boolean isAdmin) throws Exception {
        if (!isAdmin) {
            domain.User requester = userRepository.load(requesterId);
            if (requester.getRole() != UserRole.DEV) {
                return new ChangeIssueStatusOutput(false, issue.getId(), issue.getStatus(), "DEV만 FIXED 처리할 수 있습니다.");
            }
            issue.setFixerId(requesterId);
        }
        if (issue.getStatus() != IssueStatus.ASSIGNED) {
            return new ChangeIssueStatusOutput(false, issue.getId(), issue.getStatus(), "ASSIGNED 상태에서만 FIXED로 변경할 수 있습니다.");
        }
        issue.setStatus(IssueStatus.FIXED);
        issueRepository.update(issue);
        return new ChangeIssueStatusOutput(true, issue.getId(), IssueStatus.FIXED, "FIXED 처리 성공");
    }

    private ChangeIssueStatusOutput handleResolve(domain.Issue issue, Integer requesterId, boolean isAdmin) throws Exception {
        if (!isAdmin) {
            domain.User requester = userRepository.load(requesterId);
            if (requester.getRole() != UserRole.TESTER) {
                return new ChangeIssueStatusOutput(false, issue.getId(), issue.getStatus(), "TESTER만 RESOLVED 처리할 수 있습니다.");
            }
            if (!Objects.equals(issue.getReporterId(), requesterId)) {
                return new ChangeIssueStatusOutput(false, issue.getId(), issue.getStatus(), "해당 이슈를 등록한 TESTER만 RESOLVED 처리할 수 있습니다.");
            }
        }
        if (issue.getStatus() != IssueStatus.FIXED) {
            return new ChangeIssueStatusOutput(false, issue.getId(), issue.getStatus(), "FIXED 상태에서만 RESOLVED로 변경할 수 있습니다.");
        }
        issue.setStatus(IssueStatus.RESOLVED);
        issueRepository.update(issue);
        return new ChangeIssueStatusOutput(true, issue.getId(), IssueStatus.RESOLVED, "RESOLVED 처리 성공");
    }

    private ChangeIssueStatusOutput handleClose(domain.Issue issue, Integer requesterId, boolean isAdmin) throws Exception {
        if (!isAdmin) {
            domain.User requester = userRepository.load(requesterId);
            if (requester.getRole() != UserRole.PL) {
                return new ChangeIssueStatusOutput(false, issue.getId(), issue.getStatus(), "PL만 CLOSED 처리할 수 있습니다.");
            }
        }
        if (issue.getStatus() != IssueStatus.RESOLVED) {
            return new ChangeIssueStatusOutput(false, issue.getId(), issue.getStatus(), "RESOLVED 상태에서만 CLOSED로 변경할 수 있습니다.");
        }
        issue.setStatus(IssueStatus.CLOSED);
        issueRepository.update(issue);
        return new ChangeIssueStatusOutput(true, issue.getId(), IssueStatus.CLOSED, "CLOSED 처리 성공");
    }

    private ChangeIssueStatusOutput handleReopen(domain.Issue issue, Integer requesterId, boolean isAdmin) {
        if (!isAdmin) {
            return new ChangeIssueStatusOutput(false, issue.getId(), issue.getStatus(), "ADMIN만 REOPENED 처리할 수 있습니다.");
        }
        if (issue.getStatus() != IssueStatus.RESOLVED) {
            return new ChangeIssueStatusOutput(false, issue.getId(), issue.getStatus(), "RESOLVED 상태에서만 REOPENED로 변경할 수 있습니다.");
        }
        try {
            issue.setStatus(IssueStatus.REOPENED);
            issueRepository.update(issue);
        } catch (Exception e) {
            return new ChangeIssueStatusOutput(false, issue.getId(), issue.getStatus(), e.getMessage());
        }
        return new ChangeIssueStatusOutput(true, issue.getId(), IssueStatus.REOPENED, "REOPENED 처리 성공");
    }

    private IssueSummaryOutput toSummary(domain.Issue issue) {
        return new IssueSummaryOutput(
                issue.getId(),
                issue.getProjectId(),
                issue.getTitle(),
                loginIdOf(issue.getReporterId()),
                loginIdOf(issue.getAssigneeId()),
                loginIdOf(issue.getFixerId()),
                issue.getPriority(),
                issue.getStatus(),
                issue.getReportedDate()
        );
    }

    private String loginIdOf(Integer userId) {
        if (userId == null) return null;
        try {
            return userRepository.load(userId).getLoginId();
        } catch (Exception e) {
            return null;
        }
    }
}
