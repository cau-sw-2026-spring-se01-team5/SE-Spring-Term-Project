package app;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    private static final int LOGIN_WIDTH = 460;
    private static final int LOGIN_HEIGHT = 300;

    private static final int PROJECT_SELECT_WIDTH = 560;
    private static final int PROJECT_SELECT_HEIGHT = 380;

    private static final int MAIN_WIDTH = 1100;
    private static final int MAIN_HEIGHT = 720;

    public AppFrame() {
        setTitle("Issue Tracking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
    }

    public void showLogin(JPanel loginPanel) {
        applyScreen(
                "Issue Tracking System - Login",
                loginPanel,
                LOGIN_WIDTH,
                LOGIN_HEIGHT
        );
    }

    public void showProjectSelect(JPanel projectSelectPanel) {
        applyScreen(
                "Issue Tracking System - Project Gateway",
                projectSelectPanel,
                PROJECT_SELECT_WIDTH,
                PROJECT_SELECT_HEIGHT
        );
    }

    public void showMain(JPanel mainPanel) {
        applyScreen(
                "Issue Tracking System",
                mainPanel,
                MAIN_WIDTH,
                MAIN_HEIGHT
        );
    }

    private void applyScreen(
            String title,
            JPanel panel,
            int width,
            int height
    ) {
        Dimension size = new Dimension(width, height);

        setTitle(title);

        getContentPane().removeAll();
        setContentPane(panel);

        panel.setPreferredSize(size);

        setMinimumSize(size);
        setPreferredSize(size);
        setSize(size);

        pack();
        setLocationRelativeTo(null);

        revalidate();
        repaint();
    }
}