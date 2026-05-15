package auth;

import javax.swing.*;

/* 로그인 화면을 담는 최상위 Swing 창 */
/* 실제 입력 UI는 LoginPanel이 담당 */
public class LoginFrame extends JFrame {

    public LoginFrame(LoginPanel loginPanel) {
        setTitle("Issue Tracking System - Login");
        setContentPane(loginPanel);
        setSize(420, 280);
        setLocationRelativeTo(null); // 창을 화면 가운데에 띄우기 위함
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 닫기 누르면 전체 프로그램 종료
    }
}