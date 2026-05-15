package main.header;

import enums.user.v1.UserRole;
import project.dto.getProjectList.v1.ProjectInfoOutput;

import java.util.List;

public interface HeaderView {

    void setUserInfo(Integer userId, String loginId, UserRole role);

    void setProjects(List<ProjectInfoOutput> projects);

    Integer getSelectedProjectId();

    //void onRefreshProjects(Runnable handler);

    void onProjectSelected(Runnable handler);

    void onLogout(Runnable handler);

    void showMessage(String message);
}