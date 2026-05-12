package interfaces.issue.interfaceV1;

import interfaces.issue.dto.addIssueComment.v1.AddIssueCommentInput;
import interfaces.issue.dto.addIssueComment.v1.AddIssueCommentOutput;
import interfaces.issue.dto.assignIssue.v1.AssignIssueInput;
import interfaces.issue.dto.assignIssue.v1.AssignIssueOutput;
import interfaces.issue.dto.changeIssueStatus.v1.ChangeIssueStatusInput;
import interfaces.issue.dto.changeIssueStatus.v1.ChangeIssueStatusOutput;
import interfaces.issue.dto.deleteIssue.v1.DeleteIssueInput;
import interfaces.issue.dto.deleteIssue.v1.DeleteIssueOutput;
import interfaces.issue.dto.recommendAssignee.v1.RecommendAssigneeInput;
import interfaces.issue.dto.recommendAssignee.v1.RecommendAssigneeOutput;
import interfaces.issue.dto.registerIssue.v1.RegisterIssueInput;
import interfaces.issue.dto.registerIssue.v1.RegisterIssueOutput;
import interfaces.issue.dto.getIssueDetail.v1.GetIssueDetailInput;
import interfaces.issue.dto.getIssueDetail.v1.GetIssueDetailOutput;
import interfaces.issue.dto.getIssueList.v1.GetIssueListOutput;
import interfaces.issue.dto.getIssueList.v1.GetIssueListInput;

public interface Issue {

    RegisterIssueOutput registerIssue(RegisterIssueInput input);

    AssignIssueOutput assignIssue(AssignIssueInput input);

    ChangeIssueStatusOutput changeIssueStatus(ChangeIssueStatusInput input);

    AddIssueCommentOutput addIssueComment(AddIssueCommentInput command);

    GetIssueDetailOutput getIssueDetail(GetIssueDetailInput input);

    GetIssueListOutput getIssueList(GetIssueListInput input);

    RecommendAssigneeOutput recommendAssignees(RecommendAssigneeInput input);

    DeleteIssueOutput deleteIssue(DeleteIssueInput input);
}