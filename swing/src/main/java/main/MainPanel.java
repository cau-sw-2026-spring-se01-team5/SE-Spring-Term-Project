package main;

import main.header.HeaderPanel;
import main.issue.IssuePanel;
import main.user.UserPanel;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    private final HeaderPanel headerPanel = new HeaderPanel();
    private final IssuePanel issuePanel = new IssuePanel();
    private final UserPanel userPanel = new UserPanel();

    private final JTabbedPane tabbedPane = new JTabbedPane();

    public MainPanel() {
        setLayout(new BorderLayout());

        add(headerPanel, BorderLayout.NORTH);

        tabbedPane.addTab("Issues", issuePanel);
        tabbedPane.addTab("Users", userPanel);

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

    public void setUserTabEnabled(boolean enabled) {
        tabbedPane.setEnabledAt(1, enabled);
    }
}
