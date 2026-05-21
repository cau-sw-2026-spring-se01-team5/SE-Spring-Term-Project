package repository;

import domain.Issue;

import java.util.List;

public interface IssueRepository {
    Integer save(Issue issue) throws Exception;
    Issue load(Integer id) throws Exception;
    void delete(Integer id) throws Exception;
    void update(Issue issue) throws Exception;
    List<Issue> search(IssueFilter filter) throws Exception;
}
