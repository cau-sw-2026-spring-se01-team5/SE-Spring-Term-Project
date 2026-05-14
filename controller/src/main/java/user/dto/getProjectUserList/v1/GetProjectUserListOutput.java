package user.dto.getProjectUserList.v1;

import java.util.List;

public record GetProjectUserListOutput(
        boolean success,
        String message,
        List<UserInfoOutput> userList // 유저 정보
) {
}