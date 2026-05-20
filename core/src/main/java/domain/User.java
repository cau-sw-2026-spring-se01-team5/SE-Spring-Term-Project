package domain;

import enums.user.v1.UserRole;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @RequiredArgsConstructor
public class User {
    @Setter
    private Integer id;
    @NonNull
    private String loginId;
    @NonNull
    private String password;
    @NonNull
    private UserRole role;
}
