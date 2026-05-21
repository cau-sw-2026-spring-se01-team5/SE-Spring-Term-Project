package repository.lucene;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.sqlite.SqliteIssueRepository;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LuceneRecommendationRepositoryTest {
    static final int PROJECT_ID = 1;
    static final int DEV1_ID = 2;
    static final int DEV2_ID = 3;
    static final int NPE_ISSUE_ID = 5;
    static final int DB_ISSUE_ID = 6;

    LuceneRecommendationRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:lucene_test.db");
        repository = new LuceneRecommendationRepository(new SqliteIssueRepository(connection));
    }

    @Test
    void recommendNpeIssue() throws Exception {
        repository.index(PROJECT_ID);
        List<Integer> result = repository.recommend(NPE_ISSUE_ID);

        assertFalse(result.isEmpty());
        assertEquals(DEV1_ID, result.get(0));
    }

    @Test
    void recommendDbIssue() throws Exception {
        repository.index(PROJECT_ID);
        List<Integer> result = repository.recommend(DB_ISSUE_ID);

        assertFalse(result.isEmpty());
        assertEquals(DEV2_ID, result.get(0));
    }

    @Test
    void recommendReturnsEmptyBeforeIndex() throws Exception {
        List<Integer> result = repository.recommend(NPE_ISSUE_ID);
        assertTrue(result.isEmpty());
    }
}
