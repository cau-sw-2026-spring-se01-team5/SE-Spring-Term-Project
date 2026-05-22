package issue.v2;

import issue.dto.addIssueComment.v1.AddIssueCommentInput;
import issue.dto.addIssueComment.v1.AddIssueCommentOutput;
import issue.dto.assignIssue.v1.AssignIssueInput;
import issue.dto.assignIssue.v1.AssignIssueOutput;
import issue.dto.closeIssue.v1.CloseIssueInput;
import issue.dto.closeIssue.v1.CloseIssueOutput;
import issue.dto.deleteIssue.v1.DeleteIssueInput;
import issue.dto.deleteIssue.v1.DeleteIssueOutput;
import issue.dto.fixIssue.v1.FixIssueOutput;
import issue.dto.fixIssue.v1.FixIssueInput;
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

public interface Issue {
    RegisterIssueOutput registerIssue(RegisterIssueInput input);

    AssignIssueOutput assignIssue(AssignIssueInput input);

    FixIssueOutput fixIssue(FixIssueInput input);

    ResolveIssueOutput resolveIssueOutput(ResolveIssueInput input);

    CloseIssueOutput closeIssueOutput(CloseIssueInput input);

    AddIssueCommentOutput addIssueComment(AddIssueCommentInput command);

    GetIssueDetailOutput getIssueDetail(GetIssueDetailInput input);

    GetIssueListOutput getIssueList(GetIssueListInput input); // 검색 조건이 비어 있으면 전체 조회, 조건이 있으면 필터 조회

    RecommendAssigneeOutput recommendAssignees(RecommendAssigneeInput input);

    DeleteIssueOutput deleteIssue(DeleteIssueInput input);
}
