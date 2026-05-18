package ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

/* ui 공통 적용 위함 */
/* 버튼 디자인, 테이블 디자인, 입력창 디자인, 색상 */
public final class UiTheme {

    public static final Color BG = new Color(245, 247, 250);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color BORDER = new Color(218, 224, 232);
    public static final Color PRIMARY = new Color(59, 96, 176);

    private UiTheme() {
    }

    public static Border cardBorder(int padding) {
        return new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(padding, padding, padding, padding)
        );
    }

    public static void stylePrimaryButton(JButton button) {
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setFont(new Font("Dialog", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleSecondaryButton(JButton button) {
        stylePrimaryButton(button);
    }

    public static void styleDangerButton(JButton button) {
        stylePrimaryButton(button);
    }

    public static void styleTextField(JTextField textField) {
        textField.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        textField.setForeground(Color.BLACK);
    }

    public static void stylePasswordField(JPasswordField passwordField) {
        passwordField.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        passwordField.setForeground(Color.BLACK);
    }

    public static void styleCombo(JComboBox<?> comboBox) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(Color.BLACK);
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setGridColor(new Color(230, 234, 240));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(226, 237, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(241, 245, 250));
        header.setForeground(Color.BLACK);
        header.setReorderingAllowed(false);
    }
}
