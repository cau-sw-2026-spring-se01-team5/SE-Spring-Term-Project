package main.header;

import enums.user.v1.UserRole;
import ui.UiTheme;
import ui.event.UiEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

// 헤더 영역의 실제 UI 구현체
public class HeaderPanel extends JPanel implements HeaderView {


    private final JLabel userInfoLabel = new JLabel(); // 현재 로그인한 사용자 정보 표시 라벨
    private final JButton backToProjectListButton = new JButton("프로젝트 목록으로");
    private final JButton logoutButton = new JButton("로그아웃");
    private final UiEvent backToProjectListEvent = new UiEvent(); // actionListener 대신에 UiEvent로 추상화 -> 패널은 순수 UI만 그리도록
    private final UiEvent logoutEvent = new UiEvent(); // actionListener 대신에 UiEvent로 추상화 -> 패널은 순수 UI만 그리도록

    public HeaderPanel() {
        setLayout(new BorderLayout());
        setBackground(UiTheme.CARD_BG);
        setBorder(new EmptyBorder(10, 14, 10, 14));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 왼쪽 영역 패널 -> FlowLayout으로 왼쪽 정렬
        left.setOpaque(false); // 배경 투명 처리
        left.add(userInfoLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // 오른쪽 영역 패널 -> FlowLayout으로 오른쪽 정렬
        right.setOpaque(false);
        right.add(backToProjectListButton);
        right.add(logoutButton);

        userInfoLabel.setForeground(Color.BLACK); // 글자색 변경
        UiTheme.styleSecondaryButton(backToProjectListButton); // 공통 UI 테마 디자인 적용
        UiTheme.styleSecondaryButton(logoutButton);

        // 버튼에 이벤트 연결
        backToProjectListButton.addActionListener(e -> backToProjectListEvent.emit());
        logoutButton.addActionListener(e -> logoutEvent.emit());

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.EAST);
    }

    // 로그인한 유저 정보 가져오기
    @Override
    public void setUserInfo(Integer userId, String loginId, UserRole role) {
        userInfoLabel.setText("User: " + loginId + " (#" + userId + ") / Role: " + role);
    }

    // 로그아웃
    @Override
    public void onLogout(Runnable handler) {
        logoutEvent.subscribe(handler);
    }

    // 프로젝트 리스트 화면으로 돌아가기
    @Override
    public void onBackToProjectList(Runnable handler) {
        backToProjectListEvent.subscribe(handler);
    }

    // 메세지 팝업 출력
    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
