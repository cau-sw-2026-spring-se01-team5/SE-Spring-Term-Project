package user.dto.getUserInfo.v1;

import enums.user.v1.UserRole;

public record GetUserInfoOutput (
    Integer userId, // 유저 고유 id
    String loginId, // ui로 보여줄 유저 id
    UserRole role, // 유저 권한
    Integer projectId // 프로젝트 고유 id
){}