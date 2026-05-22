package main;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import main.header.HeaderPanel;

/*
 * 로그인 이후 공통 메인 화면 틀이다.
 *
 * Swing의 MainPanel처럼 사이드바와 본문 영역을 가진다.
 * 각 기능 화면은 center 영역에 교체되어 들어간다.
 */
public class MainPanel extends BorderPane {

    private final HeaderPanel headerPanel = new HeaderPanel();

    public MainPanel() {
        setStyle("-fx-background-color: #f4f6f8;");
        setLeft(headerPanel);
    }

    public void setContent(Node node) {
        setCenter(node);
    }

    public HeaderPanel headerPanel() {
        return headerPanel;
    }
}
