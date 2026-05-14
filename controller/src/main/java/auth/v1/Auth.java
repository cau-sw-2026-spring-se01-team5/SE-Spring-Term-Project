package auth.v1;

import auth.dto.login.v1.LoginOutput;
import auth.dto.login.v1.LoginInput;

public interface Auth {
    LoginOutput login(LoginInput input);
    void logout();
}