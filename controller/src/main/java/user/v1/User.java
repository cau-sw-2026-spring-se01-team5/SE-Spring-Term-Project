package user.v1;

import user.dto.createUser.v1.CreateUserInput;
import user.dto.createUser.v1.CreateUserOutput;
import user.dto.deleteUser.v1.DeleteUserInput;
import user.dto.deleteUser.v1.DeleteUserOutput;
import user.dto.getProjectUserList.v1.GetProjectUserListInput;
import user.dto.getProjectUserList.v1.GetProjectUserListOutput;

public interface User {

    CreateUserOutput createUser(CreateUserInput input);

    GetProjectUserListOutput getProjectUserList(GetProjectUserListInput input);

    DeleteUserOutput deleteUser(DeleteUserInput input);
}