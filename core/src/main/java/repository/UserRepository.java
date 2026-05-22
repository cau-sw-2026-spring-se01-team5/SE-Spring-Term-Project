package repository;

import domain.User;

import java.util.List;

public interface UserRepository {
    Integer save(User user, Integer projectId) throws Exception;
    User load(Integer id) throws Exception;
    User byLoginId(String loginId) throws Exception;
    List<User> byProjectId(Integer projectId) throws Exception;
    // 여기 수정: 이미 존재하는 사용자를 새 프로젝트의 멤버 목록에 연결하기 위한 메서드이다.
    void addProjectMembership(Integer userId, Integer projectId) throws Exception;
    void delete(Integer id) throws Exception;
}
