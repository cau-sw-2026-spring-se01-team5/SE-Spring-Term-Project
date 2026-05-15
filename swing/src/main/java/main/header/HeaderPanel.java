package main.header;

import enums.user.v1.UserRole;
import main.NamedItem;
import project.dto.getProjectList.v1.ProjectInfoOutput;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HeaderPanel extends JPanel implements HeaderView {

    private final JLabel userInfoLabel = new JLabel();
    private final JComboBox<NamedItem<Integer>> projectComboBox = new JComboBox<>();
    //private final JButton refreshProjectButton = new JButton("프로젝트 새로고침");
    private final JButton logoutButton = new JButton("로그아웃");

    private Runnable projectSelectedHandler;

    public HeaderPanel() {
        setLayout(new BorderLayout());

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(userInfoLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(new JLabel("Project"));
        right.add(projectComboBox);
        //right.add(refreshProjectButton);
        right.add(logoutButton);

        projectComboBox.addActionListener(e -> {
            if (projectSelectedHandler != null) {
                projectSelectedHandler.run();
            }
        });

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }

    @Override
    public void setUserInfo(Integer userId, String loginId, UserRole role) {
        userInfoLabel.setText("User: " + loginId + " (#" + userId + ") / Role: " + role);
    }

    @Override
    public void setProjects(List<ProjectInfoOutput> projects) {
        projectComboBox.removeAllItems();

        for (ProjectInfoOutput project : projects) {
            projectComboBox.addItem(new NamedItem<>(project.projectId(), project.title()));
        }
    }

    @Override
    public Integer getSelectedProjectId() {
        NamedItem<Integer> item = (NamedItem<Integer>) projectComboBox.getSelectedItem();
        return item == null ? null : item.value();
    }

//    @Override
//    public void onRefreshProjects(Runnable handler) {
//        refreshProjectButton.addActionListener(e -> handler.run());
//    }

    @Override
    public void onProjectSelected(Runnable handler) {
        this.projectSelectedHandler = handler;
    }

    @Override
    public void onLogout(Runnable handler) {
        logoutButton.addActionListener(e -> handler.run());
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}