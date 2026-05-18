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

// 메인에서 user 탭 들어갔을 때 보여줄 UI
public class UserPanel extends JPanel implements UserView {

    private final DefaultListModel<UserInfoOutput> userListModel = new DefaultListModel<>(); // 유저 리스트 저장
    private final JList<UserInfoOutput> userList = new JList<>(userListModel); // 실제 화면에 보여지는 유저 목록

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
            if (!showCreateUserPopup()) {
                return;
            }
            createUserEvent.emit();
        });
        deleteUserButton.addActionListener(e -> deleteUserEvent.emit());

        // JList 사용으로 유저 목록을 카드 형태로 표현
        userList.setCellRenderer(new UserCard());
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setFixedCellHeight(-1);
        userList.setVisibleRowCount(-1);
        userList.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setBorder(UiTheme.cardBorder(6));

        add(form, BorderLayout.NORTH);
        add(userScroll, BorderLayout.CENTER);
    }

    // 유저 목록 화면 갱신
    @Override
    public void setUsers(List<UserInfoOutput> users) {
        userListModel.clear();

        for (UserInfoOutput user : users) {
            userListModel.addElement(user);
        }
    }

    // 유저 생성시 입력한 loginId 값 가져오기
    @Override
    public String getNewLoginIdInput() {
        return newLoginIdInput;
    }

    // 유저 생성시 입력한 pw 값 가져오기
    @Override
    public String getNewPasswordInput() {
        return newPasswordInput;
    }

    // 유저 생성시 입력한 userRole 가져오기
    @Override
    public UserRole getNewUserRoleInput() {
        return newUserRoleInput;
    }

    // 현재 선택된 유저 id 가져오기
    @Override
    public Integer getSelectedTargetUserId() {
        UserInfoOutput selected = userList.getSelectedValue();
        return selected == null ? null : selected.userId();
    }

    // 이벤트 등록 메서드
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

    // 관리자 권한의 경우 표시할 버튼 ui 제어
    @Override
    public void applyAdminPermission(boolean admin) {
        createUserButton.setVisible(admin);
        deleteUserButton.setVisible(admin);
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    // 유저 생성 입력 팝업 생성 메서드
    private boolean showCreateUserPopup() {
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

    // JList로 표현된 유저 리스트를 카드 형태로 랜더링 하기 위함
    private static class UserCard implements ListCellRenderer<UserInfoOutput> {

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
