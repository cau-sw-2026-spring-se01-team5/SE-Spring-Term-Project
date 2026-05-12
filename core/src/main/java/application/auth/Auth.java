package application.auth;

import application.auto.dto.LoginResult;
import application.auto.dto.LoginCommand;

public interface Auth {
    LoginResult login(LoginCommand command);
    void logout();
}