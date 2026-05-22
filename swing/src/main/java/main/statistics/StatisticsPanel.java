package main.statistics;

import statistics.dto.getDailyIssueCounts.v1.DailyIssueCountOutput;
import ui.UiTheme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public final class StatisticsPanel {

    private StatisticsPanel() {
    }

    public static void show(Component parent, Map<String, Long> statusCounts, List<DailyIssueCountOutput> dailyCounts) {
        JPanel root = new JPanel(new GridLayout(1, 2, 14, 0));
        root.setBackground(UiTheme.BG);
        root.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel statusCard = buildStatusCard(statusCounts);
        JPanel dailyCard = buildDailyCard(dailyCounts);

        root.add(statusCard);
        root.add(dailyCard);

        JOptionPane.showMessageDialog(parent, root, "프로젝트 통계", JOptionPane.PLAIN_MESSAGE);
    }

    private static JPanel buildStatusCard(Map<String, Long> statusCounts) {
        JPanel card = createCard("상태별 이슈 현황");
        JPanel content = getCardContent(card);
        long max = Math.max(1L, statusCounts.values().stream().mapToLong(Long::longValue).max().orElse(1L));
        for (Map.Entry<String, Long> entry : statusCounts.entrySet()) {
            content.add(createBarRow(entry.getKey(), entry.getValue(), max));
        }
        return card;
    }

    private static JPanel buildDailyCard(List<DailyIssueCountOutput> dailyCounts) {
        JPanel card = createCard("일별 이슈 발생 추이");
        JPanel content = getCardContent(card);
        wrapCardContentWithScroll(card, content);
        long max = Math.max(1L, dailyCounts == null ? 1L : dailyCounts.stream().mapToLong(DailyIssueCountOutput::count).max().orElse(1L));
        if (dailyCounts == null || dailyCounts.isEmpty()) {
            JLabel empty = new JLabel("데이터 없음");
            empty.setForeground(Color.BLACK);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(empty);
            return card;
        }
        for (DailyIssueCountOutput item : dailyCounts) {
            content.add(createBarRow(item.date(), item.count(), max));
        }
        return card;
    }

    private static JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 224, 230), 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setFont(new Font("Dialog", Font.PLAIN, 28));
        card.add(titleLabel, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(14, 0, 0, 0));
        card.add(content, BorderLayout.CENTER);
        card.putClientProperty("content", content);
        return card;
    }

    private static JPanel getCardContent(JPanel card) {
        Object content = card.getClientProperty("content");
        if (content instanceof JPanel panel) {
            return panel;
        }
        return card;
    }

    private static void wrapCardContentWithScroll(JPanel card, JPanel content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(430, 250));
        card.add(scrollPane, BorderLayout.CENTER);
    }

    private static JPanel createBarRow(String label, long count, long max) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(new EmptyBorder(4, 0, 4, 0));
        row.setPreferredSize(new Dimension(0, 36));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.setMinimumSize(new Dimension(0, 36));

        JLabel left = new JLabel(label);
        left.setForeground(Color.BLACK);
        left.setPreferredSize(new Dimension(170, 28));
        left.setFont(new Font("Dialog", Font.PLAIN, 20));
        left.setVerticalAlignment(SwingConstants.CENTER);

        JPanel track = new JPanel(null);
        track.setOpaque(false);
        track.setPreferredSize(new Dimension(190, 28));
        track.setMaximumSize(new Dimension(190, 28));
        track.setMinimumSize(new Dimension(190, 28));

        int width = (int) Math.max(24, Math.round((count * 170.0) / max));
        JPanel bar = new JPanel();
        bar.setBackground(new Color(46, 102, 220));
        bar.setBounds(0, 3, Math.min(width, 180), 22);
        bar.setBorder(new LineBorder(new Color(46, 102, 220), 1, true));
        track.add(bar);

        JLabel right = new JLabel(count + "건");
        right.setForeground(Color.BLACK);
        right.setFont(new Font("Dialog", Font.PLAIN, 20));
        right.setPreferredSize(new Dimension(60, 28));
        right.setVerticalAlignment(SwingConstants.CENTER);

        row.add(left, BorderLayout.WEST);
        row.add(track, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);
        return row;
    }
}
