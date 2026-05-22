package repository.sqlite;

import domain.Comment;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import repository.CommentRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SqliteCommentRepository implements CommentRepository {
    @NonNull
    private Connection connection;

    @Override
    public Integer save(Comment comment) throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO comments(issue_id, author_id, body, created_at) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );

        statement.setInt(1, comment.getIssueId());
        statement.setInt(2, comment.getAuthorId());
        statement.setString(3, comment.getBody());
        statement.setString(4, comment.getCreatedAt().toString());
        statement.executeUpdate();

        ResultSet keys = statement.getGeneratedKeys();
        if (keys.next()) {
            return keys.getInt(1);
        }

        return null;
    }

    @Override
    public List<Comment> byIssueId(Integer issueId) throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT id, author_id, body, created_at FROM comments WHERE issue_id = ? ORDER BY created_at ASC"
        );

        statement.setInt(1, issueId);
        ResultSet rs = statement.executeQuery();

        List<Comment> comments = new ArrayList<>();
        while (rs.next()) {
            Comment comment = new Comment(
                    LocalDateTime.parse(rs.getString("created_at")),
                    rs.getString("body"),
                    rs.getInt("author_id"),
                    issueId
            );
            comment.setId(rs.getInt("id"));
            comments.add(comment);
        }

        return comments;
    }
}
