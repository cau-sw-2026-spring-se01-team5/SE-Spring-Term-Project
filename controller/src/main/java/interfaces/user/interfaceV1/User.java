package interfaces.user.interfaceV1;

import interfaces.user.dto.createUser.v1.CreateUserInput;
import interfaces.user.dto.createUser.v1.CreateUserOutput;
import interfaces.user.dto.deleteUser.v1.DeleteUserInput;
import interfaces.user.dto.deleteUser.v1.DeleteUserOutput;
import interfaces.user.dto.getProjectUserList.v1.GetProjectUserListInput;
import interfaces.user.dto.getProjectUserList.v1.GetProjectUserListOutput;

public interface User {

    CreateUserOutput createUser(CreateUserInput input);

    GetProjectUserListOutput getProjectUserList(GetProjectUserListInput input);

    DeleteUserOutput deleteUser(DeleteUserInput input);
}