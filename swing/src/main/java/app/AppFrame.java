package app;

import javax.swing.*;
import java.awt.*;

/* 전체 Swing JFrame을 관리 */
public class AppFrame extends JFrame {

    // 로그인 화면 크기 정의
    private static final int LOGIN_WIDTH = 460;
    private static final int LOGIN_HEIGHT = 300;

    // 프로젝트 선택 화면 크기 정의
    private static final int PROJECT_SELECT_WIDTH = 560;
    private static final int PROJECT_SELECT_HEIGHT = 380;

    // 메인 화면 크기 정의
    private static final int MAIN_WIDTH = 1100;
    private static final int MAIN_HEIGHT = 720;

    public AppFrame() {
        setTitle("Issue Tracking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫으면 프로그램 종료 되도록
        setResizable(true);
    }

    // 로그인 화면 오픈 메서드 - 객체 주입 -> 생성 책임 외부로
    public void showLogin(JPanel loginPanel) {
        setScreen(
                "Issue Tracking System - Login",
                loginPanel,
                LOGIN_WIDTH,
                LOGIN_HEIGHT
        );
    }

    // 프로젝트 선택 화면 오픈 메서드 - 객체 주입 -> 생성 책임 외부로
    public void showProjectSelect(JPanel projectSelectPanel) {
        setScreen(
                "Issue Tracking System - Project Gateway",
                projectSelectPanel,
                PROJECT_SELECT_WIDTH,
                PROJECT_SELECT_HEIGHT
        );
    }

    // 메인 화면 오픈 메서드 - 객체 주입 -> 생성 책임 외부로
    public void showMain(JPanel mainPanel) {
        setScreen(
                "Issue Tracking System",
                mainPanel,
                MAIN_WIDTH,
                MAIN_HEIGHT
        );
    }

    // 화면에 설정값 적용 위한 메서드
    private void setScreen(
            String title, // 제목
            JPanel panel, // 보여줄 화면
            int width, // 크기
            int height // 크기
    ) {
        // 화면 크기 설정 객체 생성
        Dimension size = new Dimension(width, height);

        // 제목 설정
        setTitle(title);

        // 기존 화면 종료
        getContentPane().removeAll();
        // 지금 띄우고 싶은 화면 set
        setContentPane(panel);

        // 기본 크기 설정
        panel.setPreferredSize(size);

        // 최소 크기 설정
        setMinimumSize(size);

        setPreferredSize(size);

        // 창 크기 바로 적용
        setSize(size);

        pack(); // preferredsize로 바로 창 크기 다시 계산하고 적용 -> 화면 크기 강제 재적용
        setLocationRelativeTo(null); // 화면 가운데 정렬

        // 새로운 패널 기준으로 UI 재배치
        revalidate();
        repaint();
    }
}