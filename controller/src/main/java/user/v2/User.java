package user.v2;

import user.dto.createUser.v1.CreateUserInput;
import user.dto.createUser.v1.CreateUserOutput;
import user.dto.deleteUser.v1.DeleteUserInput;
import user.dto.deleteUser.v1.DeleteUserOutput;
import user.dto.getProjectUserList.v1.GetProjectUserListInput;
import user.dto.getProjectUserList.v1.GetProjectUserListOutput;
import user.dto.getUserInfo.v1.GetUserInfoInput;
import user.dto.getUserInfo.v1.GetUserInfoOutput;

public interface User {

    CreateUserOutput createUser(CreateUserInput input);

    GetProjectUserListOutput getProjectUserList(GetProjectUserListInput input);

    DeleteUserOutput deleteUser(DeleteUserInput input);

    GetUserInfoOutput getUserInfo(GetUserInfoInput input);
}
