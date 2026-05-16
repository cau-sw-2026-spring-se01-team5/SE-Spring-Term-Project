package main.header;

import enums.user.v1.UserRole;
public interface HeaderView {

    void setUserInfo(Integer userId, String loginId, UserRole role);

    void onBackToProjectList(Runnable handler);

    void onLogout(Runnable handler);

    void showMessage(String message);
}
