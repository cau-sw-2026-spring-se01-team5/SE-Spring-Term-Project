package interfaces.auth.interfaceV1;

import interfaces.auth.dto.login.v1.LoginOutput;
import interfaces.auth.dto.login.v1.LoginInput;

public interface Auth {
    LoginOutput login(LoginInput input);
    void logout();
}