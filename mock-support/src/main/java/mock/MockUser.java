package mock;

import enums.user.v1.UserRole;
import mock.model.MockUserData;
import user.dto.createUser.v1.CreateUserInput;
import user.dto.createUser.v1.CreateUserOutput;
import user.dto.deleteUser.v1.DeleteUserInput;
import user.dto.deleteUser.v1.DeleteUserOutput;
import user.dto.getProjectUserList.v1.GetProjectUserListInput;
import user.dto.getProjectUserList.v1.GetProjectUserListOutput;
import user.dto.getProjectUserList.v1.UserInfoOutput;
import user.dto.getUserInfo.v1.GetUserInfoInput;
import user.dto.getUserInfo.v1.GetUserInfoOutput;
import user.v1.User;

import java.util.List;
import java.util.Objects;

public class MockUser implements User {

    private final MockDatabase database;

    public MockUser(MockDatabase database) {
        this.database = database;
    }

    @Override
    public CreateUserOutput createUser(CreateUserInput input) {
        if (!isAdmin(input.requesterUserId())) {
            return new CreateUserOutput(false, null, "ADMIN만 계정을 생성할 수 있습니다.");
        }

        if (input.loginId() == null || input.loginId().isBlank()) {
            return new CreateUserOutput(false, null, "로그인 ID는 비어 있을 수 없습니다.");
        }

        if (input.password() == null || input.password().isBlank()) {
            return new CreateUserOutput(false, null, "비밀번호는 비어 있을 수 없습니다.");
        }

        if (input.role() == null) {
            return new CreateUserOutput(false, null, "권한을 선택해야 합니다.");
        }

        int userId = database.nextUserId();

        database.users().put(
                userId,
                new MockUserData(
                        userId,
                        input.loginId(),
                        input.password(),
                        input.role(),
                        input.projectId()
                )
        );

        return new CreateUserOutput(true, userId, "계정 생성 성공");
    }

    @Override
    public GetProjectUserListOutput getProjectUserList(GetProjectUserListInput input) {
        List<UserInfoOutput> result = database.users()
                .values()
                .stream()
                .filter(user -> Objects.equals(user.projectId(), input.projectId()))
                .map(user -> new UserInfoOutput(
                        user.userId(),
                        user.loginId(),
                        user.role(),
                        user.projectId()
                ))
                .toList();

        return new GetProjectUserListOutput(true, "유저 목록 조회 성공", result);
    }

    @Override
    public DeleteUserOutput deleteUser(DeleteUserInput input) {
        if (!isAdmin(input.requesterUserId())) {
            return new DeleteUserOutput(false, "ADMIN만 계정을 삭제할 수 있습니다.");
        }

        if (!database.users().containsKey(input.targetUserId())) {
            return new DeleteUserOutput(false, "삭제할 유저가 존재하지 않습니다.");
        }

        database.users().remove(input.targetUserId());

        return new DeleteUserOutput(true, "계정 삭제 성공");
    }

    @Override
    public GetUserInfoOutput getUserInfo(GetUserInfoInput input) {
        MockUserData user = database.users().get(input.userId());
        if (user == null) {
            return new GetUserInfoOutput(
                    false,
                    null,
                    null,
                    null,
                    input.projectId(),
                    "유저를 찾을 수 없습니다."
            );
        }

        return new GetUserInfoOutput(
                true,
                user.userId(),
                user.loginId(),
                user.role(),
                user.projectId(),
                "유저 정보 조회 성공"
        );
    }

    private boolean isAdmin(Integer userId) {
        return database.users().containsKey(userId)
                && database.users().get(userId).role() == UserRole.ADMIN;
    }
}
