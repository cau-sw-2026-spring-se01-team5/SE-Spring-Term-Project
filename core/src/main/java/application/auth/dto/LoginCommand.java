package application.auth.dto;

public record LoginCommand(
        String loginId, // 계정 아이디
        String password // 계정 비밀번호
) {
}