package repository;

import domain.User;

import java.util.List;

public interface UserRepository {
    Integer save(User user, Integer projectId) throws Exception;
    User load(Integer id) throws Exception;
    List<User> byProjectId(Integer projectId) throws Exception;
    void delete(Integer id) throws Exception;
}
