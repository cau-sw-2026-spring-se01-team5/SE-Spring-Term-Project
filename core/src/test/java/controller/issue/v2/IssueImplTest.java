package controller.issue.v2;

import domain.Issue;
import domain.User;
import enums.issue.v1.IssuePriority;
import enums.issue.v1.IssueStatus;
import enums.user.v1.UserRole;
import issue.dto.assignIssue.v1.AssignIssueInput;
import issue.dto.assignIssue.v1.AssignIssueOutput;
import issue.dto.fixIssue.v1.FixIssueInput;
import issue.dto.fixIssue.v1.FixIssueOutput;
import issue.dto.registerIssue.v1.RegisterIssueInput;
import issue.dto.registerIssue.v1.RegisterIssueOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.CommentRepository;
import repository.IssueRepository;
import repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IssueImplTest {
    IssueImpl issueImpl;
    User tester, nonTester, pl, dev;
    Issue existingIssue;

    @BeforeEach
    void setUp() throws Exception {
        tester = new User("tester", "1234", UserRole.TESTER);
        tester.setId(1);
        nonTester = new User("dev1", "1234", UserRole.DEV);
        nonTester.setId(2);
        pl = new User("pl1", "1234", UserRole.PL);
        pl.setId(3);
        dev = new User("dev2", "1234", UserRole.DEV);
        dev.setId(4);

        existingIssue = new Issue(1, "로그인 시 NPE", "재현 확인", IssuePriority.MAJOR, IssueStatus.NEW);
        existingIssue.setId(10);
        existingIssue.setReporterId(tester.getId());
        existingIssue.setReportedDate(LocalDateTime.now());

        UserRepository userRepository = mock(UserRepository.class);
        IssueRepository issueRepository = mock(IssueRepository.class);
        CommentRepository commentRepository = mock(CommentRepository.class);

        when(userRepository.load(tester.getId())).thenReturn(tester);
        when(userRepository.load(nonTester.getId())).thenReturn(nonTester);
        when(userRepository.load(pl.getId())).thenReturn(pl);
        when(userRepository.load(dev.getId())).thenReturn(dev);
        when(issueRepository.save(any())).thenReturn(existingIssue.getId());
        when(issueRepository.load(existingIssue.getId())).thenReturn(existingIssue);

        issueImpl = new IssueImpl(userRepository, issueRepository, commentRepository);
    }

    @Test
    void registerIssue() {
        RegisterIssueOutput output = issueImpl.registerIssue(
                new RegisterIssueInput(1, "로그인 시 NPE", "재현 확인", IssuePriority.MAJOR, tester.getId())
        );

        assertEquals(true, output.success());
        assertEquals(existingIssue.getId(), output.issueId());
    }

    @Test
    void registerIssueByNonTester() {
        RegisterIssueOutput output = issueImpl.registerIssue(
                new RegisterIssueInput(1, "로그인 시 NPE", "재현 확인", IssuePriority.MAJOR, nonTester.getId())
        );

        assertEquals(false, output.success());
    }

    @Test
    void assignIssue() {
        AssignIssueOutput output = issueImpl.assignIssue(
                new AssignIssueInput(existingIssue.getId(), pl.getId(), dev.getId(), "담당자 배정합니다")
        );

        assertEquals(true, output.success());
    }

    @Test
    void fixIssue() {
        existingIssue.setStatus(IssueStatus.ASSIGNED);
        existingIssue.setAssigneeId(dev.getId());

        FixIssueOutput output = issueImpl.fixIssue(
                new FixIssueInput(existingIssue.getId(), dev.getId())
        );

        assertEquals(true, output.success());
    }
}
