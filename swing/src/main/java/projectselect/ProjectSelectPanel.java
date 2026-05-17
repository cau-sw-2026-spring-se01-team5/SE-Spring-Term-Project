package projectselect;

import main.NamedItem;
import project.dto.getProjectList.v1.ProjectInfoOutput;
import ui.UiTheme;
import ui.event.UiEvent;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProjectSelectPanel extends JPanel implements ProjectSelectView {

    private final JComboBox<NamedItem<Integer>> projectComboBox = new JComboBox<>();

    private final JButton refreshButton = new JButton("목록 새로고침");
    private final JButton enterButton = new JButton("프로젝트 입장");
    private final JButton logoutButton = new JButton("로그아웃");

    private final JButton createProjectButton = new JButton("프로젝트 생성");
    private final JButton updateProjectButton = new JButton("프로젝트 제목 수정");
    private final JButton deleteProjectButton = new JButton("선택 프로젝트 삭제");

    private final UiEvent loadProjectsEvent = new UiEvent();
    private final UiEvent createProjectEvent = new UiEvent();
    private final UiEvent updateProjectEvent = new UiEvent();
    private final UiEvent deleteProjectEvent = new UiEvent();
    private final UiEvent enterProjectEvent = new UiEvent();
    private final UiEvent logoutEvent = new UiEvent();

    private final JPanel adminPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    private final JLabel hintLabel = new JLabel("프로젝트를 선택한 뒤 입장하세요.");

    public ProjectSelectPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UiTheme.BG);
        setBorder(BorderFactory.createEmptyBorder(28, 36, 28, 36));

        JLabel title = new JLabel("Project List", SwingConstants.LEFT);
        title.setForeground(Color.BLACK);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UiTheme.CARD_BG);
        center.setBorder(UiTheme.cardBorder(16));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel projectLabel = new JLabel("Project");
        projectLabel.setForeground(Color.BLACK);
        center.add(projectLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        center.add(projectComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;

        hintLabel.setForeground(Color.BLACK);
        center.add(hintLabel, gbc);

        JPanel basicActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        basicActionPanel.setOpaque(false);
        basicActionPanel.add(refreshButton);
        basicActionPanel.add(enterButton);
        basicActionPanel.add(logoutButton);

        adminPanel.setOpaque(false);
        adminPanel.add(createProjectButton);
        adminPanel.add(updateProjectButton);
        adminPanel.add(deleteProjectButton);

        JPanel bottom = new JPanel(new GridLayout(2, 1, 0, 10));
        bottom.setOpaque(false);
        bottom.add(basicActionPanel);
        bottom.add(adminPanel);

        UiTheme.styleCombo(projectComboBox);
        UiTheme.styleSecondaryButton(refreshButton);
        UiTheme.stylePrimaryButton(enterButton);
        UiTheme.styleSecondaryButton(logoutButton);
        UiTheme.stylePrimaryButton(createProjectButton);
        UiTheme.styleSecondaryButton(updateProjectButton);
        UiTheme.styleDangerButton(deleteProjectButton);

        refreshButton.addActionListener(e -> loadProjectsEvent.emit());
        createProjectButton.addActionListener(e -> createProjectEvent.emit());
        updateProjectButton.addActionListener(e -> updateProjectEvent.emit());
        deleteProjectButton.addActionListener(e -> deleteProjectEvent.emit());
        enterButton.addActionListener(e -> enterProjectEvent.emit());
        logoutButton.addActionListener(e -> logoutEvent.emit());

        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    @Override
    public void setProjects(List<ProjectInfoOutput> projects) {
        projectComboBox.removeAllItems();

        projectComboBox.addItem(
                new NamedItem<>(
                        null,
                        "<프로젝트를 선택해주세요>"
                )
        );

        for (ProjectInfoOutput project : projects) {
            projectComboBox.addItem(
                    new NamedItem<>(
                            project.projectId(),
                            project.title()
                    )
            );
        }

        projectComboBox.setSelectedIndex(0);
    }

    @Override
    public Integer getSelectedProjectId() {
        NamedItem<Integer> selected =
                (NamedItem<Integer>) projectComboBox.getSelectedItem();

        return selected == null ? null : selected.value();
    }

    @Override
    public String getNewProjectTitleInput() {
        String input = JOptionPane.showInputDialog(
                this,
                "새 프로젝트 이름을 입력하세요.",
                "프로젝트 생성",
                JOptionPane.PLAIN_MESSAGE
        );

        return input == null ? null : input.trim();
    }

    @Override
    public String getUpdateProjectTitleInput() {
        String input = JOptionPane.showInputDialog(
                this,
                "변경할 프로젝트 이름을 입력하세요.",
                "프로젝트 제목 수정",
                JOptionPane.PLAIN_MESSAGE
        );

        return input == null ? null : input.trim();
    }

    @Override
    public void onLoadProjects(Runnable handler) {
        loadProjectsEvent.subscribe(handler);
    }

    @Override
    public void onCreateProject(Runnable handler) {
        createProjectEvent.subscribe(handler);
    }

    @Override
    public void onUpdateProject(Runnable handler) {
        updateProjectEvent.subscribe(handler);
    }

    @Override
    public void onDeleteProject(Runnable handler) {
        deleteProjectEvent.subscribe(handler);
    }

    @Override
    public void onEnterProject(Runnable handler) {
        enterProjectEvent.subscribe(handler);
    }

    @Override
    public void onLogout(Runnable handler) {
        logoutEvent.subscribe(handler);
    }

    @Override
    public void applyAdminPermission(boolean admin) {
        adminPanel.setVisible(admin);
        revalidate();
        repaint();
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
