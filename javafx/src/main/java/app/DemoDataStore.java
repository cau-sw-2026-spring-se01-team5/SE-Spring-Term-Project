package app;

import enums.user.v1.UserRole;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/*
 * JavaFX 데모 실행을 위한 임시 저장소이다.
 *
 * 실제 백엔드 구현체가 완성되기 전까지 UI 흐름을 검증하기 위해 사용한다.
 * 화면은 이 클래스를 직접 사용하지 않고 DemoJavaFxBackend를 통해 접근한다.
 */
public class DemoDataStore implements Serializable {

    private static final Path DATA_PATH = Path.of("javafx-demo-data.ser");
    private static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static DemoDataStore instance;

    private final List<ProjectRecord> projects = new ArrayList<>();
    private final List<AppUser> users = new ArrayList<>();
    private final List<IssueRecord> issues = new ArrayList<>();

    private int nextProjectId = 1;
    private int nextUserId = 1;
    private int nextIssueId = 1;

    public static DemoDataStore getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static DemoDataStore load() {
        if (Files.exists(DATA_PATH)) {
            try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(DATA_PATH))) {
                Object loaded = input.readObject();
                if (loaded instanceof DemoDataStore store) {
                    return store;
                }
            } catch (IOException | ClassNotFoundException ignored) {
                /* 저장 파일 형식이 바뀌었거나 깨졌으면 새 상태로 시작한다. */
            }
        }

        DemoDataStore store = new DemoDataStore();
        store.seedAdminOnly();
        store.save();
        return store;
    }

    public static void resetLocalData() {
        try {
            Files.deleteIfExists(DATA_PATH);
        } catch (IOException ignored) {
            /* 데모 데이터 삭제 실패가 UI 실행을 막지 않게 한다. */
        }

        instance = new DemoDataStore();
        instance.seedAdminOnly();
        instance.save();
    }

    public void save() {
        try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(DATA_PATH))) {
            output.writeObject(this);
        } catch (IOException ignored) {
            /* 저장 실패가 데모 화면 실행을 막지 않게 한다. */
        }
    }

    private void seedAdminOnly() {
        users.add(new AppUser(nextUserId++, "admin", "admin", UserRole.ADMIN, null));
    }

    public Optional<AppUser> findUser(String loginId, String password) {
        return users.stream()
                .filter(user -> user.loginId().equalsIgnoreCase(loginId))
                .filter(user -> user.password().equals(password))
                .findFirst();
    }

    public List<ProjectRecord> projects() {
        return List.copyOf(projects);
    }

    public List<AppUser> usersForProject(int projectId) {
        return users.stream()
                .filter(user -> user.projectId() != null && user.projectId() == projectId)
                .collect(Collectors.toList());
    }

    public List<AppUser> usersForProjectByRole(int projectId, UserRole role) {
        return users.stream()
                .filter(user -> user.projectId() != null && user.projectId() == projectId)
                .filter(user -> user.role() == role)
                .collect(Collectors.toList());
    }

    public List<ProjectRecord> projectsForUser(String loginId, UserRole role) {
        if (role == UserRole.ADMIN) {
            return projects();
        }

        return users.stream()
                .filter(user -> user.loginId().equalsIgnoreCase(loginId))
                .map(AppUser::projectId)
                .filter(Objects::nonNull)
                .flatMap(projectId -> projects.stream().filter(project -> project.id() == projectId))
                .collect(Collectors.toList());
    }

    public ProjectRecord addProject(String name, String description) {
        ProjectRecord project = new ProjectRecord(nextProjectId++, name, description);
        projects.add(project);
        save();
        return project;
    }

    public void deleteProject(int projectId) {
        projects.removeIf(project -> project.id() == projectId);
        users.removeIf(user -> user.projectId() != null && user.projectId() == projectId);
        issues.removeIf(issue -> issue.projectId() == projectId);
        save();
    }

    public AppUser addUser(String loginId, String password, UserRole role, int projectId) {
        AppUser user = new AppUser(nextUserId++, loginId, password, role, projectId);
        users.add(user);
        save();
        return user;
    }

    public void deleteUser(String loginId) {
        users.removeIf(user -> user.loginId().equalsIgnoreCase(loginId));
        save();
    }

    public boolean hasLoginId(String loginId) {
        return users.stream().anyMatch(user -> user.loginId().equalsIgnoreCase(loginId));
    }

    public IssueRecord registerIssue(int projectId, String title, String description, String reporter, String priority) {
        IssueRecord issue = new IssueRecord(nextIssueId++, projectId, title, description, reporter, now(), priority);
        issue.addComment(reporter, "이슈를 등록함");
        issues.add(issue);
        save();
        return issue;
    }

    public List<IssueRecord> issuesForRole(String loginId, UserRole role) {
        return issues.stream()
                .filter(issue -> switch (role) {
                    case ADMIN -> true;
                    case PL -> isProjectMember(loginId, issue.projectId());
                    case DEV -> loginId.equalsIgnoreCase(issue.assignee());
                    case TESTER -> loginId.equalsIgnoreCase(issue.reporter())
                            || (isProjectMember(loginId, issue.projectId()) && "FIXED".equals(issue.status()));
                })
                .collect(Collectors.toList());
    }

    private boolean isProjectMember(String loginId, int projectId) {
        return users.stream()
                .anyMatch(user -> user.loginId().equalsIgnoreCase(loginId)
                        && user.projectId() != null
                        && user.projectId() == projectId);
    }

    public Optional<IssueRecord> findIssue(int issueId) {
        return issues.stream().filter(issue -> issue.id() == issueId).findFirst();
    }

    public void assignIssue(int issueId, String assignee, String actor, String comment) {
        findIssue(issueId).ifPresent(issue -> {
            issue.assignee(assignee);
            issue.status("ASSIGNED");
            issue.addComment(actor, comment);
            save();
        });
    }

    public void addComment(int issueId, String author, String comment) {
        findIssue(issueId).ifPresent(issue -> {
            issue.addComment(author, comment);
            save();
        });
    }

    public void markFixed(int issueId, String fixer, String comment) {
        findIssue(issueId).ifPresent(issue -> {
            issue.fixer(fixer);
            issue.status("FIXED");
            issue.addComment(fixer, comment);
            save();
        });
    }

    public void resolveIssue(int issueId, String tester, String comment) {
        findIssue(issueId).ifPresent(issue -> {
            issue.status("RESOLVED");
            issue.addComment(tester, comment);
            save();
        });
    }

    public void reopenIssue(int issueId, String tester, String comment) {
        findIssue(issueId).ifPresent(issue -> {
            issue.status("REOPENED");
            issue.addComment(tester, comment);
            save();
        });
    }

    public void closeIssue(int issueId, String pl, String comment) {
        findIssue(issueId).ifPresent(issue -> {
            issue.status("CLOSED");
            issue.addComment(pl, comment);
            save();
        });
    }

    public List<String> recommendAssignees(IssueRecord targetIssue) {
        Map<String, Integer> scores = new HashMap<>();
        List<String> targetWords = keywords(targetIssue.title() + " " + targetIssue.description());

        issues.stream()
                .filter(issue -> issue.projectId() == targetIssue.projectId())
                .filter(issue -> List.of("FIXED", "RESOLVED", "CLOSED").contains(issue.status()))
                .filter(issue -> issue.fixer() != null && !issue.fixer().isBlank())
                .forEach(issue -> {
                    List<String> historyWords = keywords(issue.title() + " " + issue.description());
                    long overlap = targetWords.stream().filter(historyWords::contains).count();
                    int score = (int) overlap + ("CLOSED".equals(issue.status()) ? 2 : 1);
                    scores.merge(issue.fixer(), score, Integer::sum);
                });

        usersForProjectByRole(targetIssue.projectId(), UserRole.DEV)
                .forEach(user -> scores.putIfAbsent(user.loginId(), 0));

        return scores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public int countByStatus(String status) {
        return (int) issues.stream().filter(issue -> issue.status().equals(status)).count();
    }

    public Map<String, Long> dailyIssueCounts() {
        return issues.stream().collect(Collectors.groupingBy(
                issue -> issue.reportedDate().substring(0, 10),
                Collectors.counting()
        ));
    }

    public List<String> developerLoginIds() {
        return users.stream()
                .filter(user -> user.role() == UserRole.DEV)
                .map(AppUser::loginId)
                .collect(Collectors.toList());
    }

    public List<String> developerLoginIdsForProject(int projectId) {
        return usersForProjectByRole(projectId, UserRole.DEV).stream()
                .map(AppUser::loginId)
                .collect(Collectors.toList());
    }

    public List<String> testerLoginIds() {
        return users.stream()
                .filter(user -> user.role() == UserRole.TESTER)
                .map(AppUser::loginId)
                .collect(Collectors.toList());
    }

    private List<String> keywords(String text) {
        return List.of(text.toLowerCase(Locale.ROOT).split("[^a-z0-9가-힣]+")).stream()
                .filter(word -> word.length() > 1)
                .distinct()
                .collect(Collectors.toList());
    }

    private String now() {
        return LocalDateTime.now().format(DISPLAY_TIME);
    }

    public record AppUser(int id, String loginId, String password, UserRole role, Integer projectId) implements Serializable {
        @Override
        public String toString() {
            return loginId + " / " + roleText(role);
        }
    }

    public record ProjectRecord(int id, String name, String description) implements Serializable {
        @Override
        public String toString() {
            return name;
        }
    }

    public static class IssueRecord implements Serializable {
        private final int id;
        private final int projectId;
        private final String title;
        private final String description;
        private final String reporter;
        private final String reportedDate;
        private final String priority;
        private final List<IssueComment> comments = new ArrayList<>();
        private String status = "NEW";
        private String assignee = "-";
        private String fixer = "";

        public IssueRecord(int id, int projectId, String title, String description,
                           String reporter, String reportedDate, String priority) {
            this.id = id;
            this.projectId = projectId;
            this.title = Objects.requireNonNull(title);
            this.description = Objects.requireNonNull(description);
            this.reporter = Objects.requireNonNull(reporter);
            this.reportedDate = Objects.requireNonNull(reportedDate);
            this.priority = Objects.requireNonNull(priority);
        }

        public int id() {
            return id;
        }

        public int projectId() {
            return projectId;
        }

        public String title() {
            return title;
        }

        public String description() {
            return description;
        }

        public String reporter() {
            return reporter;
        }

        public String reportedDate() {
            return reportedDate;
        }

        public String priority() {
            return priority;
        }

        public String status() {
            return status;
        }

        public void status(String status) {
            this.status = status;
        }

        public String assignee() {
            return assignee;
        }

        public void assignee(String assignee) {
            this.assignee = assignee;
        }

        public String fixer() {
            return fixer;
        }

        public void fixer(String fixer) {
            this.fixer = fixer;
        }

        public List<IssueComment> comments() {
            return comments;
        }

        public void addComment(String author, String content) {
            comments.add(new IssueComment(author, LocalDateTime.now().format(DISPLAY_TIME), content));
        }
    }

    public record IssueComment(String author, String createdAt, String content) implements Serializable {
        @Override
        public String toString() {
            return "[" + createdAt + "] " + author + ": " + content;
        }
    }

    private static String roleText(UserRole role) {
        return switch (role) {
            case ADMIN -> "관리자";
            case PL -> "PL";
            case DEV -> "개발자";
            case TESTER -> "테스터";
        };
    }
}
