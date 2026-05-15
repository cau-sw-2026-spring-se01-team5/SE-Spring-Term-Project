package main;

import main.header.HeaderPanel;
import main.issue.IssuePanel;
import main.project.ProjectPanel;
import main.user.UserPanel;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    private final HeaderPanel headerPanel = new HeaderPanel();
    private final IssuePanel issuePanel = new IssuePanel();
    private final ProjectPanel projectPanel = new ProjectPanel();
    private final UserPanel userPanel = new UserPanel();

    private final JTabbedPane tabbedPane = new JTabbedPane();

    public MainPanel() {
        setLayout(new BorderLayout());

        add(headerPanel, BorderLayout.NORTH);

        tabbedPane.addTab("Issues", issuePanel);
        tabbedPane.addTab("Projects", projectPanel);
        tabbedPane.addTab("Users", userPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public HeaderPanel headerPanel() {
        return headerPanel;
    }

    public IssuePanel issuePanel() {
        return issuePanel;
    }

    public ProjectPanel projectPanel() {
        return projectPanel;
    }

    public UserPanel userPanel() {
        return userPanel;
    }

    public void setProjectTabEnabled(boolean enabled) {
        tabbedPane.setEnabledAt(1, enabled);
    }

    public void setUserTabEnabled(boolean enabled) {
        tabbedPane.setEnabledAt(2, enabled);
    }
}