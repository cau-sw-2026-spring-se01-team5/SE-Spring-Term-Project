package app;

import javax.swing.*;

public class AppFrame extends JFrame {

    public AppFrame() {
        setTitle("Issue Tracking System");
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void showLogin(JPanel loginPanel) {
        setContentPane(loginPanel);
        revalidate();
        repaint();
    }

    public void showMain(JPanel mainPanel) {
        setContentPane(mainPanel);
        revalidate();
        repaint();
    }
}