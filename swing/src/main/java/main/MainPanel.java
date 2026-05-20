package main;

import main.header.HeaderPanel;
import main.issue.IssuePanel;
import main.user.UserPanel;
import ui.UiTheme;

import javax.swing.*;
import java.awt.*;

// main화면에서 필요한 개별 패널들을 모두 조립하는 역할
public class MainPanel extends JPanel {

    private final HeaderPanel headerPanel = new HeaderPanel();
    private final IssuePanel issuePanel = new IssuePanel();
    private final UserPanel userPanel = new UserPanel();

    // 이슈/유저 탭 생성 부분
    private final JTabbedPane tabbedPane = new JTabbedPane();

    public MainPanel() {
        setLayout(new BorderLayout());
        setBackground(UiTheme.BG);

        add(headerPanel, BorderLayout.NORTH);

        tabbedPane.addTab("Issues", issuePanel);
        tabbedPane.addTab("Users", userPanel);
        tabbedPane.setBackground(UiTheme.CARD_BG);
        tabbedPane.setForeground(Color.BLACK);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public HeaderPanel headerPanel() {
        return headerPanel;
    }

    public IssuePanel issuePanel() {
        return issuePanel;
    }

    public UserPanel userPanel() {
        return userPanel;
    }
}
