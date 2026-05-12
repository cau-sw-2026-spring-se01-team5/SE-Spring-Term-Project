package application.auth;

import application.auth.dto.LoginResult;
import application.auth.dto.LoginCommand;

public interface Auth {
    LoginResult login(LoginCommand command);
    void logout();
}