package user.dto.getUserInfo.v1;

public record GetUserInfoInput(
        Integer userId, // 유저 고유 ID
        Integer projectId // 해당 프로젝트 ID
) {
}
