package main.header;

import enums.user.v1.UserRole;
import ui.UiTheme;
import ui.event.UiEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HeaderPanel extends JPanel implements HeaderView {

    private final JLabel userInfoLabel = new JLabel();
    private final JButton backToProjectListButton = new JButton("프로젝트 목록으로");
    private final JButton logoutButton = new JButton("로그아웃");
    private final UiEvent backToProjectListEvent = new UiEvent();
    private final UiEvent logoutEvent = new UiEvent();

    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(UiTheme.CARD_BG);
        setBorder(new EmptyBorder(10, 14, 10, 14));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        left.add(userInfoLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(backToProjectListButton);
        right.add(logoutButton);

        userInfoLabel.setForeground(Color.BLACK);
        UiTheme.styleSecondaryButton(backToProjectListButton);
        UiTheme.styleSecondaryButton(logoutButton);

        backToProjectListButton.addActionListener(e -> backToProjectListEvent.emit());
        logoutButton.addActionListener(e -> logoutEvent.emit());

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }

    @Override
    public void setUserInfo(Integer userId, String loginId, UserRole role) {
        userInfoLabel.setText("User: " + loginId + " (#" + userId + ") / Role: " + role);
    }

    @Override
    public void onLogout(Runnable handler) {
        logoutEvent.subscribe(handler);
    }

    @Override
    public void onBackToProjectList(Runnable handler) {
        backToProjectListEvent.subscribe(handler);
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
