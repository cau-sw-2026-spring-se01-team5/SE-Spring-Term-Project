package repository;

import domain.Comment;

import java.util.List;

public interface CommentRepository {
    Integer save(Comment comment) throws Exception;
    List<Comment> byIssueId(Integer issueId) throws Exception;
}
