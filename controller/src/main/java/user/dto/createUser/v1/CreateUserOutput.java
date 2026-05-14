package user.dto.createUser.v1;

public record CreateUserOutput(
        boolean success, // 생성 성공 여부
        Integer createdUserId, // 생성한 유저의 고유 ID
        String message // ui로 던져줄 메세지 - 실패시 실패 meg등
) {
}