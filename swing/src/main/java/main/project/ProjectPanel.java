package main.project;

import javax.swing.*;
import java.awt.*;

public class ProjectPanel extends JPanel implements ProjectView {

    private final JTextField projectTitleField = new JTextField(20);
    private final JButton createProjectButton = new JButton("프로젝트 생성");
    private final JButton updateProjectButton = new JButton("프로젝트 수정");
    private final JButton deleteProjectButton = new JButton("프로젝트 삭제");

    public ProjectPanel() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Title"));
        form.add(projectTitleField);
        form.add(createProjectButton);
        form.add(updateProjectButton);
        form.add(deleteProjectButton);

        add(form, BorderLayout.NORTH);
        add(new JLabel("프로젝트 관리는 Admin 전용 기능입니다."), BorderLayout.CENTER);
    }

    @Override
    public String getProjectTitleInput() {
        return projectTitleField.getText().trim();
    }

    @Override
    public void onCreateProject(Runnable handler) {
        createProjectButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onUpdateProject(Runnable handler) {
        updateProjectButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onDeleteProject(Runnable handler) {
        deleteProjectButton.addActionListener(e -> handler.run());
    }

    @Override
    public void applyAdminPermission(boolean admin) {
        createProjectButton.setVisible(admin);
        updateProjectButton.setVisible(admin);
        deleteProjectButton.setVisible(admin);
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}