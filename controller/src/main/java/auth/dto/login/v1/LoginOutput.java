package auth.dto.login.v1;

public record LoginOutput(
        boolean success, // 로그인 성공 여부
        Integer userId, // 로그인 계정
        String message // 실패시 실패 이유
) {
}