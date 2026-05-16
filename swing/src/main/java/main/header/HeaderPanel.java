package main.header;

import enums.user.v1.UserRole;
import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel implements HeaderView {

    private final JLabel userInfoLabel = new JLabel();
    private final JButton backToProjectListButton = new JButton("프로젝트 목록으로");
    private final JButton logoutButton = new JButton("로그아웃");

    public HeaderPanel() {
        setLayout(new BorderLayout());

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(userInfoLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(backToProjectListButton);
        right.add(logoutButton);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }

    @Override
    public void setUserInfo(Integer userId, String loginId, UserRole role) {
        userInfoLabel.setText("User: " + loginId + " (#" + userId + ") / Role: " + role);
    }

    @Override
    public void onLogout(Runnable handler) {
        logoutButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onBackToProjectList(Runnable handler) {
        backToProjectListButton.addActionListener(e -> handler.run());
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
