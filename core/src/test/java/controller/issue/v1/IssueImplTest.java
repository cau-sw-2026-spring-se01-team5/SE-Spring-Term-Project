package controller.issue.v1;

import domain.Issue;
import domain.User;
import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.assignIssue.v1.AssignIssueInput;
import issue.dto.assignIssue.v1.AssignIssueOutput;
import issue.dto.changeIssueStatus.v1.ChangeIssueStatusInput;
import issue.dto.changeIssueStatus.v1.ChangeIssueStatusOutput;
import issue.dto.deleteIssue.v1.DeleteIssueInput;
import issue.dto.deleteIssue.v1.DeleteIssueOutput;
import issue.dto.registerIssue.v1.RegisterIssueInput;
import issue.dto.registerIssue.v1.RegisterIssueOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.CommentRepository;
import repository.IssueRepository;
import repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IssueImplTest {
    IssueImpl issueImpl;

    User tester, dev, pl;
    Issue issue;

    @BeforeEach
    void setUp() throws Exception {
        tester = new User("tester", "1234", UserRole.TESTER);
        tester.setId(1);
        dev = new User("dev", "1234", UserRole.DEV);
        dev.setId(2);
        pl = new User("pl", "1234", UserRole.PL);
        pl.setId(3);

        issue = new Issue(1, "버그 발생", "로그인 시 NPE", IssuePriority.MAJOR, IssueStatus.NEW);
        issue.setId(10);
        issue.setReporterId(tester.getId());

        UserRepository userRepository = mock(UserRepository.class);
        IssueRepository issueRepository = mock(IssueRepository.class);
        CommentRepository commentRepository = mock(CommentRepository.class);

        when(userRepository.load(tester.getId())).thenReturn(tester);
        when(userRepository.load(dev.getId())).thenReturn(dev);
        when(userRepository.load(pl.getId())).thenReturn(pl);
        when(issueRepository.save(any())).thenReturn(issue.getId());
        when(issueRepository.load(issue.getId())).thenReturn(issue);

        issueImpl = new IssueImpl(userRepository, issueRepository, commentRepository);
    }

    @Test
    void registerIssue() {
        RegisterIssueOutput output = issueImpl.registerIssue(
                new RegisterIssueInput(1, "버그 발생", "로그인 시 NPE", IssuePriority.MAJOR, tester.getId())
        );

        assertEquals(true, output.success());
        assertEquals(issue.getId(), output.issueId());
    }

    @Test
    void registerIssueByNonTester() {
        RegisterIssueOutput output = issueImpl.registerIssue(
                new RegisterIssueInput(1, "버그 발생", "로그인 시 NPE", IssuePriority.MAJOR, dev.getId())
        );

        assertEquals(false, output.success());
    }

    @Test
    void assignIssue() {
        AssignIssueOutput output = issueImpl.assignIssue(
                new AssignIssueInput(issue.getId(), pl.getId(), dev.getId(), null)
        );

        assertEquals(true, output.success());
    }

    @Test
    void assignIssueByNonPl() {
        AssignIssueOutput output = issueImpl.assignIssue(
                new AssignIssueInput(issue.getId(), dev.getId(), dev.getId(), null)
        );

        assertEquals(false, output.success());
    }

    @Test
    void fixIssue() {
        issue.setStatus(IssueStatus.ASSIGNED);

        ChangeIssueStatusOutput output = issueImpl.changeIssueStatus(
                new ChangeIssueStatusInput(issue.getId(), dev.getId(), IssueStatus.FIXED)
        );

        assertEquals(true, output.success());
    }

    @Test
    void fixIssueByNonDev() {
        issue.setStatus(IssueStatus.ASSIGNED);

        ChangeIssueStatusOutput output = issueImpl.changeIssueStatus(
                new ChangeIssueStatusInput(issue.getId(), tester.getId(), IssueStatus.FIXED)
        );

        assertEquals(false, output.success());
    }

    @Test
    void resolveIssue() {
        issue.setStatus(IssueStatus.FIXED);

        ChangeIssueStatusOutput output = issueImpl.changeIssueStatus(
                new ChangeIssueStatusInput(issue.getId(), tester.getId(), IssueStatus.RESOLVED)
        );

        assertEquals(true, output.success());
    }

    @Test
    void closeIssue() {
        issue.setStatus(IssueStatus.RESOLVED);

        ChangeIssueStatusOutput output = issueImpl.changeIssueStatus(
                new ChangeIssueStatusInput(issue.getId(), pl.getId(), IssueStatus.CLOSED)
        );

        assertEquals(true, output.success());
    }

    @Test
    void deleteIssueByPl() {
        DeleteIssueOutput output = issueImpl.deleteIssue(
                new DeleteIssueInput(pl.getId(), issue.getId())
        );

        assertEquals(true, output.success());
    }

    @Test
    void deleteIssueByNonPl() {
        DeleteIssueOutput output = issueImpl.deleteIssue(
                new DeleteIssueInput(dev.getId(), issue.getId())
        );

        assertEquals(false, output.success());
    }
}
