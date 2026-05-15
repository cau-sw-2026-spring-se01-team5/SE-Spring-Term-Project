package main.user;

import enums.user.v1.UserRole;
import main.NamedItem;
import user.dto.getProjectUserList.v1.UserInfoOutput;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserPanel extends JPanel implements UserView {

    private final DefaultListModel<NamedItem<Integer>> userListModel = new DefaultListModel<>();
    private final JList<NamedItem<Integer>> userList = new JList<>(userListModel);

    private final JTextField newLoginIdField = new JTextField(10);
    private final JPasswordField newPasswordField = new JPasswordField(10);
    private final JComboBox<UserRole> newUserRoleComboBox = new JComboBox<>(UserRole.values());

    private final JButton refreshUserButton = new JButton("유저 목록 조회");
    private final JButton createUserButton = new JButton("유저 생성");
    private final JButton deleteUserButton = new JButton("선택 유저 삭제");

    public UserPanel() {
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Login ID"));
        form.add(newLoginIdField);
        form.add(new JLabel("Password"));
        form.add(newPasswordField);
        form.add(new JLabel("Role"));
        form.add(newUserRoleComboBox);
        form.add(createUserButton);
        form.add(refreshUserButton);
        form.add(deleteUserButton);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(userList), BorderLayout.CENTER);
    }

    @Override
    public void setUsers(List<UserInfoOutput> users) {
        userListModel.clear();

        for (UserInfoOutput user : users) {
            String label = user.loginId() + " (#" + user.userId() + ") / " + user.role();
            userListModel.addElement(new NamedItem<>(user.userId(), label));
        }
    }

    @Override
    public String getNewLoginIdInput() {
        return newLoginIdField.getText().trim();
    }

    @Override
    public String getNewPasswordInput() {
        return new String(newPasswordField.getPassword());
    }

    @Override
    public UserRole getNewUserRoleInput() {
        return (UserRole) newUserRoleComboBox.getSelectedItem();
    }

    @Override
    public Integer getSelectedTargetUserId() {
        NamedItem<Integer> item = userList.getSelectedValue();
        return item == null ? null : item.value();
    }

    @Override
    public void onRefreshUsers(Runnable handler) {
        refreshUserButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onCreateUser(Runnable handler) {
        createUserButton.addActionListener(e -> handler.run());
    }

    @Override
    public void onDeleteUser(Runnable handler) {
        deleteUserButton.addActionListener(e -> handler.run());
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
}