package auth.dto.login.v1;

public record LoginInput(
        String loginId, // 계정 아이디
        String password // 계정 비밀번호
) {
}