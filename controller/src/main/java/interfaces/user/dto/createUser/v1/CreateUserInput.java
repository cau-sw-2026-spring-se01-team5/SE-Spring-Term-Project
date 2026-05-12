package interfaces.user.dto.createUser.v1;

import enums.user.v1.UserRole;

public record CreateUserInput(
        Integer requesterUserId, // 유저 생성 요청한 유저(admin만)
        String loginId, // 생성할 유저의 id
        String password, // 생성할 유저의 pw
        UserRole role, // 생성할 유저의 권한
        Integer projectId // 어느 프로젝트에서 생성할 유저인지
) {
}