package main.project;

public interface ProjectView {

    String getProjectTitleInput();

    void onCreateProject(Runnable handler);

    void onUpdateProject(Runnable handler);

    void onDeleteProject(Runnable handler);

    void applyAdminPermission(boolean admin);

    void showMessage(String message);
}