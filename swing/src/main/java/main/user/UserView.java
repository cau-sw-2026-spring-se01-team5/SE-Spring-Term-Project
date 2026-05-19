package main.user;

import enums.user.v1.UserRole;
import user.dto.getProjectUserList.v1.UserInfoOutput;

import java.util.List;

public interface UserView {

    void setUsers(List<UserInfoOutput> users);

    String getNewLoginIdInput();

    String getNewPasswordInput();

    UserRole getNewUserRoleInput();

    Integer getSelectedTargetUserId();

    void onRefreshUsers(Runnable handler);

    void onCreateUser(Runnable handler);

    void onDeleteUser(Runnable handler);

    void applyAdminPermission(boolean admin);

    void showMessage(String message);
}