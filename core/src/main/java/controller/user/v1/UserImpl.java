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
import user.dto.getProjectUserList.v1.UserInfoOutput;
import user.v1.User;

import java.util.ArrayList;
import java.util.List;

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
                        "ADMIN만 계정을 생성할 수 있습니다."
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
                    "계정 생성 성공"
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
        try {
            List<domain.User> users = repository.byProjectId(input.projectId());
            List<UserInfoOutput> userInfoOutputs = new ArrayList<>();
            for (domain.User user : users) {
                userInfoOutputs.add(new UserInfoOutput(
                        user.getId(),
                        user.getLoginId(),
                        user.getRole(),
                        input.projectId()
                ));
            }
            return new GetProjectUserListOutput(true, "유저 목록 조회 성공", userInfoOutputs);
        } catch (Exception e) {
            return new GetProjectUserListOutput(false, e.getMessage(), null);
        }
    }

    @Override
    public DeleteUserOutput deleteUser(DeleteUserInput input) {
        try {
            domain.User requester = repository.load(input.requesterUserId());
            if (requester.getRole() != UserRole.ADMIN) {
                return new DeleteUserOutput(
                        false,
                        "ADMIN만 계정을 삭제할 수 있습니다."
                );
            }
        } catch (Exception e) {
            return new DeleteUserOutput(
                    false,
                    e.getMessage()
            );
        }

        try {
            repository.delete(input.targetUserId());

            return new DeleteUserOutput(true, "계정 삭제 성공");
        } catch (Exception e) {
            return new DeleteUserOutput(false, e.getMessage());
        }
    }
}
