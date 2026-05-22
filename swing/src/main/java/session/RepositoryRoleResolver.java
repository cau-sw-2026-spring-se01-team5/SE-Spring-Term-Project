package session;

import enums.user.v1.UserRole;
import repository.UserRepository;

public class RepositoryRoleResolver implements RoleResolver {

    private final UserRepository userRepository;

    public RepositoryRoleResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserRole resolveRole(Integer userId) {
        try {
            domain.User user = userRepository.load(userId);
            return user == null ? null : user.getRole();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String resolveLoginId(Integer userId) {
        try {
            domain.User user = userRepository.load(userId);
            return user == null ? null : user.getLoginId();
        } catch (Exception e) {
            return null;
        }
    }
}
