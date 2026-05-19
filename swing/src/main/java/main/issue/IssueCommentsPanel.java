package main.issue;

import issue.dto.getIssueDetail.v1.CommentOutput;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

// 이슈 상세보기에서 댓글 목록 보는 패널
class IssueCommentsPanel extends JPanel {

    private final JPanel commentsListPanel;
    private final JScrollPane commentsScrollPane;

    IssueCommentsPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Comments"));

        commentsListPanel = new JPanel();
        commentsListPanel.setLayout(new BoxLayout(commentsListPanel, BoxLayout.Y_AXIS));
        commentsListPanel.setBackground(Color.WHITE);

        commentsScrollPane = new JScrollPane(commentsListPanel);
        commentsScrollPane.setPreferredSize(new Dimension(600, 250));
        add(commentsScrollPane, BorderLayout.CENTER);
    }

    // comment 목록 갱신하는 메서드 -> 새로 등록 후 바로 갱신하기 위함
    void renderComments(List<CommentOutput> comments) {
        commentsListPanel.removeAll();

        for (CommentOutput comment : comments) {
            JPanel bubble = new JPanel(new BorderLayout(6, 6));
            bubble.setBorder(BorderFactory.createCompoundBorder(
                    new EmptyBorder(6, 2, 6, 2),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(225, 229, 236)),
                            new EmptyBorder(8, 10, 8, 10)
                    )
            ));
            bubble.setBackground(new Color(250, 251, 253));

            JLabel meta = new JLabel(comment.authorUserId() + "  ·  "
                    + comment.createdAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            meta.setForeground(Color.BLACK);

            JTextArea text = new JTextArea(comment.comment());
            text.setEditable(false);
            text.setLineWrap(true);
            text.setWrapStyleWord(true);
            text.setOpaque(false);

            bubble.add(meta, BorderLayout.NORTH);
            bubble.add(text, BorderLayout.CENTER);
            commentsListPanel.add(bubble);
            commentsListPanel.add(Box.createVerticalStrut(6));
        }

        commentsListPanel.revalidate();
        commentsListPanel.repaint();
        JScrollBar bar = commentsScrollPane.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
    }
}
