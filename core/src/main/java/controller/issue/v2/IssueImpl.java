package controller.issue.v2;

import issue.dto.addIssueComment.v1.AddIssueCommentInput;
import issue.dto.addIssueComment.v1.AddIssueCommentOutput;
import issue.dto.assignIssue.v1.AssignIssueInput;
import issue.dto.assignIssue.v1.AssignIssueOutput;
import issue.dto.closeIssue.v1.CloseIssueInput;
import issue.dto.closeIssue.v1.CloseIssueOutput;
import issue.dto.deleteIssue.v1.DeleteIssueInput;
import issue.dto.deleteIssue.v1.DeleteIssueOutput;
import issue.dto.fixIssue.v1.FixIssueInput;
import issue.dto.fixIssue.v1.FixIssueOutput;
import issue.dto.getIssueDetail.v1.GetIssueDetailInput;
import issue.dto.getIssueDetail.v2.GetIssueDetailOutput;
import issue.dto.getIssueList.v1.GetIssueListInput;
import issue.dto.getIssueList.v1.GetIssueListOutput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeInput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeOutput;
import issue.dto.registerIssue.v1.RegisterIssueInput;
import issue.dto.registerIssue.v1.RegisterIssueOutput;
import issue.dto.resolveIssue.v1.ResolveIssueInput;
import issue.dto.resolveIssue.v1.ResolveIssueOutput;
import issue.v2.Issue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import repository.CommentRepository;
import repository.IssueRepository;
import repository.UserRepository;

@RequiredArgsConstructor
public class IssueImpl implements Issue {
    @NonNull
    private UserRepository userRepository;
    @NonNull
    private IssueRepository issueRepository;
    @NonNull
    private CommentRepository commentRepository;

    @Override
    public RegisterIssueOutput registerIssue(RegisterIssueInput input) {
        return null;
    }

    @Override
    public AssignIssueOutput assignIssue(AssignIssueInput input) {
        return null;
    }

    @Override
    public FixIssueOutput fixIssue(FixIssueInput input) {
        return null;
    }

    @Override
    public ResolveIssueOutput resolveIssueOutput(ResolveIssueInput input) {
        return null;
    }

    @Override
    public CloseIssueOutput closeIssueOutput(CloseIssueInput input) {
        return null;
    }

    @Override
    public AddIssueCommentOutput addIssueComment(AddIssueCommentInput command) {
        return null;
    }

    @Override
    public GetIssueDetailOutput getIssueDetail(GetIssueDetailInput input) {
        return null;
    }

    @Override
    public GetIssueListOutput getIssueList(GetIssueListInput input) {
        return null;
    }

    @Override
    public RecommendAssigneeOutput recommendAssignees(RecommendAssigneeInput input) {
        return null;
    }

    @Override
    public DeleteIssueOutput deleteIssue(DeleteIssueInput input) {
        return null;
    }
}
