package domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @RequiredArgsConstructor
public class User {
    @NonNull
    private Integer id;
    @NonNull
    private String loginId;
    @NonNull
    private String password;
    @NonNull
    private Role role;
}
