package repository.lucene;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import repository.sqlite.SqliteIssueRepository;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class LuceneRecommendationRepositoryTest {
    static final int PROJECT_ID = 1;
    static final int DEV1_ID = 2; // NPE specialist
    static final int DEV2_ID = 3; // DB specialist
    static final int DEV3_ID = 5; // UI specialist
    static final int DEV4_ID = 6; // Performance specialist
    static final int DEV5_ID = 7; // Security specialist

    // New issue IDs (no fixer): past fixed 17개 이후 18~22
    static final int NPE_ISSUE_ID  = 18;
    static final int DB_ISSUE_ID   = 19;
    static final int UI_ISSUE_ID   = 20;
    static final int PERF_ISSUE_ID = 21;
    static final int SEC_ISSUE_ID  = 22;

    LuceneRecommendationRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        String sql = Files.readString(Paths.get("src/test/java/repository/lucene/test_data.sql"));
        try (Statement stmt = connection.createStatement()) {
            for (String s : sql.split(";")) {
                String trimmed = s.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
        repository = new LuceneRecommendationRepository(new SqliteIssueRepository(connection));
        repository.index(PROJECT_ID);
    }

    @Test
    void recommendNpeIssue() throws Exception {
        List<Integer> result = repository.recommend(NPE_ISSUE_ID);
        assertFalse(result.isEmpty());
        assertEquals(DEV1_ID, result.get(0));
    }

    @Test
    void recommendDbIssue() throws Exception {
        List<Integer> result = repository.recommend(DB_ISSUE_ID);
        assertFalse(result.isEmpty());
        assertEquals(DEV2_ID, result.get(0));
    }

    @Test
    void recommendUiIssue() throws Exception {
        List<Integer> result = repository.recommend(UI_ISSUE_ID);
        assertFalse(result.isEmpty());
        assertEquals(DEV3_ID, result.get(0));
    }

    @Test
    void recommendPerformanceIssue() throws Exception {
        List<Integer> result = repository.recommend(PERF_ISSUE_ID);
        assertFalse(result.isEmpty());
        assertEquals(DEV4_ID, result.get(0));
    }

    @Test
    void recommendSecurityIssue() throws Exception {
        List<Integer> result = repository.recommend(SEC_ISSUE_ID);
        assertFalse(result.isEmpty());
        assertEquals(DEV5_ID, result.get(0));
    }
}
