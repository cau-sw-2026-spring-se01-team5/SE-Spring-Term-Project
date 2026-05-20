package repository;

import domain.User;

public interface UserRepository {
    Integer save(User user, Integer projectId) throws Exception;
    User load(Integer id) throws Exception;
}
