package projectselect;

import project.dto.getProjectList.v1.ProjectInfoOutput;

import java.util.List;

public interface ProjectSelectView {

    void setProjects(List<ProjectInfoOutput> projects);

    Integer getSelectedProjectId();

    String getNewProjectTitleInput();

    String getUpdateProjectTitleInput();

    void onLoadProjects(Runnable handler);

    void onCreateProject(Runnable handler);

    void onUpdateProject(Runnable handler);

    void onDeleteProject(Runnable handler);

    void onEnterProject(Runnable handler);

    void onLogout(Runnable handler);

    void applyAdminPermission(boolean admin);

    void showMessage(String message);
}
