package controller.user.v1;

import domain.User;
import enums.user.v1.UserRole;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import repository.UserRepository;
import user.v1.RoleResolver;

@RequiredArgsConstructor
public class RoleResolverImpl implements RoleResolver {
    @NonNull
    private UserRepository repository;

    @Override
    public UserRole resolveRole(Integer userId) {
        try {
            return repository.load(userId).getRole();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String resolveLoginId(Integer userId) {
        try {
            return repository.load(userId).getLoginId();
        } catch (Exception e) {
            return null;
        }
    }
}
