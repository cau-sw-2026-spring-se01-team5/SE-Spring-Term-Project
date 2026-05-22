package main.issue;

import issue.dto.addIssueComment.v1.AddIssueCommentInput;
import issue.dto.assignIssue.v1.AssignIssueInput;
import issue.dto.changeIssueStatus.v1.ChangeIssueStatusInput;
import issue.dto.deleteIssue.v1.DeleteIssueInput;
import issue.dto.getIssueDetail.v1.GetIssueDetailInput;
import issue.dto.getIssueList.v1.GetIssueListInput;
import issue.dto.recommendAssignee.v1.RecommendAssigneeInput;
import issue.dto.registerIssue.v1.RegisterIssueInput;
import issue.v1.Issue;
import enums.user.v1.UserRole;
import main.support.getCurrentProj;
import session.UserSession;
import user.dto.getProjectUserList.v1.GetProjectUserListInput;
import user.v1.User;

import java.util.List;

/* 이슈 관련 화면 이벤트를 받아서 처리하는 controller */
public class IssueController {

    private final IssueView view; // 이슈 UI랑 묶을 인터페이스
    private final Issue issueService; // 백엔드 아슈 api 인터페이스
    private final User userService; // 백엔드 유저 api 인터페이스
    private final UserSession session; // 로그인 정보 관리
    private final getCurrentProj getCurrentProj;

    public IssueController(
            IssueView view,
            Issue issueService,
            User userService,
            UserSession session
    ) {
        this.view = view;
        this.issueService = issueService;
        this.userService = userService;
        this.session = session;
        this.getCurrentProj = new getCurrentProj(session);

        bind(); // 버튼 클릭 이벤트와 연결
    }

    // 현재 접속한 유저 권한에 맞춰서 UI 생성
    public void applyRole() {
        view.applyRole(session.role());
        loadAssignableDev(); // 유저 리스트 갱신
    }

    // UI 버튼과 이벤트 메서드 연결
    private void bind() {
        view.onSearchIssues(this::searchIssues);
        view.onRegisterIssue(this::registerIssue);
        view.onAssignIssue(this::assignIssue);
        view.onChangeIssueStatus(this::changeIssueStatus);
        view.onAddIssueComment(this::addIssueComment);
        view.onShowIssueDetail(this::showSelectedIssueDetail);
        view.onRecommendAssignee(this::recommendAssignee);
        view.onDeleteIssue(this::deleteIssue);
    }

    // 이슈 검색
    public void searchIssues() {
        Integer projectId = requireProjectId();

        // 에러 방지용 -> 프로젝트ID 없으면 아무것도 안띄움
        if (projectId == null) {
            return;
        }

        IssueView.SearchCondition condition; // 검색 조건을 담을 객체
        try {
            condition = view.showSearchDialog(); // 검색 조건 입력 팝업 띄우고 결과를 condition객체에 담음
        } catch (IllegalArgumentException e) {
            view.showMessage(e.getMessage()); // 입력값 잘못되면 그 에러 받아서 팝업으로 띄움
            return;
        }

        // 사용자가 검색창 취소하면 아무것도 안함
        if (condition == null) {
            return;
        }

        // 사용자가 입력한 검색 조건을 DTO로 만들어서 Issue 서비스에 전달함
        var output = issueService.getIssueList(
                new GetIssueListInput(
                        projectId,
                        session.userId(),
                        condition.assigneeUserId(),
                        condition.reporterUserId(),
                        condition.fixerUserId(),
                        condition.status(),
                        condition.priority(),
                        condition.keyword()
                )
        );

        // 조건 검색 실패 시 메세지 띄움
        if (!output.success()) {
            view.showMessage(output.message());
            return;
        }

        Integer selectedIssueId = view.showSearchResultAndSelectIssue(output.issues()); // 검색 결과를 띄우고 사용자 선택 이슈 ID 가져옴

        // 사용자가 이슈 선택했으면 해당 이슈 상세 정보 보여줌
        if (selectedIssueId != null) {
            showIssueDetail(selectedIssueId);
        }
    }

    // 현재 프로젝트의 전체 이슈 목록 불러옴
    public void loadAllIssues() {
        Integer projectId = requireProjectId(); // 현재 프로젝트 ID 가져옴

        // 에러 방지용 -> 프로젝트 ID 없으면 아무것도 안띄움
        if (projectId == null) {
            return;
        }

        //  조건 없이 전체 이슈 조회
        var output = issueService.getIssueList(
                new GetIssueListInput(
                        projectId,
                        session.userId(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );

        // 조회 실패 시 메세지 출력
        if (!output.success()) {
            view.showMessage(output.message());
            return;
        }

        view.setIssues(output.issues()); // 조회된 이슈 목록을 화면에 표시
        loadAssignableDev(); // 담당자 배정 DEV 목록 갱신
    }

    // 이슈 등록 기능
    private void registerIssue() {
        Integer projectId = requireProjectId(); // 프로젝트 id 가져오기

        // 프로젝트 아이디 못 가져오면 아무것도 안띄움
        if (projectId == null) {
            return;
        }

        IssueView.CreateIssueForm form = view.showCreateIssueDialog(); // 이슈 등록 입력창 띄우고 사용자 입력값 받아오기

        // 입력값 없으면 아무것도 안함
        if (form == null) {
            return;
        }

        // 입력값을 DTO로 만들어서 이슈 등록 api 호출
        var output = issueService.registerIssue(
                new RegisterIssueInput(
                        projectId,
                        form.title(),
                        form.description(),
                        form.priority(),
                        session.userId()
                )
        );

        // 백엔드 api 호출 결과 받아옴
        view.showMessage(output.message());

        // 등록 성공 시 전체 이슈 목록 리랜더링
        if (output.success()) {
            loadAllIssues();
        }
    }

    // 이슈에 dev 배정
    private void assignIssue() {
        Integer issueId = requireIssueId(); // 이슈 id 가져오기

        // 이슈 아이디 없으면 아무것도 안함
        if (issueId == null) {
            return;
        }

        Integer assigneeUserId = view.getAssigneeUserIdInput(); // ui 상에서 선택된 배정할 사람 id 가져옴

        // 배정 선택한 사람 아무도 없으면 에러 처리
        if (assigneeUserId == null) {
            view.showMessage("배정할 DEV를 선택하세요.");
            return;
        }

        // dto로 만들어서 백 api 호출
        var output = issueService.assignIssue(
                new AssignIssueInput(
                        issueId,
                        session.userId(),
                        assigneeUserId,
                        null
                )
        );

        // 결과 메세지로 띄우기
        view.showMessage(output.message());

        // 배정 성공시 갱신해서 다시 ui에 띄움
        if (output.success()) {
            loadAllIssues();
            showIssueDetail(issueId);
        }
    }

    // 이슈 상태 변경
    private void changeIssueStatus() {
        Integer issueId = requireIssueId(); // 대상 이슈 id 가져오기

        // 이슈 id 없으면 아무것도 안함
        if (issueId == null) {
            return;
        }

        // dto로 만들어서 백 api 호출
        var output = issueService.changeIssueStatus(
                new ChangeIssueStatusInput(
                        issueId,
                        session.userId(),
                        view.getTargetIssueStatusInput()
                )
        );

        // api 호출 후 결과
        view.showMessage(output.message());

        // 백엔드 api 호출 성공시
        if (output.success()) {
            loadAllIssues(); // 전체 이슈 리스트 갱신
            showIssueDetail(issueId); // 이슈 상세 화면 갱신
        }
    }

    // 댓글 추가 기능
    private void addIssueComment() {
        Integer issueId = requireIssueId();

        if (issueId == null) {
            return;
        }

        var output = issueService.addIssueComment(
                new AddIssueCommentInput(
                        issueId,
                        session.userId(),
                        view.getIssueCommentInput()
                )
        );

        view.showMessage(output.message());

        if (output.success()) {
            showIssueDetail(issueId);
        }
    }

    // 선택한 이슈 상세 정보 보여주는 메서드
    private void showSelectedIssueDetail() {
        Integer issueId = view.getSelectedIssueId(); // ui상에서 사용자가 선택한 이슈 id 가져옴

        if (issueId == null) {
            view.showMessage("이슈를 선택하세요.");
            return;
        }

        showIssueDetail(issueId);
    }

    // 이슈 상세 정보 보여주기
    private void showIssueDetail(Integer issueId) {
        if (issueId == null) {
            return;
        }

        var output = issueService.getIssueDetail(
                new GetIssueDetailInput(issueId)
        );

        view.showIssueDetail(output);
    }

    // 담당자 추천 기능
    private void recommendAssignee() {
        Integer projectId = requireProjectId();
        Integer issueId = requireIssueId();

        if (projectId == null || issueId == null) {
            return;
        }

        var output = issueService.recommendAssignees(
                new RecommendAssigneeInput(
                        issueId,
                        projectId
                )
        );

        view.showRecommendations(output);
    }

    // 이슈 삭제
    private void deleteIssue() {
        Integer issueId = requireIssueId();

        if (issueId == null) {
            return;
        }

        var output = issueService.deleteIssue(
                new DeleteIssueInput(
                        session.userId(),
                        issueId
                )
        );

        view.showMessage(output.message());

        if (output.success()) {
            loadAllIssues();
            showIssueDetail(issueId);
        }
    }

    // 프로젝트ID 가져오는 메서드
    private Integer requireProjectId() {
        return getCurrentProj.requireProjectId(view::showMessage);
    }

    // 이슈ID 가져오는 메서드
    private Integer requireIssueId() {
        Integer issueId = view.getActiveDetailIssueId();

        if (issueId != null) {
            return issueId;
        }

        issueId = view.getSelectedIssueId();

        if (issueId == null) {
            view.showMessage("이슈를 선택하세요.");
            return null;
        }

        return issueId;
    }

    // 배정 가능한 dev 목록과 프로젝트 전체 사용자 목록 가져오는 메서드
    private void loadAssignableDev() {
        Integer projectId = session.selectedProjectId();
        if (projectId == null) {
            return;
        }

        var output = userService.getProjectUserList(new GetProjectUserListInput(projectId));
        if (!output.success()) {
            return;
        }

        // 전체 프로젝트 유저 중 dev만 필터링해서 배정 가능 유저 리스트 만듬
        List<IssueView.AssigneeCandidate> candidates = output.userList()
                .stream()
                .filter(user -> user.role() == UserRole.DEV)
                .map(user -> new IssueView.AssigneeCandidate(user.userId(), user.loginId()))
                .toList();

        // 검색 조건에 사용할 프로젝트 전체 사용자 목록
        List<IssueView.ProjectUserOption> projectUsers = output.userList()
                .stream()
                .map(user -> new IssueView.ProjectUserOption(
                        user.userId(),
                        user.loginId(),
                        user.role()
                ))
                .toList();

        view.setAssigneeCandidates(candidates); // 배정 가능한 dev만 필터링
        view.setProjectUsers(projectUsers); // 프로젝트에 존재하는 모든 유저
    }
}
