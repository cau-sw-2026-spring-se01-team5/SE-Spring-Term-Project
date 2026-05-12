package application.user;

import application.user.dto.CreateUserCommand;
import application.user.dto.CreateUserResult;

public interface User {
    CreateUserResult createUser(CreateUserCommand command);
}