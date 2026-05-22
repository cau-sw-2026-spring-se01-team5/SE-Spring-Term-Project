package repository.sqlite;

import domain.Comment;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import repository.CommentRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class SqliteCommentRepositoryTest {

    @Test
    void save() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:test.db");
        CommentRepository repository = new SqliteCommentRepository(connection);

        Comment comment = new Comment(LocalDateTime.now(), "NPE 재현 확인했습니다", 1, 1);
        Integer id = repository.save(comment);
        assertNotNull(id);
        System.out.println("저장된 commentId: " + id);
    }

    @Test
    void byIssueId() throws Exception {
        Connection connection = DriverManager.getConnection("jdbc:sqlite:test.db");
        CommentRepository repository = new SqliteCommentRepository(connection);

        List<Comment> comments = repository.byIssueId(1);
        assertFalse(comments.isEmpty());
        for (Comment comment : comments) {
            System.out.println("[" + comment.getCreatedAt() + "] " + comment.getBody());
        }
    }
}
