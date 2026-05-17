package main.user;

import enums.user.v1.UserRole;
import ui.UiTheme;
import ui.event.UiEvent;
import user.dto.getProjectUserList.v1.UserInfoOutput;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class UserPanel extends JPanel implements UserView {

    private final DefaultListModel<UserInfoOutput> userListModel = new DefaultListModel<>();
    private final JList<UserInfoOutput> userList = new JList<>(userListModel);

    private String newLoginIdInput = "";
    private String newPasswordInput = "";
    private UserRole newUserRoleInput = UserRole.DEV;

    private final JButton refreshUserButton = new JButton("유저 목록 조회");
    private final JButton createUserButton = new JButton("유저 생성");
    private final JButton deleteUserButton = new JButton("선택 유저 삭제");
    private final UiEvent refreshUsersEvent = new UiEvent();
    private final UiEvent createUserEvent = new UiEvent();
    private final UiEvent deleteUserEvent = new UiEvent();

    public UserPanel() {
        setLayout(new BorderLayout());
        setBackground(UiTheme.BG);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(10, 10, 6, 10));
        form.add(createUserButton);
        form.add(refreshUserButton);
        form.add(deleteUserButton);

        UiTheme.stylePrimaryButton(createUserButton);
        UiTheme.styleSecondaryButton(refreshUserButton);
        UiTheme.styleDangerButton(deleteUserButton);

        refreshUserButton.addActionListener(e -> refreshUsersEvent.emit());
        createUserButton.addActionListener(e -> {
            if (!showCreateUserDialog()) {
                return;
            }
            createUserEvent.emit();
        });
        deleteUserButton.addActionListener(e -> deleteUserEvent.emit());

        userList.setCellRenderer(new UserCardRenderer());
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setFixedCellHeight(-1);
        userList.setVisibleRowCount(-1);
        userList.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setBorder(UiTheme.cardBorder(6));

        add(form, BorderLayout.NORTH);
        add(userScroll, BorderLayout.CENTER);
    }

    @Override
    public void setUsers(List<UserInfoOutput> users) {
        userListModel.clear();

        for (UserInfoOutput user : users) {
            userListModel.addElement(user);
        }
    }

    @Override
    public String getNewLoginIdInput() {
        return newLoginIdInput;
    }

    @Override
    public String getNewPasswordInput() {
        return newPasswordInput;
    }

    @Override
    public UserRole getNewUserRoleInput() {
        return newUserRoleInput;
    }

    @Override
    public Integer getSelectedTargetUserId() {
        UserInfoOutput selected = userList.getSelectedValue();
        return selected == null ? null : selected.userId();
    }

    @Override
    public void onRefreshUsers(Runnable handler) {
        refreshUsersEvent.subscribe(handler);
    }

    @Override
    public void onCreateUser(Runnable handler) {
        createUserEvent.subscribe(handler);
    }

    @Override
    public void onDeleteUser(Runnable handler) {
        deleteUserEvent.subscribe(handler);
    }

    @Override
    public void applyAdminPermission(boolean admin) {
        createUserButton.setVisible(admin);
        deleteUserButton.setVisible(admin);
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private boolean showCreateUserDialog() {
        JTextField loginIdField = new JTextField(16);
        JPasswordField passwordField = new JPasswordField(16);
        JComboBox<UserRole> roleComboBox = new JComboBox<>(UserRole.values());
        UiTheme.styleTextField(loginIdField);
        UiTheme.stylePasswordField(passwordField);
        UiTheme.styleCombo(roleComboBox);

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.add(new JLabel("Login ID"));
        panel.add(loginIdField);
        panel.add(new JLabel("Password"));
        panel.add(passwordField);
        panel.add(new JLabel("Role"));
        panel.add(roleComboBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "유저 생성",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        newLoginIdInput = loginIdField.getText().trim();
        newPasswordInput = new String(passwordField.getPassword());
        newUserRoleInput = (UserRole) roleComboBox.getSelectedItem();
        return true;
    }

    private static class UserCardRenderer implements ListCellRenderer<UserInfoOutput> {

        @Override
        public Component getListCellRendererComponent(
                JList<? extends UserInfoOutput> list,
                UserInfoOutput value,
                int index,
                boolean isSelected,
                boolean cellHasFocus
        ) {
            JPanel card = new JPanel(new BorderLayout(10, 6));
            card.setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(6, 4, 6, 4),
                    BorderFactory.createCompoundBorder(
                            new LineBorder(isSelected ? new Color(77, 128, 255) : new Color(214, 220, 228), 1, true),
                            new EmptyBorder(10, 12, 10, 12)
                    )
            ));

            Color cardBg = isSelected ? new Color(236, 244, 255) : Color.WHITE;
            card.setBackground(cardBg);

            JLabel title = new JLabel(value.loginId());
            title.setForeground(Color.BLACK);
            title.setOpaque(false);

            JPanel info = new JPanel(new GridLayout(2, 2, 8, 4));
            info.setOpaque(false);
            info.add(metaLabel("User ID"));
            info.add(valueLabel("#" + value.userId()));
            info.add(metaLabel("Role"));
            info.add(valueLabel(String.valueOf(value.role())));

            card.add(title, BorderLayout.NORTH);
            card.add(info, BorderLayout.CENTER);

            return card;
        }

        private JLabel metaLabel(String text) {
            JLabel label = new JLabel(text);
            label.setForeground(Color.BLACK);
            return label;
        }

        private JLabel valueLabel(String text) {
            JLabel label = new JLabel(text);
            label.setForeground(Color.BLACK);
            return label;
        }
    }
}
