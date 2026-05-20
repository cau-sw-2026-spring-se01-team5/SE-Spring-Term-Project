package controller.user.v1;

import enums.user.v1.UserRole;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import repository.UserRepository;
import user.dto.createUser.v1.CreateUserInput;
import user.dto.createUser.v1.CreateUserOutput;
import user.dto.deleteUser.v1.DeleteUserInput;
import user.dto.deleteUser.v1.DeleteUserOutput;
import user.dto.getProjectUserList.v1.GetProjectUserListInput;
import user.dto.getProjectUserList.v1.GetProjectUserListOutput;
import user.v1.User;

@RequiredArgsConstructor
public class UserImpl implements User {
    @NonNull
    private UserRepository repository;

    @Override
    public CreateUserOutput createUser(CreateUserInput input) {
        try {
            domain.User requester = repository.load(input.requesterUserId());
            if (requester.getRole() != UserRole.ADMIN) {
                return new CreateUserOutput(
                        false,
                        null,
                        "ADMIN만 계정을 생성할 수 있다"
                );
            }
        } catch (Exception e) {
            return new CreateUserOutput(
                    false,
                    null,
                    e.getMessage()
            );
        }


        domain.User user = new domain.User(
                input.loginId(),
                input.password(),
                input.role()
        );

        try {
            Integer userId = repository.save(user, input.projectId());

            return new CreateUserOutput(
                    true,
                    userId,
                    null
            );
        } catch (Exception e) {
            String message = e.getMessage();

            return new CreateUserOutput(
                    false,
                    null,
                    message
            );
        }
    }

    @Override
    public GetProjectUserListOutput getProjectUserList(GetProjectUserListInput input) {
        return null;
    }

    @Override
    public DeleteUserOutput deleteUser(DeleteUserInput input) {
        return null;
    }
}
