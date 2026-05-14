package user.dto.deleteUser.v1;

public record DeleteUserInput(
        Integer requesterUserId, // 삭제 요청한 user(권한 확인용 - admin만 가능)
        Integer targetUserId, // 대상 유저 고유 id
        Integer projectId // 대상 프로젝트 고유 id
) {
}